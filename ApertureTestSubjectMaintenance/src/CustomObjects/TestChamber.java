package CustomObjects;

import java.util.concurrent.locks.ReentrantLock;

public class TestChamber {
    //Each chamber needs to be locked in hand over hand locking
    public ReentrantLock lock = new ReentrantLock();

    public int testSubjectId, age;

    public int testCompletionStatus = 0;

    public String subjectNameHere;

    public Boolean isTesting;

    public TestChamber nextChamber;

    public TestChamber(int testSubjectId, int age, int testCompletionStatus, String subjectNameHere, Boolean isTesting) {
        this.testSubjectId = testSubjectId;
        this.age = age;
        this.testCompletionStatus = testCompletionStatus;
        this.subjectNameHere = subjectNameHere;
        this.isTesting = isTesting;
    }

}
