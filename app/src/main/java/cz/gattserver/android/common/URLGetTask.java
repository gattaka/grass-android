package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLGetTask<T> extends AsyncTask<URLTaskParamTO, Void, URLTaskInfoBundle> {

    protected WeakReference<T> urlTaskClientWeakReference;
    protected OnSuccessAction<T> onSuccessAction;

    public URLGetTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    public URLGetTask(T urlTaskClient) {
        this(urlTaskClient, null);
    }

    @Override
    protected URLTaskInfoBundle doInBackground(URLTaskParamTO... params) {
        URLTaskParamTO taskParamTO = params[0];
        String address = taskParamTO.getUrl();
        try {
            Log.d("URLGetTask", "Trying... URL GET: " + address);
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (taskParamTO.getSessionId() != null) {
                connection.setRequestProperty("Cookie", "JSESSIONID=" + taskParamTO.getSessionId());
            }
            InputStream is = connection.getInputStream();
            byte[] bytes = ByteUtils.readBytes(is);
            URLTaskInfoBundle bundle = URLTaskInfoBundle.onSuccess(taskParamTO, bytes, connection.getResponseCode());
            Log.d("URLGetTask", "Success! URL GET: " + address);
            return bundle;
        } catch (Exception ex) {
            Log.e("URLGetTask", "Failure. URL GET: " + address, ex);
            return URLTaskInfoBundle.onFail(taskParamTO, ex);
        }
    }

    @Override
    protected void onPostExecute(URLTaskInfoBundle result) {
        super.onPostExecute(result);
        final T instance = urlTaskClientWeakReference.get();
        if (instance != null) {
            runOnWeakReference(instance, result);
        }
    }

    protected void runOnWeakReference(T instance, URLTaskInfoBundle result) {
        if (onSuccessAction != null)
            onSuccessAction.run(instance, result);
    }
}
