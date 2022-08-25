package de.rytrox.spicy.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AsyncSyncQueryResultTest {

    @Mock
    private SyncQueryResult<Object> mock;

    private final CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void shouldCallSubscribeWithFutureData() throws InterruptedException {
        Mockito.doReturn(new ArrayList<>())
                .when(mock).get();

        AsyncQueryResult<Object> result = new AsyncQueryResult<>(CompletableFuture.completedFuture(mock));
        Consumer subscription = (_val) -> {
            assertEquals(mock.get(), _val);

            lock.countDown();
        };

        result.subscribe(subscription);

        lock.await();
    }

    @Test
    public void shouldSubscribeWithFirst() throws InterruptedException {
        AsyncQueryResult<Integer> result = new AsyncQueryResult<>(CompletableFuture.completedFuture(new SyncQueryResult<>(List.of(0, 1))));

        result.subscribeToFirst((zero) -> {
            assertEquals(0, zero);

            lock.countDown();
        });

        lock.await();
    }

    @Test
    public void shouldConvertWithMap() throws InterruptedException {
        AsyncQueryResult<Integer> result = new AsyncQueryResult<>(CompletableFuture.supplyAsync(() ->
                new SyncQueryResult<>(Arrays.asList(0, 1, 2, 3, 4, 5)))
        );

        Function<Integer, String> convertFunction = String::valueOf;

        AsyncQueryResult<String> resultAfterMap = result.map(convertFunction);
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
