package de.timeout.libs.gui.events;

import de.timeout.libs.gui.GUI;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Event that will be emitted when a player clicks on a button inside a GUI
 *
 * @author Timeout
 */
public class ButtonClickEvent extends InventoryClickEvent implements Cancellable {

    private GUI gui;

    private boolean cancel;

    public ButtonClickEvent(@NotNull InventoryView view,
                            @NotNull InventoryType.SlotType type,
                            int slot, @NotNull ClickType click,
                            @NotNull InventoryAction action) {
        super(view, type, slot, click, action);

        this.gui = GUI.getOpenedGUI(getWhoClicked());
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Cancels the Button click.
     * @param b
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
