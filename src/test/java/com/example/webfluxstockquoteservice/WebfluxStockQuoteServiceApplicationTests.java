package com.example.webfluxstockquoteservice;

import com.example.webfluxstockquoteservice.model.Quote;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*
  @WebFluxTest does not work because it disables @Component and @Service classes
  See https://howtodoinjava.com/spring-webflux/webfluxtest-with-webtestclient/ for more information.
 */
@Slf4j
class WebfluxStockQuoteServiceApplicationTests {

    //private static final Logger logger = LoggerFactory.getLogger(WebfluxStockQuoteServiceApplicationTests.class.getName());

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFetchQuotes() {
        webTestClient
                .get()
                .uri("/quotes?size=20") // set size = 20
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Quote.class)
                .hasSize(20)
                .consumeWith(allQuotes -> {
                    assertThat(allQuotes.getResponseBody())
                            .allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());
                    assertThat(allQuotes.getResponseBody()).hasSize(20);
                });

        log.info("Test Complete.");
    }

    @Test
    void testStreamQuotes() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(10);

        webTestClient
                .get()
                .uri("streamQuotes")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .returnResult(Quote.class)
                .getResponseBody()
                .take(10)
                .subscribe(quote -> {
                    assertThat(quote.getPrice()).isPositive();
                    countDownLatch.countDown();
                });

        countDownLatch.await();

        log.info("Test Complete.");

    }

}
