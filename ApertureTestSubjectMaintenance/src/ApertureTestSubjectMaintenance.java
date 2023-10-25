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

        TestChamberHandler testChamberHandler = new TestChamberHandler();

        testChamberHandler.initializeTestChambers(10);

        printChamberStats(testChamberHandler.head);

        System.out.println("Total threads: " + NCPUS);

        Worker[] workers = new Worker[NCPUS];

        for(int i = 0; i < NCPUS; i++) {
            Worker worker = new Worker();
            workers[i] = worker;
            worker.start();
        }
    }

    /**
     * Prints stats down the linked list of test chambers starting at the given chamber.
     * @param currentChamber The TestChamber to start with.
     */
    public static void printChamberStats(TestChamber currentChamber) {
        System.out.println(
                "Generated test chamber with following data\n" +
                        "Name:            " + currentChamber.subjectNameHere + "\n" +
                        "Age:             " + currentChamber.age + "\n" +
                        "ID:              " + currentChamber.testSubjectId + "\n" +
                        "Is Testing:      " + currentChamber.isTesting + "\n" +
                        "Test Completion: " + currentChamber.testCompletionStatus + "%\n"
        );

        if (currentChamber.nextChamber != null)
            printChamberStats(currentChamber.nextChamber);


    }
}
