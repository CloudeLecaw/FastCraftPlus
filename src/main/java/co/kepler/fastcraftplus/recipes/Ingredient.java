package co.kepler.fastcraftplus.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

/**
 * An ingredient to an item recipe.
 */
public class Ingredient {
    private static final byte ANY_DATA = -1;

    private final MaterialData material;
    private final ItemMeta meta;
    private final String name;

    /**
     * Create an ingredient from an item.
     *
     * @param item The item to create an ingredient from.
     */
    public Ingredient(ItemStack item) {
        material = item.getData();
        meta = item.hasItemMeta() ? item.getItemMeta() : null;
        name = RecipeUtil.getItemName(item);
    }

    /**
     * Get a Map of ingredients, with the map's values being the number of the ingredient.
     *
     * @param items The items to convert to ingredients.
     * @return Returns a Map of ingredients and amounts.
     */
    public static Map<Ingredient, Integer> fromItems(ItemStack... items) {
        Map<Ingredient, Integer> result = new HashMap<>();
        for (ItemStack is : items) {
            if (is == null || is.getType() == Material.AIR) continue;
            Ingredient i = new Ingredient(is);
            Integer old = result.get(i);
            result.put(i, (old == null ? 0 : old) + 1);
        }
        return result;
    }

    /**
     * See if any data can be used for this ingredient.
     *
     * @return Returns true if any data can be used.
     */
    @SuppressWarnings("deprecation")
    public boolean anyData() {
        return material.getData() == ANY_DATA;
    }

    /**
     * Get the material data of this ingredient.
     *
     * @return Returns the material data of this ingredient.
     */
    public MaterialData getMaterialData() {
        return material.clone();
    }

    /**
     * Get the material type of this ingredient.
     *
     * @return Returns the material type of this item.
     */
    public Material getMaterial() {
        return material.getItemType();
    }

    /**
     * Get the name of the ingredient.
     *
     * @return Returns the ingredient's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Create an ItemStack from this ingredient.
     *
     * @param amount The number of items in the ItemStack.
     * @return Returns a new ItemStack.
     */
    public ItemStack toItemStack(int amount) {
        ItemStack result = material.toItemStack(amount);
        result.setItemMeta(meta);
        return result;
    }

    /**
     * Remove this ingredient from an inventory.
     *
     * @param items  The items to remove the ingredients from.
     * @param amount The number of ingredients to remove.
     * @return Returns true if the ingredients were all removed.
     */
    public boolean removeIngredients(ItemStack[] items, int amount) {
        for (int i = items.length - 1; i >= 0 && amount > 0; i--) {
            ItemStack is = items[i];
            if (!matchesItem(is)) continue;
            if (amount >= is.getAmount()) {
                amount -= is.getAmount();
                items[i] = null;
            } else {
                items[i].setAmount(items[i].getAmount() - amount);
                amount = 0;
            }
        }
        return amount == 0;
    }

    /**
     * See if an ItemStack matches this ingredient.
     *
     * @param is The ItemStack to compare.
     * @return Returns true if the ItemStack can be used as this ingredient.
     */
    @SuppressWarnings("deprecation")
    public boolean matchesItem(ItemStack is) {
        if (is == null) return false;
        else if (material.getItemType() != is.getType()) return false;
        else if (!anyData() && material.getData() != is.getData().getData()) return false;
        return Bukkit.getItemFactory().equals(meta, is.getItemMeta());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Ingredient)) return false;

        Ingredient ing = (Ingredient) o;
        return material.equals(ing.material) && Bukkit.getItemFactory().equals(meta, ing.meta);
    }

    @Override
    public int hashCode() {
        int hash = material.hashCode();
        return 31 * hash + (meta == null ? 0 : meta.hashCode());
    }
}
