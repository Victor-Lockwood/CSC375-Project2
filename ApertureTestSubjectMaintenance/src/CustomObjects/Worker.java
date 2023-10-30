package CustomObjects;

import java.util.concurrent.ThreadLocalRandom;

public class Worker extends Thread {

    final TestChamberHandler chamberHandler;

    public int completedWrites = 0;

    public int completedReads = 0;

    final int MAX_WRITES;

    public Worker(TestChamberHandler chamberHandler) {
        this.chamberHandler = chamberHandler;
        MAX_WRITES = (int) Math.floor((this.chamberHandler.MAX_ID - (this.chamberHandler.NUM_INITIAL_CHAMBERS + 1)) / this.chamberHandler.workers.length);
    }

    public void run() {
        boolean isWrite = ThreadLocalRandom.current().nextInt(10) >= 8;

        //If this is greater than MAX_WRITES we'll loop
        while((completedWrites + completedReads) < (MAX_WRITES * 10)) {

            if(isWrite && (completedWrites < MAX_WRITES)) {
                createAndInsertChamber();
                completedWrites++;
            } else {
                int chamberId = ThreadLocalRandom.current().nextInt(chamberHandler.MAX_ID);
                traverseList(chamberId);
                completedReads++;
            }

            isWrite = ThreadLocalRandom.current().nextInt(100) >= 80;
        }
        chamberHandler.threadFinished();
    }

    private boolean traverseList(int id) {
        long stamp = this.chamberHandler.head.lock.readLock();
        TestChamber currentChamber = this.chamberHandler.head;
        boolean found = false;
        boolean notPresent = false;

            while (!found && !notPresent) {
                if (currentChamber.testSubjectId == id) {
                    int x = currentChamber.age;
                    String name = currentChamber.subjectNameHere;

                    currentChamber.lock.unlock(stamp);
                    found = true;
                } else if (currentChamber.testSubjectId > id || currentChamber.nextChamber == null) {
                    currentChamber.lock.unlock(stamp);
                    notPresent = true;
                } else if (currentChamber.nextChamber != null) {
                    long stamp2 = currentChamber.nextChamber.lock.readLock();
                    TestChamber nextInLine = currentChamber.nextChamber;
                    currentChamber.lock.unlock(stamp);
                    stamp = stamp2;
                    currentChamber = nextInLine;
                }
            }

        return found;
    }

    //***** WRITE CODE *****

    /**
     * Main code for writers to add chambers willy-nilly.
     */
    private void createAndInsertChamber() {
        this.chamberHandler.head.lock.writeLock();
        TestChamber testChamberToAdd = chamberHandler.generateRandomTestChamber(false);
        //testChamberToAdd.lock.lock();

        if(this.chamberHandler.head.nextChamber == null) {
            this.chamberHandler.head.nextChamber = testChamberToAdd;
            this.chamberHandler.head.lock.tryUnlockWrite();

            return;
        } else {
            this.chamberHandler.head.nextChamber.lock.writeLock();
        }

        addChamberToList(this.chamberHandler.head, testChamberToAdd);
    }

    /**
     * Internal method for handling adding on beyond the head.
     * @param currentChamber Current chamber we're looking at in the list.
     * @param testChamberToAdd Chamber we're looking to add to the list.
     */
    private void addChamberToList(TestChamber currentChamber, TestChamber testChamberToAdd) {

        //Test chamber is the new head.
        if(currentChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber;

            if(currentChamber == this.chamberHandler.head)
                this.chamberHandler.head = testChamberToAdd; //Update current head if applicable

            currentChamber.lock.tryUnlockWrite();
            if(currentChamber.nextChamber != null) currentChamber.nextChamber.lock.tryUnlockWrite();
            return;
        }

        //Test chamber has a greater ID value and head is the end of the list
        if (currentChamber.nextChamber == null) {
            currentChamber.nextChamber = testChamberToAdd;

            currentChamber.lock.tryUnlockWrite();
            return;
        }

        //Current chamber's next node ID is > than testChamberToAdd's
        //Current chamber's next pointer will point to testChamberToAdd,
        //testChamberToAdd's next pointer points to head's original next
        if (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber.nextChamber;
            currentChamber.nextChamber = testChamberToAdd;

            currentChamber.lock.tryUnlockWrite();
            testChamberToAdd.nextChamber.lock.tryUnlockWrite();
            return;
        }

        //Current chamber's next node's ID is < testChamberToAdd's,
        //recursively call passing in the next node as the head
        if(currentChamber.nextChamber.testSubjectId < testChamberToAdd.testSubjectId) {
            if(currentChamber.nextChamber.nextChamber != null) currentChamber.nextChamber.nextChamber.lock.writeLock();
            TestChamber nextInLine = currentChamber.nextChamber;
            currentChamber.lock.tryUnlockWrite();

            addChamberToList(nextInLine, testChamberToAdd);
        }
    }

}
