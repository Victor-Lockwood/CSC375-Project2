package ConcurrentObjects;

import CustomObjects.TestChamber;
import CustomObjects.Worker;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class TestChamberHandlerConcurrent {
    //Not that deep, just the max age we want a test subject to be so we
    //don't get crazy numbers.
    final int MAX_AGE = 65;

    final int MIN_AGE = 18;

    //Progress should be out of 100.
    final int MAX_PROGRESS = 100;

    final int MAX_ID = 101;

    final int NUM_INITIAL_CHAMBERS = 10;

    public final ConcurrentHashMap<Integer, TestChamberConcurrent> chamberMap;

    //Some names generated by ChatGPT.  A selection of first and last names
    //to assign to chambers.
    String[] FIRST_NAMES = {"BILLY", "BOB", "CAROLINE", "CAVE", "FACT", "SPACE", "WHEATLEY", "CHELL", "KEVIN", "RICK", "ROBO", "CYBER", "MECH", "BOLT", "CIRCUIT", "STEEL", "PIXEL", "NANO", "SPARK", "TECH", "GIZMO", "VOLT", "RUSTY", "ASTRID", "ZARA", "REX", "ORION", "LEXI", "DROIDA", "NOVA"};
    String[] LAST_NAMES = {"CORE", "SPHERE", "JOHNSON", "ECHO", "MACHINA", "CIRCUITRY", "STEELHEART", "GEARSMITH", "BOLTSTRIDER", "WIREDON", "PIXELWELD", "NANOTECH", "SPARKFORGE", "CYBERLOCK", "GIZMOTECH", "VOLTSTREAM", "RUSTON", "ANDROSON", "ZARATECH", "REXTRON", "ORIONIX", "LEXINGTON", "DROIDSTEIN", "NOVAFLUX"};

    public final WorkerConcurrent[] workers;

    final CountDownLatch finished;

    public TestChamberHandlerConcurrent(int numberOfWorkers) {
        this.workers = new WorkerConcurrent[numberOfWorkers];
        this.finished = new CountDownLatch(numberOfWorkers);

        for(int i = 0; i<numberOfWorkers; i++) {
            WorkerConcurrent worker = new WorkerConcurrent(this);
            this.workers[i] = worker;
        }

        this.chamberMap = new ConcurrentHashMap<>(NUM_INITIAL_CHAMBERS + (this.workers[0].MAX_WRITES * this.workers.length));
    }

    /**
     * Start all our threads.
     */
    public void start() {
        for(WorkerConcurrent worker : workers) {
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
        for(WorkerConcurrent worker : workers) {
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
    public synchronized TestChamberConcurrent generateRandomTestChamber() {
        int idCandidate = ThreadLocalRandom.current().nextInt(MAX_ID);

        //If it's in the list of IDs, regenerate a new one.
        while(this.chamberMap.containsKey(idCandidate)) {
            idCandidate = ThreadLocalRandom.current().nextInt(MAX_ID);
        }

        int testSubjectAge = ThreadLocalRandom.current().nextInt(MIN_AGE, MAX_AGE);
        String testSubjectName = pickRandomName();
        int testCompletionStatus = ThreadLocalRandom.current().nextInt(MAX_PROGRESS);
        boolean isTesting = ThreadLocalRandom.current().nextBoolean();

        return new TestChamberConcurrent(idCandidate, testSubjectAge, testCompletionStatus, testSubjectName, isTesting);
    }

    public void initializeTestChambers() {
        for(int i = 0; i < NUM_INITIAL_CHAMBERS; i++) {
            TestChamberConcurrent testChamber = generateRandomTestChamber();
            this.chamberMap.put(testChamber.testSubjectId, testChamber);
        }
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
}
