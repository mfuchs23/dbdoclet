/*
 * ### Copyright (C) 2006-2008 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.DocletServices;
import org.dbdoclet.trafo.tag.docbook.DocBookElement;
import org.dbdoclet.trafo.tag.docbook.DocBookTagFactory;
import org.dbdoclet.trafo.tag.docbook.Modifier;
import org.dbdoclet.trafo.tag.docbook.OoClass;
import org.dbdoclet.xiphias.XmlServices;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public abstract class Synopsis {

	private static Log logger = LogFactory.getLog(Synopsis.class);

	@Inject
	protected DocBookTagFactory dbfactory;

	@Inject
	protected DocletContext context;

	@Inject
	protected DbdScript script;

	public abstract boolean process(ClassDoc doc, DocBookElement parent)
			throws DocletException;

	protected final void createAnnotationElements(AnnotationDesc desc,
			DocBookElement parent) {

		if (desc == null) {
			throw new IllegalArgumentException(
					"The argument desc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		AnnotationDesc.ElementValuePair[] elements = desc.elementValues();
		String buffer;

		if (elements.length > 0) {
			parent.appendChild("(");
		}

		for (int i = 0; i < elements.length; i++) {

			buffer = elements[i].toString();

			if (i < (elements.length - 1)) {
				buffer += ",";
			}

			parent.appendChild(buffer);
		}

		if (elements.length > 0) {
			parent.appendChild(")");
		}

	}

	protected final void createAnnotations(ProgramElementDoc doc,
			DocBookElement parent) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		AnnotationDesc[] descList = doc.annotations();
		AnnotationTypeDoc adoc;
		Modifier modifier;

		for (int i = 0; i < descList.length; i++) {

			try {
				adoc = descList[i].annotationType();
			} catch (Throwable oops) {
				continue;
			}

			logger.debug("Annotation: " + adoc.name());

			if (script.isForceAnnotationDocumentationEnabled()
					|| findAnnotation(adoc, "documented") != null) {

				modifier = dbfactory.createModifier("@" + adoc.name());
				modifier.setRole("annotation");

				createAnnotationElements(descList[i], modifier);

				parent.appendChild(modifier);
			}
		}
	}

	protected final void createClassModifier(ClassDoc doc, OoClass parent) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		createAnnotations(doc, parent);
		createVisibility(doc, parent);

		if (doc.isAnnotationType()) {
			parent.appendChild(dbfactory.createModifier("@interface"));
		} else if (doc.isInterface()) {
			parent.appendChild(dbfactory.createModifier("interface"));
		} else {
			if (doc.isAbstract()) {
				parent.appendChild(dbfactory.createModifier("abstract"));
			}
			parent.appendChild(dbfactory.createModifier("class"));
		}
	}

	protected final String createClassName(ClassDoc doc,
			boolean showFullQualifiedName) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		StringBuilder buffer = new StringBuilder();

		if (showFullQualifiedName == true) {
			buffer.append(XmlServices.makeWrapable(doc.qualifiedName(), "."));
		} else {
			buffer.append(doc.name());
		}

		createTypeParameters(buffer, doc.typeParameters(),
				showFullQualifiedName);
		return buffer.toString();
	}

	protected final String createSuperClassName(Type superDoc,
			boolean showFullQualifiedName) {

		if (superDoc == null) {
			throw new IllegalArgumentException(
					"The argument superDdoc must not be null!");
		}

		StringBuilder buffer = new StringBuilder();

		buffer.append(DocletServices.typeToString(superDoc,
				showFullQualifiedName));

		// buffer.append(createTypeName(superDoc, showFullQualifiedName));

		/*
		 * TypeVariable[] types = superDoc.typeParameters();
		 * 
		 * if (types != null && types.length > 0) {
		 * 
		 * buffer.append("<");
		 * 
		 * boolean first = true;
		 * 
		 * for (Type type : types) {
		 * 
		 * if (first == false) { buffer.append(", "); }
		 * 
		 * buffer.append(superDoc.asParameterizedType());
		 * 
		 * first = false; }
		 * 
		 * buffer.append(">"); }
		 */

		return buffer.toString();
	}

	protected final String createTypeName(Type type,
			boolean showFullQualifiedName) {

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type must not be null!");
		}

		return DocletServices.typeToString(type, showFullQualifiedName);
	}

	protected String getPackageName(ClassDoc doc) {

		PackageDoc pdoc = doc.containingPackage();

		if (pdoc == null) {
			return "";
		}

		String pname = pdoc.name();

		if (pname == null || pname.length() == 0) {
			return "";
		}

		if (pname.endsWith(".") == false) {
			pname = pname + ".";
		}

		return pname;
	}

	public final void createMemberModifier(MemberDoc doc, DocBookElement parent) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent# must not be null!");
		}

		createAnnotations(doc, parent);
		createVisibility(doc, parent);
	}

	public final void createType(Type type, DocBookElement parent,
			boolean showFullQualifiedName) {

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String buffer = DocletServices
				.typeToString(type, showFullQualifiedName);
		buffer = buffer.replace(".", ".&#x200b;");
		parent.appendChild(buffer);
	}

	private void createTypeParameters(StringBuilder buffer,
			TypeVariable[] typeList, boolean showFullQualifiedName) {

		if (typeList.length > 0) {

			buffer.append("<");

			for (int i = 0; i < typeList.length; i++) {

				buffer.append(DocletServices.typeToString(typeList[i],
						showFullQualifiedName));

				if (i < typeList.length - 1) {
					buffer.append(",");
				}
			}

			buffer.append(">");
		}
	}

	public final void createVisibility(ProgramElementDoc doc,
			DocBookElement parent) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		FieldDoc fdoc;
		MethodDoc mdoc;

		if (doc.isPublic()) {
			parent.appendChild(dbfactory.createModifier("public"));
		}

		if (doc.isProtected()) {
			parent.appendChild(dbfactory.createModifier("protected"));
		}

		if (doc.isPrivate()) {
			parent.appendChild(dbfactory.createModifier("private"));
		}

		if (doc.isStatic()) {
			parent.appendChild(dbfactory.createModifier("static"));
		}

		if (doc instanceof MethodDoc) {

			mdoc = (MethodDoc) doc;

			if (mdoc.isAbstract()) {
				parent.appendChild(dbfactory.createModifier("abstract"));
			}

			if (mdoc.isSynchronized()) {
				parent.appendChild(dbfactory.createModifier("synchronized"));
			}
		}

		if (doc.isFinal()) {
			parent.appendChild(dbfactory.createModifier("final"));
		}

		if (doc instanceof FieldDoc) {

			fdoc = (FieldDoc) doc;

			if (fdoc.isTransient()) {
				parent.appendChild(dbfactory.createModifier("transient"));
			}

			if (fdoc.isVolatile()) {
				parent.appendChild(dbfactory.createModifier("volatile"));
			}
		}

		if (doc instanceof MethodDoc) {

			mdoc = (MethodDoc) doc;

			if (mdoc.isNative()) {
				parent.appendChild(dbfactory.createModifier("native"));
			}
		}
	}

	protected final AnnotationTypeDoc findAnnotation(AnnotationTypeDoc doc,
			String name) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name must not be null!");
		}

		logger.debug("doc=" + doc);

		AnnotationDesc[] descList = doc.annotations();
		logger.debug("descList.length=" + descList.length);

		AnnotationTypeDoc adoc;
		String str = "";

		for (int i = 0; i < descList.length; i++) {

			try {
				adoc = descList[i].annotationType();
			} catch (ClassCastException e) {
				continue;
			}

			str = adoc.name();
			logger.debug("Annotation: " + str);

			if (name.equalsIgnoreCase(str)) {
				return adoc;
			}

		}

		return null;
	}

}
