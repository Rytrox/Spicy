package de.rytrox.spicy.log;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a set of Minecraft-ColorCodes and their ANSI-Codes
 * @author Timeout
 *
 */
enum ConsoleColor {

    BLACK('0', Constants.COLOR_PATTERN, 0),
    DARK_GREEN('2', Constants.COLOR_PATTERN, 2),
    DARK_RED('4', Constants.COLOR_PATTERN, 1),
    GOLD('6', Constants.COLOR_PATTERN, 172),
    DARK_GREY('8', Constants.COLOR_PATTERN, 8),
    GREEN('a', Constants.COLOR_PATTERN, 10),
    RED('c', Constants.COLOR_PATTERN, 9),
    YELLOW('e', Constants.COLOR_PATTERN, 11),
    DARK_BLUE('1', Constants.COLOR_PATTERN, 4),
    DARK_AQUA('3', Constants.COLOR_PATTERN, 30),
    DARK_PURPLE('5', Constants.COLOR_PATTERN, 54),
    GRAY('7', Constants.COLOR_PATTERN, 246),
    BLUE('9', Constants.COLOR_PATTERN, 4),
    AQUA('b', Constants.COLOR_PATTERN, 51),
    LIGHT_PURPLE('d', Constants.COLOR_PATTERN, 13),
    WHITE('f', Constants.COLOR_PATTERN, 15),
    STRIKETHROUGH('m', Constants.FORMAT_PATTERN, 9),
    ITALIC('o', Constants.FORMAT_PATTERN, 3),
    BOLD('l', Constants.FORMAT_PATTERN, 1),
    UNDERLINE('n', Constants.FORMAT_PATTERN, 4),
    RESET('r', Constants.FORMAT_PATTERN, 0);

    private final char bukkitColor;
    private final String ansiColor;

    ConsoleColor(char bukkitColor, String pattern, int ansiCode) {
        this.bukkitColor = bukkitColor;
        this.ansiColor = String.format(pattern, ansiCode);
    }

    /**
     * Searches if the code is a valid colorcode and returns the right enum
     * @author Timeout
     *
     * @param code the Minecraft-ColorCode without Formatter-Char
     * @return the Color enum or null if no enum can be found
     */
    public static @NotNull ConsoleColor getColorByCode(char code) {
        // run trough colors
        for(ConsoleColor color: values()) {
            // check code
            if(color.bukkitColor == code) return color;
        }
        // return null for not found
        throw new IllegalArgumentException("Color with code " + code + " does not exists");
    }

    /**
     * Returns the ANSI-ColorCode of the Colorcode
     * @author Timeout
     *
     * @return the Ansi-ColorCode
     */
    public String getAnsiColor() {
        return ansiColor;
    }

    private static class Constants {

        public static final String COLOR_PATTERN = "\u001b[38;5;%dm";
        public static final String FORMAT_PATTERN = "\u001b[%dm";

    }
}
