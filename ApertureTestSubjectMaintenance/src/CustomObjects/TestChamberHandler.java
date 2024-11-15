package CustomObjects;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class TestChamberHandler {
    //The head of the linked list.
    public TestChamber head = null;

    //Not that deep, just the max age we want a test subject to be so we
    //don't get crazy numbers.
    final int MAX_AGE = 65;

    final int MIN_AGE = 18;

    //Progress should be out of 100.
    final int MAX_PROGRESS = 100;

    final int MAX_ID = 101;

    final int NUM_INITIAL_CHAMBERS = 10;

    public volatile ArrayList<Integer> idList = new ArrayList<>();

    //Some names generated by ChatGPT.  A selection of first and last names
    //to assign to chambers.
    String[] FIRST_NAMES = {"BILLY", "BOB", "CAROLINE", "CAVE", "FACT", "SPACE", "WHEATLEY", "CHELL", "KEVIN", "RICK", "ROBO", "CYBER", "MECH", "BOLT", "CIRCUIT", "STEEL", "PIXEL", "NANO", "SPARK", "TECH", "GIZMO", "VOLT", "RUSTY", "ASTRID", "ZARA", "REX", "ORION", "LEXI", "DROIDA", "NOVA"};
    String[] LAST_NAMES = {"CORE", "SPHERE", "JOHNSON", "ECHO", "MACHINA", "CIRCUITRY", "STEELHEART", "GEARSMITH", "BOLTSTRIDER", "WIREDON", "PIXELWELD", "NANOTECH", "SPARKFORGE", "CYBERLOCK", "GIZMOTECH", "VOLTSTREAM", "RUSTON", "ANDROSON", "ZARATECH", "REXTRON", "ORIONIX", "LEXINGTON", "DROIDSTEIN", "NOVAFLUX"};

    public final Worker[] workers;

    final CountDownLatch finished;

    public TestChamberHandler(int numberOfWorkers) {
        this.workers = new Worker[numberOfWorkers];
        finished = new CountDownLatch(numberOfWorkers);

        for(int i = 0; i<numberOfWorkers; i++) {
            Worker worker = new Worker(this);
            this.workers[i] = worker;
        }
    }

    /**
     * Start all our threads.
     */
    public void start() {
        for(Worker worker : workers) {
            worker.start();
        }
    }

    /**
     * Decrement our countdown latch.
     */
    public void threadFinished() {
        finished.countDown();
    }

    /**
     * Close up shop.
     */
    public void shutdown() {
        for(Worker worker : workers) {
            worker.interrupt();
        }
    }

    /**
     * Wait til the countdown latch has released then the code calling this can resume.
     * @throws InterruptedException
     */
    public void awaitDone() throws InterruptedException {
        finished.await();
    }

    /**
     * Create a test chamber using random data.
     * @return The request test chamber.
     */
    public synchronized TestChamber generateRandomTestChamber(boolean isInitial) {
        int idCandidate = 0;

        //If it's in the list of IDs, regenerate a new one.
        if(!isInitial) {
            while(findIdInList(idCandidate)) {
                idCandidate = ThreadLocalRandom.current().nextInt(MAX_ID);
            }
        }

        this.idList.add(idCandidate);

        int testSubjectId = idCandidate;


        int testSubjectAge = ThreadLocalRandom.current().nextInt(MIN_AGE, MAX_AGE);
        String testSubjectName = pickRandomName();
        int testCompletionStatus = ThreadLocalRandom.current().nextInt(MAX_PROGRESS);
        boolean isTesting = ThreadLocalRandom.current().nextBoolean();

        return new TestChamber(testSubjectId, testSubjectAge, testCompletionStatus, testSubjectName, isTesting);
    }

    public synchronized boolean findIdInList(int id) {

        for(Integer i: idList) {
            if(i.equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a random chamber ID.
     * @return ID of a random generator.
     */
    public synchronized int getRandomChamberId() {
        return idList.get(ThreadLocalRandom.current().nextInt(idList.size() - 1));
    }

    /**
     * Create a full name based on randomly selected first and last names from the list.
     * @return
     */
    public String pickRandomName() {
        int firstnameIndex = ThreadLocalRandom.current().nextInt(FIRST_NAMES.length);
        int lastNameIndex = ThreadLocalRandom.current().nextInt(LAST_NAMES.length);

        return FIRST_NAMES[firstnameIndex] + " " + LAST_NAMES[lastNameIndex];
    }

    /**
     * Add a chamber to the list.  Set the specified chamber as the head if we don't have one already.
     * The bulk of the work happens in the internal method by the same name - this just makes sure we always start at
     * the head for our "main" list.
     * @param testChamberToAdd The test chamber to add to the list.
     */
    //TODO: This is NOT the parallel version.
    private synchronized void addChamberToList(TestChamber testChamberToAdd) {
        if(this.head == null) {
            testChamberToAdd.testSubjectId = 0; //See if we can save ourselves some heartache here
            this.head = testChamberToAdd;
            return;
        }

        addChamberToList(this.head, testChamberToAdd);
    }

    /**
     * Internal method for handling adding on beyond the head.
     * @param currentChamber Current chamber we're looking at in the list.
     * @param testChamberToAdd Chamber we're looking to add to the list.
     */
    //TODO: This is NOT the parallel version.  Use this ONLY for initialization.
    private void addChamberToList(TestChamber currentChamber, TestChamber testChamberToAdd) {

        //Test chamber is the new head.
        if(currentChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber;

            if(currentChamber == this.head)
                this.head = testChamberToAdd; //Update current head if applicable
            return;
        }

        //Test chamber has a greater ID value and head is the end of the list
        if (currentChamber.nextChamber == null) {
            currentChamber.nextChamber = testChamberToAdd;
            return;
        }

        //Current chamber's next node ID is > than testChamberToAdd's
        //Current chamber's next pointer will point to testChamberToAdd,
        //testChamberToAdd's next pointer points to head's original next
        if (currentChamber.nextChamber.testSubjectId > testChamberToAdd.testSubjectId) {
            testChamberToAdd.nextChamber = currentChamber.nextChamber;
            currentChamber.nextChamber = testChamberToAdd;
            return;
        }

        //Current chamber's next node's ID is < testChamberToAdd's,
        //recursively call passing in the next node as the head
        if(currentChamber.nextChamber.testSubjectId < testChamberToAdd.testSubjectId) {
            addChamberToList(currentChamber.nextChamber, testChamberToAdd);
        }
    }

    /**
     * Initialize a linked list of test chambers of the given size.
     */
    public void initializeTestChambers() {
        //Handle the head separately
        TestChamber firstTestChamber = generateRandomTestChamber(true);
        addChamberToList(firstTestChamber);

        for(int i = 0; i < (NUM_INITIAL_CHAMBERS - 1); i++) {
            TestChamber testChamber = generateRandomTestChamber(false);
            addChamberToList(testChamber);
        }
    }
}
