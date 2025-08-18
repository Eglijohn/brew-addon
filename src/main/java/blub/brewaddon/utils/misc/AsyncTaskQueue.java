package blub.brewaddon.utils.misc;

import java.util.concurrent.CompletableFuture;
import java.util.List;

public class AsyncTaskQueue {
    /**
     * Executes a series of tasks sequentially in async manner
     * @param tasks List of Runnable tasks to execute in order
     * @return CompletableFuture that completes when all tasks are done
     */
    public static CompletableFuture<Void> execute(List<Runnable> tasks) {
        if (tasks.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Void> chain = CompletableFuture.runAsync(tasks.get(0));

        for (int i = 1; i < tasks.size(); i++) {
            final Runnable task = tasks.get(i);
            chain = chain.thenRun(task);
        }

        return chain;
    }
}
