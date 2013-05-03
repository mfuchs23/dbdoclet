package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.Chapter;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.FormalPara;
import org.dbdoclet.tag.docbook.ProgramListing;
import org.dbdoclet.xiphias.NodeSerializer;
import org.dbdoclet.xiphias.dom.DocumentImpl;
import org.dbdoclet.xiphias.dom.DocumentTypeImpl;
import org.dbdoclet.xiphias.dom.ElementImpl;
import org.dbdoclet.xiphias.dom.EntityImpl;
import org.dbdoclet.xiphias.dom.NodeImpl;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;

public class LinkSourceManager {

	@Inject DbdScript script;

    public void createDocBook(RootDoc rootDoc) throws IOException {

        File destDir = script.getDestinationDirectory();
        FileServices.createPath(destDir);

        DocumentImpl tdd = new DocumentImpl();
        ElementImpl targetSet = tdd.createElement("targetset");
        tdd.setDocumentElement(targetSet);

        for (ClassDoc cdoc : rootDoc.classes()) {

            SourcePosition sp = cdoc.position();
            File sourceFile = sp.file();

            DocBookDocument doc = new DocBookDocument();
            DocBookTagFactory dbf = new DocBookTagFactory();

            Chapter chapter = dbf.createChapter();
            chapter.setDocument(doc);
            doc.setDocumentElement(chapter);
            chapter.appendChild(dbf.createTitle(cdoc.qualifiedName()));

            FormalPara formalPara = dbf.createFormalPara();
            formalPara.setDocument(doc);
            formalPara.setId("listing");
            chapter.appendChild(formalPara);

            ProgramListing programlisting = dbf.createProgramListing();
            programlisting.setAttribute("linenumbering", "numbered");
            programlisting.setDocument(doc);
            formalPara.appendChild(programlisting);

            programlisting.appendChild(FileServices.readToString(sourceFile));

            // FileServices.copyFileToFile(sourceFile, createOutputFile(destDir,
            // cdoc));
            FileServices.writeFromString(createOutputFile(cdoc), NodeSerializer.toXML(doc));
        }

        createTargetDocumentDatabase(rootDoc);
    }

    private void createTargetDocumentDatabase(RootDoc rootDoc) throws IOException {

        DocumentImpl tdd = new DocumentImpl();
        DocumentTypeImpl docType = new DocumentTypeImpl();
        docType.setName("targetset");
        docType.setSystemId("targetdatabase.dtd");
        tdd.setDoctype(docType);

        ElementImpl targetSet = tdd.createElement("targetset");
        tdd.setDocumentElement(targetSet);

        NodeImpl sitemap = tdd.createElement("sitemap");
        targetSet.appendChild(sitemap);

        ElementImpl mainDir = tdd.createElement("dir");
        mainDir.setAttribute("name", "main");
        sitemap.appendChild(mainDir);

        EntityImpl entity = new EntityImpl("dbdoclet", "target.db");
        docType.addEntity(entity);

        ElementImpl document = tdd.createElement("document");
        document.setFormatType(NodeImpl.FORMAT_CONTENT);
        document.setAttribute("targetdoc", "dbdoclet");
        document.appendChild(entity);
        mainDir.appendChild(document);

        ElementImpl dir = tdd.createElement("dir");
        dir.setAttribute("name", "src");
        sitemap.appendChild(dir);

        for (ClassDoc cdoc : rootDoc.classes()) {

            String tdbPath = getRelativeTargetFile(cdoc).getPath();
            entity = new EntityImpl(cdoc.qualifiedName(), tdbPath);
            docType.addEntity(entity);

            document = tdd.createElement("document");
            document.setFormatType(NodeImpl.FORMAT_CONTENT);
            document.setAttribute("targetdoc", cdoc.qualifiedName());
            document.appendChild(entity);
            dir.appendChild(document);
        }

        String fileName = FileServices.appendFileName(script.getDestinationDirectory(), "olinkdb.xml");
        File file = new File(fileName);
        FileServices.writeFromString(file, NodeSerializer.toXML(tdd));
    }

    private File createOutputFile(ClassDoc cdoc) throws IOException {

        File file = getDocBookFile(cdoc);
        FileServices.createPath(file.getParentFile());
        return file;
    }

    public File getDocBookFile(ClassDoc cdoc) {

        File file = getRelativeDocBookFile(cdoc);
        String fqfn = FileServices.appendFileName(script.getDestinationDirectory(), file.getPath());

        return new File(fqfn);
    }

    public static File getRelativeDocBookFile(ClassDoc cdoc) {

        String fileName = StringServices.replace(cdoc.qualifiedName(), ".", File.separator) + ".xml";
        fileName = FileServices.appendFileName("src", fileName);
        return new File(fileName);
    }

    public static File getRelativeTargetFile(ClassDoc cdoc) {

        String fileName = StringServices.replace(cdoc.qualifiedName(), ".", File.separator) + ".xml";
        fileName = FileServices.appendFileName("targetdb", fileName);
        return new File(fileName);
    }
}
