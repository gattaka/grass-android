package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class URLTask<T extends URLTask.URLTaskClient> extends AsyncTask<String, Void, String> {

    private WeakReference<T> urlTaskClientWeakReference;

    public URLTask(T urlTaskClient) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
    }

    public interface URLTaskClient {
        void onSuccess(String result);
    }

    @Override
    protected String doInBackground(String... params) {
        URLConnection urlConn;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(params[0]);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception ex) {
            Log.e("JSONTask", "JSON fetch", ex);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        final T instance = urlTaskClientWeakReference.get();
        if (instance != null) {
            runOnWeakReference(instance, result);
        }
    }

    protected void runOnWeakReference(T instance, String result) {
        instance.onSuccess(result);
    }
}
