package ConcurrentObjects;

/**
 * This version is for use with ConcurrentHashMap.
 */
public class TestChamberConcurrent {

    public int testSubjectId, age;

    public int testCompletionStatus = 0;

    public String subjectNameHere;

    public Boolean isTesting;

    public TestChamberConcurrent(int testSubjectId, int age, int testCompletionStatus, String subjectNameHere, Boolean isTesting) {
        this.testSubjectId = testSubjectId;
        this.age = age;
        this.testCompletionStatus = testCompletionStatus;
        this.subjectNameHere = subjectNameHere;
        this.isTesting = isTesting;
    }
}
