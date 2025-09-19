package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private final List<BackendServer> activeBackends = new ArrayList<>();
    private final List<BackendServer> offlineBackends = new ArrayList<>();
    private final Random random = new Random();
    private final AtomicInteger counter = new AtomicInteger(0);

    public LoadBalancer(List<BackendServer> initialBackends) {
        activeBackends.addAll(initialBackends);
    }

    public String forwardRequest(String request) {
        if (activeBackends.isEmpty()) {
            performHealthCheck();
            if (activeBackends.isEmpty()) {
                return "No healthy backends available!";
            }
        }

        performHealthCheck();

        var index = counter.getAndUpdate(i -> (i + 1) % activeBackends.size());
        var backend = activeBackends.get(index);

        System.out.printf("Forwarding to: %s%n", backend.name());
        return backend.handleRequest(request);
    }

    private void performHealthCheck() {
        activeBackends.removeIf(backend -> {
            boolean healthy = random.nextBoolean();
            if (!healthy) {
                System.out.printf("Marking backend offline: %s%n", backend.name());
                offlineBackends.add(backend);
            }
            return !healthy;
        });

        offlineBackends.removeIf(backend -> {
            boolean recovered = random.nextBoolean();
            if (recovered) {
                System.out.printf("Recovering backend: %s%n", backend.name());
                activeBackends.add(backend);
            }
            return recovered;
        });
    }

    public static void main(String[] args) throws InterruptedException {
        var servers = List.of(
                new BackendServer("Backend-1"),
                new BackendServer("Backend-2"),
                new BackendServer("Backend-3")
        );

        var lb = new LoadBalancer(new ArrayList<>(servers));

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> tasks = new ArrayList<>();

            // Simulate 20 concurrent requests
            for (int i = 1; i <= 20; i++) {
                int requestId = i;
                tasks.add(() -> {
                    var response = lb.forwardRequest("Request-" + requestId);
                    System.out.printf("Response: %s%n", response);
                    return null;
                });
            }

            executor.invokeAll(tasks); // Run all requests concurrently
        }
    }
}

