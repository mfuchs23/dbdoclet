package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.fail;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

import com.sun.tools.javadoc.RootDocImpl;

public class CommentTests extends AbstractTestCase {

    @Test
    public void testComment_1() throws DocletException {

        String[] sources = {
                sourcePath + "org/dbdoclet/music/AbstractElement.java",
                sourcePath + "org/dbdoclet/music/Note.java" };

        String classpath = sourcePath;

        RootDocImpl root = javadoc(sources, classpath, null);

        if (root == null) {
            fail("RootDoc == null");
            return;
        }

        // ClassDoc[] classDocs = root.specifiedClasses();
        DocBookDoclet.start(root);
    }
}
