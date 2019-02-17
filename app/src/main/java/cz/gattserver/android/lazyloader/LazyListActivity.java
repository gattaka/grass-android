package cz.gattserver.android.lazyloader;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.GrassActivity;

public abstract class LazyListActivity<T> extends GrassActivity {

    ListView listView;
    ProgressBar progressBar;
    ArrayAdapter<T> adapter;

    private @LayoutRes
    int layoutResID;
    private String title;

    protected abstract T constructTO(JSONObject jsonObject) throws JSONException;

    protected abstract void onItemClick(T item);

    protected abstract String createFetchURL(int pageSize, int page);

    protected abstract String createCountURL();

    public LazyListActivity(@LayoutRes int layoutResID, String title) {
        this.layoutResID = layoutResID;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        setTitle(title);

        createComponents();
        initLazyLoaderTask();

        Log.d("LazyListActivity", "The onCreate() event");
    }

    protected ArrayAdapter<T> getAdapter() {
        return adapter;
    }

    protected ListView getListView() {
        return listView;
    }

    protected void initLazyLoaderTask() {
        LazyLoaderCountTask<T> lazyLoaderTask = new LazyLoaderCountTask<>(this);
        lazyLoaderTask.execute(createCountURL());
    }

    protected void createComponents() {
        listView = createListView();
        setContentView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    protected ListView createListView() {
        listView = new ListView(this);
        listView.addFooterView(progressBar = new ProgressBar(this));
        adapter = createArrayAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LazyListActivity.this.adapter.getCount() <= position)
                    return;
                T item = LazyListActivity.this.adapter.getItem(position);
                LazyListActivity.this.onItemClick(item);
            }
        });
        return listView;
    }

    protected ArrayAdapter<T> createArrayAdapter() {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    }

}
