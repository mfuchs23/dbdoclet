/*
 * ### Copyright (C) 2005-2009 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.inject.Inject;
import javax.lang.model.element.PackageElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.trafo.TrafoConstants;
import org.dbdoclet.trafo.TrafoResult;
import org.dbdoclet.trafo.html.docbook.HtmlDocBookTrafo;
import org.dbdoclet.trafo.param.TextParam;
import org.dbdoclet.trafo.script.Script;
import org.dbdoclet.xiphias.HtmlServices;
import org.dbdoclet.xiphias.dom.NodeImpl;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;

/**
 * The class <code>DbdTransformer</code> transforms the information created by
 * the javadoc tool to DocBook trees.
 * 
 * @author <a href ="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class DbdTransformer {

	private static Log logger = LogFactory.getLog(DbdTransformer.class);

	@Inject
	TagManager tagManager;
	@Inject
	DocBookTagFactory tagFactory;
	@Inject
	Script script;

	/**
	 * The method <code>transform</code> transforms the documentation of a
	 * javadoc tag.
	 * 
	 * @param classDoc
	 *            a <code>Tag</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 * @return a <code>Element</code> value
	 * @exception DocletException
	 *                if an error occurs
	 */
	public NodeImpl transform(PackageElement pkgElem, DocCommentTree dcTree, DocBookElement parent)
			throws DocletException {

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (dcTree  == null) {
			return null;
		}

		StringBuilder buffer = new StringBuilder();
		
		if (nonNull(dcTree.getFirstSentence())) {
			buffer.append(dcTree.getFirstSentence());
			buffer.append(String.format("%n"));
		}
		
		for (DocTree dtree : dcTree.getBody()) {
			
			if (dtree.getKind() == DocTree.Kind.LINK_PLAIN) {
			
				LinkTree linkTree = (LinkTree) dtree;
				
				// String label = referenceManager.createReferenceLabel(link);
				String label = linkTree.getLabel().toString();
				label = HtmlServices.textToHtml(label);

				// reference = referenceManager.findReference(link);
			
				String comment = "<javadoc:linkplain" + " ref=\"" + linkTree.getReference().toString()
							+ "\"" + " name=\"" + label + "\">" + label
							+ "</javadoc:linkplain>";
				buffer.append(comment);

			} else {
				buffer.append(dtree.toString());
			}
		}
		
		return transform(pkgElem, buffer.toString(), parent);
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

		if (tags.length == 0) {
			return null;
		}

		String comment = "";

		for (int i = 0; i < tags.length; i++) {
			comment += tagManager.processTag(tags[i]);
		}

		// return transform(tags[0].holder(), comment, parent);
		return null;
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
	public NodeImpl transform(PackageElement pkgElem, String comment, DocBookElement parent)
			throws DocletException {

		if (comment == null) {
			throw new IllegalArgumentException("Parameter comment is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		try {

			HtmlDocBookTrafo transformer = new HtmlDocBookTrafo();
			transformer.setTagFactory(tagFactory);

			String subPath = StringServices.replace(pkgElem.getQualifiedName().toString(), ".",
					File.separator);

			if (subPath != null && subPath.trim().length() > 0) {
				script.setVariable(new TextParam(
						TrafoConstants.VAR_IMAGE_SUBPATH, subPath));
			} else {
				script.unsetVariable(TrafoConstants.VAR_IMAGE_SUBPATH);					
			}

			script.getNamespace()
			.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
			.setParam(new TextParam(TrafoConstants.PARAM_DOCUMENT_ELEMENT,
					parent.getTagName()));

			transformer.setInputStream(new ByteArrayInputStream(comment
					.getBytes(script.getTextParameter("javadoc",
							TrafoConstants.PARAM_ENCODING, "UTF-8"))));
			
			// TransformPosition ctx = new TransformPosition(doc);
			// script.setTransformPosition(ctx);
			TrafoResult result = transformer.transform(script);

			NodeImpl node = null;

			if (result.isFailed() == false) {
				node = result.getRootNode();
			} else {
				logger.error("Transformation failed!" + Sfv.LSEP
						+ result.toString());
			}

			if (node != null) {

				if (node instanceof DocumentFragment) {

					NodeList childList = node.getChildNodes();

					for (int i = 0; i < childList.getLength(); i++) {

						NodeImpl child = (NodeImpl) childList.item(i);

						if (child instanceof DocBookElement) {

							DocBookElement childElem = (DocBookElement) child;

							if (parent instanceof Para == false && childElem.isInline()) {

								Para para = tagFactory.createPara();
								parent.appendChild(para);
								parent = para;
							}
							
							Node parentElem = parent;

							if ((childElem instanceof Para || childElem.isSection()) && parentElem instanceof Para) {
								parentElem = parentElem.getParentNode();
							}
							
							while (parentElem != null
									&& childElem.isValidParent(null, parentElem) == false) {
								parentElem = parentElem.getParentNode();
							}

							if (parentElem != null) {
								parentElem.appendChild(child);
							} else {
								logger.error(String
										.format("Invalid child %s for parent %s and possible ancestors.",
												child.getNodeName(),
												parent.getNodeName()));
								parent.appendChild(child);
							}

						} else if (child instanceof Text) {
							
							if (parent instanceof Para == false) {
								
								Para para = tagFactory.createPara();
								parent.appendChild(para);
								para.appendChild(child);
								parent = para;
							
							} else {
								parent.appendChild(child);								
							}
							
						} else {
							parent.appendChild(child);
						}
					}

				} else {
					parent.appendChild(node);
				}

			} else {
				logger.error(result.toString());
				throw new NullPointerException(
						"Transformation failed. Root element is null!");
			}

			node.traverse(new HyphenationVisitor());
			return node;

		} catch (Exception oops) {

			ExceptionHandler.handleException(oops);
		}

		return null;
	}

	public void transform(Doc holder, String exceptionName, DocBookElement parent) {
	}
}
