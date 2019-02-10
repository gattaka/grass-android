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

public class RecipesActivity extends GrassActivity {

    private static String msg = "GrassAPP: ";

    private static class FetchTask extends URLTask {

        private GrassActivity grassActivity;

        public FetchTask(GrassActivity grassActivity) {
            this.grassActivity = grassActivity;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                LinearLayout layout = grassActivity.findViewById(R.id.linearLayout);

                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Button btn = new Button(grassActivity);
                    btn.setText(jsonObject.getString("name"));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(grassActivity, RecipeActivity.class);
                                intent.putExtra("id", jsonObject.getString("id"));
                                grassActivity.startActivity(intent);
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
    }

    private static class CountTask extends URLTask {

        private GrassActivity grassActivity;

        public CountTask(GrassActivity grassActivity) {
            this.grassActivity = grassActivity;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int count = Integer.parseInt(result);

            FetchTask fetchTask = new FetchTask(grassActivity);
            // TODO časem udělat opravdové stránkování
            // http://www.gattserver.cz/ws/recipes/list?pageSize=10&page=0
            fetchTask.execute(Config.RECIPES_LIST_RESOURCE + "?pageSize=" + count + "&page=0");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        setTitle("Recepty");

        CountTask countTask = new CountTask(this);
        countTask.execute(Config.RECIPES_COUNT_RESOURCE);

        Log.d(msg, "The onCreate() event");
    }
}
