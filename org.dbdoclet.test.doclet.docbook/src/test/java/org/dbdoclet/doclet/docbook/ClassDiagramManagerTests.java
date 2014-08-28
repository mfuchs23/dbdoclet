package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

import com.sun.tools.javadoc.RootDocImpl;

public class ClassDiagramManagerTests extends AbstractTestCase {

    @Test
    public void createClassDiagram() throws DocletException {

        String srcpath = sourcePath + "org/dbdoclet/music/MusicXmlElement.java";
        String classpath = sourcePath + ":lib/jaxb-api.jar";

        javadoc("-cp", classpath, srcpath);
        String value = xpath("//db:modifier[. = '@Deprecated']");
        assertEquals("@Deprecated", value);
    }
}
