package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.fail;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.javadoc.RootDocImpl;

public class ArticleManagerTests extends AbstractTestCase {

    @Test
    public void test_1() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
        String classpath = sourcePath;

        RootDocImpl root = javadoc(new String[] { srcpath }, classpath, null);

        if (root == null) {
            fail("RootDoc == null");
            return;
        }

        ClassDoc[] classDocs = root.specifiedClasses();

        if (classDocs.length != 1) {
            fail("Die Anzahl der gefundenen ClassDoc-Instanzen muß 1 betragen.");
        }

        DocBookDoclet.start(root);
    }
}
