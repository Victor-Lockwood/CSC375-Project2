package CustomObjects;

import java.util.concurrent.ThreadLocalRandom;

public class Worker extends Thread {

    final TestChamberHandler chamberHandler;

    int completedWrites = 0;

    int completedReads = 0;

    final int MAX_WRITES;

    public Worker(TestChamberHandler chamberHandler) {
        this.chamberHandler = chamberHandler;
        MAX_WRITES = (int) Math.floor((this.chamberHandler.MAX_ID - (this.chamberHandler.NUM_INITIAL_CHAMBERS + 1)) / this.chamberHandler.workers.length);
    }

    public void run() {
        boolean isWrite = ThreadLocalRandom.current().nextInt(10) >= 8;

        while((completedWrites + completedReads) < 5) {
            createAndInsertChamber();
            completedWrites++;

/*            if(isWrite) {
                createAndInsertChamber();
                completedWrites++;
            } else {
                readRandomChamber();
                completedReads++;
            }

            isWrite = ThreadLocalRandom.current().nextInt(100) >= 80;*/
        }
        chamberHandler.threadFinished();
    }

    private void readRandomChamber() {
        int chamberId = ThreadLocalRandom.current().nextInt(chamberHandler.MAX_ID);

        while(traverseList(chamberId, chamberHandler.head));
    }

    private boolean traverseList(int id, TestChamber currentChamber) {
        if(currentChamber.testSubjectId == id) {
            int x = currentChamber.age;
            String name = currentChamber.subjectNameHere;
            return true;
        } else if(currentChamber.testSubjectId > id) {
            return false;
        } else if(currentChamber.nextChamber != null) {
            traverseList(id, currentChamber.nextChamber);
        }
        return false;
    }

    //***** WRITE CODE *****

    /**
     * Main code for writers to add chambers willy-nilly.
     */
    private void createAndInsertChamber() {
        this.chamberHandler.head.lock.lock();
        TestChamber testChamberToAdd = chamberHandler.generateRandomTestChamber(false);
        //testChamberToAdd.lock.lock();

        if(this.chamberHandler.head.nextChamber == null) {
            this.chamberHandler.head.nextChamber = testChamberToAdd;
            this.chamberHandler.head.lock.unlock();

            return;
        } else {
            this.chamberHandler.head.nextChamber.lock.lock();
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

            currentChamber.lock.unlock();
            if(currentChamber.nextChamber != null) currentChamber.nextChamber.lock.unlock();
            return;
        }

        //Test chamber has a greater ID value and head is the end of the list
        if (currentChamber.nextChamber == null) {
            currentChamber.nextChamber = testChamberToAdd;

            currentChamber.lock.unlock();
            return;
        }

        //Current chamber's next node ID is > than testChamberToAdd's
        //Current chamber's next pointer will point to testChamberToAdd,
        //testChamberToAdd's next pointer points to head's original next
        if (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber.nextChamber;
            currentChamber.nextChamber = testChamberToAdd;

            currentChamber.lock.unlock();
            testChamberToAdd.nextChamber.lock.unlock();
            return;
        }

        //Current chamber's next node's ID is < testChamberToAdd's,
        //recursively call passing in the next node as the head
        if(currentChamber.nextChamber.testSubjectId < testChamberToAdd.testSubjectId) {
            if(currentChamber.nextChamber.nextChamber != null) currentChamber.nextChamber.nextChamber.lock.lock();
            TestChamber nextInLine = currentChamber.nextChamber;
            currentChamber.lock.unlock();

            addChamberToList(nextInLine, testChamberToAdd);
        }
    }

}
