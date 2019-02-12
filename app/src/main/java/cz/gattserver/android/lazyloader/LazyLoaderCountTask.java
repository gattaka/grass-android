package cz.gattserver.android.lazyloader;

import android.util.Log;
import android.widget.AbsListView;

import java.lang.ref.WeakReference;

import cz.gattserver.android.common.URLTask;

public class LazyLoaderCountTask<T> extends URLTask {

    private static int PAGE_SIZE = 10;

    private int currentPage = 0;
    private WeakReference<LazyListActivity<T>> lazyListViewActivity;

    LazyLoaderCountTask(LazyListActivity<T> lazyListViewActivity) {
        this.lazyListViewActivity = new WeakReference<>(lazyListViewActivity);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        final int totalCount = Integer.parseInt(result);

        final LazyListActivity<T> instance = lazyListViewActivity.get();
        if (instance != null) {
            instance.listView.setOnScrollListener(new LazyLoaderScrollListener() {
                @Override
                public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Log.d("LazyLoaderCountTask", "load() event");
                    if (totalCount > currentPage * PAGE_SIZE) {
                        LazyLoaderFetchTask<T> fetchTask = new LazyLoaderFetchTask<>(instance);
                        fetchTask.execute(instance.createFetchURL(PAGE_SIZE, currentPage));
                        currentPage++;
                    } else {
                        instance.listView.removeFooterView(instance.progressBar);
                    }
                }
            });
        }
    }
}