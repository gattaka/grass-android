package cz.gattserver.android.lazyloader;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.AbsListView;

import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;

public class LazyLoaderCountTask<T> extends URLGetTask<LazyListActivity<T>> {

    private static int PAGE_SIZE = 10;

    private int currentPage = 0;
    private int totalCount;

    LazyLoaderCountTask(LazyListActivity<T> lazyListViewActivity) {
        super(lazyListViewActivity);
    }

    private void alertError(String err) {
        LazyListActivity<T> instance = urlTaskClientWeakReference.get();
        if (instance != null) {
            new AlertDialog.Builder(instance)
                    .setTitle("Chyba")
                    .setMessage(err)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    @Override
    protected void onPostExecute(URLTaskInfoBundle result) {
        if (result.isSuccess()) {
            String countAsString = result.getResultAsStringUTF();
            try {
                totalCount = Integer.parseInt(countAsString);
                Log.d("LazyLoaderCountTask", "totalCount: " + totalCount);
                super.onPostExecute(result);
            } catch (Exception ex) {
                String err = "Nezdařilo se parsování počtu záznamů, server zaslal 'počet': '" + countAsString + "'";
                Log.e("LazyLoaderCountTask", err, ex);
                alertError(err);
            }
        } else {
            alertError("Nezdařilo se získat počet záznamů");
        }
    }

    @Override
    protected void runOnWeakReference(final LazyListActivity<T> instance, URLTaskInfoBundle result) {
        Log.d("LazyLoaderCountTask", "(id: " + LazyLoaderCountTask.this.toString() + ") runOnWeakReference");
        instance.listView.setOnScrollListener(new LazyLoaderScrollListener() {
            @Override
            public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("L.LoaderScrollListener", "currentPage: " + currentPage + ", totalCount: " + totalCount);
                if (totalCount > currentPage * PAGE_SIZE) {
                    LazyLoaderFetchTask<T> fetchTask = new LazyLoaderFetchTask<>(instance);
                    fetchTask.execute(instance.createFetchURL(PAGE_SIZE, currentPage));
                    currentPage++;
                }
                if (totalCount <= currentPage * PAGE_SIZE) {
                    instance.listView.removeFooterView(instance.progressBar);
                }
            }
        });
    }
}