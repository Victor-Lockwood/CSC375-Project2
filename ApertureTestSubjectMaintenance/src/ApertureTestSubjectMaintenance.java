import CustomObjects.TestChamber;
import CustomObjects.TestChamberHandler;

public class ApertureTestSubjectMaintenance {

    public static void main(String[] args) throws Exception {

        TestChamberHandler testChamberHandler = new TestChamberHandler();

        testChamberHandler.initializeTestChambers(10);

        printChamberStats(testChamberHandler.head);
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
