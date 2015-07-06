package org.dbdoclet.doclet.docbook;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class CommentTests extends AbstractTestCase {

    @Test
    public void testComment_1() throws DocletException {

        String[] sources = {
                sourcePath + "org/dbdoclet/music/AbstractElement.java",
                sourcePath + "org/dbdoclet/music/Note.java" };

        javadoc(sources);
    }
}
