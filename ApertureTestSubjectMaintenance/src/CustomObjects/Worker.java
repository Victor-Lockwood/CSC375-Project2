package CustomObjects;

public class Worker extends Thread {

    final TestChamberHandler chamberHandler;

    public Worker(TestChamberHandler chamberHandler) {
        this.chamberHandler = chamberHandler;
    }

    public void run() {
        createAndInsertChamber();
        chamberHandler.threadFinished();
    }

    //Pattern I found here: https://www.linkedin.com/pulse/concurrent-simple-data-structure-danil-tolonbekov

    /**
     * Main code for writers to add chambers willy-nilly.
     */
    private void createAndInsertChamber() {
        this.chamberHandler.head.lock.lock();
        TestChamber testChamberToAdd = chamberHandler.generateRandomTestChamber();
        //testChamberToAdd.lock.lock();

        if(this.chamberHandler.head.nextChamber == null) {
            this.chamberHandler.head.nextChamber = testChamberToAdd;
            this.chamberHandler.head.lock.unlock();
            ///testChamberToAdd.lock.unlock();
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

            //testChamberToAdd.lock.unlock();
            currentChamber.lock.unlock();
            if(currentChamber.nextChamber != null) currentChamber.nextChamber.lock.unlock();
            return;
        }

        //Test chamber has a greater ID value and head is the end of the list
        if (currentChamber.nextChamber == null) {
            currentChamber.nextChamber = testChamberToAdd;

            //testChamberToAdd.lock.unlock();
            currentChamber.lock.unlock();
            return;
        }

        //Current chamber's next node ID is > than testChamberToAdd's
        //Current chamber's next pointer will point to testChamberToAdd,
        //testChamberToAdd's next pointer points to head's original next
        if (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber.nextChamber;
            currentChamber.nextChamber = testChamberToAdd;

            //testChamberToAdd.lock.unlock();
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

    //Pattern I found here: https://www.linkedin.com/pulse/concurrent-simple-data-structure-danil-tolonbekov
    /**
     * Locks the given TestChamber and the one attached to it if one exists.
     * We can't do an unlocked pair because some reference switching will cause them to be unlinked.
     * @param testChamber  The TestChamber to lock.
     */
    private void lockPair(TestChamber testChamber) {
        testChamber.lock.lock();

        if(testChamber.nextChamber != null) {
            testChamber.nextChamber.lock.lock();
        }
    }
}
