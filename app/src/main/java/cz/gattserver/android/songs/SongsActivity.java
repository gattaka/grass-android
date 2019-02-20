package cz.gattserver.android.songs;

import android.content.Intent;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.FilteredLazyListActivity;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class SongsActivity extends FilteredLazyListActivity<ItemTO> {

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
    protected ViewGroup getLayout() {
        return findViewById(R.id.songsLayout);
    }

    @Override
    protected String createCountURL(String filter) {
        return Config.SONGS_COUNT_RESOURCE + "?filter=" + filter;
    }

    @Override
    protected String createFetchURL(String filter, int pageSize, int page) {
        return Config.SONGS_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page + "&filter=" + filter;
    }

}
