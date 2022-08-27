package de.rytrox.spicy.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public record AsyncQueryResult<T>(@NotNull CompletableFuture<SyncQueryResult<T>> task) implements QueryResult<T> {

    @Override
    public @NotNull <R> AsyncQueryResult<R> map(@NotNull Function<T, R> mappingFunction) {
        CompletableFuture<SyncQueryResult<R>> future = this.task
                .thenApply((list) -> list.map(mappingFunction)
        );

        return new AsyncQueryResult<>(future);
    }

    public void subscribe(@NotNull Consumer<List<T>> result) {
        task.thenAcceptAsync((results) -> result.accept(results.get()));
    }

    public void subscribeToFirst(@NotNull Consumer<T> result) {
        task.thenAcceptAsync((results) -> result.accept(results.get(0)));
    }

    @NotNull
    public CompletableFuture<List<T>> get() {
        return this.task.thenApply(SyncQueryResult::get);
    }
}
