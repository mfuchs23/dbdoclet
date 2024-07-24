package org.dbdoclet.doclet8.docbook;

import java.util.ArrayList;
import java.util.TreeMap;

import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.xiphias.dom.DocumentTypeImpl;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

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

	public static final Tag findComment(Tag[] tags, String... tagNames) {

		for (int i = 0; i < tags.length; i++) {
			for (String tagName : tagNames) {
				if (tags[i].kind().equals(tagName)) {
					return tags[i];
				}
			}
		}

		return null;
	}

	public static final Tag[] findComments(Tag[] tags, String... tagNames) {

		ArrayList<Tag> list = new ArrayList<Tag>();

		for (int i = 0; i < tags.length; i++) {
			for (String tagName : tagNames) {
				if (tags[i].kind().equals(tagName)) {
					list.add(tags[i]);
				}
			}
		}

		Tag[] rtags = new Tag[list.size()];

		for (int i = 0; i < list.size(); i++)
			rtags[i] = list.get(i);

		return rtags;
	}

	public static final Tag findReturnComment(Tag[] tags) {

		for (int i = 0; i < tags.length; i++) {

			if (tags[i].kind().equals("@return")) {
				return tags[i];
			}
		}

		return null;
	}

	public static ArrayList<Doc> createDocList(
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap) {

		ArrayList<Doc> docList = new ArrayList<Doc>();

		for (String pkgName : pkgMap.keySet()) {

			TreeMap<String, ClassDoc> classMap = pkgMap.get(pkgName);

			for (String className : classMap.keySet()) {

				ClassDoc cdoc = classMap.get(className);

				if (cdoc != null) {

					docList.add(cdoc);
					addClassDoc(docList, cdoc);
				}
			}
		}

		return docList;
	}

	private static void addClassDoc(ArrayList<Doc> docList, ClassDoc cdoc) {

		for (Doc doc : cdoc.constructors()) {
			docList.add(doc);
		}

		for (Doc doc : cdoc.fields()) {
			docList.add(doc);

		}

		for (Doc doc : cdoc.methods()) {
			docList.add(doc);
		}

		if (cdoc.isAnnotationType()) {

			AnnotationTypeDoc atd = (AnnotationTypeDoc) cdoc;

			if (atd.elements() != null) {
				for (AnnotationTypeElementDoc ated : atd.elements()) {
					docList.add(ated);
				}
			}
		}

		for (ClassDoc doc : cdoc.innerClasses()) {
			addClassDoc(docList, doc);
		}
	}
}
