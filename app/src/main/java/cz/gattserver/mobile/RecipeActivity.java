package cz.gattserver.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask fetchTask = new URLTask() {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    LinearLayout layout = findViewById(R.id.recipeLayout);

                    TextView nameText = findViewById(R.id.recipeName);
                    nameText.setText(jsonObject.getString("name"));

                    TextView descriptionText = findViewById(R.id.recipeDescription);
                    descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));
                } catch (JSONException e) {
                    Log.e(msg, "JSONObject", e);
                }
            }
        };
        // http://www.gattserver.cz/ws/recipes/recipe?id=4
        fetchTask.execute(Config.RECIPE_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }
}
