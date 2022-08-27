package de.rytrox.spicy.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sync Implementation of SQL-Result
 *
 * @param <T> The Class of the Entitytypes
 */
public record SyncQueryResult<T>(List<T> entities) implements QueryResult<T> {

    @Override
    public @NotNull <R> SyncQueryResult<R> map(@NotNull Function<T, R> mappingFunction) {
        return new SyncQueryResult<>(
            entities.stream()
                    .map(mappingFunction)
                    .collect(Collectors.toList())
        );
    }

    /**
     * Returns the Entities as a list
     *
     * @return the Entities as a list
     */
    @NotNull
    public List<T> get() {
        return entities;
    }

    @NotNull
    public T get(int index) {
        return entities.get(index);
    }
}
