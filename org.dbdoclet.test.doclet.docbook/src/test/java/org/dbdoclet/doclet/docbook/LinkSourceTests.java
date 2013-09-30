package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.dbdoclet.doclet.DocletException;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.tools.javadoc.RootDocImpl;

public class LinkSourceTests extends AbstractTestCase {

    @Test
    public void testLinkSource_1() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        RootDocImpl root = javadoc(new String[] { srcpath }, classpath, 
                    new String[][] { { "-linksource" },  { "-nostatistics" } });

        if (root == null) {
            fail("RootDoc == null");
            return;
        }

        DocBookDoclet.start(root);
        
        String value = xpath("//db:title");
        assertEquals("org.dbdoclet.music", value);
    }
}
