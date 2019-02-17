package cz.gattserver.android.lazyloader;

import android.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.gattserver.android.common.URLGetTask;
import cz.gattserver.android.common.URLTaskInfoBundle;

public class LazyLoaderFetchTask<T> extends URLGetTask<LazyListActivity<T>> {

    LazyLoaderFetchTask(LazyListActivity<T> lazyListViewActivity) {
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
    protected void runOnWeakReference(LazyListActivity<T> instance, URLTaskInfoBundle result) {
        try {
            if (result.isSuccess()) {
                JSONArray jsonArray = new JSONArray(result.getResultAsStringUTF());
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    instance.adapter.add(instance.constructTO(jsonObject));
                }
            } else {
                alertError("Nezdařilo se získat data:" + result.getError().getLocalizedMessage());
            }
        } catch (JSONException e) {
            Log.e("LazyLoaderFetchTask", "JSONArray", e);
        }
    }
}