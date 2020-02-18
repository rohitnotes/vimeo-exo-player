
package com.vikas.videoplayer.vimeo;

import java.util.List;

public class FileCodecs {

    private Hevc hevc;
    private List<Object> av1 = null;
    private List<String> avc = null;

    public Hevc getHevc() {
        return hevc;
    }

    public void setHevc(Hevc hevc) {
        this.hevc = hevc;
    }

    public List<Object> getAv1() {
        return av1;
    }

    public void setAv1(List<Object> av1) {
        this.av1 = av1;
    }

    public List<String> getAvc() {
        return avc;
    }

    public void setAvc(List<String> avc) {
        this.avc = avc;
    }

}
