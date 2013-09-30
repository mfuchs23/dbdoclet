/* 
 * $Id$
 *
 * ### Copyright (C) 2005 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 *
 * RCS Information
 * Author..........: $Author$
 * Date............: $Date$
 * Revision........: $Revision$
 * State...........: $State$
 */
package org.dbdoclet.doclet.docbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.ExceptionName;
import org.dbdoclet.tag.docbook.ListItem;
import org.dbdoclet.tag.docbook.Member;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.SimpleList;
import org.dbdoclet.tag.docbook.Type;
import org.dbdoclet.tag.docbook.VarName;
import org.dbdoclet.tag.docbook.VariableList;
import org.dbdoclet.xiphias.dom.ProcessingInstructionImpl;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

/**
 * The class <code>StyleNoTables</code> provides a layout without any tables.
 * There could be tables in the comments of cours!e, but the style will not
 * generate any tables by itself.
 * 
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class StyleVariableList extends StyleCoded implements Style {

	private static Log logger = LogFactory.getLog(StyleVariableList.class);

	/**
	 * The method <code>addParamInfo</code> adds a list containing all parameter
	 * tags with their comments.
	 * 
	 * @param memberDoc
	 *            an <code>ExecutableMemberDoc</code> value
	 * @param parent
	 *            a <code>DocBookElement</code> value
	 */
	@Override
	public boolean addParamInfo(ExecutableMemberDoc memberDoc,
			DocBookElement parent) throws DocletException {

		Tag returnTag = null;

		if (memberDoc instanceof MethodDoc) {
			returnTag = DbdServices.findReturnComment(memberDoc.tags());
		}

		com.sun.javadoc.ParamTag[] paramTags = memberDoc.paramTags();

		if ((returnTag != null) || (paramTags.length > 0)) {

			VariableList varlist = dbfactory.createVariableList();
			varlist.appendChild(new ProcessingInstructionImpl("dbfo",
					"list-presentation=\"blocks\""));
			varlist.appendChild(dbfactory.createTitle(ResourceServices
					.getString(res, "C_PARAMETERS")));

			parent.appendChild(varlist);

			for (int j = 0; j < paramTags.length; j++) {

				ListItem listItem = dbfactory.createListItem();

				varlist.appendChild(dbfactory
						.createVarListEntry()
						.appendChild(
								dbfactory.createTerm().appendChild(
										dbfactory.createVarName(paramTags[j]
												.parameterName())))
						.appendChild(listItem));
				
				Para para = dbfactory.createPara();
				listItem.appendChild(para);
				htmlDocBookTrafo.transform(paramTags[j].inlineTags(), para);
			}

			if (returnTag != null) {

				ListItem listItem = dbfactory.createListItem();
				varlist.appendChild(dbfactory
						.createVarListEntry()
						.appendChild(
								dbfactory.createTerm().appendChild(
										dbfactory.createEmphasis("return")))
						.appendChild(listItem));
				
				Para para = dbfactory.createPara();
				listItem.appendChild(para);
				htmlDocBookTrafo.transform(returnTag.inlineTags(), para);
			}
		}

		return true;
	}

	@Override
	public boolean addThrowsInfo(ExecutableMemberDoc memberDoc,
			DocBookElement parent) throws DocletException {

		ThrowsTag[] tags = memberDoc.throwsTags();

		if (tags.length > 0) {

			VariableList varlist = dbfactory.createVariableList();
			varlist.appendChild(new ProcessingInstructionImpl("dbfo",
					"list-presentation=\"blocks\""));
			varlist.appendChild(dbfactory.createTitle("Exceptions"));

			parent.appendChild(varlist);

			for (int i = 0; i < tags.length; i++) {

				ExceptionName exceptionName = dbfactory.createExceptionName();
				Para commentPara = dbfactory.createPara();

				varlist.appendChild(dbfactory
						.createVarListEntry()
						.appendChild(
								dbfactory.createTerm().appendChild(
										exceptionName))
						.appendChild(
								dbfactory.createListItem().appendChild(
										commentPara)));

				htmlDocBookTrafo.transform(tags[i].holder(), tags[i].exceptionName(),
						exceptionName);

				Tag[] inlineTags = tags[i].inlineTags();

				if (inlineTags.length > 0) {
					htmlDocBookTrafo.transform(tags[i].inlineTags(),
							commentPara);
				}

				if (commentPara.hasChildNodes() == false) {

					ClassDoc doc = tags[i].exception();
					if (doc != null) {
						htmlDocBookTrafo.transform(doc.firstSentenceTags(),
								commentPara);
					}
				}

				if (commentPara.hasChildNodes() == false) {

					ClassDoc doc = tags[i].exception();

					if (doc != null) {
						commentPara.appendChild(doc.qualifiedName());
					} else {
						commentPara.appendChild("");
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean addSerialFieldsInfo(FieldDoc fieldDoc, DocBookElement parent)
			throws DocletException {

		SerialFieldTag[] tags = fieldDoc.serialFieldTags();

		if (tags.length > 0) {

			VariableList varlist = dbfactory.createVariableList();
			varlist.appendChild(dbfactory.createTitle(ResourceServices
					.getString(res, "C_SERIAL_FIELDS")));

			parent.appendChild(varlist);

			for (int i = 0; i < tags.length; i++) {

				VarName varName = dbfactory.createVarName();
				Type type = dbfactory.createType();
				Para description = dbfactory.createPara();


				varlist.appendChild(dbfactory
						.createVarListEntry()
						.appendChild(
								dbfactory.createTerm().appendChild(varName))
						.appendChild(dbfactory.createTerm().appendChild(type))
						.appendChild(
								dbfactory.createListItem().appendChild(
										description)));

				htmlDocBookTrafo.transform(tags[i].holder(), tags[i].fieldName(), varName);
				htmlDocBookTrafo.transform(tags[i].holder(), tags[i].fieldType(), type);
				htmlDocBookTrafo.transform(tags[i].holder(), tags[i].description(), description);
			}
		}

		return true;
	}

	@Override
	public boolean addMetaInfo(Doc doc, DocBookElement parent)
			throws DocletException {

		if (doc == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		boolean foundSomething = false;

		if (script.isCreateMetaInfoEnabled() == false) {
			return false;
		}

		VariableList varlist = dbfactory.createVariableList();
		parent.appendChild(varlist);

		// varlist.appendChild("<?dbfo list-presentation=\"blocks\"?>");
		// varlist.appendChild(new
		// Title(ResourceServices.getString(res,"C_ADDITIONAL_INFORMATION")));
		LinkedHashMap<String, ArrayList<Tag>> tagMap = createTagMap(doc);

		String kind;
		String label;
		ArrayList<Tag> list;

		for (Iterator<String> i = tagMap.keySet().iterator(); i.hasNext();) {

			kind = i.next();
			label = tagManager.getTagLabel(kind, res);
			list = tagMap.get(kind);

			if (tagManager.isMetaTag(kind) == false) {
				continue;
			}

			logger.debug("Tag kind is " + kind + ".");

			if (tagManager.showTag(kind) == true) {

				if (kind.equals("@see")) {

					if (addSeeAlsoInfo(doc, label, varlist)) {
						foundSomething = true;
					}

					continue;
				}

				if (addMetaInfoEntry(list, label, varlist)) {

					foundSomething = true;
				}
			}
		}

		if (foundSomething == false) {
			parent.removeChild(varlist);
		}

		if (addDeprecatedInfo(doc, parent)) {
			foundSomething = true;
		}

		return foundSomething;
	}

	private boolean addMetaInfoEntry(ArrayList<Tag> tagList, String label,
			VariableList varlist) throws DocletException {

		Member member;
		SimpleList list = dbfactory.createSimpleList(SimpleList.FORMAT_INLINE);

		logger.debug("Adding tags size = " + tagList.size() + ".");

		if (tagList.size() == 0) {
			return false;
		}

		if ((label == null) || (label.length() == 0)) {
			label = tagList.get(0).name();
		}

		varlist.appendChild(dbfactory
				.createVarListEntry()
				.appendChild(
						dbfactory.createTerm().appendChild(
								dbfactory.createEmphasis(label)))
				.appendChild(
						dbfactory.createListItem().appendChild(
								dbfactory.createPara().appendChild(list))));


		for (Iterator<Tag> i = tagList.iterator(); i.hasNext();) {

			Tag tag = i.next();

			logger.debug("Adding tag " + tag + ".");
			member = dbfactory.createMember();
			list.appendChild(member);

			htmlDocBookTrafo.transform(tag, member);
		}
		return true;
	}

	private boolean addSeeAlsoInfo(Doc doc, String name, VariableList varlist)
			throws DocletException {

		String label;
		String ref;


		if (script.isCreateSeeAlsoInfoEnabled() == false) {
			return false;
		}

		SeeTag[] tags = doc.seeTags();

		if (tags.length == 0) {
			return false;
		}

		SimpleList list = dbfactory.createSimpleList(SimpleList.FORMAT_INLINE);

		varlist.appendChild(dbfactory
				.createVarListEntry()
				.appendChild(
						dbfactory.createTerm().appendChild(
								dbfactory.createEmphasis(name)))
				.appendChild(dbfactory.createListItem().appendChild(list)));

		for (int i = 0; i < tags.length; i++) {

			ref = referenceManager.findReference(tags[i]);

			if ((ref != null) && (ref.length() > 0)) {

				label = tags[i].label();

				if ((label == null) || (label.length() == 0)) {

					list.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createLiteral().appendChild(
									dbfactory.createLink(ref).appendChild(
											dbfactory.createXRef(ref)))));
				} else {

					list.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createLiteral().appendChild(
									dbfactory.createLink(label, ref))));
				}
			
			} else {

				label = referenceManager.createReferenceLabel(tags[i]);

				Member member = dbfactory.createMember();
				list.appendChild(member);
				htmlDocBookTrafo.transform(tags[i].holder(), label, member);

				logger.debug("label = " + label);
				logger.debug("member = " + member);
			}
		}
		return true;
	}
}
