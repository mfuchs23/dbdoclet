package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class DeprecatedTests extends AbstractTestCase {

    @Test
    public void testDeprecatedList() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        javadoc("-cp", classpath, srcpath);
        String value = xpath("//db:modifier[. = '@Deprecated']");
        assertEquals("@Deprecated", value);
    }
}
