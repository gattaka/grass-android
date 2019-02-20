package cz.gattserver.android.lazyloader;

import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

public abstract class FilteredLazyListActivity<T> extends LazyListActivity<T> {

    protected EditText filterText;
    protected ViewGroup layout;

    protected abstract ViewGroup getLayout();

    protected abstract String createCountURL(String filter);

    protected abstract String createFetchURL(String filter, int pageSize, int page);

    public FilteredLazyListActivity(@LayoutRes int layoutResID, String title) {
        super(layoutResID, title);
    }

    @Override
    protected void createComponents() {
        layout = getLayout();

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

    private String getFilterText() {
        return Uri.encode(filterText.getText() == null ? "" : filterText.getText().toString());
    }

    @Override
    protected String createCountURL() {
        String url = createCountURL(getFilterText());
        Log.d("F.LazyListActivity", "createCountURL(): " + url);
        return url;
    }

    @Override
    protected String createFetchURL(int pageSize, int page) {
        String url = createFetchURL(getFilterText(), pageSize, page);
        Log.d("F.LazyListActivity", "createFetchURL(): " + url);
        return url;
    }
}
