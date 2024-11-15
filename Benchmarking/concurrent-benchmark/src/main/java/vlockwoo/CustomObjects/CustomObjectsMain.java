package vlockwoo.CustomObjects;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CustomObjectsMain {


    /**
     * Total number of CPUs
     */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {

        System.out.println("Total threads: " + NCPUS);

        //Referred to Scarlett Weeks for thread pool code
        TestChamberHandler testChamberHandler = new TestChamberHandler(128, 80, 1000);

        ExecutorService threadPool = Executors.newFixedThreadPool(128);

        for (int i=0;i<128;i++){
            threadPool.submit(() -> {
                testChamberHandler.start();
            });
        };

        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        //Wait til everything's done its stuff
//        testChamberHandler.awaitDone();

        //Let's see what we've got
        printChamberStats(testChamberHandler.head, 0, -1, true);
        printWorkerStats(testChamberHandler);

    }

    /**
     * Prints stats down the linked list of test chambers starting at the given chamber.
     * @param currentChamber The TestChamber to start with.
     */
    public static void printChamberStats(TestChamber currentChamber, int counter, int previousId, boolean inOrder) {
        counter++;

        System.out.println(
                "COUNT: " + counter + "\n" +
                "Generated test chamber with following data\n" +
                        "ID:              " + currentChamber.testSubjectId + "\n" +
                        "Name:            " + currentChamber.subjectNameHere + "\n" +
                        "Is Testing:      " + currentChamber.isTesting + "\n" +
                        "Age:             " + currentChamber.age + "\n" +
                        "Test Completion: " + currentChamber.testCompletionStatus + "%\n"
        );

        if(currentChamber.testSubjectId < previousId) {
            inOrder = false;
        }

        previousId = currentChamber.testSubjectId;
        System.out.println("In order: " + inOrder + "\n");

        if (currentChamber.nextChamber != null)
            printChamberStats(currentChamber.nextChamber, counter, previousId, inOrder);

    }

    /**
     * Print the worker read/write stats of a given TestChamberHandler.
     * @param chamberHandler The TestChamberHandler to work with.
     */
    public static void printWorkerStats(TestChamberHandler chamberHandler) {
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
