package de.rytrox.spicy.skin.mineskin;

import org.jetbrains.annotations.NotNull;

class GenerateOptions {

    private String variant = Variant.CLASSIC.getType();
    private String name = "";
    private int visibility = Visibility.PRIVATE.getType();

    @NotNull
    public String getVariant() {
        return variant;
    }

    public void setVariant(@NotNull Variant variant) {
        this.variant = variant.type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility.type;
    }

    /**
     * Enum for Skin-Variant
     */
    public enum Variant {

        CLASSIC("classic"),
        SLIM("slim");

        private final String type;

        Variant(@NotNull String type) {
            this.type = type;
        }

        /**
         * Returns the name of the Variant
         *
         * @return the name of the Variant
         */
        public String getType() {
            return type;
        }
    }

    public enum Visibility {

        PUBLIC(0),
        PRIVATE(1);

        private final int type;

        Visibility(int type) {
            this.type = type;
        }

        /**
         * Returns the id of the Visibility
         *
         * @return the ID of the Visibility
         */
        public int getType() {
            return type;
        }
    }
}
