package cz.gattserver.android.drinks;

import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.FormatUtils;
import cz.gattserver.android.common.RateItemArrayAdapter;
import cz.gattserver.android.common.URLTaskParamTO;
import cz.gattserver.android.interfaces.RatedItemTO;
import cz.gattserver.android.lazyloader.FilteredLazyListActivity;

public class BeersActivity extends FilteredLazyListActivity<RatedItemTO> {

    public BeersActivity() {
        super(R.layout.activity_beers, "Pivo");
    }

    @Override
    protected RatedItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new RatedItemTO(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))), jsonObject.getString("brewery") + ": " + jsonObject.getString("name"), jsonObject.getString("id"));
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
    protected ViewGroup getLayout() {
        return findViewById(R.id.beersLayout);
    }

    @Override
    protected URLTaskParamTO createCountTaskParamTO(String filter) {
        return new URLTaskParamTO(Config.DRINKS_BEER_COUNT_RESOURCE + "?filter=" + filter);
    }

    @Override
    protected URLTaskParamTO createFetchTaskParamTO(String filter, int pageSize, int page) {
        return new URLTaskParamTO(Config.DRINKS_BEER_LIST_RESOURCE + "?filter=" + filter + "&pageSize=" + pageSize + "&page=" + page);
    }
}
