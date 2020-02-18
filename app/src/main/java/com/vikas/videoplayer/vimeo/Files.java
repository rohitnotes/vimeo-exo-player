
package com.vikas.videoplayer.vimeo;

import java.util.List;

public class Files {

    private Dash dash;
    private Hls hls;
    private List<Progressive> progressive = null;

    public Dash getDash() {
        return dash;
    }

    public void setDash(Dash dash) {
        this.dash = dash;
    }

    public Hls getHls() {
        return hls;
    }

    public void setHls(Hls hls) {
        this.hls = hls;
    }

    public List<Progressive> getProgressive() {
        return progressive;
    }

    public void setProgressive(List<Progressive> progressive) {
        this.progressive = progressive;
    }

}
