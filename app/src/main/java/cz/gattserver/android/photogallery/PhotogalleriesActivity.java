package cz.gattserver.android.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.Config;
import cz.gattserver.android.R;
import cz.gattserver.android.interfaces.ItemTO;
import cz.gattserver.android.lazyloader.LazyListActivity;

public class PhotogalleriesActivity extends LazyListActivity<ItemTO> {

    private EditText filterText;
    private LinearLayout layout;

    public PhotogalleriesActivity() {
        super(R.layout.activity_photogalleries, "Fotogalerie");
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
    protected void createComponents() {
        layout = findViewById(R.id.photogalleriesLayout);

        filterText = new EditText(this);
        filterText.setHint("Filtr");
        filterText.setMaxLines(1);
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getListView().setOnScrollListener(null);
                layout.removeView(getListView());
                createAndPlaceListView();
                initLazyLoaderTask();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        layout.addView(filterText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        createAndPlaceListView();
    }

    private void createAndPlaceListView() {
        ListView listView = createListView();
        layout.addView(listView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected String createCountURL() {
        String url;
        if (filterText.getText() != null && !filterText.getText().toString().trim().isEmpty()) {
            url = Config.PG_COUNT_RESOURCE + "?filter=" + Uri.encode(filterText.getText().toString());
        } else {
            url = Config.PG_COUNT_RESOURCE;
        }
        Log.d("PhotogalleriesActivity", "createCountURL(): " + url);
        return url;
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        String url;
        if (filterText.getText() != null && !filterText.getText().toString().trim().isEmpty()) {
            url = Config.PG_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page + "&filter=" + Uri.encode(filterText.getText().toString());
        } else {
            url = Config.PG_LIST_RESOURCE + "?pageSize=" + pageSize + "&page=" + page;
        }
        Log.d("PhotogalleriesActivity", "createFetchURL(): " + url);
        return url;
    }
}
