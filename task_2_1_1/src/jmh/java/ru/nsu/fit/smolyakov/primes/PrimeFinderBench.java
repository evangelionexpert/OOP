package ru.nsu.fit.smolyakov.primes;


import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
public class PrimeFinderBench {
    @Benchmark
    public void sequentialStreamNonPrimeFinderBench() {
        assertThat(TestSources.sequentialStreamNonPrimeFinder.find(TestSources.smallestArray)).isTrue();
//        assertThat(TestSources.sequentialStreamNonPrimeFinder.find(TestSources.smallArray)).isFalse();
//        assertThat(TestSources.sequentialStreamNonPrimeFinder.find(TestSources.largeArray)).isFalse();
    }

    @Benchmark
    public void parallelStreamNonPrimeFinderBench() {
        assertThat(TestSources.parallelStreamNonPrimeFinder.find(TestSources.smallestArray)).isTrue();
//        assertThat(TestSources.parallelStreamNonPrimeFinder.find(TestSources.smallArray)).isFalse();
//        assertThat(TestSources.parallelStreamNonPrimeFinder.find(TestSources.largeArray)).isFalse();
    }
}
