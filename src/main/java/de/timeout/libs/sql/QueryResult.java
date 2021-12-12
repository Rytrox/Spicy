package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class QueryResult<T> implements FutureResult<T> {

    private final CompletableFuture<List<T>> task;

    public QueryResult(@NotNull CompletableFuture<List<T>> task) {
        this.task = task;
    }

    @Override
    public @NotNull <R> FutureResult<R> map(@NotNull Function<T, R> mappingFunction) {
        CompletableFuture<List<R>> future = this.task.thenApply((list) ->
            list.stream()
                    .map(mappingFunction)
                    .collect(Collectors.toList())
        );

        return new QueryResult<>(future);
    }

    @Override
    public void subscribe(@NotNull Consumer<List<T>> result) {
        task.thenAcceptAsync(result);
    }
}
