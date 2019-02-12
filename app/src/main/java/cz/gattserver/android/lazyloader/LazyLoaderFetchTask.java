package cz.gattserver.android.lazyloader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cz.gattserver.android.common.URLTask;

public class LazyLoaderFetchTask<T> extends URLTask {

    private WeakReference<LazyListActivity<T>> lazyListViewActivity;

    LazyLoaderFetchTask(LazyListActivity<T> lazyListViewActivity) {
        this.lazyListViewActivity = new WeakReference<>(lazyListViewActivity);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        final LazyListActivity<T> instance = lazyListViewActivity.get();
        if (instance != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    instance.adapter.add(instance.constructTO(jsonObject));
                }
            } catch (JSONException e) {
                Log.e("LazyLoaderFetchTask", "JSONArray", e);
            }
        }
    }
}