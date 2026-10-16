package dev.bigstacks;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Setzt bei Items die maximale Stackgroesse (Item-Komponente max_stack_size).
 */
public final class StackNormalizer {

    private final int target;

    public StackNormalizer(int target) {
        this.target = Math.max(1, Math.min(99, target));
    }

    public int getTarget() {
        return target;
    }

    /**
     * Veraendert den Stack direkt.
     *
     * @return true, wenn etwas geaendert wurde
     */
    public boolean apply(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        // Nicht stapelbare Items (Werkzeuge, Ruestung, ...) bleiben unveraendert
        if (item.getType().getMaxStackSize() <= 1) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        if (meta.hasMaxStackSize()) {
            int current = meta.getMaxStackSize();
            if (current == target || current <= 1) {
                return false; // schon fertig bzw. von anderem Plugin bewusst auf 1 gesetzt
            }
        }
        meta.setMaxStackSize(target);
        item.setItemMeta(meta);
        return true;
    }

    public void normalizeInventory(Inventory inv) {
        if (inv == null) {
            return;
        }
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (apply(item)) {
                inv.setItem(i, item);
            }
        }
    }
}
