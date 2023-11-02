package vlockwoo.ConcurrentObjects;

import java.util.concurrent.ThreadLocalRandom;

public class WorkerConcurrent extends Thread {
    final TestChamberHandlerConcurrent chamberHandler;

    public int completedWrites = 0;

    public int completedReads = 0;

    final int MAX_WRITES;

    public WorkerConcurrent(TestChamberHandlerConcurrent chamberHandler) {
        this.chamberHandler = chamberHandler;
        MAX_WRITES = (int) Math.floor((this.chamberHandler.MAX_ID - (this.chamberHandler.NUM_INITIAL_CHAMBERS + 1)) / this.chamberHandler.workers.length);
    }

    public void run() {
        boolean isWrite = ThreadLocalRandom.current().nextInt(10) >= 8;

        while((completedWrites + completedReads) < (MAX_WRITES * 10)) {

            if(isWrite && (completedWrites < MAX_WRITES)) {
                createAndInsertChamber();
                completedWrites++;
            } else {
                int chamberId = ThreadLocalRandom.current().nextInt(chamberHandler.MAX_ID);
                readChamber(chamberId);
                completedReads++;
            }

            isWrite = ThreadLocalRandom.current().nextInt(100) >= 80;
        }
        chamberHandler.threadFinished();
    }

    //***** READ CODE *****

    private boolean readChamber(int id) {
        TestChamberConcurrent currentChamber = this.chamberHandler.chamberMap.get(id);

        if(currentChamber != null) {
            int x = currentChamber.age;
            String name = currentChamber.subjectNameHere;

            return true;
        } else {
            return false;
        }
    }

    //***** WRITE CODE *****
    private void createAndInsertChamber() {
        TestChamberConcurrent testChamberToAdd = chamberHandler.generateRandomTestChamber();

        chamberHandler.chamberMap.put(testChamberToAdd.testSubjectId, testChamberToAdd);
    }
}
