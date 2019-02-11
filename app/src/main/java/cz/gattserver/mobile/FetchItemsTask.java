package cz.gattserver.mobile;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchItemsTask<T> extends URLTask {

    private LazyListViewActivity<T> activity;

    public FetchItemsTask(LazyListViewActivity<T> activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                activity.adapter.add(activity.constructTO(jsonObject));
            }
        } catch (JSONException e) {
            Log.e("FetchItemsTask", "JSONArray", e);
        }
    }
}