package cz.gattserver.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class RecipesActivity extends AppCompatActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        URLTask countTask = new URLTask() {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                int count = Integer.parseInt(result);

                URLTask fetchTask = new URLTask() {

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            LinearLayout layout = findViewById(R.id.linearLayout);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Button btn = new Button(RecipesActivity.this);
                                btn.setText(jsonObject.getString("name"));
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            Intent intent = new Intent(RecipesActivity.this, RecipeActivity.class);
                                            intent.putExtra("id", jsonObject.getString("id"));
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            Log.e(msg, "Recipes detail switch", e);
                                        }
                                    }
                                });
                                layout.addView(btn);
                            }
                        } catch (JSONException e) {
                            Log.e(msg, "JSONArray", e);
                        }
                    }
                };
                // TODO časem udělat opravdové stránkování
                // http://www.gattserver.cz/ws/recipes/list?pageSize=10&page=0
                fetchTask.execute(Config.RECIPES_LIST_RESOURCE + "?pageSize=" + count + "&page=0");
            }
        };
        countTask.execute(Config.RECIPES_COUNT_RESOURCE);

        Log.d(msg, "The onCreate() event");
    }
}
