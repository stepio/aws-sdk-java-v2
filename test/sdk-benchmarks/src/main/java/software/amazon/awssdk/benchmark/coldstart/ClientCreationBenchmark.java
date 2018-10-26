/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.benchmark.coldstart;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.CommandLineOptionException;
import org.openjdk.jmh.runner.options.CommandLineOptions;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import software.amazon.awssdk.benchmark.SdkBaseBenchmark;
import software.amazon.awssdk.benchmark.utils.BenchmarkCallableFactory;

/**
 * Benchmark for creating the clients
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Fork(3)
public class ClientCreationBenchmark extends SdkBaseBenchmark {

    @Param({"customizedClient", "defaultClient", "v1DefaultClient"})
    private String client;
    private Callable<?> callable;

    @Setup(Level.Trial)
    public void setup() {
        callable = BenchmarkCallableFactory.callableOfSdkClient(client);
    }

    @Override
    public Callable<?> callable() {
        return callable;
    }

    public static void main(String... args) throws RunnerException, CommandLineOptionException {
        Options opt = new OptionsBuilder()
            .parent(new CommandLineOptions())
            .include(ClientCreationBenchmark.class.getSimpleName())
            .addProfiler(StackProfiler.class)
            .build();
        Collection<RunResult> run = new Runner(opt).run();
    }
}
