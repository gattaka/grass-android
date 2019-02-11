package cz.gattserver.mobile;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class LazyListViewActivity<T> extends GrassActivity {

    private static String msg = "GrassAPP: ";
    private static int PAGE_SIZE = 10;

    ArrayAdapter<T> adapter;
    private @LayoutRes
    int layoutResID;
    private String title;
    private String countURL;

    private class LazyLoaderTask<T> extends URLTask {

        private LazyListViewActivity<T> activity;
        private ListView listView;
        private ProgressBar progressBar;
        private int totalCount = 0;
        private int currentPage = 0;

        LazyLoaderTask(LazyListViewActivity<T> activity) {
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            totalCount = Integer.parseInt(result);

            listView = new ListView(activity);
            listView.addFooterView(progressBar = new ProgressBar(activity));
            activity.adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
            listView.setAdapter(activity.adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    T item = activity.adapter.getItem(position);
                    activity.onItemClick(item);
                }
            });

            activity.setContentView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            listView.setOnScrollListener(new LazyLoader() {
                @Override
                public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Log.d(msg, "load() event");
                    if (totalCount > currentPage * PAGE_SIZE) {
                        FetchItemsTask<T> fetchTask = new FetchItemsTask<>(activity);
                        fetchTask.execute(activity.createFetchURL(PAGE_SIZE, currentPage));
                        currentPage++;
                    } else {
                        listView.removeFooterView(progressBar);
                    }
                }
            });
        }
    }

    abstract T constructTO(JSONObject jsonObject) throws JSONException;

    abstract void onItemClick(T item);

    abstract String createFetchURL(int pageSize, int page);

    public LazyListViewActivity(@LayoutRes int layoutResID, String title, String countURL) {
        this.layoutResID = layoutResID;
        this.title = title;
        this.countURL = countURL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        setTitle(title);

        LazyLoaderTask<T> lazyLoaderTask = new LazyLoaderTask<>(this);
        lazyLoaderTask.execute(countURL);

        Log.d(msg, "The onCreate() event");
    }

}
