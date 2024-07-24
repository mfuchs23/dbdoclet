/*
 * ### Copyright (C) 2001-2007 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.google.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet8.docbook.DbdScript;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.HtmlServices;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

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

	private static Log logger = LogFactory.getLog(TagManager.class);

	private LinkedHashMap<String, CompiledTag> tagMap = new LinkedHashMap<String, CompiledTag>();
	private TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>> constantFieldMap;

	@Inject
	private ReferenceManager referenceManager;
	@Inject
	private DbdScript script;

	public static TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>> createConstantFieldMap(
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException(
					"The argument pkgMap must not be null!");
		}

		ClassDoc cdoc;
		String className;
		String pkgName;
		TreeMap<String, TreeMap<String, FieldDoc>> constantClassMap;
		TreeMap<String, FieldDoc> constantFieldMap;
		TreeMap<String, ClassDoc> classMap;
		TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>> constantMap = new TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>>();

		for (Iterator<String> iterator = pkgMap.keySet().iterator(); iterator
				.hasNext();) {

			pkgName = iterator.next();
			classMap = pkgMap.get(pkgName);

			if (classMap != null) {

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					cdoc = classMap.get(className);

					constantClassMap = constantMap.get(pkgName);

					if (constantClassMap == null) {

						constantClassMap = new TreeMap<String, TreeMap<String, FieldDoc>>();
						constantMap.put(pkgName, constantClassMap);
					}

					FieldDoc[] fields = cdoc.fields(false);

					for (int j = 0; j < fields.length; j++) {

						if (fields[j].isPublic() && fields[j].isStatic()
								&& fields[j].isFinal()
								&& fields[j].constantValue() != null) {

							constantFieldMap = constantClassMap
									.get(cdoc.name());

							if (constantFieldMap == null) {

								constantFieldMap = new TreeMap<String, FieldDoc>();
								constantClassMap.put(cdoc.name(),
										constantFieldMap);
							}

							constantFieldMap.put(fields[j].name(), fields[j]);
						}
					}
				}
			}
		}

		return constantMap;
	}

	public void createTagMap(TreeMap<String, TreeMap<String, ClassDoc>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException(
					"The argument pkgMap must not be null!");
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

				logger.error("Invalid tag name '" + tagName + "'! Value was '"
						+ value + "'.");
			}
		}
	}

	public TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>> getConstantFieldMap() {
		return constantFieldMap;
	}

	public String getSeeNameClass(ProgramElementDoc doc, String name) {

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

				ClassDoc cdoc = doc.containingClass();

				if (cdoc != null) {
					return cdoc.name();
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

	public String getSeeNamePackage(ProgramElementDoc doc, String name) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
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

			PackageDoc pdoc = doc.containingPackage();

			if (pdoc != null) {
				return pdoc.name();
			} else {
				return "";
			}
		}

		pkgName = pkgName.substring(0, index);
		return pkgName;
	}

	public String getTagLabel(String kind, ResourceBundle res) {

		if (kind == null) {

			throw new IllegalArgumentException(
					" The argument kind must not be null!");
		}

		if (kind.equals("@author")) {
			return ResourceServices.getString(res, "C_AUTHOR");
		}

		if (kind.equals("@version")) {
			return ResourceServices.getString(res, "C_VERSION");
		}

		if (kind.equals("@since")) {
			return ResourceServices.getString(res, "C_SINCE");
		}

		if (kind.equals("@serial")) {
			return ResourceServices.getString(res, "C_SERIAL");
		}

		if (kind.equals("@serialData")) {
			return ResourceServices.getString(res, "C_SERIAL_DATA");
		}

		if (kind.equals("@see")) {
			return ResourceServices.getString(res, "C_SEE_ALSO");
		}

		String label = kind;

		if ((label.length() > 1) && label.startsWith("@")) {

			label = label.substring(1);
		}

		CompiledTag tag = tagMap.get(label);

		if (tag != null) {

			return tag.getLabel();
		}

		return StringServices.capFirstLetter(label);
	}

	public String processTag(Tag tag) throws DocletException {

		Doc doc;
		SeeTag link;
		String label;
		String reference;
		String value;
		String text;

		String comment = "";
		String kind = tag.kind();
		String name = tag.name();

		logger.debug("name=" + name + ", kind=" + kind + ", text=" + tag.text());

		if (kind.equals("@inheritDoc")) {

			return processInheritDoc(tag);
		}

		if (kind.equals("@value")) {

			value = "Tag(@value): UnknownValueException!";

			text = tag.text();

			if (text == null) {
				text = "";
			}

			doc = tag.holder();
			logger.debug("@value holder=" + doc + ", text=" + text);

			if (text.length() == 0 && doc instanceof FieldDoc) {

				FieldDoc fdoc = (FieldDoc) doc;
				value = fdoc.constantValueExpression();
			}

			if (text.length() > 0 && doc instanceof ProgramElementDoc) {

				logger.debug("constantFieldMap.size()="
						+ getConstantFieldMap().size());
				String pkgName = getSeeNamePackage((ProgramElementDoc) doc,
						text);
				logger.debug("pkgName=" + pkgName);

				TreeMap<String, TreeMap<String, FieldDoc>> classMap = getConstantFieldMap()
						.get(pkgName);

				if (classMap != null) {

					String className = getSeeNameClass((ProgramElementDoc) doc,
							text);
					logger.debug("className=" + className);

					TreeMap<String, FieldDoc> fieldMap = classMap
							.get(className);

					if (fieldMap != null) {

						String fieldName = getSeeNameMember(text);
						logger.debug("fieldName=" + fieldName);

						FieldDoc fdoc = fieldMap.get(fieldName);

						if (fdoc != null) {
							value = fdoc.constantValueExpression();
						}
					}
				}
			}

			comment = "<javadoc:value>" + HtmlServices.textToHtml(value)
					+ "</javadoc:value>";
			logger.debug(comment);

			return comment;
		}

		if (kind.equals("@code")) {

			logger.debug("tag=" + tag.toString());
			String html = HtmlServices.textToHtml(tag.text());
			comment = "<javadoc:code>" + html + "</javadoc:code>";
			return comment;
		}

		if (kind.equals("@literal")) {

			logger.debug("tag=" + tag.toString());
			String html = HtmlServices.textToHtml(tag.text());
			comment = "<javadoc:literal>" + html + "</javadoc:literal>";
			return comment;
		}

		if (kind.equals("@see")) {

			link = (SeeTag) tag;

			name = link.name();

			label = referenceManager.createReferenceLabel(link);
			label = HtmlServices.textToHtml(label);

			reference = referenceManager.findReference(link);

			if ((reference != null) && (reference.length() > 0)) {

				if (name.equalsIgnoreCase("@linkplain")) {

					comment += "<javadoc:linkplain" + " ref=\"" + reference
							+ "\"" + " name=\"" + label + "\">" + link.label()
							+ "</javadoc:linkplain>";

				} else {

					comment += "<javadoc:link" + " ref=\"" + reference + "\""
							+ " name=\"" + label + "\">" + link.label()
							+ "</javadoc:link>";
				}

			} else if ((reference != null) && reference.startsWith("<a")) {

				comment = label;

			} else {

				comment += label;
			}

		} else {

			comment += tag.text();
		}

		return comment;
	}

	private String processInheritDoc(Tag tag) throws DocletException {

		Doc doc = tag.holder();
		String comment = "";

		if (doc instanceof MethodDoc) {

			MethodDoc methodDoc = (MethodDoc) doc;
			MethodDoc overriddenMethod = methodDoc.overriddenMethod();

			if (overriddenMethod != null) {

				Tag[] tags = overriddenMethod.inlineTags();
				comment = "<p>";

				for (int i = 0; i < tags.length; i++) {
					comment += processTag(tags[i]);
				}

				comment += "</p>";
			}
		}

		return comment;
	}

	public boolean isMetaTag(String kind) {

		if (kind.equals("@param")) {
			return false;
		}

		if (kind.equals("@return")) {
			return false;
		}

		if (kind.equals("@exception") || kind.equals("@throws")) {
			return false;
		}

		if (kind.equals("@serial") || kind.equals("@serialData")
				|| kind.equals("@serialField")) {
			return false;
		}

		if (kind.equals("@deprecated")) {
			return false;
		}

		return true;
	}

	public boolean showTag(String kind) {

		if (kind.equals("@author")
				&& (script.isCreateAuthorInfoEnabled() == false)) {
			return false;
		}

		if (kind.equals("@version")
				&& (script.isCreateVersionInfoEnabled() == false)) {
			return false;
		}

		if (kind.equals("@since")
				&& (script.isCreateSinceInfoEnabled() == false)) {
			return false;
		}

		if (kind.equals("@see")
				&& (script.isCreateSeeAlsoInfoEnabled() == false)) {
			return false;
		}

		if (kind.equals("@param")
				&& (script.isCreateParameterInfoEnabled() == false)) {
			return false;
		}

		if (kind.equals("@return")
				&& (script.isCreateParameterInfoEnabled() == false)) {
			return false;
		}

		return true;
	}

	private void initTags() {

		tagMap = new LinkedHashMap<String, CompiledTag>();

		CompiledTag tag;

		tag = new CompiledTag("@author", "X", null);
		tagMap.put("@author", tag);
	}

}

class CompiledTag {

	private String label = "";

	public CompiledTag(String name, String flags, String label) {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name must not be null!");
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
