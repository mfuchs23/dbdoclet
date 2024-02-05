/*
 * ### Copyright (C) 2005-2024 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Member;
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

import com.google.inject.Inject;
import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.InlineTagTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.util.DocTreePath;

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
	private TagManager tagManager;
	@Inject
	private DocBookTagFactory tagFactory;
	@Inject
	private Script script;
	@Inject
	private DocManager docManager;
	
	public NodeImpl transform(Element pkgElem, Element classElem, DocBookElement parent)
			throws DocletException {

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		if (classElem == null) {
			throw new IllegalArgumentException("Parameter elemis null!");
		}

		DocCommentTree commentTree = docManager.getDocCommentTree(classElem);
		if (nonNull(commentTree)) {
			String buffer = docTreeToString(commentTree.getFullBody());
			return transform(pkgElem, buffer, parent);
		}
		
		return null;
	}

	public NodeImpl transform(DocTreePath path, DocTree docTree, DocBookElement parent) throws DocletException {
		ArrayList<DocTree> docTreeList = new ArrayList<>();
		docTreeList.add(docTree);
		return transform(path, docTreeList, parent);
	}
	
	public NodeImpl transform(DocTreePath path, List<? extends DocTree> docTreeList, DocBookElement parent) throws DocletException {

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		StringBuilder buffer = new StringBuilder();
		for (var dt : docTreeList) {
			if (dt instanceof InlineTagTree) {
				buffer.append(tagManager.processInlineTag(path, dt));
			} else if (dt instanceof BlockTagTree) {
				buffer.append(tagManager.processBlockTag(path, dt));
			} else {
				buffer.append(dt.toString());
			}
		}

		HtmlDocBookTrafo transformer = new HtmlDocBookTrafo();
		transformer.setTagFactory(tagFactory);
		
		try {
			transformer.setInputStream(new ByteArrayInputStream(
					buffer.toString().getBytes(script.getTextParameter("javadoc", TrafoConstants.PARAM_ENCODING, "UTF-8"))));
			TrafoResult result = transformer.transform(script);
			
			NodeImpl node = null;
			if (result.isFailed() == false) {
				node = result.getRootNode();
			} else {
				logger.error("Transformation failed!" + Sfv.LSEP + result.toString());
				return null;
			}
			
			appendToParent(parent, node);
			return node;
			
		} catch (Exception e) {
			throw new DocletException(e);
		}

	}

	private String docTreeToString(List<? extends DocTree> docTreeList) {

		StringBuilder buffer = new StringBuilder();

		for (DocTree dtree : docTreeList) {

			if (dtree.getKind() == DocTree.Kind.LINK_PLAIN) {

				LinkTree linkTree = (LinkTree) dtree;

				// String label = referenceManager.createReferenceLabel(link);
				String label = linkTree.getLabel().toString();
				label = HtmlServices.textToHtml(label);

				// reference = referenceManager.findReference(link);

				String comment = "<javadoc:linkplain" + " ref=\"" + linkTree.getReference().toString() + "\""
						+ " name=\"" + label + "\">" + label + "</javadoc:linkplain>";
				buffer.append(comment);

			} else {
				buffer.append(dtree.toString());
			}
		}

		return buffer.toString();
	}

	/**
	 * The method <code>transform</code> transforms the content of a String buffer
	 * into a DocBook tree.
	 * 
	 * @param comment a <code>String</code> value
	 * @param parent  a <code>DocBookElement</code> value
	 * @param skipTo  skip all lines until the the tag <code>skipTo</code> is found.
	 * @return a <code>Element</code> value
	 * 
	 * @exception DocletException if an error occurs
	 */
	public NodeImpl transform(Element pkgElem, String comment, DocBookElement parent) throws DocletException {

		if (comment == null) {
			throw new IllegalArgumentException("Parameter comment is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		try {

			HtmlDocBookTrafo transformer = new HtmlDocBookTrafo();
			transformer.setTagFactory(tagFactory);

			String subPath = StringServices.replace(docManager.getQualifiedName(pkgElem), ".", File.separator);

			if (subPath != null && subPath.trim().length() > 0) {
				script.setVariable(new TextParam(TrafoConstants.VAR_IMAGE_SUBPATH, subPath));
			} else {
				script.unsetVariable(TrafoConstants.VAR_IMAGE_SUBPATH);
			}

			script.getNamespace().findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
					.setParam(new TextParam(TrafoConstants.PARAM_DOCUMENT_ELEMENT, parent.getTagName()));

			transformer.setInputStream(new ByteArrayInputStream(
					comment.getBytes(script.getTextParameter("javadoc", TrafoConstants.PARAM_ENCODING, "UTF-8"))));

			TransformPosition ctx = new TransformPosition(pkgElem);
			script.setTransformPosition(ctx);
			TrafoResult result = transformer.transform(script);

			NodeImpl node = null;

			if (result.isFailed() == false) {
				node = result.getRootNode();
			} else {
				logger.error("Transformation failed!" + Sfv.LSEP + result.toString());
			}

			if (isNull(node)) {
				logger.error(result.toString());
				throw new NullPointerException("Transformation failed. Root element is null!");
			}

			appendToParent(parent, node);
			return node;

		} catch (Exception oops) {

			ExceptionHandler.handleException(oops);
		}

		return null;
	}

	private void appendToParent(DocBookElement parent, NodeImpl node) throws Exception {

		if (node instanceof DocumentFragment) {

			NodeList childList = node.getChildNodes();

			for (int i = 0; i < childList.getLength(); i++) {

				NodeImpl child = (NodeImpl) childList.item(i);

				if (child instanceof DocBookElement) {

					DocBookElement childElem = (DocBookElement) child;
					Node parentElem = parent;

					if ((childElem instanceof Para || childElem.isSection()) && parentElem instanceof Para) {
						parentElem = parentElem.getParentNode();
					}

					while (parentElem != null && childElem.isValidParent(null, parentElem) == false) {
						parentElem = parentElem.getParentNode();
					}

					if (parentElem != null) {
						parentElem.appendChild(child);
					} else {
						logger.error(String.format("Invalid child %s for parent %s and possible ancestors.",
								child.getNodeName(), parent.getNodeName()));
						parent.appendChild(child);
					}

				} else {
					parent.appendChild(child);
				}
			}

		} else {
			parent.appendChild(node);
		}

		node.traverse(new HyphenationVisitor());

	}

	public void transform(PackageElement elem, DocBookElement parent) throws DocletException {
		
		DocCommentTree docCommentTree = docManager.getDocCommentTree(elem);
		if (nonNull(docCommentTree)) {
			transform(docManager.getDocTreePath(elem), parent);
		}
	}

	public void transform(DocTreePath docTreePath, DocBookElement parent) throws DocletException {
		if (nonNull(docTreePath.getDocComment())) {
			transform(docTreePath, docTreePath.getDocComment().getFullBody(), parent);
		}
	}

	public void transform(TypeElement elem, DocBookElement parent) throws DocletException {
		DocCommentTree docCommentTree = docManager.getDocCommentTree(elem);
		if (nonNull(docCommentTree)) {
			transform(docManager.getDocTreePath(elem), parent);
		}
	}
}
