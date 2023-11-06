/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package vlockwoo;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import vlockwoo.ConcurrentObjects.TestChamberConcurrent;
import vlockwoo.ConcurrentObjects.TestChamberHandlerConcurrent;
import vlockwoo.ConcurrentObjects.WorkerConcurrent;
import vlockwoo.CustomObjects.TestChamber;
import vlockwoo.CustomObjects.TestChamberHandler;
import vlockwoo.CustomObjects.Worker;

import java.util.concurrent.TimeUnit;

public class MyBenchmark {

    //Many thanks yet again to Scarlett Weeks for having her repos public.  A lot of tutorials
    //gloss over more intricate details about the benchmarking annotations as well as file structures
    //for projects that didn't start out with a Maven project.  My benchmarking stuff takes a somewhat different form
    //for various reasons, but I have a better understanding of what a lot of the functionality means due to her example.
    //Check out her repo here: https://github.com/Kayyali78/JMH_Perf_Testing/
    //I referenced this tutorial as well: https://jenkov.com/tutorials/java-performance/jmh.html

    @State(Scope.Benchmark)
    public static class CustomObject {
        TestChamberHandler testChamberHandler;

        @Param({"2", "12"})
        int numWorkers;

        @Setup(Level.Trial)
        public void setup() {
            testChamberHandler = new TestChamberHandler(numWorkers, 80, 100);
        }
    }

    //Setup stuff for testing ConcurrentHashMap
    @State(Scope.Benchmark)
    public static class ConcurrentObject {
        TestChamberHandlerConcurrent testChamberHandler;

        @Param({"2", "12"})
        int numWorkers;

        //If this isn't Invocation, it'll break
        @Setup(Level.Trial)
        public void setup() {
            testChamberHandler = new TestChamberHandlerConcurrent(numWorkers, 80, 100);
        }
    }

    @State(Scope.Thread)
    public static class LinkedListStuff {
//        @Threads(2)
//        @Benchmark
//        @BenchmarkMode(Mode.Throughput)
//        @OutputTimeUnit(TimeUnit.MICROSECONDS)
//        @Fork(value = 2,warmups = 2)
//        @Warmup( iterations = 3, time = 2)
//        @Measurement(iterations = 4)
//        public void runLinkedList2(CustomObject co, Blackhole blackhole) {
//            co.testChamberHandler.start();
//
//            for(Worker worker : co.testChamberHandler.workers) {
//                blackhole.consume(worker.dumpingGrounds);
//            }
//        }

        @Threads(Threads.MAX)
        @Benchmark
        @BenchmarkMode(Mode.Throughput)
        @OutputTimeUnit(TimeUnit.MICROSECONDS)
        @Fork(value = 2,warmups = 2)
        @Warmup( iterations = 3, time = 2)
        @Measurement(iterations = 4)
        public void runLinkedListMax(CustomObject co, Blackhole blackhole) {
            co.testChamberHandler.start();

            for(Worker worker : co.testChamberHandler.workers) {
                blackhole.consume(worker.dumpingGrounds);
            }
        }
    }


    @State(Scope.Thread)
    public static class ConcurrentStuff {
        @Threads(2)
        @Benchmark
        @BenchmarkMode(Mode.Throughput)
        @OutputTimeUnit(TimeUnit.MICROSECONDS)
        @Fork(value = 2,warmups = 2)
        @Warmup( iterations = 3, time = 2)
        @Measurement(iterations = 4)
        public void runConcurrentObjects2(ConcurrentObject co, Blackhole blackhole) {
            co.testChamberHandler.start();

            for(WorkerConcurrent worker : co.testChamberHandler.workers) {
                blackhole.consume(worker.dumpingGrounds);
            }
        }

        @Threads(Threads.MAX)
        @Benchmark
        @BenchmarkMode(Mode.Throughput)
        @OutputTimeUnit(TimeUnit.MICROSECONDS)
        @Fork(value = 2,warmups = 2)
        @Warmup( iterations = 3, time = 2)
        @Measurement(iterations = 4)
        public void runConcurrentObjectsMax(ConcurrentObject co, Blackhole blackhole) {
            co.testChamberHandler.start();

            for(WorkerConcurrent worker : co.testChamberHandler.workers) {
                blackhole.consume(worker.dumpingGrounds);
            }
        }
    }

}
