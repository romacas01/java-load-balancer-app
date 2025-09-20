This application simulates a Layer 4 (transport layer) load balancer.
It distributes incoming requests to backend servers using a round-robin strategy, while also simulating server failures and recoveries.

# BackendServer
- Represents a simple backend server that accepts requests forwarded by the load balancer.
- Uses Thread.sleep with random delays to simulate varying processing times.
- Currently returns only a string response (no actual networking).

# LoadBalancer
- Represents the load balancer.
- Maintains two server lists:
- Active servers (available to receive requests).
  Offline servers (temporarily unavailable).
- On each request, it performs a health check (performHealthCheck), which randomly moves servers between active and offline lists to simulate failures and recoveries.
- Uses a round-robin counter to forward requests across active servers.

# Main Class
- Creates a list of Callable tasks, each representing a client request to the load balancer.
- Uses an ExecutorService with virtual threads (Executors.newVirtualThreadPerTaskExecutor) to execute all tasks concurrently.
- Prints request routing and response details to the console.

# improvements and new features
- Make the health check run on a separated thread
- Use socket to closely simulate servers
