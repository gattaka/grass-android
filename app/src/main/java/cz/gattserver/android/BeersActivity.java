package cz.gattserver.android;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class BeersActivity extends LazyListActivity<ItemTO> {

    public BeersActivity() {
        super(R.layout.activity_beers, "Piva", Config.DRINKS_BEER_COUNT_RESOURCE);
    }

    @Override
    protected ItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new ItemTO(jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected void onItemClick(ItemTO item) {
        Intent intent = new Intent(this, BeerActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.DRINKS_BEER_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
