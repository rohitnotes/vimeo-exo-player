
package com.vikas.videoplayer.vimeo;


public class Hls {

    private Boolean separateAv;
    private String defaultCdn;
    private Cdns_ cdns;

    public Boolean getSeparateAv() {
        return separateAv;
    }

    public void setSeparateAv(Boolean separateAv) {
        this.separateAv = separateAv;
    }

    public String getDefaultCdn() {
        return defaultCdn;
    }

    public void setDefaultCdn(String defaultCdn) {
        this.defaultCdn = defaultCdn;
    }

    public Cdns_ getCdns() {
        return cdns;
    }

    public void setCdns(Cdns_ cdns) {
        this.cdns = cdns;
    }

}
