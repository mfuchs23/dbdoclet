/*
 * ### Copyright (C) 2006-2012 Michael Fuchs ###
 * ### All Rights Reserved.                 ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import java.util.Arrays;
import java.util.TreeMap;

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
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

public class StrictSynopsis extends Synopsis {

	private void addConstructors(DocBookElement parent, ClassDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		addExecutableMembers(parent, doc.constructors(), "Constructors");
	}

	public void addConstructorSynopsis(ConstructorDoc doc, DocBookElement parent) {

		Constructorsynopsis synopsis = dbfactory.createConstructorsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(doc, synopsis);

		synopsis.appendChild(dbfactory.createMethodname(doc.name()));

		addParameters(synopsis, doc);
		addExceptions(synopsis, doc);
	}

	private void addElements(DocBookElement parent, AnnotationTypeDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		AnnotationTypeElementDoc elements[] = doc.elements();

		Fieldsynopsis synopsis;
		Type type;

		for (int i = 0; i < elements.length; i++) {

			synopsis = dbfactory.createFieldsynopsis();

			createMemberModifier(elements[i], synopsis);

			type = dbfactory.createType();
			synopsis.appendChild(type);

			createType(elements[i].returnType(), type,
					script.isCreateFullyQualifiedNamesEnabled());

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

	private void addExecutableMembers(DocBookElement parent,
			ExecutableMemberDoc[] members, String title) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (members == null) {
			throw new IllegalArgumentException(
					"The argument members must not be null!");
		}

		if (title == null) {
			throw new IllegalArgumentException(
					"The argument title must not be null!");
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

					qualifiedName = members[i].qualifiedName() + "("
							+ members[i].signature() + ")";
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

				addExecutableMemberSection(parent, publicStaticMembers,
						"Public Static " + title);
			}

			if (publicMembers.size() > 0) {

				addExecutableMemberSection(parent, publicMembers, "Public "
						+ title);
			}

			if (protectedMembers.size() > 0) {

				addExecutableMemberSection(parent, protectedMembers,
						"Protected " + title);
			}

			if (packagePrivateMembers.size() > 0) {

				addExecutableMemberSection(parent, packagePrivateMembers,
						"Package Private " + title);
			}

			if (privateMembers.size() > 0) {

				addExecutableMemberSection(parent, privateMembers, "Private "
						+ title);
			}
		}
	}

	private void addExecutableMemberSection(DocBookElement parent,
			TreeMap<String, ExecutableMemberDoc> map, String title) {

		if (map.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// "
				+ title);
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

	private void addFields(DocBookElement parent, ClassDoc doc) {

		FieldDoc[] fields;

		if (doc.isEnum()) {
			fields = doc.enumConstants();
		} else {
			fields = doc.fields();
		}

		Arrays.sort(fields);

		String title = "Fields";

		TreeMap<String, FieldDoc> publicFields = new TreeMap<String, FieldDoc>();
		TreeMap<String, FieldDoc> publicStaticFields = new TreeMap<String, FieldDoc>();
		TreeMap<String, FieldDoc> protectedFields = new TreeMap<String, FieldDoc>();
		TreeMap<String, FieldDoc> privateFields = new TreeMap<String, FieldDoc>();
		TreeMap<String, FieldDoc> defaultFields = new TreeMap<String, FieldDoc>();

		String qualifiedName;

		if ((fields != null) && (fields.length > 0)) {

			for (int i = 0; i < fields.length; i++) {

				qualifiedName = fields[i].qualifiedName();

				if (fields[i].isPublic()) {

					if (fields[i].isStatic()) {
						publicStaticFields.put(qualifiedName, fields[i]);
					} else {
						publicFields.put(qualifiedName, fields[i]);
					}

				} else if (fields[i].isProtected()) {
					protectedFields.put(qualifiedName, fields[i]);
				} else if (fields[i].isPrivate()) {
					privateFields.put(qualifiedName, fields[i]);
				} else {
					defaultFields.put(qualifiedName, fields[i]);
				}
			}

			addFieldSection(parent, publicStaticFields, "Public Static "
					+ title);
			addFieldSection(parent, publicFields, "Public " + title);
			addFieldSection(parent, protectedFields, "Protected " + title);
			addFieldSection(parent, defaultFields, "Package " + title);
			addFieldSection(parent, privateFields, "Private " + title);
		}
	}

	private void addFieldSection(DocBookElement parent,
			TreeMap<String, FieldDoc> map, String title) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (map.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// "
				+ title);
		comment.setRole("comment");
		parent.appendChild(comment);

		for (FieldDoc fdoc : map.values()) {

			addFieldSynopsis(fdoc, parent);
		}
	}

	public void addFieldSynopsis(FieldDoc doc, DocBookElement parent) {

		Fieldsynopsis synopsis = dbfactory.createFieldsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(doc, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		String typeName = typeToString(doc.type(),
				script.isCreateFullyQualifiedNamesEnabled(), 1);
		type.appendChild(typeName);

		synopsis.appendChild(dbfactory.createVarname(doc.name()));

		String value = doc.constantValueExpression();

		if (value != null) {
			synopsis.appendChild(dbfactory.createInitializer(value));
		}
	}

	private void addInterfaces(DocBookElement parent, ClassDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		com.sun.javadoc.Type[] interfaces = doc.interfaceTypes();

		DocBookElement ooelem;

		for (int i = 0; i < interfaces.length; i++) {

			if (doc.isAnnotationType()
					&& interfaces[i].qualifiedTypeName().equals(
							"java.lang.annotation.Annotation")) {

				continue;
			}

			if (doc.isInterface()) {

				ooelem = dbfactory.createOoclass();
				ooelem.appendChild(dbfactory.createClassName(createTypeName(
						interfaces[i],
						script.isCreateFullyQualifiedNamesEnabled())));

			} else {

				ooelem = dbfactory.createOointerface();
				ooelem.appendChild(dbfactory
						.createInterfacename(createTypeName(interfaces[i],
								script.isCreateFullyQualifiedNamesEnabled())));
			}

			parent.appendChild(ooelem);
		}

	}

	private void addMethods(DocBookElement parent, ClassDoc doc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		addExecutableMembers(parent, doc.methods(), "Methods");
	}

	public void addMethodSynopsis(MethodDoc doc, DocBookElement parent) {

		Methodsynopsis synopsis = dbfactory.createMethodsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(doc, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		createType(((MethodDoc) doc).returnType(), type,
				script.isCreateFullyQualifiedNamesEnabled());

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

			String typeName = typeToString(parameters[i].type(),
					script.isCreateFullyQualifiedNamesEnabled(), 1);
			type.appendChild(typeName);
			String name = parameters[i].name();
			param.appendChild(dbfactory.createParameter(name));

			parent.appendChild(param);
		}
	}

	public boolean process(ClassDoc doc, DocBookElement parent)
			throws DocletException {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String name;

		try {

			Classsynopsis synopsis = dbfactory.createClasssynopsis();

			if (doc.isInterface()) {
				synopsis.setInterface(true);
			}

			Ooclass ooclass = dbfactory.createOoclass();
			synopsis.appendChild(ooclass);

			createClassModifier(doc, ooclass);

			ooclass.appendChild(dbfactory.createClassName(createClassName(doc,
					script.isCreateFullyQualifiedNamesEnabled())));

			com.sun.javadoc.Type superDoc = doc.superclassType();

			if (superDoc != null) {

				name = superDoc.qualifiedTypeName();

				if (name.equals("java.lang.Object") == false) {

					Ooclass extend = dbfactory.createOoclass();
					synopsis.appendChild(extend);

					Classname className = dbfactory.createClassname();
					String classNameText = createSuperClassName(superDoc,
							script.isCreateFullyQualifiedNamesEnabled());

					String ref = referenceManager.findReference(superDoc
							.asClassDoc());

					if (ref != null) {
						className.appendChild(dbfactory.createLink(
								classNameText, ref));
					} else {
						className.appendChild(classNameText);
					}

					extend.appendChild(className);
				}
			}

			addInterfaces(synopsis, doc);

			addFields(synopsis, doc);
			addConstructors(synopsis, doc);
			addMethods(synopsis, doc);

			if (doc.isAnnotationType()) {
				addElements(synopsis, (AnnotationTypeDoc) doc);
			}

			parent.appendChild(synopsis);

			if (script.isLinkSourceEnabled()) {

				Para para = dbfactory.createPara();
				Olink olink = dbfactory.createOlink("Source",
						doc.qualifiedName(), "listing");
				para.appendChild(olink);

				parent.appendChild(para);
			}

			return true;

		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}
}
