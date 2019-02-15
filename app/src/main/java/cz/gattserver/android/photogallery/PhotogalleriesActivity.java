package cz.gattserver.android.photogallery;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class PhotogalleriesActivity extends LazyListActivity<ItemTO> {

    public PhotogalleriesActivity() {
        super(R.layout.activity_photogalleries, "Fotogalerie", Config.PG_COUNT_RESOURCE);
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, PhotogalleryActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.PG_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
