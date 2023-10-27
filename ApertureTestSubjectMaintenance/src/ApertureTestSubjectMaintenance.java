import CustomObjects.TestChamber;
import CustomObjects.TestChamberHandler;
import CustomObjects.Worker;

public class ApertureTestSubjectMaintenance {


    /**
     * Total number of CPUs
     */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /** Runs start with two threads, increasing by two through max */
    static final int DEFAULT_MAX_THREADS = Math.max(4, NCPUS + NCPUS/2);

    public static void main(String[] args) throws Exception {
        System.out.println("Total threads: " + NCPUS);
        TestChamberHandler testChamberHandler = new TestChamberHandler(NCPUS);

        testChamberHandler.initializeTestChambers(10);

        //Fire it up!
        testChamberHandler.start();

        //Wait til everything's done its stuff
        testChamberHandler.awaitDone();

        //Let's see what we've got
        printChamberStats(testChamberHandler.head, 0);


        //System.out.println("placeholder");
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
                        "Name:            " + currentChamber.subjectNameHere + "\n" +
                        "Age:             " + currentChamber.age + "\n" +
                        "ID:              " + currentChamber.testSubjectId + "\n" +
                        "Is Testing:      " + currentChamber.isTesting + "\n" +
                        "Test Completion: " + currentChamber.testCompletionStatus + "%\n"
        );

        if (currentChamber.nextChamber != null)
            printChamberStats(currentChamber.nextChamber, counter);



    }
}
