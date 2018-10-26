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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import software.amazon.awssdk.utils.Logger;

public class BenchmarkRunner {

    private static final String BENCHMARK_ARG = "-benchmarks";
    private static final Logger log = Logger.loggerFor(BenchmarkRunner.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final List<String> benchmarksToRun;

    public BenchmarkRunner(List<String> benchmarksToRun) {
        this.benchmarksToRun = benchmarksToRun;
    }

    public static void main(String... args) throws RunnerException, IOException {
        if (args.length == 0) {
            log.error(() -> "No Argument provided");
            System.exit(0);
        }

        List<String> benchmarksToRun = parseBenchmarkArgs(args);
        BenchmarkRunner runner = new BenchmarkRunner(benchmarksToRun);
        runner.runBenchmark();
    }

    private void runBenchmark() throws IOException, RunnerException {
        ChainedOptionsBuilder optionsBuilder = new OptionsBuilder();
        List<BenchmarkScore> benchmarkScores = retrieveBenchmarkBaseline();

        benchmarksToRun.forEach(optionsBuilder::include);

        log.info(() -> "benchmarks to run " + benchmarksToRun);

        Collection<RunResult> results = new Runner(optionsBuilder.build()).run();

        BenchmarkResultProcessor processor = new BenchmarkResultProcessor(benchmarkScores);

        List<String> failedBenchmarkResults = processor.processBenchmarkResult(results);

        if (!failedBenchmarkResults.isEmpty()) {
            throw new RuntimeException("Failed the benchmark regression " + failedBenchmarkResults);
        }

        log.info(() -> "Passed the performance regression tests!");
    }

    private List<BenchmarkScore> retrieveBenchmarkBaseline() throws IOException {
        List<BenchmarkScore> benchmarkScores;

        try {
            benchmarkScores = MAPPER.readValue(getClass().getClassLoader().getResourceAsStream("baseline_score.json"),
                                               new TypeReference<List<BenchmarkScore>>() {});

        } catch (Exception ex) {
            log.error(() -> "Failed to retrieve the baseline score", ex);
            throw ex;
        }

        return benchmarkScores;
    }

    /**
     * Parsing the args. eg: -Dbenchmarks=ApiCallSyncHttpClientBenchmark, ClientCreationBenchmark
     *
     * @param args the args
     * @return the names of the benchmarks to run
     */
    private static List<String> parseBenchmarkArgs(String[] args) {
        Iterator<String> iterator = Arrays.asList(args).iterator();
        List<String> benchmarksToRurn = new ArrayList<>();

        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (arg.equalsIgnoreCase(BENCHMARK_ARG)) {
                String value = iterator.next();

                String[] splitedValue = value.split(",");

                benchmarksToRurn.addAll(Arrays.asList(splitedValue));
            }
        }

        return benchmarksToRurn;
    }
}
