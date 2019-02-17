package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLGetTask<T> extends AsyncTask<String, Void, URLTaskInfoBundle> {

    private WeakReference<T> urlTaskClientWeakReference;
    private OnSuccessAction<T> onSuccessAction;

    public interface OnSuccessAction<T> {
        void run(T urlTaskClient, URLTaskInfoBundle result);
    }

    public URLGetTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    public URLGetTask(T urlTaskClient) {
        this(urlTaskClient, null);
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    @Override
    protected URLTaskInfoBundle doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            byte[] bytes = readBytes(connection.getInputStream());
            return URLTaskInfoBundle.onSuccess(params, bytes, connection.getResponseCode());
        } catch (Exception ex) {
            Log.e("JSONTask", "JSON fetch", ex);
            return URLTaskInfoBundle.onFail(params, ex);
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
