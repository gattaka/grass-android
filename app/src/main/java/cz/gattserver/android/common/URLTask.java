package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class URLTask<T> extends AsyncTask<String, Void, URLTask.URLTaskInfoBundle> {

    private WeakReference<T> urlTaskClientWeakReference;
    private OnSuccessAction<T> onSuccessAction;

    public static class URLTaskInfoBundle {
        private String[] params;
        private byte[] result;

        URLTaskInfoBundle(String[] params, byte[] result) {
            this.params = params;
            this.result = result;
        }

        public String[] getParams() {
            return params;
        }

        public byte[] getResult() {
            return result;
        }

        public String getResultAsStringUTF() {
            try {
                return getResultAsString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "UnsupportedEncodingException";
            }
        }

        public String getResultAsString(String charset) throws UnsupportedEncodingException {
            return new String(result, charset);
        }
    }

    public interface OnSuccessAction<T> {
        void run(T urlTaskClient, URLTaskInfoBundle result);
    }

    public URLTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    public URLTask(T urlTaskClient) {
        this(urlTaskClient, null);
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    @Override
    protected URLTaskInfoBundle doInBackground(String... params) {
        URLConnection urlConn;
        try {
            URL url = new URL(params[0]);
            urlConn = url.openConnection();
            byte[] bytes = readBytes(urlConn.getInputStream());
            return new URLTaskInfoBundle(params, bytes);
        } catch (Exception ex) {
            Log.e("JSONTask", "JSON fetch", ex);
            return null;
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
