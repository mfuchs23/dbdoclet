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

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.Colspec;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Entry;
import org.dbdoclet.tag.docbook.Exceptionname;
import org.dbdoclet.tag.docbook.Informaltable;
import org.dbdoclet.tag.docbook.Member;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Row;
import org.dbdoclet.tag.docbook.Simplelist;
import org.dbdoclet.tag.docbook.Tag;
import org.dbdoclet.tag.docbook.Tbody;
import org.dbdoclet.tag.docbook.Tgroup;
import org.dbdoclet.tag.docbook.Type;
import org.dbdoclet.tag.docbook.Variablelist;
import org.dbdoclet.tag.docbook.Varname;
import org.dbdoclet.xiphias.dom.ProcessingInstructionImpl;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.util.DocTreePath;

/**
 * The class <code>StyleNoTables</code> provides a layout without any tables.
 * There could be tables in the comments of cours!e, but the style will not
 * generate any tables by itself.
 * 
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class StyleTable extends StyleCoded implements Style {

	private static Log logger = LogFactory.getLog(StyleTable.class);

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

		List<ParamTree> paramTagList = docManager.findDocTreeList(memberDoc, ParamTree.class, DocTree.Kind.PARAM);

		if (nonNull(returnTag) || paramTagList.size() > 0) {

			Informaltable table = dbfactory.createInformaltable();
			parent.appendChild(table);
			table.setRole("parameter");
			table.setFrame("all");
			table.appendChild(new ProcessingInstructionImpl("dbfo", "table-width=\"98%\""));

			Tgroup tgroup = dbfactory.createTgroup();
			tgroup.setCols(2);

			Colspec c1 = dbfactory.createColspec();
			tgroup.appendChild(c1);
			c1.setAttribute("colname", "c1");
			c1.setAttribute("colwidth", "1*");

			Colspec c2 = dbfactory.createColspec();
			tgroup.appendChild(c2);
			c2.setAttribute("colname", "c2");
			c2.setAttribute("colwidth", "3*");

			table.appendChild(tgroup);

			Tbody tbody = dbfactory.createTbody();
			tgroup.appendChild(tbody);

			Row row;
			Entry entry;
			Para para;

			row = dbfactory.createRow();
			tbody.appendChild(row);

			entry = dbfactory.createEntry(ResourceServices.getString(res, "C_PARAMETERS"));
			row.appendChild(entry);
			entry.setAlign("left");
			entry.setNameSt("c1");
			entry.setNameEnd("c2");
			entry.appendChild(new ProcessingInstructionImpl("dbfo", "bgcolor=\"#eeeeee\""));

			for (var paramTag : paramTagList) {

				row = dbfactory.createRow();
				tbody.appendChild(row);

				entry = dbfactory.createEntry();
				row.appendChild(entry);

				para = dbfactory.createPara(hyphenation.hyphenateAfter(paramTag.getName().toString(), "\\."));
				entry.appendChild(para);

				entry = dbfactory.createEntry();
				row.appendChild(entry);

				para = dbfactory.createPara();
				entry.appendChild(para);

				dbdTrafo.transform(docManager.getDocTreePath(memberDoc), paramTag, para);
			}

			if (returnTag != null) {

				row = dbfactory.createRow();
				tbody.appendChild(row);

				entry = dbfactory.createEntry();
				row.appendChild(entry);

				para = dbfactory.createPara();
				entry.appendChild(para);
				para.appendChild(dbfactory.createEmphasis("return"));

				entry = dbfactory.createEntry();
				row.appendChild(entry);

				para = dbfactory.createPara();
				entry.appendChild(para);

				dbdTrafo.transform(docManager.getDocTreePath(memberDoc), returnTag.getDescription(), para);
			}
		}

		return true;
	}

	@Override
	public boolean addThrowsInfo(ExecutableElement elem, DocBookElement parent) throws DocletException {

		List<ThrowsTree> tags = tagManager.findThrowsTags(elem);
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

		for (ThrowsTree tag : tags) {

			Exceptionname exceptionName = dbfactory.createExceptionname();
			Para commentPara = dbfactory.createPara();

			varlist.appendChild(
					dbfactory.createVarlistentry().appendChild(dbfactory.createTerm().appendChild(exceptionName))
							.appendChild(dbfactory.createListitem().appendChild(commentPara)));

			dbdTrafo.transform(elem, tag.getExceptionName().toString(), exceptionName);
			dbdTrafo.transform(docManager.getDocTreePath(elem), tag, commentPara);

			if (commentPara.hasChildNodes() == false) {

				ReferenceTree refTree = tag.getExceptionName();
				if (nonNull(refTree)) {
					dbdTrafo.transform(docManager.getDocTreePath(elem), refTree, commentPara);
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

		for (SerialFieldTree tag : tags) {

			Varname varName = dbfactory.createVarname();
			Type type = dbfactory.createType();
			Para description = dbfactory.createPara();

			varlist.appendChild(
					dbfactory.createVarlistentry().appendChild(dbfactory.createTerm().appendChild(varName))
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

		boolean foundSomething = false;

		if (elem == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException("Parameter parent is null!");
		}

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

			logger.debug("Tag kind is " + kind + ".");

			if (tagManager.showTag(kind) == true) {

				if (kind.equals(DocTree.Kind.SEE)) {

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

		Member member;
		Tag tag = null;

		logger.debug("Adding tags size = " + tagList.size() + ".");

		if (tagList.size() == 0) {
			return false;
		}

		Simplelist list = dbfactory.createSimplelist(Simplelist.FORMAT_INLINE);

		if ((label == null) || (label.length() == 0)) {
			label = tagList.get(0).getTagName();
		}

		varlist.appendChild(dbfactory.createVarlistentry()
				.appendChild(dbfactory.createTerm().appendChild(dbfactory.createEmphasis(label)))
				.appendChild(dbfactory.createListitem().appendChild(dbfactory.createPara().appendChild(list))));

		for (BlockTagTree bt : tagList) {

			logger.debug("Adding tag " + tag + ".");
			member = dbfactory.createMember();
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

		Simplelist list = dbfactory.createSimplelist(Simplelist.FORMAT_INLINE);

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

				logger.debug("label = " + label);
				logger.debug("member = " + member);
			}
		}

		return true;
	}
}
