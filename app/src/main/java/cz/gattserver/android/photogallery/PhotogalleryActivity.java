package cz.gattserver.android.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.GrassActivity;
import cz.gattserver.android.common.URLTask;

public class PhotogalleryActivity extends GrassActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photogallery);

        hideActionBar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        URLTask<PhotogalleryActivity> fetchTask = new URLTask<>(this, new PhotogalleryBaseLoadAction());

        // http://gattserver.cz/ws/pg/gallery?id=383
        fetchTask.execute(Config.PG_DETAIL_RESOURCE + "?id=" + id);

        Log.d(msg, "The onCreate() event");
    }

    public void populateBaseInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            TextView nameText = findViewById(R.id.photogalleryName);
            nameText.setText(jsonObject.getString("name"));

        } catch (JSONException e) {
            Log.e(msg, "JSONObject", e);
        }
    }

}
