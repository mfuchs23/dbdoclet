package org.dbdoclet.doclet.docbook;

import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.xiphias.dom.DocumentTypeImpl;
import javax.lang.model.element.Element;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;

public class DbdServices {

	public static void appendDoctype(DocBookDocument doc, String tagName) {

		DocumentTypeImpl doctype = new DocumentTypeImpl();
		doctype.setName(tagName);
		doctype.setPublicId("-//OASIS//DTD DocBook XML V4.5//EN");
		doctype.setSystemId("http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd");
		doc.appendChild(doctype);
	}

	public static void addNamespace(DocBookElement elem) {

		elem.setAttribute("xmlns", "http://docbook.org/ns/docbook");
		elem.setAttribute("xmlns:xl", "http://www.w3.org/1999/xlink");
		elem.setAttribute("version", "5.0");
	}
}
