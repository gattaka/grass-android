package cz.gattserver.android;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.interfaces.SongTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class SongsActivity extends LazyListActivity<SongTO> {

    public SongsActivity() {
        super(R.layout.activity_songs, "Zpěvník", Config.SONGS_COUNT_RESOURCE);
    }

    @Override
    protected SongTO constructTO(JSONObject jsonObject) throws JSONException {
        return new SongTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(SongTO item) {
        Intent intent = new Intent(this, SongActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.SONGS_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
