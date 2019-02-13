package cz.gattserver.android.lazyloader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.URLTask;

public class LazyLoaderFetchTask<T> extends URLTask<LazyListActivity<T>> {

    LazyLoaderFetchTask(LazyListActivity<T> lazyListViewActivity) {
        super(lazyListViewActivity);
    }

    @Override
    protected void runOnWeakReference(LazyListActivity<T> instance, String result) {
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