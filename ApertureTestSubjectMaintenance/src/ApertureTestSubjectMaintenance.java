import ConcurrentObjects.TestChamberConcurrent;
import ConcurrentObjects.TestChamberHandlerConcurrent;
import CustomObjects.TestChamber;
import CustomObjects.TestChamberHandler;
import CustomObjects.Worker;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApertureTestSubjectMaintenance {


    /**
     * Total number of CPUs
     */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {

        boolean isLinkedList = false;

        if(isLinkedList) {
            System.out.println("Total threads: " + NCPUS);
            TestChamberHandler testChamberHandler = new TestChamberHandler(NCPUS);

            testChamberHandler.initializeTestChambers();

            //Fire it up!
            testChamberHandler.start();

            //Wait til everything's done its stuff
            testChamberHandler.awaitDone();

            //Let's see what we've got
            printChamberStats(testChamberHandler.head, 0);
            printWorkerStats(testChamberHandler);
        } else {
            System.out.println("Total threads: " + NCPUS);
            TestChamberHandlerConcurrent testChamberHandler = new TestChamberHandlerConcurrent(NCPUS);

            testChamberHandler.initializeTestChambers();

            testChamberHandler.start();

            testChamberHandler.awaitDone();

            printChamberStats(testChamberHandler);
            printWorkerStats(testChamberHandler);
        }

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
            counter++;
        }

    }

    /**
     * Prints stats down the linked list of test chambers starting at the given chamber.
     * @param currentChamber The TestChamber to start with.
     */
    public static void printChamberStats(TestChamber currentChamber, int counter) {
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

        if (currentChamber.nextChamber != null)
            printChamberStats(currentChamber.nextChamber, counter);

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
        }

        int totalOps = totalReads + totalWrites;
        System.out.println("TOTAL WRITES: " + totalWrites); //THIS WILL PROBABLY FLUCTUATE... IT'S OK.  DUE TO RANDOMNESS IN READ/WRITE SELECTION.
        System.out.println("TOTAL READS : " + totalReads);
        System.out.println("TOTAL OPS   :  " + totalOps);
        System.out.println("----------");

        System.out.println("PERCENT WRITES: " + (((double)totalWrites / (double)totalOps) * 100) + "%");
        System.out.println("PERCENT WRITES: " + (((double)totalReads / (double)totalOps) * 100) + "%");
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
        }

        int totalOps = totalReads + totalWrites;
        System.out.println("TOTAL WRITES: " + totalWrites); //THIS WILL PROBABLY FLUCTUATE... IT'S OK.  DUE TO RANDOMNESS IN READ/WRITE SELECTION.
        System.out.println("TOTAL READS : " + totalReads);
        System.out.println("TOTAL OPS   :  " + totalOps);
        System.out.println("----------");

        System.out.println("PERCENT WRITES: " + (((double)totalWrites / (double)totalOps) * 100) + "%");
        System.out.println("PERCENT WRITES: " + (((double)totalReads / (double)totalOps) * 100) + "%");
    }
}
