package de.timeout.libs.sql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AsyncSyncQueryResultTest {

    @Mock
    private CompletableFuture<List<Object>> mock;

    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void shouldCallSubscribeWithFutureData() {
        AsyncQueryResult<Object> result = new AsyncQueryResult<>(mock);
        Consumer<List<Object>> subscription = mock(Consumer.class);

        result.subscribe(subscription);

        verify(mock, times(1)).thenAcceptAsync(subscription);
    }

    @Test
    public void shouldConvertWithMap() throws InterruptedException {
        AsyncQueryResult<Integer> result = new AsyncQueryResult<>(CompletableFuture.supplyAsync(() ->
                Arrays.asList(0, 1, 2, 3, 4, 5))
        );

        Function<Integer, String> convertFunction = String::valueOf;

        QueryResult<String> resultAfterMap = result.map(convertFunction);
        resultAfterMap.subscribe((res) -> {
            assertEquals("0", res.get(0));
            assertEquals("1", res.get(1));
            assertEquals("2", res.get(2));
            assertEquals("3", res.get(3));
            assertEquals("4", res.get(4));
            assertEquals("5", res.get(5));

            lock.countDown();
        });

        lock.await();
    }
}
