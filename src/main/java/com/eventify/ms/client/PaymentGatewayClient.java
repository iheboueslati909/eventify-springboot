package com.eventify.ms.client;

import com.eventify.ms.configuration.PaymentGatewayConfig;
import com.eventify.ms.dto.payment.PaymentGatewayRequest;
import com.eventify.ms.dto.payment.PaymentGatewayResponse;
import com.eventify.ms.dto.payment.PaymentSessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
public class PaymentGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayClient.class);
    private final WebClient webClient;
    private final PaymentGatewayConfig config;

    public PaymentGatewayClient(WebClient.Builder builder, PaymentGatewayConfig config) {
        this.config = config;
        this.webClient = builder
                .baseUrl(config.getBaseUrl())
                .build();
    }

    public PaymentSessionResponse createPaymentSession(String jwtToken, PaymentGatewayRequest request) {
        return webClient.post()
                .uri("/api/payments/create-session")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                    resp.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException(
                            "Payment Gateway error: " + resp.statusCode() + " - " + body
                        )))
                )
                .bodyToMono(PaymentGatewayResponse.class)
                .timeout(Duration.ofSeconds(5)) // avoid hanging calls
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(200))
                             .filter(this::isTransientError)
                             .onRetryExhaustedThrow((spec, signal) -> 
                                 new IllegalStateException("Payment gateway unreachable after retries", signal.failure()))
                )
                .doOnSubscribe(sub -> log.debug("Initiating payment session for request {}", request.intendId()))
                .doOnSuccess(resp -> log.info("Payment session created successfully: {}", resp.paymentId()))
                .doOnError(err -> log.error("Payment session initiation failed: {}", err.getMessage()))
                .map(resp -> new PaymentSessionResponse(resp.checkoutUrl(), resp.paymentId()))
                .block(); // block only in sync service layer (controller safe)
    }

    private boolean isTransientError(Throwable t) {
        return (t instanceof WebClientResponseException wex && 
                wex.getStatusCode().is5xxServerError()) || 
               t instanceof java.net.ConnectException ||
               t instanceof java.net.SocketTimeoutException;
    }
}
