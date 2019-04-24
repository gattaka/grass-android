package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLPostTask<T> extends AsyncTask<String, Void, URLTaskInfoBundle> {

    private static final String LINE_END = "\r\n";

    private WeakReference<T> urlTaskClientWeakReference;
    private OnSuccessAction<T> onSuccessAction;

    public URLPostTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
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
            connection.setDoInput(true);

            if (params.length > 1) {
                if ((params.length - 1) % 2 != 0)
                    throw new IllegalArgumentException("Po prvním parametru (adresa) musí následovat dvojice argumentů name:value");

                DataOutputStream outputStream;
                outputStream = new DataOutputStream(connection.getOutputStream());

                for (int s = 1; s < params.length; s += 2) {
                    outputStream.writeBytes(params[s] + "=" + params[s + 1]);
                    if (s == params.length - 1) {
                        outputStream.writeBytes(LINE_END);
                    } else {
                        outputStream.writeBytes("&");
                    }
                }

                outputStream.flush();
                outputStream.close();
            }

            InputStream is = connection.getInputStream();
            byte[] bytes = ByteUtils.readBytes(is);

            Log.e("URLPostTask", "ResponseCode: " + connection.getResponseCode());
            return URLTaskInfoBundle.onSuccess(params, bytes, connection.getResponseCode());
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
