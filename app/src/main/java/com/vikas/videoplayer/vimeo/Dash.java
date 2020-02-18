
package com.vikas.videoplayer.vimeo;

import java.util.List;

public class Dash {

    private Boolean separateAv;
    private List<Stream> streams = null;
    private Cdns cdns;
    private List<StreamsAvc> streamsAvc = null;
    private String defaultCdn;

    public Boolean getSeparateAv() {
        return separateAv;
    }

    public void setSeparateAv(Boolean separateAv) {
        this.separateAv = separateAv;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public Cdns getCdns() {
        return cdns;
    }

    public void setCdns(Cdns cdns) {
        this.cdns = cdns;
    }

    public List<StreamsAvc> getStreamsAvc() {
        return streamsAvc;
    }

    public void setStreamsAvc(List<StreamsAvc> streamsAvc) {
        this.streamsAvc = streamsAvc;
    }

    public String getDefaultCdn() {
        return defaultCdn;
    }

    public void setDefaultCdn(String defaultCdn) {
        this.defaultCdn = defaultCdn;
    }

}
