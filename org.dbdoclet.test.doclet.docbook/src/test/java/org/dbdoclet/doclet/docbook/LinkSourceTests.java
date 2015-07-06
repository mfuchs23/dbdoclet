package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class LinkSourceTests extends AbstractTestCase {

    @Test
    public void testLinkSource_1() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        javadoc("-linksource", "-cp", classpath, srcpath);
        String value = xpath("//db:title");
        assertEquals("org.dbdoclet.music", value);
    }
}
