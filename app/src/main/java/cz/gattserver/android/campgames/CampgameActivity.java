package cz.gattserver.android.campgames;

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
import cz.gattserver.android.common.URLTaskParamTO;

public class CampgameActivity extends GrassActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campgame);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<CampgameActivity> fetchTask = new URLGetTask<>(this, new OnSuccessAction<CampgameActivity>() {
            @Override
            public void run(CampgameActivity urlTaskClient, URLTaskInfoBundle bundle) {
                urlTaskClient.init(bundle.getResultAsStringUTF());
            }
        });

        // http://www.gattserver.cz/ws/campgames/campgame?id=4
        fetchTask.execute(new URLTaskParamTO(Config.CAMPGAMES_DETAIL_RESOURCE + "?id=" + id));

        Log.d("CampgameActivity", "The onCreate() event");
    }

    public void init(String result) {
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
            Log.e("CampgameActivity", "JSONObject", e);
        }
    }
}
