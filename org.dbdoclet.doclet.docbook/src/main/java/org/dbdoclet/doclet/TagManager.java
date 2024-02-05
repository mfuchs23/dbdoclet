/*
 * ### Copyright (C) 2001-20 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.docbook.DbdScript;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.HtmlServices;

import com.google.inject.Inject;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.InlineTagTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.util.DocTreePath;

class CompiledTag {

	private String label = "";

	public CompiledTag(String name, String flags, String label) {

		if (name == null) {
			throw new IllegalArgumentException("The argument name must not be null!");
		}

		if (label != null) {
			this.label = label;
		} else {
			label = StringServices.capFirstLetter(name);
		}
	}

	public String getLabel() {
		return label;
	}
}

/**
 * The class <code>TagManager</code> is reponsible for handling the javadoc
 * tags.
 * 
 * It also handles tags, which are defined on the command line with the -tag
 * option.
 * 
 * @author <a href="mailto:michael.fuchs@unico-group.com">Michael Fuchs</a>
 * @version 1.0
 */
public class TagManager {

	private static Logger logger = Logger.getLogger(TagManager.class.getName());

	private TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>> constantFieldMap;
	private DocManager docManager;

	@Inject
	private ReferenceManager referenceManager;
	@Inject
	private DbdScript script;

	private LinkedHashMap<String, CompiledTag> tagMap = new LinkedHashMap<String, CompiledTag>();

	public TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>> createConstantFieldMap(
			TreeMap<String, TreeMap<String, TypeElement>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException("The argument pkgMap must not be null!");
		}

		TypeElement cdoc;
		String className;
		String pkgName;
		TreeMap<String, TreeMap<String, VariableElement>> constantClassMap;
		TreeMap<String, VariableElement> constantFieldMap;
		TreeMap<String, TypeElement> classMap;
		TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>> constantMap = new TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>>();

		for (Iterator<String> iterator = pkgMap.keySet().iterator(); iterator.hasNext();) {

			pkgName = iterator.next();
			classMap = pkgMap.get(pkgName);

			if (classMap != null) {

				for (Iterator<String> classIterator = classMap.keySet().iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					cdoc = classMap.get(className);

					constantClassMap = constantMap.get(pkgName);

					if (constantClassMap == null) {

						constantClassMap = new TreeMap<String, TreeMap<String, VariableElement>>();
						constantMap.put(pkgName, constantClassMap);
					}

					Set<VariableElement> fields = docManager.getFieldElements(cdoc);

					for (var field : fields) {

						if (docManager.isPublic(field) && docManager.isStatic(field) && docManager.isFinal(field)
								&& field.getConstantValue() != null) {

							constantFieldMap = constantClassMap.get(docManager.getName(cdoc));

							if (constantFieldMap == null) {

								constantFieldMap = new TreeMap<String, VariableElement>();
								constantClassMap.put(docManager.getName(cdoc), constantFieldMap);
							}

							constantFieldMap.put(docManager.getName(cdoc), field);
						}
					}
				}
			}
		}

		return constantMap;
	}

	public void createTagMap(TreeMap<String, TreeMap<String, TypeElement>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException("The argument pkgMap must not be null!");
		}

		constantFieldMap = createConstantFieldMap(pkgMap);

		initTags();

		ArrayList<String> list = script.getTagList();

		if (list == null) {
			return;
		}

		String value;

		logger.info("Found " + list.size() + " tag script.");

		StringTokenizer stz;
		String token;
		String tagName;
		String tagFlags;
		String tagLabel;
		int index;

		for (Iterator<String> i = list.iterator(); i.hasNext();) {

			value = i.next();

			if ((value == null) || (value.trim().length() == 0)) {

				continue;
			}

			logger.info("Compiling tag " + value + ".");

			stz = new StringTokenizer(value, ":");

			tagName = "";
			tagFlags = "";
			tagLabel = "";
			index = 0;

			while (stz.hasMoreTokens()) {

				token = stz.nextToken();

				switch (index) {

				case 0:
					tagName = token;

					break;

				case 1:
					tagFlags = token;

					break;

				case 2:
					tagLabel = token;

					break;

				default:
					tagLabel += (":" + token);

					break;
				}

				index++;
			}

			if ((tagName != null) && (tagName.length() > 0)) {

				CompiledTag tag = new CompiledTag(tagName, tagFlags, tagLabel);
				tagMap.put(tagName, tag);

			} else {

				logger.warning("Invalid tag name '" + tagName + "'! Value was '" + value + "'.");
			}
		}
	}

	public List<AuthorTree> findAuthorTags(Element elem) {
		return findBlockTags(elem, () -> new ArrayList<AuthorTree>(), Kind.AUTHOR);
	}

	public <T extends BlockTagTree> List<T> findBlockTags(Element elem, Supplier<List<T>> supplier,
			DocTree.Kind... kind) {

		if (isNull(elem)) {
			throw new IllegalArgumentException("The argument elem must not be null!");
		}

		if (isNull(kind)) {
			throw new IllegalArgumentException("The argument kind must not be null!");
		}

		DocCommentTree dctree = docManager.getDocCommentTree(elem);
		if (isNull(dctree)) {
			return Collections.emptyList();
		}

		@SuppressWarnings("unchecked")
		List<T> list = dctree.getBlockTags().stream().filter(t -> Arrays.asList(kind).contains(t.getKind()))
				.collect(supplier, (l, e) -> l.add((T) e), (r1, r2) -> r1.addAll(r2));
		
		if (isNull(list)) {
			return Collections.emptyList();
		}
		
		return list;
	}

	public DocTree findDeprecatedTag(Element elem) {

		List<DeprecatedTree> deprecatedTags = findBlockTags(elem, () -> new ArrayList<DeprecatedTree>(), Kind.DEPRECATED);
		if (isNull(deprecatedTags)) {
			return null;
		}

		if (deprecatedTags.size() > 1) {
			logger.warning(String.format("More than one deprecated tag in elem %s.", docManager.getQualifiedName(elem)));
		}

		if (nonNull(deprecatedTags) && !deprecatedTags.isEmpty()) {
			return deprecatedTags.get(0);
		}

		return null;
	}

	public List<ParamTree> findParamTags(Element elem) {
		return findBlockTags(elem, () -> new ArrayList<ParamTree>(), Kind.PARAM);
	}

	public ReturnTree findReturnTag(Element elem) {

		List<ReturnTree> returnTags = findBlockTags(elem, () -> new ArrayList<ReturnTree>(), Kind.RETURN);

		if (isNull(returnTags)) {
			return null;
		}

		if (returnTags.size() > 1) {
			logger.warning(String.format("More than one return tag in elem %s.", docManager.getQualifiedName(elem)));
		}

		if (nonNull(returnTags) && !returnTags.isEmpty()) {
			return returnTags.get(0);
		}

		return null;
	}

	public List<SeeTree> findSeeTags(Element elem) {
		return findBlockTags(elem, () -> new ArrayList<SeeTree>(), Kind.SEE);
	}

	public List<SerialFieldTree> findSerialFieldTags(Element elem) {
		return findBlockTags(elem, () -> new ArrayList<SerialFieldTree>(), Kind.SERIAL_FIELD);
	}

	public List<ThrowsTree> findThrowsTags(Element elem) {
		return findBlockTags(elem, () -> new ArrayList<ThrowsTree>(), Kind.EXCEPTION, Kind.THROWS);
	}

	public TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>> getConstantFieldMap() {
		return constantFieldMap;
	}

	public String getSeeNameClass(Element doc, String name) {

		if (name == null) {
			return "";
		}

		int index = name.lastIndexOf('#');

		if (index == -1) {
			return "";
		}

		String className = name.substring(0, index);

		index = className.lastIndexOf('.');

		if (index == -1) {

			if (className.length() > 0) {
				return className;
			} else {
				TypeElement cdoc = docManager.getContainingClass(doc);
				if (cdoc != null) {
					return docManager.getName(cdoc);
				} else {
					return "";
				}
			}
		}

		if (index < className.length() - 1) {
			className = className.substring(index + 1);
		} else {
			className = "";
		}

		return className;
	}

	public String getSeeNameMember(String name) {

		if (name == null) {
			return "";
		}

		int index = name.lastIndexOf('#');

		if (index == -1 || index == (name.length() - 1)) {
			return "";
		}

		String member = name.substring(index + 1);
		return member;
	}

	public String getSeeNamePackage(Element doc, String name) {

		if (doc == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		if (name == null) {
			return "";
		}

		int index = name.lastIndexOf('#');

		if (index == -1) {
			return "";
		}

		String pkgName = name.substring(0, index);

		index = pkgName.lastIndexOf('.');

		if (index == -1) {

			TypeElement cdoc = null;

			if (docManager.isClassOrInterface(doc)) {
				cdoc = (TypeElement) doc;
			} else {
				cdoc = docManager.getContainingClass(doc);
			}

			PackageElement pdoc = docManager.containingPackage(cdoc);
			if (pdoc != null) {
				return docManager.getName(pdoc);
			} else {
				return "";
			}
		}

		pkgName = pkgName.substring(0, index);
		return pkgName;
	}

	public String getTagLabel(DocTree.Kind kind, ResourceBundle res) {

		if (isNull(kind)) {
			throw new IllegalArgumentException(" The argument kind must not be null!");
		}

		switch (kind) {
		case AUTHOR:
			return ResourceServices.getString(res, "C_AUTHOR");
		case SINCE:
			return ResourceServices.getString(res, "C_SINCE");
		case VERSION:
			return ResourceServices.getString(res, "C_VERSION");
		case SEE:
			return ResourceServices.getString(res, "C_SEE_ALSO");
		case SERIAL:
			return ResourceServices.getString(res, "C_SERIAL");
		case SERIAL_DATA:
			return ResourceServices.getString(res, "C_SERIAL_DATA");
		case SERIAL_FIELD:
		default:
			break;
		}

		String label = kind.tagName;
		if (nonNull(label) && !label.isBlank()) {
			label = label.substring(1);
		} else {
			label = ResourceServices.getString(res, "C_UNKNOWN");
		}

		CompiledTag tag = tagMap.get(label);
		if (nonNull(tag)) {
			return tag.getLabel();
		}

		return StringServices.capFirstLetter(label);
	}

	private void initTags() {

		tagMap = new LinkedHashMap<String, CompiledTag>();

		CompiledTag tag;
		tag = new CompiledTag("@author", "X", null);
		tagMap.put("@author", tag);
	}

	public boolean isMetaTag(DocTree.Kind kind) {

		switch (kind) {
		case DEPRECATED:
		case EXCEPTION:
		case PARAM:
		case RETURN:
		case SERIAL:
		case SERIAL_DATA:
		case SERIAL_FIELD:
		case THROWS:
			return false;
		default:
			return true;
		}
	}

	public String processBlockTag(DocTreePath path, DocTree docTree) {
		
		String comment = "";
		if (docTree instanceof BlockTagTree == false) {
			return comment;
		}

		BlockTagTree tag = (BlockTagTree) docTree;
		Kind kind = tag.getKind();
		String name = tag.getTagName();
		logger.fine("name=" + name + ", kind=" + kind + ", text=" + tag.toString());

		switch (kind) {
		case AUTHOR:
			comment = ((AuthorTree) tag).getName().toString();
			break;
		case SINCE:
			comment = ((SinceTree) tag).getBody().toString();
			break;
		default:
			comment = docTree.toString();
		}

		return comment;
	}

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

	public void setDocManager(DocManager docManager) {
		this.docManager = docManager;
	}

	public boolean showTag(Element elem, DocTree.Kind kind) {

		if (Kind.AUTHOR == kind && (!script.isCreateAuthorInfoEnabled() || !docManager.isClassOrInterface(elem))) {
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
