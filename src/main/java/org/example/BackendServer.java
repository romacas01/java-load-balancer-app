package org.example;

public record BackendServer(String name) {
    public String handleRequest(String request) {
        try {
            // Simulate processing time
            Thread.sleep(200 + (long)(Math.random() * 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Response from %s -> %s".formatted(name, request);
    }
}


