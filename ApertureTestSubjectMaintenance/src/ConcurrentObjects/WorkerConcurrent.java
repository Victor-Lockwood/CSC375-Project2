package ConcurrentObjects;

import CustomObjects.TestChamberHandler;

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
    }
}
