package CustomObjects;

public class TestChamber {
    int testSubjectId, age;

    int testCompletionStatus = 0;

    String subjectNameHere;

    Boolean isTesting;

    TestChamber nextChamber;

    public TestChamber(int testSubjectId, int age, String subjectNameHere, Boolean isTesting) {
        this.testSubjectId = testSubjectId;
        this.age = age;
        this.subjectNameHere = subjectNameHere;
        this.isTesting = isTesting;
    }
}
