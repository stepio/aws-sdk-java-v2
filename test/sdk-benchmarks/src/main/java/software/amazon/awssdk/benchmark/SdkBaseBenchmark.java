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

package software.amazon.awssdk.benchmark;

import java.util.concurrent.Callable;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Base class for the Benchmark classed used by the SDK. Each benchmark class
 * should only have one inherited benchmark method {@link #run(Blackhole)} and
 * should not add any new methods marked with {@link Benchmark}.
 */
public abstract class SdkBaseBenchmark {

    @Benchmark
    public void run(Blackhole blackhole) throws Exception {
        blackhole.consume(callable().call());
    }

    /**
     * @return the callable to be consumed by the benchmark method
     */
    public abstract Callable<?> callable();
}
