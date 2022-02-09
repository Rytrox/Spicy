package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

record AsyncQueryResult<T>(CompletableFuture<List<T>> task) implements QueryResult<T> {

    AsyncQueryResult(@NotNull CompletableFuture<List<T>> task) {
        this.task = task;
    }

    @Override
    public @NotNull <R> QueryResult<R> map(@NotNull Function<T, R> mappingFunction) {
        CompletableFuture<List<R>> future = this.task
                .thenApply((list) ->
                    list.stream()
                        .map(mappingFunction)
                        .collect(Collectors.toList())
        );

        return new AsyncQueryResult<>(future);
    }

    @Override
    public void subscribe(@NotNull Consumer<List<T>> result) {
        task.thenAcceptAsync(result);
    }
}
