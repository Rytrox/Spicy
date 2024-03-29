package de.rytrox.spicy.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface QueryResult<T> {

    @NotNull
    <R> QueryResult<R> map(@NotNull Function<T, R> mappingFunction);

}
