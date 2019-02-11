package cz.gattserver.mobile;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipesActivity extends LazyListViewActivity<RecipeTO> {

    public RecipesActivity() {
        super(R.layout.activity_recipes, "Recepty", Config.RECIPES_COUNT_RESOURCE);
    }

    @Override
    RecipeTO constructTO(JSONObject jsonObject) throws JSONException {
        return new RecipeTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    void onItemClick(RecipeTO item) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    String createFetchURL(int pageSize, int page) {
        return Config.RECIPES_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
