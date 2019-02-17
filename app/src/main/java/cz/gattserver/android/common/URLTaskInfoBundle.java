package cz.gattserver.android.common;

import java.io.UnsupportedEncodingException;

public class URLTaskInfoBundle {

    private String[] params;
    private byte[] result;
    private boolean success;
    private Throwable error;
    private Integer responseCode;

    public static URLTaskInfoBundle onFail(String[] params, Throwable error) {
        return new URLTaskInfoBundle(params, null, false, error, null);
    }

    public static URLTaskInfoBundle onSuccess(String[] params, byte[] result, int responseCode) {
        return new URLTaskInfoBundle(params, result, true, null, responseCode);
    }

    private URLTaskInfoBundle(String[] params, byte[] result, boolean success, Throwable error, Integer responseCode) {
        this.params = params;
        this.result = result;
        this.success = success;
        this.error = error;
        this.responseCode = responseCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getError() {
        return error;
    }

    public Integer getResponseCode() {
        return responseCode;
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