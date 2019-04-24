package cz.gattserver.android.drinks;

import android.content.Intent;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.FormatUtils;
import cz.gattserver.android.common.RateItemArrayAdapter;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.RatedItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class RumsActivity extends LazyListActivity<RatedItemTO> {

    public RumsActivity() {
        super(R.layout.activity_rums, "Rum");
    }

    @Override
    protected RatedItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new RatedItemTO(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))), jsonObject.getString("country") + ": " + jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected ArrayAdapter<RatedItemTO> createArrayAdapter() {
        return new RateItemArrayAdapter(this, R.layout.ranked_item_listview_row);
    }

    @Override
    protected void onItemClick(RatedItemTO item) {
        Intent intent = new Intent(this, RumActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected URLTaskParamTO createCountTaskParamTO() {
        return new URLTaskParamTO(Config.DRINKS_RUM_COUNT_RESOURCE);
    }

    @Override
    protected URLTaskParamTO createFetchTaskParamTO(int pageSize, int page) {
        return new URLTaskParamTO(Config.DRINKS_RUM_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page);
    }
}
