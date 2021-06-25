package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");
        final Flux<String> merge = Flux.merge(flux1, flux2);
        StepVerifier.create(merge.log())
            .expectSubscription()
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    public void combineUsingMergeDelay() {
        final Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        final Flux<String> merge = Flux.merge(flux1, flux2);
        StepVerifier.create(merge.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");
        final Flux<String> merge = Flux.concat(flux1, flux2);
        StepVerifier.create(merge.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatDelay() {
        VirtualTimeScheduler.getOrSet();
        final Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        final Flux<String> merge = Flux.concat(flux1, flux2);
//        StepVerifier.create(merge.log())
//                .expectSubscription()
//                .expectNext("A", "B", "C", "D", "E", "F")
//                .verifyComplete();
        StepVerifier.withVirtualTime(merge::log)
            .expectSubscription()
            .thenAwait(Duration.ofSeconds(6))
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    public void combineUsingZip() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> merge = Flux.zip(flux1, flux2, String::concat); // AD, BE, CF
        StepVerifier.create(merge.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
