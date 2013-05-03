package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Section;
import org.dbdoclet.xiphias.NodeSerializer;
import org.junit.Test;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.javadoc.RootDocImpl;

public class StrictSynopsisTests extends AbstractTestCase {

    @Test
    public void testGenerics_1() throws DocletException, IOException {

        String srcpath = sourcePath + "org/dbdoclet/music/AbstractElement.java";
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

        DocBookTagFactory dbfactory = new DocBookTagFactory();
        Section section = dbfactory.createSection("Test");

        StrictSynopsis synop = new StrictSynopsis();
        synop.process(classDocs[0], section);
        pln(NodeSerializer.toXML(section));
    }

    @Test
    public void testGenerics_2() throws DocletException, IOException {

        String[] sources = {
                sourcePath + "org/dbdoclet/music/AbstractElement.java",
                sourcePath + "org/dbdoclet/music/Note.java",
                sourcePath
                        + "org/dbdoclet/music/annotation/Transpose.java" };

        String classpath = sourcePath;

        RootDocImpl root = javadoc(sources, classpath, null);

        if (root == null) {
            fail("RootDoc == null");
            return;
        }

        ClassDoc[] classDocs = root.specifiedClasses();

        DocBookTagFactory dbfactory = new DocBookTagFactory();
        Section section = dbfactory.createSection("Test");

        StrictSynopsis synop = new StrictSynopsis();
        synop.process(classDocs[1], section);
        pln(NodeSerializer.toXML(section));
    }
}
