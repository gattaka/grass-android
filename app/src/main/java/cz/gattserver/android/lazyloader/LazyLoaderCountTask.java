package cz.gattserver.android.lazyloader;

import android.util.Log;
import android.widget.AbsListView;

import cz.gattserver.android.common.URLTask;

public class LazyLoaderCountTask<T> extends URLTask<LazyListActivity<T>> {

    private static int PAGE_SIZE = 10;

    private int currentPage = 0;
    private int totalCount;

    public LazyLoaderCountTask(LazyListActivity<T> lazyListViewActivity) {
        super(lazyListViewActivity);
    }

    @Override
    protected void onPostExecute(URLTaskInfoBundle result) {
        totalCount = Integer.parseInt(result.getResultAsStringUTF());
        super.onPostExecute(result);
    }

    @Override
    protected void runOnWeakReference(final LazyListActivity<T> instance, URLTaskInfoBundle result) {
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