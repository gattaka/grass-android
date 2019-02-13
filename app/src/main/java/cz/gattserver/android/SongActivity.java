package cz.gattserver.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class SongActivity extends GrassActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask fetchTask = new URLTask() {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    LinearLayout layout = findViewById(R.id.songLayout);

                    TextView nameText = findViewById(R.id.songName);
                    nameText.setText(jsonObject.getString("name"));

                    TextView descriptionText = findViewById(R.id.songText);
                    descriptionText.setText(jsonObject.getString("text").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));
                } catch (JSONException e) {
                    Log.e(msg, "JSONObject", e);
                }
            }
        };
        // http://www.gattserver.cz/ws/songs/song?id=4
        fetchTask.execute(Config.SONG_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }
}
