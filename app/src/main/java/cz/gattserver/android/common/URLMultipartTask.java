package cz.gattserver.android.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLMultipartTask<T> extends AsyncTask<URLTaskParamTO, String, URLTaskInfoBundle> {

    private static final String FILES_FORM_DATA_NAME = "files";
    private static final String LINE_END = "\r\n";

    protected WeakReference<T> urlMultipartTaskClientWeakReference;
    protected OnSuccessAction<T> onSuccessAction;

    public URLMultipartTask(T urlTaskClient, OnSuccessAction<T> onSuccessAction) {
        this.urlMultipartTaskClientWeakReference = new WeakReference<>(urlTaskClient);
        this.onSuccessAction = onSuccessAction;
    }

    @Override
    protected URLTaskInfoBundle doInBackground(URLTaskParamTO[] params) {
        URLTaskParamTO taskParamTO = params[0];
        Log.e("URLMultipartTask", "doInBackground");
        try {
            String twoHyphens = "--";
            String boundary = Long.toString(System.currentTimeMillis(), 16);

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            URL url = new URL(taskParamTO.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            if (taskParamTO.getSessionId() != null)
                connection.setRequestProperty("Cookie", "JSESSIONID=" + taskParamTO.getSessionId());

            DataOutputStream outputStream;
            outputStream = new DataOutputStream(connection.getOutputStream());

            for (int i = 0; i < taskParamTO.getParams().length; i += 2) {

                // -- + boundary + CRLF
                outputStream.writeBytes(twoHyphens + boundary + LINE_END);

                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + taskParamTO.getParams()[i] + "\"" + LINE_END);
                outputStream.writeBytes("Content-Type: text/plain" + LINE_END);
                outputStream.writeBytes(LINE_END);
                outputStream.writeBytes(taskParamTO.getParams()[i + 1]);
                outputStream.writeBytes(LINE_END);
            }

            for (int i = 0; i < taskParamTO.getFileParams().length; i += 2) {

                // -- + boundary + CRLF
                outputStream.writeBytes(twoHyphens + boundary + LINE_END);

                FileInputStream fileInputStream;

                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FILES_FORM_DATA_NAME + "\"; filename=\"" + taskParamTO.getFileParams()[i + 1] + "\"" + LINE_END);
                outputStream.writeBytes("Content-Type: application/octet-stream" + LINE_END);
                outputStream.writeBytes(LINE_END);

                fileInputStream = new FileInputStream(taskParamTO.getFileParams()[i]);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                fileInputStream.close();

                outputStream.writeBytes(LINE_END);

            }

            // -- + boundary + CRLF + --
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + LINE_END);

            outputStream.flush();
            outputStream.close();

            Log.e("URLMultipartTask Resp.", String.valueOf(connection.getResponseCode()));
            return URLTaskInfoBundle.onSuccess(taskParamTO, new byte[0], connection.getResponseCode());
        } catch (Exception e) {
            Log.e("URLMultipartTask Error", e.toString());
            return URLTaskInfoBundle.onFail(taskParamTO, e);
        }
    }

    @Override
    protected void onPostExecute(URLTaskInfoBundle result) {
        super.onPostExecute(result);
        final T instance = urlMultipartTaskClientWeakReference.get();
        if (instance != null) {
            runOnWeakReference(instance, result);
        }
    }

    protected void runOnWeakReference(T instance, URLTaskInfoBundle result) {
        if (onSuccessAction != null)
            onSuccessAction.run(instance, result);
    }

}