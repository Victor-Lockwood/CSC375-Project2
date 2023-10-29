import CustomObjects.TestChamber;
import CustomObjects.TestChamberHandler;
import CustomObjects.Worker;

public class ApertureTestSubjectMaintenance {


    /**
     * Total number of CPUs
     */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
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

        System.out.println("TOTAL WRITES: " + totalWrites);
        System.out.println("TOTAL READS : " + totalReads);
    }
}
