package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;

import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.Chapter;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Formalpara;
import org.dbdoclet.tag.docbook.Programlisting;
import org.dbdoclet.xiphias.NodeSerializer;
import org.dbdoclet.xiphias.dom.DocumentImpl;
import org.dbdoclet.xiphias.dom.DocumentTypeImpl;
import org.dbdoclet.xiphias.dom.ElementImpl;
import org.dbdoclet.xiphias.dom.EntityImpl;
import org.dbdoclet.xiphias.dom.NodeImpl;

public class LinkSourceManager {

	@Inject DbdScript script;

    public void createDocBook(DocManager docManager) throws IOException {

        File destDir = script.getDestinationDirectory();
        FileServices.createPath(destDir);

        DocumentImpl tdd = new DocumentImpl();
        ElementImpl targetSet = tdd.createElement("targetset");
        tdd.setDocumentElement(targetSet);
        
        for (TypeElement typeElem : docManager.getTypeElements()) {

        	FileObject sourceFileObject = docManager.getFileObject(typeElem);

            DocBookDocument doc = new DocBookDocument();
            DocBookTagFactory dbf = new DocBookTagFactory();

            Chapter chapter = dbf.createChapter();
            chapter.setDocument(doc);
            doc.setDocumentElement(chapter);
            chapter.appendChild(dbf.createTitle(typeElem.getQualifiedName().toString()));

            Formalpara formalPara = dbf.createFormalpara();
            formalPara.setDocument(doc);
            formalPara.setId("listing");
            chapter.appendChild(formalPara);

            Programlisting programlisting = dbf.createProgramlisting();
            programlisting.setAttribute("linenumbering", "numbered");
            programlisting.setDocument(doc);
            formalPara.appendChild(programlisting);

            programlisting.appendChild(FileServices.readToString(sourceFileObject.openInputStream()));
            FileServices.writeFromString(createOutputFile(typeElem), new NodeSerializer().toXML(doc));
        }

        createTargetDocumentDatabase(docManager);
    }

    private void createTargetDocumentDatabase(DocManager docManager) throws IOException {

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

        for (TypeElement typeElem : docManager.getTypeElements()) {

            String tdbPath = getRelativeTargetFile(typeElem).getPath();
            entity = new EntityImpl(typeElem.getQualifiedName().toString(), tdbPath);
            docType.addEntity(entity);

            document = tdd.createElement("document");
            document.setFormatType(NodeImpl.FORMAT_CONTENT);
            document.setAttribute("targetdoc", typeElem.getQualifiedName().toString());
            document.appendChild(entity);
            dir.appendChild(document);
        }

        String fileName = FileServices.appendFileName(script.getDestinationDirectory(), "olinkdb.xml");
        File file = new File(fileName);
        FileServices.writeFromString(file, new NodeSerializer().toXML(tdd));
    }

    private File createOutputFile(TypeElement typeElem) throws IOException {

        File file = getDocBookFile(typeElem);
        FileServices.createPath(file.getParentFile());
        return file;
    }

    public File getDocBookFile(TypeElement typeElem) {

        File file = getRelativeDocBookFile(typeElem);
        String fqfn = FileServices.appendFileName(script.getDestinationDirectory(), file.getPath());

        return new File(fqfn);
    }

    public static File getRelativeDocBookFile(TypeElement typeElem) {

        String fileName = StringServices.replace(typeElem.getQualifiedName().toString(), ".", File.separator) + ".xml";
        fileName = FileServices.appendFileName("src", fileName);
        return new File(fileName);
    }

    public static File getRelativeTargetFile(TypeElement typeElem) {

        String fileName = StringServices.replace(typeElem.getQualifiedName().toString(), ".", File.separator) + ".xml";
        fileName = FileServices.appendFileName("targetdb", fileName);
        return new File(fileName);
    }
}
