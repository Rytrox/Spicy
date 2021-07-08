package de.timeout.libs.skin.mineskin;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Future;

public class MineskinBuilder {

    private final GenerateOptions options = new GenerateOptions();

    public MineskinBuilder variant(@NotNull Mineskin.Variant variant) {
        this.options.setVariant(variant.getType());

        return this;
    }

    public MineskinBuilder visibility(@NotNull Mineskin.Visibility visibility) {
        this.options.setVisibility(visibility.getType());

        return this;
    }

    public MineskinBuilder name(@NotNull String name) {
        Validate.isTrue(name.length() <= 20, "Name cannot be longer than 20 characters");
        this.options.setName(name);

        return this;
    }

    public Future<Mineskin> generate(@NotNull String url) {
        return new MineskinHandler().generate(options, url);
    }

    public Future<Mineskin> generate(@NotNull File file) {

    }

    public Future<Mineskin> generate(@NotNull UUID uuid) {

    }
}
