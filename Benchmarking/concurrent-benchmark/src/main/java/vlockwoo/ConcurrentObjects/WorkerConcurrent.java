package vlockwoo.ConcurrentObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WorkerConcurrent implements Runnable {
    final TestChamberHandlerConcurrent chamberHandler;

    public int completedWrites = 0;

    public int completedReads = 0;

    final int MAX_WRITES;

    final int PERCENT_READS;

    //Just so our reads don't get washed away
    public long dumpingGrounds = 0;

    final int MAX_CHAMBERS;

    public WorkerConcurrent(TestChamberHandlerConcurrent chamberHandler) {
        this.chamberHandler = chamberHandler;
        this.PERCENT_READS = this.chamberHandler.PERCENT_READS;
        this.MAX_CHAMBERS = this.chamberHandler.MAX_CHAMBERS;
        MAX_WRITES = (int) Math.floor((this.MAX_CHAMBERS - (this.chamberHandler.NUM_INITIAL_CHAMBERS + 1)) / this.chamberHandler.workers.length);
    }

    public void run() {
        boolean isWrite = ThreadLocalRandom.current().nextInt(100) >= this.PERCENT_READS;

        while((completedWrites + completedReads) < 100) {

            if(isWrite && (completedWrites < MAX_WRITES)) {
                createAndInsertChamber();
                completedWrites++;
            } else {
                List<UUID> keys = new ArrayList<>(this.chamberHandler.chamberMap.keySet());
                UUID chamberId = keys.get(ThreadLocalRandom.current().nextInt(keys.size() - 1));
                readChamber(chamberId);
                completedReads++;
            }

            isWrite = ThreadLocalRandom.current().nextInt(100) >= this.PERCENT_READS;
        }

    }

    //***** READ CODE *****

    private boolean readChamber(UUID id) {
        TestChamberConcurrent currentChamber = this.chamberHandler.chamberMap.get(id);

        if(currentChamber != null) {
            int x = currentChamber.age;
            String name = currentChamber.subjectNameHere;

            this.dumpingGrounds += x;
            this.dumpingGrounds += name.length();

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
