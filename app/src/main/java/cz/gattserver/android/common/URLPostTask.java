package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class URLPostTask<T> extends AsyncTask<String, Void, URLTaskInfoBundle> {

    private WeakReference<T> urlTaskClientWeakReference;
    private OnSuccessAction<T> onSuccessAction;

    public interface OnSuccessAction<T> {
        void run(T urlTaskClient, URLTaskInfoBundle result);
    }

    public URLPostTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    public URLPostTask(T urlTaskClient) {
        this(urlTaskClient, null);
    }

    @Override
    protected URLTaskInfoBundle doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setRequestProperty("Accept", "*/*");
            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(params[1]);
            writer.close();

            connection.connect();

            Log.e("URLPostTask Response", connection.getResponseMessage());
            return URLTaskInfoBundle.onSuccess(params, connection.getResponseMessage().getBytes(), connection.getResponseCode());
        } catch (Exception e) {
            Log.e("URLPostTask Error", e.toString());
            return URLTaskInfoBundle.onFail(params, e);
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
