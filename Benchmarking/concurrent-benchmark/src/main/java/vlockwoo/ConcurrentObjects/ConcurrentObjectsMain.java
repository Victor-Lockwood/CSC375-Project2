package vlockwoo.ConcurrentObjects;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentObjectsMain {


    /**
     * Total number of CPUs
     */
    //static final int NCPUS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {

        //System.out.println("Total threads: " + NCPUS);
        TestChamberHandlerConcurrent testChamberHandler = new TestChamberHandlerConcurrent(50, 80, 100);
        ExecutorService threadPool = Executors.newFixedThreadPool(50);

        threadPool.submit(testChamberHandler::start);

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        printChamberStats(testChamberHandler);
        printWorkerStats(testChamberHandler);

    }

    /**
     * Prints stats down the linked list of test chambers starting at the given chamber.
     * @param chamberHandler The TestChamberHandlerConcurrent to work with.
     */
    public static void printChamberStats(TestChamberHandlerConcurrent chamberHandler) {
        int counter = 1;

        for(TestChamberConcurrent currentChamber : chamberHandler.chamberMap.values()) {
            System.out.println(
                    "COUNT: " + counter + "\n" +
                            "Generated test chamber with following data\n" +
                            "ID:              " + currentChamber.testSubjectId + "\n" +
                            "Name:            " + currentChamber.subjectNameHere + "\n" +
                            "Is Testing:      " + currentChamber.isTesting + "\n" +
                            "Age:             " + currentChamber.age + "\n" +
                            "Test Completion: " + currentChamber.testCompletionStatus + "%\n"
            );

            //It's a hashmap, we don't need to know if it's in order
            counter++;
        }

    }


    public static void printWorkerStats(TestChamberHandlerConcurrent chamberHandler) {
        int totalWrites = 0;
        int totalReads = 0;

        for(int i = 0; i<chamberHandler.workers.length; i++) {
            totalWrites += chamberHandler.workers[i].completedWrites;
            totalReads += chamberHandler.workers[i].completedReads;

            System.out.println(
                    "-- Stats for Worker " + i + "--\n - " +
                            "Reads :  " + chamberHandler.workers[i].completedReads + "\n - " +
                            "Writes:  " +chamberHandler.workers[i].completedWrites + "\n\n"
            );

            //Just to keep from optimizing values away
            if(chamberHandler.workers[i].dumpingGrounds > 0) System.out.print("");
        }

        int totalOps = totalReads + totalWrites;
        System.out.println("TOTAL WRITES: " + totalWrites); //THIS WILL PROBABLY FLUCTUATE... IT'S OK.  DUE TO RANDOMNESS IN READ/WRITE SELECTION.
        System.out.println("TOTAL READS : " + totalReads);
        System.out.println("TOTAL OPS   :  " + totalOps);
        System.out.println("----------");

        System.out.println("PERCENT WRITES: " + (((double)totalWrites / (double)totalOps) * 100) + "%");
        System.out.println("PERCENT READS: " + (((double)totalReads / (double)totalOps) * 100) + "%");
    }
}
