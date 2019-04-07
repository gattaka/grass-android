package cz.gattserver.android.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.OnSuccessAction;

public class RecipeActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<RecipeActivity> fetchTask = new URLGetTask<>(this, new OnSuccessAction<RecipeActivity>() {
            @Override
            public void run(RecipeActivity urlTaskClient, URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        // http://www.gattserver.cz/ws/recipes/recipe?id=4
        fetchTask.execute(Config.RECIPE_DETAIL_RESOURCE + "?id=" + id);

        Log.d("RecipeActivity", "The onCreate() event");
    }

    public void init(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.recipeName);
            nameText.setText(jsonObject.getString("name"));

            TextView descriptionText = findViewById(R.id.recipeDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));
        } catch (JSONException e) {
            Log.e("RecipeActivity", "JSONObject", e);
        }
    }
}
