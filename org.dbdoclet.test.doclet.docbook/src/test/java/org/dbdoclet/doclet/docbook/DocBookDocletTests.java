package org.dbdoclet.doclet.docbook;

import org.junit.Test;

public class DocBookDocletTests extends AbstractTestCase {

	@Test
    public void testDoclet_1() {

        String cmd = "javadoc " + "-d " + destPath
        + " -docletpath " + docbookDocletJarFileName
        + " -doclet org.dbdoclet.doclet.docbook.DocBookDoclet "
        + sourcePath + "sample/Sample.java";

        runForked(cmd);
    }

    @Test
    public void testDoclet_2() {

        String cmd = "javadoc " + "-d " + destPath
        + " -docletpath " + docbookDocletJarFileName
        + " -doclet org.dbdoclet.doclet.docbook.DocBookDoclet "
        + " -nostatistics "
        + sourcePath + "sample/Sample.java";

        runForked(cmd);
    }

    @Test
    public void testDoclet_3() {

        String cmd = "javadoc " + "-d " + destPath
        + " -docletpath " + docbookDocletJarFileName
        + " -doclet org.dbdoclet.doclet.docbook.DocBookDoclet "
        + " -corporation Michael_Fuchs "
        + " -nostatistics "
        + sourcePath + "sample/Sample.java";

        runForked(cmd);
    }
}
