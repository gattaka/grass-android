package cz.gattserver.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class CampgameActivity extends GrassActivity implements URLTask.URLTaskClient{

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campgame);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask<CampgameActivity> fetchTask = new URLTask<>(this);

        // http://www.gattserver.cz/ws/campgames/campgame?id=4
        fetchTask.execute(Config.CAMPGAMES_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    @Override
    public void onSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.campgameName);
            nameText.setText(jsonObject.getString("name"));

            TextView playersText = findViewById(R.id.campgamePlayers);
            playersText.setText("Hráčů: " + jsonObject.getString("players"));

            TextView preparationTimeText = findViewById(R.id.campgamePreparationTime);
            preparationTimeText.setText("Příprava: " + jsonObject.getString("preparationTime"));

            TextView playTimeText = findViewById(R.id.campgamePlayTime);
            playTimeText.setText("Délka hry: " + jsonObject.getString("playTime"));

            TextView descriptionText = findViewById(R.id.campgameDescription);
            descriptionText.setText(jsonObject.getString("description").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));
        } catch (JSONException e) {
            Log.e(msg, "JSONObject", e);
        }
    }
}
