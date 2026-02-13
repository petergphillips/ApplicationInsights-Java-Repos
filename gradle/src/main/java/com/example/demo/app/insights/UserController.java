package com.example.demo.app.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class UserController {
    private final TelemetryClient telemetryClient;
    private final RestClient restClient = RestClient.create();
    private final WebClient webClient = WebClient.create();
    private final int serverPort;

    public UserController(
        final TelemetryClient telemetryClient,
        @Value("${server.port}") final int serverPort
    ) {
        this.telemetryClient = telemetryClient;
        this.serverPort = serverPort;
    }

    @GetMapping("/greetings")
    public String greetings() {
        return restClient.get()
            .uri(String.format("http://localhost:%s/rest-greeting", serverPort))
            .retrieve()
            .body(String.class);
    }

    @GetMapping("/rest-greeting")
    public Mono<@NonNull String> restGreeting() {
        return webClient.get()
            .uri(String.format("http://localhost:%s/web-greeting", serverPort))
            .retrieve()
            .bodyToMono(String.class);
    }

    @GetMapping("/web-greeting")
    public String webGreeting() {
        // send event
        telemetryClient.trackEvent("URI /greeting is triggered");

        return "Hello World!";
    }
}