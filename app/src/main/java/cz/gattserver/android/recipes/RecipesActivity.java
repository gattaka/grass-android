package cz.gattserver.android.recipes;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class RecipesActivity extends LazyListActivity<ItemTO> {

    public RecipesActivity() {
        super(R.layout.activity_recipes, "Recepty", Config.RECIPES_COUNT_RESOURCE);
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.RECIPES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
