package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supplies access to the plugin's language files.
 */
public class LanguageConfig extends ConfigExternal {
    private static final String NOT_FOUND = ChatColor.RED + "[Lang: <key>]";
    private static final String NOT_FOUND_KEY = "key";

    private Map<Material, ItemNames> itemNames;

    /**
     * Create an instance of Language
     */
    public LanguageConfig() {
        super(false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void load() {
        String language = FastCraft.config().getLanguage();
        resPath = "lang/" + language + ".yml";

        // Set internal and external configs
        if (FastCraft.getInstance().getResource(resPath) == null) {
            // If a resource for this language doesn't exist
            setInternalConfig("lang/EN.yml");
            setExternalConfig(resPath);
        } else {
            setInternalConfig(resPath);
            setExternalConfig(null);
        }

        // Load configuration
        super.load();

        // Load item names
        ConfigurationSection itemSection = config.getConfigurationSection("items");
        itemNames = new HashMap<>();
        if (itemSection != null) {
            for (String item : itemSection.getKeys(false)) {
                Material itemType = Bukkit.getUnsafe().getMaterialFromInternalName(item);
                if (itemType == null) {
                    FastCraft.err("Unknown item type: '" + item + "'");
                    continue;
                }
                ItemNames itemName;
                if (itemSection.isString(item)) {
                    // If item name was given directly
                    itemName = new ItemNames(itemSection.getString(item), null);
                } else {
                    // If item names are given based off of item data values
                    ConfigurationSection nameSection = itemSection.getConfigurationSection(item);
                    String defName = null;
                    Map<Integer, String> names = new HashMap<>();
                    for (String data : nameSection.getKeys(false)) {
                        if (data.equals("d")) {
                            // Get the default item name
                            defName = nameSection.getString(data);
                        } else {
                            // Get the item name for specific data values
                            try {
                                int num = Integer.parseInt(data);
                                names.put(num, nameSection.getString(data));
                            } catch (NumberFormatException e) {
                                FastCraft.err("Item data is not 'd' or a number: " + data);
                            }
                        }
                    }
                    itemName = new ItemNames(defName, names);
                }
                if (itemName.getDefName() == null) {
                    FastCraft.warning("Language (" + language + ") has missing default (d) for item: '" + item + "'");
                }
                itemNames.put(itemType, itemName);
            }
        }
    }

    /**
     * Useful method to convert an integer to a String.
     *
     * @param integer The integer to convert to a String.
     * @return Returns the integer as a String.
     */
    private String s(int integer) {
        return Integer.toString(integer);
    }

    /**
     * Format a
     *
     * @param str    The String to format.
     * @param varVal The variables and values.
     * @return Returns the formatted String
     */
    private String format(String str, String... varVal) {
        assert varVal.length % 2 == 0 : "varVal must have an even number of elements";

        for (int i = 0; i < varVal.length; i += 2) {
            str = str.replace("<" + varVal[i] + ">", varVal[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private String get(String key, String... varVal) {
        String entry = config.getString(key);
        if (entry == null) {
            return format(NOT_FOUND, NOT_FOUND_KEY, key);
        }
        return format(entry, varVal);
    }

    private List<String> getList(String key, String... varVal) {
        List<String> entry = config.getStringList(key);
        if (entry == null) {
            return Collections.singletonList(format(NOT_FOUND, NOT_FOUND_KEY, key));
        }

        for (int i = 0; i < entry.size(); i++) {
            entry.set(i, format(entry.get(i), varVal));
        }
        return entry;
    }

    public String gui_title() {
        return get("gui.title");
    }

    public String gui_itemName(ItemStack item) {
        if (config.getString("gui.item-name") == null) {
            return item.getItemMeta().getDisplayName();
        }
        String name = RecipeUtil.getItemName(item);
        return get("gui.item-name", "name", name);
    }

    public String gui_ingredients_item(int amount, String item) {
        return get("gui.ingredients.item", "amount", s(amount), "item", item);
    }

    public String gui_ingredients_label() {
        return get("gui.ingredients.label");
    }

    public String gui_results_label() {
        return get("gui.results.label");
    }

    public String gui_results_item(ItemStack is) {
        String itemName = RecipeUtil.getItemName(is);
        return get("gui.results.item", "amount", s(is.getAmount()), "item", itemName);
    }

    public String gui_toolbar_pagePrev_title() {
        return get("gui.toolbar.page-prev.title");
    }

    public List<String> gui_toolbar_pagePrev_description(int prev, int total, int cur) {
        return getList("gui.toolbar.page-prev.description", "prev", s(prev), "total", s(total), "cur", s(cur));
    }

    public String gui_toolbar_pageNext_title() {
        return get("gui.toolbar.page-next.title");
    }

    public List<String> gui_toolbar_pageNext_description(int prev, int total, int cur) {
        return getList("gui.toolbar.page-next.description", "next", s(prev), "total", s(total), "cur", s(cur));
    }

    public String gui_toolbar_craftItems_title() {
        return get("gui.toolbar.craft-items.title");
    }

    public List<String> gui_toolbar_craftItems_description() {
        return getList("gui.toolbar.craft-items.description");
    }

    public String gui_toolbar_craftArmor_title() {
        return get("gui.toolbar.craft-armor.title");
    }

    public List<String> gui_toolbar_craftArmor_description() {
        return getList("gui.toolbar.craft-armor.description");
    }

    public String gui_toolbar_craftFireworks_title() {
        return get("gui.toolbar.craft-fireworks.title");
    }

    public List<String> gui_toolbar_craftFireworks_description() {
        return getList("gui.toolbar.craft-fireworks.description");
    }

    public String gui_toolbar_refresh_title() {
        return get("gui.toolbar.refresh.title");
    }

    public List<String> gui_toolbar_refresh_description() {
        return getList("gui.toolbar.refresh.description");
    }

    public String gui_toolbar_multiplier_title(int mult) {
        return get("gui.toolbar.multiplier.title", "mult", s(mult));
    }

    public List<String> gui_toolbar_multiplier_description(int mult) {
        return getList("gui.toolbar.multiplier.description", "mult", s(mult));
    }

    public String gui_toolbar_workbench_title() {
        return get("gui.toolbar.workbench.title");
    }

    public List<String> gui_toolbar_workbench_description() {
        return getList("gui.toolbar.workbench.description");
    }

    public String commands_usage(String usage) {
        return get("commands.usage", "usage", usage);
    }

    public String commands_noPerm(String permission) {
        return get("commands.no-perm", "perm", permission);
    }

    public String commands_playerOnly() {
        return get("commands.player-only");
    }

    public String commands_fastcraft_toggle_output_on() {
        return get("commands.fastcraft toggle.output.on");
    }

    public String commands_fastcraft_toggle_output_off() {
        return get("commands.fastcraft toggle.output.off");
    }

    public String commands_fastcraft_toggle_output_on_other(String player) {
        return get("commands.fastcraft toggle.output.on-other");
    }

    public String commands_fastcraft_toggle_output_off_other(String player) {
        return get("commands.fastcraft toggle.output.off-other");
    }

    public String commands_fastcraftadmin_reload_output() {
        return get("commands.fastcraftadmin reload.output");
    }

    @SuppressWarnings("deprecation")
    public String items_name(ItemStack item) {
        ItemNames names = itemNames.get(item.getType());
        if (names == null) return null;
        return names.getName(item.getData().getData());
    }

    /**
     * Keeps track of an item's names.
     */
    private class ItemNames {
        private final String defName;
        private final Map<Integer, String> names;

        public ItemNames(String defName, Map<Integer, String> names) {
            this.defName = defName;
            this.names = names;
        }

        public String getDefName() {
            return defName;
        }

        public String getName(int data) {
            if (names == null) return defName;
            String name = names.get(data);
            return name == null ? defName : name;
        }
    }
}
