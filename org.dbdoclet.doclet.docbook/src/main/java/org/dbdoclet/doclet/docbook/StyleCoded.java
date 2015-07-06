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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Formalpara;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Simplelist;
import org.dbdoclet.tag.docbook.Warning;
import org.dbdoclet.xiphias.XmlServices;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

/**
 * The class <code>StyleCoded</code> is the super class for the coded styles.
 * 
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public abstract class StyleCoded extends StyleBase implements Style {

	@Override
	public boolean addClassSynopsis(ClassDoc doc, DocBookElement parent)
			throws DocletException {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String ref;
		String name;
		Para para;

		boolean rc = synopsis.process(doc, parent);

		if (script.isCreateInheritedFromInfoEnabled()) {

			ArrayList<Type> subclasses = statisticData.getSubclasses(doc
					.qualifiedName());

			if (subclasses.size() > 0) {

				para = dbfactory.createPara();
				para.setRole("direct-known-subclasses");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(ResourceServices
						.getString(res, "C_DIRECT_KNOWN_SUBCLASSES"),
						getEmphasisBoldRole()));
				para.appendChild(": ");

				Simplelist list = dbfactory
						.createSimplelist(Simplelist.FORMAT_INLINE);
				para.appendChild(list);

				for (Type cdoc : subclasses) {

					ref = referenceManager.findReference(cdoc.asClassDoc());

					name = XmlServices.textToXml(cdoc.qualifiedTypeName());
					name = hyphenation.hyphenateAfter(name, "\\.");

					if ((ref != null) && (ref.length() > 0)) {
						list.appendChild(dbfactory.createMember().appendChild(
								dbfactory.createLink(
										dbfactory.createVarname(name), ref)));
					} else {
						list.appendChild(dbfactory.createMember().appendChild(
								dbfactory.createVarname(name)));
					}
				}
			}

			addMethodsInheritedFrom(parent, doc.superclassType());
			addFieldsInheritedFrom(parent, doc.superclassType());
		}

		return rc;
	}

	protected boolean addDeprecatedInfo(Doc doc, DocBookElement parent)
			throws DocletException {

		if (script.isCreateDeprecatedInfoEnabled() == false) {
			return false;
		}

		Tag tag;
		String text;

		tag = DbdServices.findComment(doc.tags(), "@deprecated");

		if (tag != null) {

			Warning warning = dbfactory.createWarning(ResourceServices
					.getString(res, "C_DEPRECATED"));
			parent.appendChild(warning);

			text = tag.text();

			if (text != null && text.trim().length() > 0) {
				dbdTrafo.transform(tag, warning);
			} else {
				warning.appendChild(dbfactory.createPara(ResourceServices
						.getString(res, "C_DEPRECATED")));
			}

			return true;
		}

		return false;
	}

	protected void addFieldsInheritedFrom(DocBookElement parent, Type superDoc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (superDoc == null) {
			return;
		}

		String ref;
		String name;
		Para para;

		HashMap<String, FieldDoc> fieldMap = new HashMap<String, FieldDoc>();

		while (superDoc != null) {

			FieldDoc[] fields = superDoc.asClassDoc().fields();
			Arrays.sort(fields);

			int fieldCount = 0;

			Simplelist fieldList = dbfactory
					.createSimplelist(Simplelist.FORMAT_INLINE);

			for (int i = 0; i < fields.length; i++) {

				name = fields[i].name();

				if (fieldMap.get(name) != null) {
					continue;
				} else {
					fieldMap.put(name, fields[i]);
				}

				ref = referenceManager.findReference(fields[i]);
				name = XmlServices.textToXml(name);

				if ((ref != null) && (ref.length() > 0)
						&& script.isCreateFieldInfoEnabled()) {
					fieldList.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createLink(dbfactory.createLiteral(name),
									ref)));

				} else {
					fieldList.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createVarname(name)));
				}

				fieldCount++;
			}

			if (fieldCount > 0) {

				para = dbfactory.createPara();
				para.setRole("fields-inherited-from");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(
						ResourceServices.getString(res,
								"C_FIELDS_INHERITED_FROM")
								+ " "
								+ superDoc.qualifiedTypeName(),
						getEmphasisBoldRole()));

				para.appendChild(": ");
				para.appendChild(fieldList);
			}

			superDoc = superDoc.asClassDoc().superclassType();
		}
	}

	@Override
	public boolean addFieldSynopsis(FieldDoc doc, DocBookElement parent)
			throws DocletException {

		synopsis.addFieldSynopsis(doc, parent);
		return true;
	}

	@Override
	public boolean addInheritancePath(ClassDoc classDoc, DocBookElement parent)
			throws DocletException {

		if (classDoc == null) {
			throw new IllegalArgumentException(
					"The argument classDoc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (script.isCreateInheritanceInfoEnabled() == false) {
			return false;
		}

		Formalpara fpara = dbfactory.createFormalpara();
		fpara.appendChild(dbfactory.createTitle(ResourceServices.getString(res,
				"C_INHERITANCE_PATH")));

		ArrayList<Type> list = classDiagramManager.getInheritancePath(classDoc);

		String name;
		String id;
		Para para = dbfactory.createPara();

		Iterator<Type> iterator = list.iterator();

		for (Type doc : list) {

			name = doc.qualifiedTypeName();
			id = referenceManager.getId(name);

			if (id != null) {
				para.appendChild(dbfactory.createXref(id));
			} else {
				para.appendChild(name);
			}

			if (iterator.hasNext()) {
				para.appendChild("-&gt; ");
			}
		}

		fpara.appendChild(para);
		parent.appendChild(fpara);

		return true;
	}

	@Override
	public boolean addMemberSynopsis(ExecutableMemberDoc doc,
			DocBookElement parent) throws DocletException {

		if (doc.isMethod()) {
			synopsis.addMethodSynopsis((MethodDoc) doc, parent);
		}

		if (doc.isConstructor()) {
			synopsis.addConstructorSynopsis((ConstructorDoc) doc, parent);
		}

		return true;
	}

	protected void addMethodsInheritedFrom(DocBookElement parent, Type superDoc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (superDoc == null) {
			return;
		}

		String ref;
		String name;
		Para para;

		HashMap<String, MethodDoc> methodMap = new HashMap<String, MethodDoc>();

		while (superDoc != null) {

			MethodDoc[] methods = superDoc.asClassDoc().methods();
			Arrays.sort(methods);

			int methodCount = 0;

			Simplelist methodList = dbfactory
					.createSimplelist(Simplelist.FORMAT_INLINE);

			for (int i = 0; i < methods.length; i++) {

				name = methods[i].name();

				if (methodMap.get(name) != null) {
					continue;
				} else {
					methodMap.put(name, methods[i]);
				}

				if (methods[i].isConstructor()) {
					continue;
				}

				if (name.equals("<clinit>")) {
					continue;
				}

				ref = referenceManager.findReference(methods[i]);
				name = XmlServices.textToXml(name);

				if ((ref != null) && (ref.length() > 0)
						&& script.isCreateMethodInfoEnabled()) {
					methodList
							.appendChild(dbfactory.createMember()
									.appendChild(
											dbfactory.createLink(dbfactory
													.createLiteral(name), ref)));

				} else {
					methodList.appendChild(dbfactory.createMember()
							.appendChild(dbfactory.createVarname(name)));
				}

				methodCount++;
			}

			if (methodCount > 0) {

				para = dbfactory.createPara();
				para.setRole("methods-inherited-from");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(
						ResourceServices.getString(res,
								"C_METHODS_INHERITED_FROM")
								+ " "
								+ superDoc.qualifiedTypeName(),
						getEmphasisBoldRole()));

				para.appendChild(": ");
				para.appendChild(methodList);
			}

			superDoc = superDoc.asClassDoc().superclassType();
		}
	}

	@Override
	public boolean addMethodSpecifiedBy(MethodDoc doc, DocBookElement parent)
			throws DocletException {

		Para para = dbfactory.createPara();
		para.setRole("method-specified-by");
		parent.appendChild(para);

		para.appendChild(dbfactory.createEmphasis(
				ResourceServices.getString(res, "C_SPECIFIED_BY") + ": ",
				getEmphasisBoldRole()));

		String methodRef = referenceManager.findReference(doc);

		ClassDoc classDoc = doc.containingClass();
		String classRef = referenceManager.findReference(classDoc);

		para.appendChild(ResourceServices.getString(res, "C_METHOD") + " ");

		if ((methodRef != null) && (methodRef.length() > 0)
				&& (script.isCreateMethodInfoEnabled() == true)) {

			para.appendChild(dbfactory.createLink(doc.name(), methodRef));
		} else {

			para.appendChild(dbfactory.createLiteral(doc.name()));
		}

		para.appendChild(" "
				+ ResourceServices.getString(res, "C_IN_INTERFACE") + " ");

		if ((classRef != null) && (classRef.length() > 0)) {

			para.appendChild(dbfactory.createLink(classDoc.name(), classRef));
		} else {

			para.appendChild(dbfactory.createLiteral(classDoc.name()));
		}

		return true;
	}

	public abstract boolean addParamInfo(ExecutableMemberDoc memberDoc,
			DocBookElement parent) throws DocletException;

	public abstract boolean addSerialFieldsInfo(FieldDoc fieldDoc,
			DocBookElement parent) throws DocletException;

	public abstract boolean addThrowsInfo(ExecutableMemberDoc memberDoc,
			DocBookElement parent) throws DocletException;

	private String getEmphasisBoldRole() {
		return "bold";
	}
}
