package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface FutureResult<T> {

    @NotNull
    <R> FutureResult<R> map(@NotNull Function<T, R> mappingFunction);

    void subscribe(@NotNull Consumer<List<T>> result);
}
