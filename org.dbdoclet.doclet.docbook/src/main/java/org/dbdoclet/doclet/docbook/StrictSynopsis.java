/*
 * ### Copyright (C) 2006-2012 Michael Fuchs ###
 * ### All Rights Reserved.                 ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.tag.docbook.Classname;
import org.dbdoclet.tag.docbook.Classsynopsis;
import org.dbdoclet.tag.docbook.Classsynopsisinfo;
import org.dbdoclet.tag.docbook.Constructorsynopsis;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Exceptionname;
import org.dbdoclet.tag.docbook.Fieldsynopsis;
import org.dbdoclet.tag.docbook.Methodparam;
import org.dbdoclet.tag.docbook.Methodsynopsis;
import org.dbdoclet.tag.docbook.Olink;
import org.dbdoclet.tag.docbook.Ooclass;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Type;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;

public class StrictSynopsis extends Synopsis {

	private void addConstructors(DocBookElement parent, ClassDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		addExecutableMembers(parent, doc.constructors(), "Constructors");
	}

	public void addConstructorSynopsis(ConstructorDoc doc, DocBookElement parent) {

		Constructorsynopsis synopsis = dbfactory.createConstructorsynopsis();
		parent.appendChild(synopsis);

		// createMemberModifier(doc, synopsis);

		synopsis.appendChild(dbfactory.createMethodname(doc.name()));

		addParameters(synopsis, doc);
		addExceptions(synopsis, doc);
	}

	private void addElements(DocBookElement parent, AnnotationTypeDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		AnnotationTypeElementDoc elements[] = doc.elements();

		Fieldsynopsis synopsis;
		Type type;

		for (int i = 0; i < elements.length; i++) {

			synopsis = dbfactory.createFieldsynopsis();

			// createMemberModifier(elements[i], synopsis);

			type = dbfactory.createType();
			synopsis.appendChild(type);

			createType(elements[i].returnType(), type, script.isCreateFullyQualifiedNamesEnabled());

			synopsis.appendChild(dbfactory.createVarname(elements[i].name()));

			parent.appendChild(synopsis);
		}
	}

	private void addExceptions(DocBookElement parent, ExecutableMemberDoc doc) {

		ClassDoc[] exceptions = doc.thrownExceptions();

		Exceptionname name;

		for (int i = 0; i < exceptions.length; i++) {

			name = dbfactory.createExceptionname(exceptions[i].name());
			parent.appendChild(name);
		}
	}

	private void addExecutableMembers(DocBookElement parent, ExecutableMemberDoc[] members, String title) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (members == null) {
			throw new IllegalArgumentException("The argument members must not be null!");
		}

		if (title == null) {
			throw new IllegalArgumentException("The argument title must not be null!");
		}

		String qualifiedName;

		TreeMap<String, ExecutableMemberDoc> publicMembers = new TreeMap<String, ExecutableMemberDoc>();
		TreeMap<String, ExecutableMemberDoc> publicStaticMembers = new TreeMap<String, ExecutableMemberDoc>();
		TreeMap<String, ExecutableMemberDoc> protectedMembers = new TreeMap<String, ExecutableMemberDoc>();
		TreeMap<String, ExecutableMemberDoc> packagePrivateMembers = new TreeMap<String, ExecutableMemberDoc>();
		TreeMap<String, ExecutableMemberDoc> privateMembers = new TreeMap<String, ExecutableMemberDoc>();

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++) {

				if (members[i].isAnnotationTypeElement()) {

					qualifiedName = members[i].qualifiedName();

				} else {

					qualifiedName = members[i].qualifiedName() + "(" + members[i].signature() + ")";
				}

				if (members[i].isPublic()) {

					if (members[i].isStatic()) {

						publicStaticMembers.put(qualifiedName, members[i]);

					} else {

						publicMembers.put(qualifiedName, members[i]);
					}

				} else if (members[i].isProtected()) {

					protectedMembers.put(qualifiedName, members[i]);

				} else if (members[i].isPackagePrivate()) {

					packagePrivateMembers.put(qualifiedName, members[i]);

				} else if (members[i].isPrivate()) {

					privateMembers.put(qualifiedName, members[i]);
				}
			}

			if (publicStaticMembers.size() > 0) {

				addExecutableMemberSection(parent, publicStaticMembers, "Public Static " + title);
			}

			if (publicMembers.size() > 0) {

				addExecutableMemberSection(parent, publicMembers, "Public " + title);
			}

			if (protectedMembers.size() > 0) {

				addExecutableMemberSection(parent, protectedMembers, "Protected " + title);
			}

			if (packagePrivateMembers.size() > 0) {

				addExecutableMemberSection(parent, packagePrivateMembers, "Package Private " + title);
			}

			if (privateMembers.size() > 0) {

				addExecutableMemberSection(parent, privateMembers, "Private " + title);
			}
		}
	}

	private void addExecutableMemberSection(DocBookElement parent, TreeMap<String, ExecutableMemberDoc> map,
			String title) {

		if (map.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// " + title);
		comment.setRole("comment");
		parent.appendChild(comment);

		for (ExecutableMemberDoc doc : map.values()) {

			if (doc.isConstructor()) {
				addConstructorSynopsis((ConstructorDoc) doc, parent);
			} else {
				addMethodSynopsis((MethodDoc) doc, parent);
			}
		}
	}

	private void addFields(DocBookElement parent, TypeElement typeElem) {

		Set<VariableElement> fieldElements = docManager.getFieldElements(typeElem);

		/*
		 * if (docManager.isEnum(typeElem)) { fields = typeElem.enumConstants(); } else
		 * { fields = typeElem.fields(); }
		 * 
		 * Arrays.sort(fields);
		 */

		String title = "Fields";

		TreeMap<String, VariableElement> publicFields = new TreeMap<>();
		TreeMap<String, VariableElement> publicStaticFields = new TreeMap<>();
		TreeMap<String, VariableElement> protectedFields = new TreeMap<>();
		TreeMap<String, VariableElement> privateFields = new TreeMap<>();
		TreeMap<String, VariableElement> defaultFields = new TreeMap<>();

		for (VariableElement elem : fieldElements) {

			String fieldName = elem.getSimpleName().toString();
			if (docManager.isPublic(elem)) {
				if (docManager.isStatic(elem)) {
					publicStaticFields.put(fieldName, elem);
				} else {
					publicFields.put(fieldName, elem);
				}
			} else if (docManager.isProtected(elem)) {
				protectedFields.put(fieldName, elem);
			} else if (docManager.isPrivate(elem)) {
				privateFields.put(fieldName, elem);
			} else {
				defaultFields.put(fieldName, elem);
			}
		}

		addFieldSection(parent, publicStaticFields, "Public Static " + title);
		addFieldSection(parent, publicFields, "Public " + title);
		addFieldSection(parent, protectedFields, "Protected " + title);
		addFieldSection(parent, defaultFields, "Package " + title);
		addFieldSection(parent, privateFields, "Private " + title);
	}

	private void addFieldSection(DocBookElement parent, TreeMap<String, VariableElement> publicStaticFields,
			String title) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (publicStaticFields.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// " + title);
		comment.setRole("comment");
		parent.appendChild(comment);

		for (VariableElement fdoc : publicStaticFields.values()) {
			addFieldSynopsis(fdoc, parent);
		}
	}

	public void addFieldSynopsis(VariableElement varElem, DocBookElement parent) {

		Fieldsynopsis synopsis = dbfactory.createFieldsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(varElem, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		String typeName = typeToString(varElem.asType(), script.isCreateFullyQualifiedNamesEnabled());
		type.appendChild(typeName);

		synopsis.appendChild(dbfactory.createVarname(varElem.getSimpleName().toString()));

		if (nonNull(varElem.getConstantValue())) {
			String value = varElem.getConstantValue().toString();
			synopsis.appendChild(dbfactory.createInitializer(value));
		}
	}

	private void addInterfaces(DocBookElement parent, TypeElement typeElem) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (typeElem == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		List<? extends TypeMirror> interfaces = typeElem.getInterfaces();

		DocBookElement ooelem;
		Types typeUtils = docManager.getTypeUtils();
		for (TypeMirror mirror : interfaces) {

			TypeElement te = (TypeElement) typeUtils.asElement(mirror);
			if (docManager.isInterface(te) == false) {

				ooelem = dbfactory.createOoclass();
				ooelem.appendChild(
						dbfactory.createClassName(createTypeName(te, script.isCreateFullyQualifiedNamesEnabled())));

			} else {

				ooelem = dbfactory.createOointerface();
				ooelem.appendChild(
						dbfactory.createInterfacename(createTypeName(te, script.isCreateFullyQualifiedNamesEnabled())));
			}

			parent.appendChild(ooelem);
		}

	}

	private void addMethods(DocBookElement parent, ClassDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		addExecutableMembers(parent, doc.methods(), "Methods");
	}

	public void addMethodSynopsis(MethodDoc doc, DocBookElement parent) {

		Methodsynopsis synopsis = dbfactory.createMethodsynopsis();
		parent.appendChild(synopsis);

		// createMemberModifier(doc, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		createType(((MethodDoc) doc).returnType(), type, script.isCreateFullyQualifiedNamesEnabled());

		String name = doc.name();
		synopsis.appendChild(dbfactory.createMethodname(name));

		addParameters(synopsis, doc);
		addExceptions(synopsis, doc);
	}

	private void addParameters(DocBookElement parent, ExecutableMemberDoc doc) {

		com.sun.javadoc.Parameter[] parameters = doc.parameters();

		if (parameters.length == 0) {

			parent.appendChild(dbfactory.createVoid());
			return;
		}

		Methodparam param;

		for (int i = 0; i < parameters.length; i++) {

			param = dbfactory.createMethodparam();

			Type type = dbfactory.createType();
			param.appendChild(type);

			// TODO String typeName = typeToString(parameters[i].type(),
			// script.isCreateFullyQualifiedNamesEnabled(), 1);
			// type.appendChild(typeName);
			String name = parameters[i].name();
			param.appendChild(dbfactory.createParameter(name));

			parent.appendChild(param);
		}
	}

	public boolean process(TypeElement typeElem, DocBookElement parent) throws DocletException {

		if (isNull(typeElem)) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		if (isNull(parent)) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		try {

			Classsynopsis synopsis = dbfactory.createClasssynopsis();
			if (docManager.isInterface(typeElem)) {
				synopsis.setInterface(true);
			}

			Ooclass ooclass = dbfactory.createOoclass();
			synopsis.appendChild(ooclass);

			createClassModifier(typeElem, ooclass);

			ooclass.appendChild(
					dbfactory.createClassName(createClassName(typeElem, script.isCreateFullyQualifiedNamesEnabled())));

			TypeMirror superType = typeElem.getSuperclass();

			if (superType instanceof NoType == false) {

				TypeElement superElem = (TypeElement) docManager.getTypeUtils().asElement(superType);
				if (superElem.getQualifiedName().toString().equals("java.lang.Object") == false) {
					Ooclass extend = dbfactory.createOoclass();
					synopsis.appendChild(extend);

					Classname className = dbfactory.createClassname();
					String classNameText = createSuperClassName(superElem, script.isCreateFullyQualifiedNamesEnabled());

					/*
					 * TODO Migration String ref = referenceManager.findReference(superElem);
					 * 
					 * if (ref != null) { className.appendChild(dbfactory.createLink( classNameText,
					 * ref)); } else { className.appendChild(classNameText); }
					 */
					className.appendChild(classNameText);
					extend.appendChild(className);
				}
			}

			addInterfaces(synopsis, typeElem);
			addFields(synopsis, typeElem);
			// addConstructors(synopsis, typeElem);
			// addMethods(synopsis, typeElem);

			if (docManager.isAnnotationType(typeElem)) {
				// TODO addElements(synopsis, (AnnotationTypeDoc) typeElem);
			}

			parent.appendChild(synopsis);

			if (script.isLinkSourceEnabled()) {

				Para para = dbfactory.createPara();
				Olink olink = dbfactory.createOlink("Source", typeElem.getQualifiedName().toString(), "listing");
				para.appendChild(olink);

				parent.appendChild(para);
			}

			return true;

		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}

	public String typeToString(TypeMirror type, boolean showFullQualifiedName) {

		if (docManager.isPrimitiveType(type)) {
			return type.toString();
		}

		Element elem = docManager.getTypeUtils().asElement(type);
		if (nonNull(elem)) {
			if (showFullQualifiedName && elem instanceof TypeElement) {
				return ((TypeElement) elem).getQualifiedName().toString();
			} else {
				return elem.getSimpleName().toString();
			}
		} else {
			return type.toString();
		}
	}

	public String typeToString(com.sun.javadoc.Type doc, boolean showFullQualifiedName) {
		// TODO Auto-generated method stub
		return null;
	}
}
