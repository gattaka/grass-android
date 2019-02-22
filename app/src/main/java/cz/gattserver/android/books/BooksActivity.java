package cz.gattserver.android.books;

import android.content.Intent;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.common.FormatUtils;
import cz.gattserver.android.common.RateItemArrayAdapter;
import cz.gattserver.android.interfaces.RatedItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class BooksActivity extends LazyListActivity<RatedItemTO> {

    public BooksActivity() {
        super(R.layout.activity_books, "Knihy");
    }

    @Override
    protected RatedItemTO constructTO(JSONObject jsonObject) throws JSONException {
        return new RatedItemTO(FormatUtils.formatRatingStars(Double.parseDouble(jsonObject.getString("rating"))), jsonObject.getString("author") + ": " + jsonObject.getString("name"), jsonObject.getString("id"));
    }

    @Override
    protected ArrayAdapter<RatedItemTO> createArrayAdapter() {
        return new RateItemArrayAdapter(this, R.layout.ranked_item_listview_row);
    }

    @Override
    protected void onItemClick(RatedItemTO item) {
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    protected String createCountURL() {
        return Config.BOOKS_COUNT_RESOURCE;
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        return Config.BOOKS_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
    }
}
