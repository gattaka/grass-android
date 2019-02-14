package cz.gattserver.android;

import android.content.Intent;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.RateItemArrayAdapter;
import cz.gattserver.android.interfaces.RatedItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class BeersActivity extends LazyListActivity<RatedItemTO> {

    public BeersActivity() {
        super(R.layout.activity_beers, "Piva", Config.DRINKS_BEER_COUNT_RESOURCE);
    }

    @Override
    protected RatedItemTO constructTO(JSONObject jsonObject) throws JSONException {
        StringBuilder sb = new StringBuilder();
        int rating = (int) Double.parseDouble(jsonObject.getString("rating"));
        for (int i = 0; i < 5; i++)
            sb.append(i < rating ? "★" : "☆");
        return new RatedItemTO(sb.toString(), jsonObject.getString("brewery") + ": " + jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected ArrayAdapter<RatedItemTO> createArrayAdapter() {
        return new RateItemArrayAdapter(this, R.layout.ranked_item_listview_row);
    }

    @Override
    protected void onItemClick(RatedItemTO item) {
        Intent intent = new Intent(this, BeerActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.DRINKS_BEER_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
