package org.dbdoclet.music;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "XmlPermission")
public class MusicXmlElement {

    @Deprecated
    @XmlElement(required = true)
    protected String name;

}
