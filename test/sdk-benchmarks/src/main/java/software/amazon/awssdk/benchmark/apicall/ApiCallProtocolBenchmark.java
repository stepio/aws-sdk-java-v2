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

package software.amazon.awssdk.benchmark.apicall;

import static software.amazon.awssdk.benchmark.utils.BenchmarkCallableFactory.callableOfProtocol;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import software.amazon.awssdk.benchmark.SdkBaseBenchmark;

/**
 * Benchmarking for running with different protocols.
 */
@State(Scope.Thread)
@Warmup(iterations = 3, time = 15, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(2) // To reduce difference between each run
@BenchmarkMode(Mode.Throughput)
public class ApiCallProtocolBenchmark extends SdkBaseBenchmark {

    @Param({"xml", "json", "ec2", "query"})
    private String protocol;

    private Callable<?> callable;

    @Setup(Level.Trial)
    public void setup() {
        callable = callableOfProtocol(protocol);
    }

    @Override
    public Callable<?> callable() {
        return callable;
    }

    public static void main(String... args) throws Exception {
        Options opt = new OptionsBuilder()
            .include(ApiCallProtocolBenchmark.class.getSimpleName())
            .param("protocol", "query", "ec2")
            .addProfiler(StackProfiler.class)
            .build();
        new Runner(opt).run();
    }
}
