
package com.vikas.videoplayer.vimeo;


public class Sentry {

    private String url;
    private Boolean enabled;
    private Boolean debugEnabled;
    private Integer debugIntent;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(Boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public Integer getDebugIntent() {
        return debugIntent;
    }

    public void setDebugIntent(Integer debugIntent) {
        this.debugIntent = debugIntent;
    }

}
