package cz.gattserver.android;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.interfaces.RecipeTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class RecipesActivity extends LazyListActivity<RecipeTO> {

    public RecipesActivity() {
        super(R.layout.activity_recipes, "Recepty", Config.RECIPES_COUNT_RESOURCE);
    }

    @Override
    protected RecipeTO constructTO(JSONObject jsonObject) throws JSONException {
        return new RecipeTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(RecipeTO item) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.RECIPES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
