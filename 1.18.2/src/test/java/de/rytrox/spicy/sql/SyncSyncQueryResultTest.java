package de.rytrox.spicy.sql;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SyncSyncQueryResultTest {

    @Test
    public void shouldAccessGet() {
        List<Integer> result = new SyncQueryResult<>(
                Arrays.asList(0, 1, 2, 3, 4, 5)
        ).get();


        assertEquals(0, result.get(0).intValue());
        assertEquals(1, result.get(1).intValue());
        assertEquals(2, result.get(2).intValue());
        assertEquals(3, result.get(3).intValue());
        assertEquals(4, result.get(4).intValue());
        assertEquals(5, result.get(5).intValue());
    }

    @Test
    public void shouldMapToNewResultWithMap() {
        SyncQueryResult<Integer> result = new SyncQueryResult<>(
                Arrays.asList(0, 1, 2, 3, 4, 5)
        );

        List<Integer> mapped = result.map((res) -> res * res).get();

        assertEquals(0, mapped.get(0).intValue());
        assertEquals(1, mapped.get(1).intValue());
        assertEquals(4, mapped.get(2).intValue());
        assertEquals(9, mapped.get(3).intValue());
        assertEquals(16, mapped.get(4).intValue());
        assertEquals(25, mapped.get(5).intValue());
    }
}
