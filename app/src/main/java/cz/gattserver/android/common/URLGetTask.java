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

    protected WeakReference<T> urlTaskClientWeakReference;
    protected OnSuccessAction<T> onSuccessAction;

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
        String address = params[0];
        try {
            Log.d("URLGetTask", "Trying... URL GET: " + address);
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            byte[] bytes = readBytes(is);
            URLTaskInfoBundle bundle = URLTaskInfoBundle.onSuccess(params, bytes, connection.getResponseCode());
            Log.d("URLGetTask", "Success! URL GET: " + address);
            return bundle;
        } catch (Exception ex) {
            Log.e("URLGetTask", "Failure. URL GET: " + address, ex);
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
