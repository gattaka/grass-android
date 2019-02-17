package cz.gattserver.android.songs;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class SongsActivity extends LazyListActivity<ItemTO> {

    public SongsActivity() {
        super(R.layout.activity_songs, "Zpěvník");
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, SongActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createCountURL() {
        return Config.SONGS_COUNT_RESOURCE;
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.SONGS_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
