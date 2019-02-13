package cz.gattserver.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class SongActivity extends GrassActivity implements URLTask.URLTaskClient {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask<SongActivity> fetchTask = new URLTask<>(this);

        // http://www.gattserver.cz/ws/songs/song?id=4
        fetchTask.execute(Config.SONG_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    @Override
    public void onSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.songName);
            String name = jsonObject.getString("name");
            String author = jsonObject.getString("author");
            nameText.setText(getString(R.string.song_name, name, author));

            TextView descriptionText = findViewById(R.id.songText);
            descriptionText.setText(jsonObject.getString("text").replaceAll("<br/>", "\n").replaceAll("<br>", "\n"));
        } catch (JSONException e) {
            Log.e(msg, "JSONObject", e);
        }
    }
}
