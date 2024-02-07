package org.dbdoclet.doclet.docbook;

import java.util.TreeMap;
import java.util.logging.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.doc.DocletException;
import org.dbdoclet.doclet.doc.ReferenceManager;
import org.dbdoclet.doclet.doc.TagManager;
import org.dbdoclet.xiphias.HtmlServices;

import com.google.inject.Inject;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.InlineTagTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.util.DocTreePath;

public class DocBookTagManager extends TagManager {

	private static Logger logger = Logger.getLogger(DocBookTagManager.class.getName());

	@Inject
	private ReferenceManager referenceManager;
	@Inject
	private DbdScript script;
	
	public String processInlineTag(DocTreePath path, DocTree docTree) throws DocletException {

		String comment = "";
		if (docTree instanceof InlineTagTree == false) {
			return comment;
		}

		InlineTagTree tag = (InlineTagTree) docTree;
		Kind kind = tag.getKind();
		String name = tag.getTagName();

		logger.fine("name=" + name + ", kind=" + kind + ", text=" + tag.toString());

		if (kind.equals(Kind.CODE)) {
			logger.fine("tag=" + tag.toString());
			String html = HtmlServices.textToHtml(((LiteralTree) tag).getBody().toString());
			comment = "<javadoc:code>" + html + "</javadoc:code>";
			return comment;
		}

		if (kind.equals(Kind.LITERAL)) {

			logger.fine("tag=" + tag.toString());
			String html = HtmlServices.textToHtml(((LiteralTree) tag).getBody().toString());
			comment = "<javadoc:literal>" + html + "</javadoc:literal>";
			return comment;
		}

		if (kind.equals(Kind.LINK) || kind.equals(Kind.LINK_PLAIN)) {
			comment = processInlineTag(path, (LinkTree) docTree);
			return comment;
		}

		comment += docManager.getCommentText(tag);

		return comment;
	}

	public String processInlineTag(DocTreePath path, InheritDocTree tag) throws DocletException {

		String comment = docManager.getCommentText(tag);
		return comment;
	}

	public String processInlineTag(DocTreePath path, LinkTree tag) throws DocletException {

		String comment = "";

		LinkTree link = (LinkTree) tag;
		String tagName = link.getTagName();

		String label = referenceManager.createReferenceLabel(link);
		label = HtmlServices.textToHtml(label);

		String reference = referenceManager.findReference(path, link);
		if ((reference != null) && (reference.length() > 0)) {
			if (tagName.equalsIgnoreCase("linkplain")) {
				comment += "<javadoc:linkplain" + " ref=\"" + reference + "\"" + " name=\"" + label + "\">"
						+ label + "</javadoc:linkplain>";
			} else {
				comment += "<javadoc:link" + " ref=\"" + reference + "\"" + " name=\"" + label + "\">"
						+ label + "</javadoc:link>";
			}

		} else if ((reference != null) && reference.startsWith("<a")) {
			comment = label;
		} else {
			comment += label;
		}
		return comment;
	}

	public String processInlineTag(DocTreePath path, ValueTree tag) throws DocletException {

		String comment = docManager.getCommentText(tag);
		String value = "Tag(@value): UnknownValueException!";

		Element elem = docManager.getDocletEnvironment().getDocTrees().getElement(path);
		if (comment.isBlank() && elem instanceof VariableElement) {
			VariableElement fdoc = (VariableElement) elem;
			value = fdoc.getConstantValue().toString();
		}

		if (!comment.isBlank() && elem instanceof ExecutableElement) {

			logger.fine("constantFieldMap.size()=" + getConstantFieldMap().size());
			String pkgName = getSeeNamePackage(elem, comment);
			logger.fine("pkgName=" + pkgName);

			TreeMap<String, TreeMap<String, VariableElement>> classMap = getConstantFieldMap().get(pkgName);

			if (classMap != null) {

				String className = getSeeNameClass(elem, comment);
				logger.fine("className=" + className);

				TreeMap<String, VariableElement> fieldMap = classMap.get(className);

				if (fieldMap != null) {

					String fieldName = getSeeNameMember(comment);
					logger.fine("fieldName=" + fieldName);

					VariableElement fdoc = fieldMap.get(fieldName);

					if (fdoc != null) {
						value = fdoc.getConstantValue().toString();
					}
				}
			}
		}

		comment = "<javadoc:value>" + HtmlServices.textToHtml(value) + "</javadoc:value>";
		logger.fine(comment);

		return comment;
	}

	public boolean showTag(Element elem, DocTree.Kind kind) {

		if (Kind.AUTHOR == kind && !script.isCreateAuthorInfoEnabled()) {
			return false;
		}

		if (Kind.VERSION == kind && script.isCreateVersionInfoEnabled() == false) {
			return false;
		}

		if (Kind.SINCE == kind && script.isCreateSinceInfoEnabled() == false) {
			return false;
		}

		if (Kind.SEE == kind && script.isCreateSeeAlsoInfoEnabled() == false) {
			return false;
		}

		if (Kind.PARAM == kind && script.isCreateParameterInfoEnabled() == false) {
			return false;
		}

		if (Kind.RETURN == kind && script.isCreateParameterInfoEnabled() == false) {
			return false;
		}

		return true;
	}
}
