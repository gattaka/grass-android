package cz.gattserver.android.common;

import java.io.UnsupportedEncodingException;

public class URLTaskInfoBundle {

    private URLTaskParamTO taskParamTO;
    private byte[] result;
    private boolean success;
    private Throwable error;
    private Integer responseCode;

    public static URLTaskInfoBundle onFail(URLTaskParamTO taskParamTO, Throwable error) {
        return new URLTaskInfoBundle(taskParamTO, null, false, error, null);
    }

    public static URLTaskInfoBundle onSuccess(URLTaskParamTO taskParamTO, byte[] result, int responseCode) {
        return new URLTaskInfoBundle(taskParamTO, result, true, null, responseCode);
    }

    private URLTaskInfoBundle(URLTaskParamTO taskParamTO, byte[] result, boolean success, Throwable error, Integer responseCode) {
        this.taskParamTO = taskParamTO;
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

    public URLTaskParamTO getTaskParamTO() {
        return taskParamTO;
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