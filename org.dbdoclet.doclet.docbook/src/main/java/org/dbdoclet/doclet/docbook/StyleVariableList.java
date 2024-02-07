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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.doc.DocletException;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Exceptionname;
import org.dbdoclet.tag.docbook.Listitem;
import org.dbdoclet.tag.docbook.Member;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Simplelist;
import org.dbdoclet.tag.docbook.Type;
import org.dbdoclet.tag.docbook.Variablelist;
import org.dbdoclet.tag.docbook.Varname;
import org.dbdoclet.xiphias.dom.ProcessingInstructionImpl;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.ThrowsTree;

/**
 * The class <code>StyleNoTables</code> provides a layout without any tables.
 * There could be tables in the comments of cours!e, but the style will not
 * generate any tables by itself.
 * 
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class StyleVariableList extends StyleCoded implements Style {

	private static Logger logger = Logger.getLogger(StyleVariableList.class.getName());

	/**
	 * The method <code>addParamInfo</code> adds a list containing all parameter
	 * tags with their comments.
	 * 
	 * @param memberDoc an <code>ExecutableMemberDoc</code> value
	 * @param parent    a <code>DocBookElement</code> value
	 */
	@Override
	public boolean addParamInfo(ExecutableElement memberDoc, DocBookElement parent) throws DocletException {

		ReturnTree returnTag = null;

		if (ElementKind.METHOD.equals(memberDoc.getKind())) {
			returnTag = tagManager.findReturnTag(memberDoc);
		}

		List<ParamTree> paramTagList = tagManager.findParamTags(memberDoc);

		if (nonNull(returnTag) || paramTagList.size() > 0) {

			Variablelist varlist = dbfactory.createVariablelist();

			if (script.getListPresentation() != null) {
				varlist.appendChild(new ProcessingInstructionImpl("dbfo",
						"list-presentation=\"" + script.getListPresentation() + "\""));
			}

			varlist.appendChild(dbfactory.createTitle(ResourceServices.getString(res, "C_PARAMETERS")));

			parent.appendChild(varlist);

			for (var paramTag : paramTagList) {

				Listitem listItem = dbfactory.createListitem();

				varlist.appendChild(dbfactory.createVarlistentry()
						.appendChild(dbfactory.createTerm()
								.appendChild(dbfactory.createVarname(
										hyphenation.hyphenateAfter(paramTag.getName().toString(), "\\."))))
						.appendChild(listItem));

				Para para = dbfactory.createPara();
				listItem.appendChild(para);
				dbdTrafo.transform(docManager.getDocTreePath(memberDoc), paramTag.getDescription(), para);
			}

			if (returnTag != null) {

				Listitem listItem = dbfactory.createListitem();
				varlist.appendChild(dbfactory.createVarlistentry()
						.appendChild(dbfactory.createTerm()
								.appendChild(dbfactory.createEmphasis().appendChild(dbfactory.createVarname("return"))))
						.appendChild(listItem));

				Para para = dbfactory.createPara();
				listItem.appendChild(para);
				dbdTrafo.transform(docManager.getDocTreePath(memberDoc), returnTag.getDescription(), para);
			}
		}

		return true;
	}

	@Override
	public boolean addThrowsInfo(ExecutableElement memberDoc, DocBookElement parent) throws DocletException {

		List<ThrowsTree> tags = tagManager.findThrowsTags(memberDoc);
		if (tags.isEmpty()) {
			return false;
		}

		Variablelist varlist = dbfactory.createVariablelist();

		if (script.getListPresentation() != null) {
			varlist.appendChild(new ProcessingInstructionImpl("dbfo",
					"list-presentation=\"" + script.getListPresentation() + "\""));
		}

		varlist.appendChild(dbfactory.createTitle("Exceptions"));

		parent.appendChild(varlist);

		for (var tag : tags) {

			Exceptionname exceptionName = dbfactory.createExceptionname();
			Para commentPara = dbfactory.createPara();

			varlist.appendChild(
					dbfactory.createVarlistentry().appendChild(dbfactory.createTerm().appendChild(exceptionName))
							.appendChild(dbfactory.createListitem().appendChild(commentPara)));

			exceptionName.appendChild(tag.getExceptionName().toString());
			dbdTrafo.transform(docManager.getDocTreePath(memberDoc), tag, commentPara);

			if (commentPara.hasChildNodes() == false) {

				List<? extends DocTree> doc = tag.getDescription();
				if (doc != null) {
					dbdTrafo.transform(docManager.getDocTreePath(memberDoc), doc, commentPara);
				}
			}

			if (commentPara.hasChildNodes() == false) {
				commentPara.appendChild("");
			}
		}

		return true;
	}

	@Override
	public boolean addSerialFieldsInfo(VariableElement elem, DocBookElement parent) throws DocletException {

		List<SerialFieldTree> tags = tagManager.findSerialFieldTags(elem);
		if (tags.isEmpty()) {
			return false;
		}

		Variablelist varlist = dbfactory.createVariablelist();
		varlist.appendChild(dbfactory.createTitle(ResourceServices.getString(res, "C_SERIAL_FIELDS")));

		parent.appendChild(varlist);

		for (var tag : tags) {

			Varname varName = dbfactory.createVarname();
			Type type = dbfactory.createType();
			Para description = dbfactory.createPara();

			varlist.appendChild(dbfactory.createVarlistentry().appendChild(dbfactory.createTerm().appendChild(varName))
					.appendChild(dbfactory.createTerm().appendChild(type))
					.appendChild(dbfactory.createListitem().appendChild(description)));

			dbdTrafo.transform(elem, tag.getName().toString(), varName);
			dbdTrafo.transform(elem, tag.getType().toString(), type);
			dbdTrafo.transform(elem, tag.getDescription().toString(), description);
		}

		return true;
	}

	@Override
	public boolean addMetaInfo(Element elem, DocBookElement parent) throws DocletException {

		if (elem == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

		boolean foundSomething = false;

		if (script.isCreateMetaInfoEnabled() == false) {
			return false;
		}

		Variablelist varlist = dbfactory.createVariablelist();
		parent.appendChild(varlist);

		LinkedHashMap<DocTree.Kind, ArrayList<BlockTagTree>> tagMap = createTagMap(elem);

		for (DocTree.Kind kind : tagMap.keySet()) {

			String label = tagManager.getTagLabel(kind, res);
			ArrayList<BlockTagTree> list = tagMap.get(kind);

			if (tagManager.isMetaTag(kind) == false) {
				continue;
			}

			logger.fine("Tag kind is " + kind + ".");

			if (tagManager.showTag(elem, kind) == true) {

				if (kind == Kind.SEE) {

					if (addSeeAlsoInfo(elem, label, varlist)) {
						foundSomething = true;
					}

					continue;
				}

				if (addMetaInfoEntry(elem, list, label, varlist)) {
					foundSomething = true;
				}
			}
		}

		if (foundSomething == false) {
			parent.removeChild(varlist);
		}

		if (addDeprecatedInfo(elem, parent)) {
			foundSomething = true;
		}

		return foundSomething;
	}

	private boolean addMetaInfoEntry(Element elem, ArrayList<BlockTagTree> tagList, String label, Variablelist varlist)
			throws DocletException {

		logger.fine("Adding tags size = " + tagList.size() + ".");

		if (tagList.isEmpty()) {
			return false;
		}

		BlockTagTree tag = tagList.get(0);
		if (isNull(label) || label.isBlank()) {
			label = tag.getTagName();
		}

		Simplelist.Type type = Simplelist.Type.INLINE;
		if (DocTree.Kind.UNKNOWN_BLOCK_TAG == tag.getKind()) {
			type = Simplelist.Type.HORIZ;
		}
		
		Simplelist list = dbfactory.createSimplelist(type);
			varlist.appendChild(dbfactory.createVarlistentry()
					.appendChild(dbfactory.createTerm().appendChild(dbfactory.createEmphasis(label)))
						.appendChild(dbfactory.createListitem().appendChild(dbfactory.createPara().appendChild(list))));

		for (BlockTagTree bt : tagList) {

			logger.fine("Adding tag " + bt.getTagName() + ".");
			Member member = dbfactory.createMember();
			list.appendChild(member);
			dbdTrafo.transform(docManager.getDocTreePath(elem), bt, member);
		}

		return true;
	}

	private boolean addSeeAlsoInfo(Element elem, String name, Variablelist varlist) throws DocletException {

		String label;
		String ref;

		if (script.isCreateSeeAlsoInfoEnabled() == false) {
			return false;
		}

		List<SeeTree> tags = tagManager.findSeeTags(elem);
		if (tags.isEmpty()) {
			return false;
		}

		Simplelist list = dbfactory.createSimplelist(Simplelist.Type.INLINE);

		varlist.appendChild(dbfactory.createVarlistentry()
				.appendChild(dbfactory.createTerm().appendChild(dbfactory.createEmphasis(name)))
				.appendChild(dbfactory.createListitem().appendChild(list)));

		for (SeeTree st : tags) {

			ref = referenceManager.findReference(st);

			if ((ref != null) && (ref.length() > 0)) {

				label = st.getReference().toString();
				if ((label == null) || (label.length() == 0)) {

					list.appendChild(dbfactory.createMember().appendChild(dbfactory.createLiteral()
							.appendChild(dbfactory.createLink(ref).appendChild(dbfactory.createXref(ref)))));
				} else {

					list.appendChild(dbfactory.createMember()
							.appendChild(dbfactory.createLiteral().appendChild(dbfactory.createLink(label, ref))));
				}

			} else {

				label = referenceManager.createReferenceLabel(st);

				Member member = dbfactory.createMember();
				list.appendChild(member);
				dbdTrafo.transform(elem, label, member);

				logger.fine("label = " + label);
				logger.fine("member = " + member);
			}
		}

		return true;
	}
}
