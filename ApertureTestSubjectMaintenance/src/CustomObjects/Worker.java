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
    public void createAndInsertChamber() {
        TestChamber testChamberToAdd = this.chamberHandler.generateRandomTestChamber();

        //Not shared - shouldn't need to be atomic or anything
        boolean foundNextSpot = false;
        TestChamber currentChamber = this.chamberHandler.head;
        TestChamber nextChamber = null;

        currentChamber.lock.lock();

        if(currentChamber.nextChamber != null) {
            nextChamber = currentChamber.nextChamber;
            nextChamber.lock.lock();
        }

        while(!foundNextSpot) {

            //Either at the end of the list or the head is the only guy in our list

            if(currentChamber.nextChamber == null){
                if(currentChamber.testSubjectId < testChamberToAdd.testSubjectId) {
                    currentChamber.nextChamber = testChamberToAdd;

                    foundNextSpot = true;
                } else {
                    testChamberToAdd.nextChamber = currentChamber;
                    //If we get in here, this was the original head and nothing else was on the list
                    //We don't need to try to lock it again because it should already be locked as currentChamber
                    this.chamberHandler.head = testChamberToAdd;

                    foundNextSpot = true;
                }
            //Add it in if we found the right spot.
            } else if((currentChamber.testSubjectId < testChamberToAdd.testSubjectId) &&
               (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId)) {

                testChamberToAdd.nextChamber = currentChamber.nextChamber;
                currentChamber.nextChamber = testChamberToAdd;

                foundNextSpot = true;
            }

            //Pass hands
            if(!foundNextSpot) {
                TestChamber tempChamber = currentChamber;
                currentChamber = nextChamber;
                tempChamber.lock.unlock();

                //Grab the next chamber in line
                if(currentChamber.nextChamber != null) {
                    nextChamber = currentChamber.nextChamber;
                    nextChamber.lock.lock();
                } else {
                    nextChamber = null;
                }

            //We found a spot and inserted the new chamber - unlock and leave
            } else {
                currentChamber.lock.unlock();
                if(nextChamber != null)
                    nextChamber.lock.unlock();
            }

        }

        //if(lockedChamber1.lock.isHeldByCurrentThread()) lockedChamber1.lock.unlock();
        //if(lockedChamber2 != null && lockedChamber2.lock.isHeldByCurrentThread()) lockedChamber2.lock.unlock();
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
