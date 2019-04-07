package cz.gattserver.android.songs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;
import cz.gattserver.android.common.OnSuccessAction;

public class SongActivity extends GrassActivity {

    private String msg = "GrassAPP: ";

    private static class SongActivityInitAction implements OnSuccessAction<SongActivity> {
        @Override
        public void run(SongActivity urlTaskClient, URLTaskInfoBundle bundle) {
            urlTaskClient.init(bundle.getResultAsStringUTF());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLGetTask<SongActivity> fetchTask = new URLGetTask<>(this, new SongActivityInitAction());

        // http://www.gattserver.cz/ws/songs/song?id=4
        fetchTask.execute(Config.SONG_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void init(String result) {
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
