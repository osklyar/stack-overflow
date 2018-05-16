import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Test;


public class TestThrottledReadsWithConcurrentProcessing {

	private static final int ENTRIES = 200;

	private static final int CONCURRENCY_LEVEL = 20;

	interface Reader {

		String read();
	}

	private final Reader reader = new Reader() {

		int counter = 0;

		@Override
		public String read() {
			if (counter++ < ENTRIES) {
				return String.valueOf(counter);
			}
			return null;
		}
	};

	private final AtomicInteger count = new AtomicInteger();

	private final Consumer<String> processor = (value) -> {
		count.incrementAndGet();
		try {
			Thread.sleep(200);
		}
		catch (InterruptedException ex) {
			// ignore
		}
	};

	@Test
	public void onlyReadWhenExecutorAvailable() throws Exception {
		Executor executor = Executors.newCachedThreadPool();

		CompletableFuture<Void> done = CompletableFuture.completedFuture(null);
		for (Semaphore semaphore = new Semaphore(CONCURRENCY_LEVEL); ; ) {
			String value = reader.read();
			if (value == null) {
				break;
			}

			semaphore.acquire();

			CompletableFuture<Void> future = CompletableFuture.completedFuture(value)
				.thenAcceptAsync(v -> {
					processor.accept(v);
					semaphore.release();
				}, executor);

			done = done.thenCompose($ -> future);
		}
		done.get();

		assertEquals(ENTRIES, count.get());
	}
}
