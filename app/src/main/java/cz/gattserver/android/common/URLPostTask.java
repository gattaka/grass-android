package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLPostTask<T> extends AsyncTask<URLTaskParamTO, Void, URLTaskInfoBundle> {

    private static final String LINE_END = "\r\n";

    private WeakReference<T> urlTaskClientWeakReference;
    private OnSuccessAction<T> onSuccessAction;

    public URLPostTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    @Override
    protected URLTaskInfoBundle doInBackground(URLTaskParamTO... params) {
        URLTaskParamTO taskParamTO = params[0];
        try {

            URL url = new URL(taskParamTO.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setRequestProperty("Accept", "*/*");
            if (taskParamTO.getSessionId() != null)
                connection.setRequestProperty("Cookie", "JSESSIONID=" + taskParamTO.getSessionId());
            connection.setDoOutput(true);
            connection.setDoInput(true);

            if (taskParamTO.getParams().length > 0) {
                if (taskParamTO.getParams().length % 2 != 0)
                    throw new IllegalArgumentException("Po prvním parametru (adresa) musí následovat dvojice argumentů name:value");

                DataOutputStream outputStream;
                outputStream = new DataOutputStream(connection.getOutputStream());

                for (int s = 0; s < taskParamTO.getParams().length; s += 2) {
                    outputStream.writeBytes(taskParamTO.getParams()[s] + "=");
                    outputStream.write(taskParamTO.getParams()[s + 1].getBytes("UTF-8"));
                    if (s < taskParamTO.getParams().length - 2) {
                        outputStream.writeBytes("&");
                    }
                }

                outputStream.flush();
                outputStream.close();
            }
            int responseCode = connection.getResponseCode();
            Log.e("URLPostTask", "ResponseCode: " + responseCode);

            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                byte[] bytes = ByteUtils.readBytes(is);
                return URLTaskInfoBundle.onSuccess(taskParamTO, bytes, responseCode);
            }

            URLTaskInfoBundle bundle = URLTaskInfoBundle.onSuccess(taskParamTO, new byte[]{}, responseCode);
            return bundle;
        } catch (Exception e) {
            Log.e("URLPostTask Error", e.toString());
            return URLTaskInfoBundle.onFail(taskParamTO, e);
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
