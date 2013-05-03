/*
 * ### Copyright (C) 2005-2009 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import java.io.File;

import javax.inject.Inject;

import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.html.HtmlElement;
import org.dbdoclet.trafo.html.docbook.DocumentElementType;
import org.dbdoclet.trafo.html.docbook.HtmlDocBookTrafo;
import org.dbdoclet.trafo.internal.html.docbook.DbtConstants;
import org.dbdoclet.trafo.param.TextParam;
import org.dbdoclet.trafo.script.Script;
import org.dbdoclet.xiphias.dom.NodeImpl;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;

/**
 * The class <code>DbdTransformer</code> transforms the information created by
 * the javadoc tool to DocBook trees.
 * 
 * @author <a href ="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class DbdTransformer {

	@Inject
	TagManager tagManager;

	@Inject
	DocBookTagFactory tagFactory;

	@Inject
	DocletContext context;

	@Inject
	Script script;

	/**
	 * The method <code>transform</code> transforms the documentation of a
	 * javadoc tag.
	 * 
	 * @param tag
	 *            a <code>Tag</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 * @return a <code>Element</code> value
	 * @exception DocletException
	 *                if an error occurs
	 */
	public NodeImpl transform(Tag tag, DocBookElement parent)
			throws DocletException {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (context == null) {
			throw new IllegalArgumentException("Parameter context is null!");
		}

		Tag[] tags = tag.inlineTags();

		return transform(tags, parent);
	}

	/**
	 * The method <code>transform</code> transforms the documentation of java
	 * language constructs (class, package, method, ...), which are represented
	 * by a Doc object.
	 * 
	 * @param doc
	 *            a <code>Doc</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 * @return a <code>Element</code> value
	 * @exception DocletException
	 *                if an error occurs
	 */
	public NodeImpl transform(Doc doc, DocBookElement parent)
			throws DocletException {

		if (doc == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (context == null) {
			throw new IllegalArgumentException("Parameter context is null!");
		}

		Tag[] tags = doc.inlineTags();

		return transform(tags, parent);
	}

	/**
	 * The method <code>transform</code> transforms the documentation of an
	 * array of javadoc tag.
	 * 
	 * @param tags
	 *            a <code>Tag[]</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 * @return a <code>Element</code> value
	 * @exception DocletException
	 *                if an error occurs
	 */
	public NodeImpl transform(Tag[] tags, DocBookElement parent)
			throws DocletException {

		if (tags == null) {
			throw new IllegalArgumentException("Parameter tags is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (context == null) {
			throw new IllegalArgumentException("Parameter context is null!");
		}

		String comment = "";

		for (int i = 0; i < tags.length; i++) {
			comment += tagManager.handleInlineTag(tags[i]);
			// System.out.println("comment=" + comment);
		}

		return transform(comment, parent);
	}

	/**
	 * The method <code>transform</code> transforms the content of a String
	 * buffer into a DocBook tree.
	 * 
	 * @param comment
	 *            a <code>String</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 * @param skipTo
	 *            skip all lines until the the tag <code>skipTo</code> is found.
	 * @return a <code>Element</code> value
	 * 
	 * @exception DocletException
	 *                if an error occurs
	 */
	public NodeImpl transform(String comment, DocBookElement parent) throws DocletException {

		if (comment == null) {
			throw new IllegalArgumentException("Parameter comment is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (context == null) {
			throw new IllegalArgumentException("Parameter context is null!");
		}

		try {

			HtmlDocBookTrafo transformer = new HtmlDocBookTrafo();
			transformer.setTagFactory(tagFactory);

			script.selectSection(DbtConstants.SECTION_DOCBOOK);

			PackageDoc pkgDoc = context.getPackageDoc();
			ClassDoc classDoc = context.getClassDoc();

			String path;
			String str;

			if (classDoc != null) {

				pkgDoc = classDoc.containingPackage();
			}

			if (pkgDoc != null) {

				path = script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
						DbtConstants.PARAM_IMAGE_PATH,
						DbtConstants.DEFAULT_IMAGE_PATH);

				str = StringServices
						.replace(pkgDoc.name(), ".", File.separator);

				if ((str != null) && (str.length() > 0)) {
					path = FileServices.appendPath(path, str);
				}

				script.setVariable(new TextParam(
						DbtConstants.PARAM_IMAGE_PATH, path));
			}

			if (context.isOverview()) {
				script.setTextParameter(
						DbtConstants.PARAM_DOCUMENT_ELEMENT,
						DocumentElementType.OVERVIEW
								.toString());
			} else {
				script.setTextParameter(
						DbtConstants.PARAM_DOCUMENT_ELEMENT,
						DocumentElementType.PARAGRAPH
								.toString());
			}

			NodeImpl elem = transformer.transform(script, comment, parent);

			if (elem == null) {

				throw new NullPointerException(
						"Transformation failed. Root element is null!");
			}

			return elem;

		} catch (Exception oops) {

			ExceptionHandler.handleException(context, oops);
		}

		return null;
	}
}
