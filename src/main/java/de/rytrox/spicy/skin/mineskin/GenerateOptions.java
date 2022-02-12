package de.rytrox.spicy.skin.mineskin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GenerateOptions {

    private String variant = Mineskin.Variant.CLASSIC.getType();
    private String name;
    private int visibility = Mineskin.Visibility.PRIVATE.getType();

    @NotNull
    public String getVariant() {
        return variant;
    }

    public void setVariant(@NotNull String variant) {
        this.variant = variant;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
