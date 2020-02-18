
package com.vikas.videoplayer.vimeo;

import java.util.List;

public class Version {

    private Object current;
    private List<Available> available = null;

    public Object getCurrent() {
        return current;
    }

    public void setCurrent(Object current) {
        this.current = current;
    }

    public List<Available> getAvailable() {
        return available;
    }

    public void setAvailable(List<Available> available) {
        this.available = available;
    }

}
