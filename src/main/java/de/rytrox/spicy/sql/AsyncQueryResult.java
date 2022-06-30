package de.rytrox.spicy.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public record AsyncQueryResult<T>(@NotNull CompletableFuture<List<T>> task) implements QueryResult<T> {

    @Override
    public @NotNull <R> AsyncQueryResult<R> map(@NotNull Function<T, R> mappingFunction) {
        CompletableFuture<List<R>> future = this.task
                .thenApply((list) ->
                    list.stream()
                        .map(mappingFunction)
                        .collect(Collectors.toList())
        );

        return new AsyncQueryResult<>(future);
    }

    public void subscribe(@NotNull Consumer<List<T>> result) {
        task.thenAcceptAsync(result);
    }
}
