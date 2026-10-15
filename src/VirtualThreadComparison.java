import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Demonstrates the performance impact of using Virtual Threads vs Platform Threads.
 * Virtual Threads are lightweight, allowing for massive concurrency for blocking I/O tasks.
 */
public class VirtualThreadComparison {

    // Number of tasks to execute
    private static final int TASK_COUNT = 10_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting performance comparison with " + TASK_COUNT + " tasks...");

        // 1. Platform Threads
        // Platform threads are expensive to create and mapped 1:1 to OS threads.
        // A fixed thread pool limits concurrency, forcing tasks to wait in a queue.
        long startPlatform = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(200)) {
            IntStream.range(0, TASK_COUNT).forEach(i -> executor.submit(() -> {
                try {
                    // Simulate blocking I/O operation (e.g., DB query, API call)
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        long endPlatform = System.currentTimeMillis();
        System.out.println("Platform threads took: " + (endPlatform - startPlatform) + "ms");

        // 2. Virtual Threads
        // Virtual threads are cheap to create, not bound to OS threads.
        // When a virtual thread performs a blocking operation (like Thread.sleep),
        // it is unmounted from the carrier thread, freeing the carrier thread for other tasks.
        long startVirtual = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, TASK_COUNT).forEach(i -> executor.submit(() -> {
                try {
                    // Simulate blocking I/O operation
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        long endVirtual = System.currentTimeMillis();
        System.out.println("Virtual threads took: " + (endVirtual - startVirtual) + "ms");
    }
}
