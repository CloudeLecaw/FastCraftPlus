package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.FastRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A recipes layout that shows item crafting recipes.
 */
public class LayoutRecipesItems extends LayoutRecipes {
    private final List<FastRecipe> allRecipes;

    public LayoutRecipesItems(GUIFastCraft gui) {
        super(gui);

        allRecipes = new ArrayList<>(FastCraft.recipeManager().getRecipes(gui.getPlayer()));
        Collections.sort(allRecipes);
    }

    @Override
    public void updateRecipes() {
        this.addRecipes(allRecipes);
    }
}
