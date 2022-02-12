package de.rytrox.spicy.skin.mineskin;

class GenerateOptions {

    private String variant = Mineskin.Variant.CLASSIC.getType();
    private String name;
    private int visibility = Mineskin.Visibility.PRIVATE.getType();

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
