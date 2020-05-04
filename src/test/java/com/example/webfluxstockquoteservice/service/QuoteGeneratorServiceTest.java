package com.example.webfluxstockquoteservice.service;


import com.example.webfluxstockquoteservice.model.Quote;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class QuoteGeneratorServiceTest {

    QuoteGeneratorService quoteGeneratorService = new QuoteGeneratorService();

    @BeforeAll
    public static void setUp() throws Exception {
    }

    @Test
    public void fetchQuoteStream() throws Exception {

        //get quoteFlux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(1L));

        quoteFlux.take(22000)
                .subscribe(System.out::println);


    }

    @Test
    public void fetchQuoteStreamCountDown() throws Exception {

        //get quoteFlux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(1000L));

        //subscriber lambda
        Consumer<Quote> println = System.out::println;

        //error handler
        Consumer<Throwable> errorHandler = e -> System.out.println("Some Error Occurred");

        //set Countdown latch to 1
        CountDownLatch countDownLatch = new CountDownLatch(1);

        //runnable called upon complete, count down latch
        Runnable allDone = () -> countDownLatch.countDown();

        quoteFlux.take(10)
                .subscribe(println, errorHandler, allDone);

        countDownLatch.await();
    }

}
