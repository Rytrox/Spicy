package de.timeout.libs.gui;

import de.timeout.libs.gui.events.ButtonClickEvent;
import de.timeout.libs.gui.events.GUICloseEvent;
import de.timeout.libs.item.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class GUI {

    private static final Map<HumanEntity, GUI> openGUIs = new HashMap<>();

    private final Map<Integer, Consumer<ButtonClickEvent>> events;

    private final Inventory design;
    private Consumer<GUICloseEvent> closeFunction;

    /**
     * Creates a new chest-inventory with a certain amount of rows
     *
     * @param rows the amount of rows
     */
    public GUI(int rows) {
        this(rows, null);
    }

    /**
     * Creates a new chest inventory with a certain background and a certain amount of rows
     *
     * @param rows the amount of rows
     * @param background the background of the inventory
     */
    public GUI(int rows, @Nullable Material background) {
        this(rows, background, null);
    }

    /**
     * Creates a new Chest-Inventory with a certain amount of rows
     *
     * @param rows the amount of rows
     * @param background the background material of this gui
     * @param title the title of the inventory. ColorCodes can be used with '&'
     */
    public GUI(int rows, @Nullable Material background, @Nullable String title) {
        this.design = Optional.ofNullable(title)
                        .map((t) -> Bukkit.createInventory(null, rows * 9, ChatColor.translateAlternateColorCodes('&', title)))
                        .orElse(Bukkit.createInventory(null, rows * 9));
        this.events = new HashMap<>(rows * 9);

        fillBackground(rows * 9, background);
    }

    public GUI(@NotNull InventoryType type) {
        this(type, null);
    }

    public GUI(@NotNull InventoryType type, @Nullable Material background) {
        this(type, background, null);
    }

    /**
     * Creates a new Inventory based on the type and the title
     *
     * @param type the inventory type
     * @param title the title of the inventory
     */
    public GUI(@NotNull InventoryType type, @Nullable Material background, @Nullable String title) {
        this.design = Optional.ofNullable(title)
                .map((t) -> Bukkit.createInventory(null, type, ChatColor.translateAlternateColorCodes('&', title)))
                .orElse(Bukkit.createInventory(null, type));
        this.events = new HashMap<>(design.getSize());

        fillBackground(design.getSize(), background);
    }

    @Nullable
    public static GUI getOpenedGUI(@NotNull HumanEntity player) {
        return openGUIs.get(player);
    }

    /**
     * Fills the background with the given material. <br>
     * Does nothing when background is null or air
     *
     * @param n the amount of slots in the inventory
     * @param background the background material
     */
    private void fillBackground(int n, @Nullable Material background) {
        if(background != null && !background.isAir()) {
            ItemStack backgroundItem = new ItemStackBuilder(background)
                    .setDisplayName(ChatColor.RED.toString())
                    .toItemStack();
            for (int i = 0; i < n; i++) {
                this.design.setItem(i, backgroundItem);
            }
        }
    }

    /**
     * Opens the GUI for a player
     *
     * @param player the player. Cannot be null
     */
    public void openGUI(@NotNull HumanEntity player) {
        openGUIs.put(player, this);

        player.openInventory(this.design);
    }

    /**
     * Sets the close function of the GUI. <br>
     * The Close-Function is a Lambda which will be executed when the GUI closes and the event is not cancelled
     *
     * @param function the lambda function that will be executed after a player closes this gui
     */
    public void setCloseFunction(@Nullable Consumer<GUICloseEvent> function) {
        this.closeFunction = function;
    }

    public void registerButton(int slot, ItemStack item, Consumer<ButtonClickEvent> clickFunction) {
        this.design.setItem(slot, item);
        this.events.put(slot, clickFunction);
    }

    /*
     * INTERNAL METHODS!!
     */

    /**
     * ATTENTION: Only internal use <br>
     * Returns the close-Function
     *
     * @return the close-function
     */
    @Nullable
    Consumer<GUICloseEvent> getCloseFunction() {
        return closeFunction;
    }

    /**
     * ATTENTION: Only internal use <br>
     * Returns the click-function of a certain slot
     *
     * @param slot the slot you clicked
     * @return the clickfunction or null if this function does not exists
     */
    @Nullable
    Consumer<ButtonClickEvent> getClickFunction(int slot) {
        return this.events.get(slot);
    }
}
