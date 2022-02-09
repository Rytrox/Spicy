package de.timeout.libs.sql;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SyncSyncQueryResultTest {

    @Test
    public void shouldAccessSubscribe() {
        SyncQueryResult<Integer> result = new SyncQueryResult<>(
                Arrays.asList(0, 1, 2, 3, 4, 5)
        );

        result.subscribe((res) -> {
            assertEquals(0, res.get(0).intValue());
            assertEquals(1, res.get(1).intValue());
            assertEquals(2, res.get(2).intValue());
            assertEquals(3, res.get(3).intValue());
            assertEquals(4, res.get(4).intValue());
            assertEquals(5, res.get(5).intValue());
        });
    }

    @Test
    public void shouldMapToNewResultWithMap() {
        SyncQueryResult<Integer> result = new SyncQueryResult<>(
                Arrays.asList(0, 1, 2, 3, 4, 5)
        );

        result.map((res) -> res * res)
                .subscribe((res) -> {
                    assertEquals(0, res.get(0).intValue());
                    assertEquals(1, res.get(1).intValue());
                    assertEquals(4, res.get(2).intValue());
                    assertEquals(9, res.get(3).intValue());
                    assertEquals(16, res.get(4).intValue());
                    assertEquals(25, res.get(5).intValue());
                });
    }
}
