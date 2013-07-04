package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

import com.sun.tools.javadoc.RootDocImpl;

public class DeprecatedTests extends AbstractTestCase {

    @Test
    public void testDeprecatedList() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        RootDocImpl root = javadoc(new String[] { srcpath }, classpath, 
                    new String[][] { { "-nostatistics" } });

        if (root == null) {
            fail("RootDoc == null");
            return;
        }

        DocBookDoclet.start(root);
        
        String value = xpath("//db:modifier[. = '@Deprecated']");
        assertEquals("@Deprecated", value);
    }
}
