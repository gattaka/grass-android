package cz.gattserver.android.common;

public class URLTaskParamTO {

    private String url;
    private String sessionId;
    private String[] params;
    private String[] fileParams;

    public URLTaskParamTO(String url) {
        this(url, null);
    }

    public URLTaskParamTO(String url, String sessionId) {
        this.url = url;
        this.sessionId = sessionId;
        this.params = new String[]{};
    }

    public String getUrl() {
        return url;
    }

    public URLTaskParamTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public URLTaskParamTO setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String[] getParams() {
        return params;
    }

    public URLTaskParamTO setParams(String... params) {
        this.params = params;
        return this;
    }

    public String[] getFileParams() {
        return fileParams;
    }

    public URLTaskParamTO setFileParams(String... fileParams) {
        this.fileParams = fileParams;
        return this;
    }
}
