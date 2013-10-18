package org.dbdoclet.music;

import java.io.Serializable;

public interface MusicElement extends Serializable, Cloneable {

    /**
     * @deprecated
     * Use {@linkplain #toElement()} instead
     * 
     * @return String
     */
    public String element();
    
    public String toElement();
    
    @Deprecated
    public void sound();
    
    public void play();
}
