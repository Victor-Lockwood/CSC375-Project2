package CustomObjects;

public class Worker extends Thread {

    final TestChamberHandler chamberHandler;

    public Worker(TestChamberHandler chamberHandler) {
        this.chamberHandler = chamberHandler;
    }

    public void run() {
        createAndInsertChamber();
    }

    //Pattern I found here: https://www.linkedin.com/pulse/concurrent-simple-data-structure-danil-tolonbekov

    /**
     * Main code for writers to add chambers willy-nilly.
     */
    public void createAndInsertChamber() {
        TestChamber testChamberToAdd = this.chamberHandler.generateRandomTestChamber();
        TestChamber lockedChamber1 = null;
        TestChamber lockedChamber2 = null;

        //Not shared - shouldn't need to be atomic or anything
        Boolean foundNextSpot = false;
        TestChamber currentChamber = this.chamberHandler.head;

        while(!foundNextSpot) {
            lockPair(currentChamber);

            //Set these references so we can unlock them properly later
            lockedChamber1 = currentChamber;
            if(currentChamber.nextChamber != null) lockedChamber2 = currentChamber.nextChamber;

            try {
                //Either at the end of the list or the head is the only guy in our list
                if(currentChamber.nextChamber == null){
                    if(currentChamber.testSubjectId < testChamberToAdd.testSubjectId) {
                        currentChamber.nextChamber = testChamberToAdd;

                        System.out.println("Added chamber.");
                        foundNextSpot = true;
                    } else {
                        testChamberToAdd.nextChamber = currentChamber;
                        //If we get in here, this was the original head and nothing else was on the list
                        //We don't need to try to lock it again because it should already be locked as currentChamber
                        this.chamberHandler.head = testChamberToAdd;

                        System.out.println("Added chamber.  Replaced head.");
                        foundNextSpot = true;
                    }
                //Add it in if we found the right spot.
                } else if((currentChamber.testSubjectId < testChamberToAdd.testSubjectId) &&
                   (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId)) {

                    testChamberToAdd.nextChamber = currentChamber.nextChamber;
                    currentChamber.nextChamber = testChamberToAdd;

                    System.out.println("Added chamber.");
                    foundNextSpot = true;
                }
            } finally {
                lockedChamber1.lock.unlock();
                if(lockedChamber2 != null) lockedChamber2.lock.unlock();
            }

            if(currentChamber.nextChamber != null) {
                currentChamber = currentChamber.nextChamber;
            } else {
                //Something went wrong if we get here
                break;
            }

        }

        chamberHandler.threadFinished();
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
