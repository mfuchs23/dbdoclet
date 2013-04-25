package org.dbdoclet.doclet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.StringServices;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Type;

public class ReferenceManager {

	private static Log logger = LogFactory.getLog(ReferenceManager.class);
	private static Pattern nonXmlNameCharPattern = Pattern
			.compile("[^\\w\\._-]+");

	private HashMap<String, String> idMap;
	private String documentationId;

	public void init(String documentationId,
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap, XmlIdType type) {

		if (pkgMap == null) {
			throw new IllegalArgumentException("Parameter pkgMap is null");
		}

		if (idMap != null) {
			throw new IllegalStateException("Id map was already created!");
		}

		this.documentationId = documentationId;

		String id;

		int packageCounter = 1;
		int classCounter = 1;

		HashMap<String, String> map = new HashMap<String, String>();
		String pkgName;
		String className;
		TreeMap<String, ClassDoc> classMap;
		ClassDoc cdoc;

		for (Iterator<String> iterator = pkgMap.keySet().iterator(); iterator
				.hasNext();) {

			pkgName = iterator.next();

			if (type == XmlIdType.JAVA) {

				id = "package-" + pkgName;
				id = createJavaId(id);
				map.put(pkgName, id);

			} else {

				id = "PACKAGE-" + packageCounter++;
				id = createNumberedId(id);
				map.put(pkgName, id);
			}

			classMap = pkgMap.get(pkgName);

			if (classMap != null) {

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					cdoc = classMap.get(className);
					createClassIds(map, cdoc, classCounter, type);
					classCounter++;
				}
			}
		}

		idMap = map;
	}

	public String createReferenceLabel(SeeTag tag) throws DocletException {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		String label = "";
		String str;

		PackageDoc pdoc;
		ClassDoc cdoc;
		MemberDoc mdoc;

		str = tag.referencedClassName();

		if ((str != null) && (str.length() > 0)) {
			label = str;
		}

		str = tag.referencedMemberName();

		if ((str != null) && (str.length() > 0)) {
			label += ("#" + str);
		}

		pdoc = tag.referencedPackage();

		if (pdoc != null) {
			label = pdoc.name();
		}

		cdoc = tag.referencedClass();

		if (cdoc != null) {
			label = cdoc.qualifiedName();
		}

		mdoc = tag.referencedMember();

		if (mdoc != null) {
			label = mdoc.qualifiedName();
		}

		str = tag.label();

		if ((str != null) && (str.length() > 0)) {

			label = str;
		}

		if ((label == null) || (label.length() == 0)) {

			label = tag.text();
		}

		if ((label == null) || (label.length() == 0)) {

			label = "???";
		}

		logger.debug("Label for reference " + tag.toString() + " is " + label
				+ ".");

		return label;
	}

	public String findReference(SeeTag tag) {

		if (tag == null) {

			throw new IllegalArgumentException("Parameter tag is null!");
		}

		if (idMap == null) {

			throw new IllegalStateException("Variable idMap is null!");
		}

		logger.debug("Find reference for tag " + tag.toString());

		String key = null;
		String reference = null;

		PackageDoc pdoc;
		ClassDoc cdoc;
		MemberDoc mdoc;

		pdoc = tag.referencedPackage();

		if (pdoc != null) {

			key = pdoc.name();
			reference = getId(key);
			logger.debug("Reference for package " + key + " = " + reference
					+ ".");
		}

		cdoc = tag.referencedClass();

		if (cdoc != null) {

			key = cdoc.qualifiedName();
			reference = getId(key);
			logger.debug("Reference for class " + key + " = " + reference + ".");
		}

		mdoc = tag.referencedMember();

		if (mdoc != null) {

			if (mdoc instanceof ExecutableMemberDoc) {
				key = createMethodKey((ExecutableMemberDoc) mdoc);
			} else {
				key = mdoc.qualifiedName();
			}

			reference = getId(key);
			logger.debug("Reference for menber " + key + " = " + reference
					+ ".");

			if (reference == null) {

				cdoc = mdoc.containingClass();
				key = cdoc.qualifiedName();
				reference = idMap.get(key);
			}
		}

		logger.debug("Reference = " + reference + ".");

		return reference;
	}

	public String findReference(ProgramElementDoc elemDoc) {

		if (elemDoc == null) {
			throw new IllegalArgumentException(
					"The argument classDoc must not be null!");
		}

		if (elemDoc instanceof ExecutableMemberDoc) {
			return findReference((ExecutableMemberDoc) elemDoc);
		}

		if (elemDoc instanceof FieldDoc) {
			return findReference((FieldDoc) elemDoc);
		}

		String key = elemDoc.qualifiedName();
		String reference = getId(key);

		return reference;
	}

	public String findReference(FieldDoc fieldDoc) {

		if (fieldDoc == null) {
			throw new IllegalArgumentException(
					"The argument fieldDoc must not be null!");
		}

		String key = createFieldKey(fieldDoc);
		String reference = getId(key);

		if (reference == null) {

			ClassDoc cdoc = fieldDoc.containingClass();
			key = cdoc.qualifiedName();
			reference = idMap.get(key);
		}

		return reference;
	}

	public String findReference(ExecutableMemberDoc methodDoc) {

		if (methodDoc == null) {
			throw new IllegalArgumentException(
					"The argument methodDoc must not be null!");
		}

		String key = createMethodKey(methodDoc);
		String reference = getId(key);

		if (reference == null) {

			ClassDoc cdoc = methodDoc.containingClass();
			key = cdoc.qualifiedName();
			reference = idMap.get(key);
		}

		return reference;
	}

	public String createMethodKey(ExecutableMemberDoc doc) {

		StringBuilder id = new StringBuilder(doc.qualifiedName());
		id.append('(');

		for (Parameter param : doc.parameters()) {

			if (param == null) {
				continue;
			}

			Type type = param.type();

			if (type != null) {

				id.append(type.qualifiedTypeName());

				String dim = type.dimension();
				// System.out.println("Dim: " + dim + ", " + dim.length());
				if (dim != null && dim.length() > 0) {

					for (int i = 0; i < (dim.length() / 2); i++) {
						id.append("_A");
					}
				}

				id.append(",");
			}
		}

		return StringServices.cutSuffix(id.toString(), ",") + ")";
	}

	public String createFieldKey(FieldDoc doc) {

		String id = doc.qualifiedName();

		return id;
	}

	public String createJavaId(String id) {

		if (id == null) {
			throw new IllegalArgumentException(
					"The argument id must not be null!");
		}

		String nid = StringServices.replace(id, "<", "_");
		nid = StringServices.replace(nid, ">", "_");

		Matcher matcher = nonXmlNameCharPattern.matcher(nid);
		nid = matcher.replaceAll("-");

		nid = StringServices.cutPrefix(nid, "-");
		nid = StringServices.cutSuffix(nid, "-");

		if (documentationId != null && documentationId.trim().length() > 0) {
			nid = documentationId + "-" + nid;
		}

		return nid;
	}

	public String createNumberedId(String id) {

		String nid = id;

		if (documentationId != null && documentationId.trim().length() > 0) {
			nid = documentationId + "-" + nid;
		}

		return nid;
	}

	public String getId(String key) {

		if (key == null) {

			throw new IllegalArgumentException("Parameter key is null!");
		}

		if (idMap == null) {

			throw new IllegalStateException("Variable idMap is null!");
		}

		Object obj = idMap.get(key);

		if (obj == null) {

			return null;
		}

		if (!(obj instanceof String)) {

			throw new IllegalStateException("Value for key '" + key
					+ "' is not of type String but of type '"
					+ obj.getClass().getName() + "'!");
		}

		return (String) obj;
	}

	private void createClassIds(HashMap<String, String> map, ClassDoc cdoc,
			int index, XmlIdType type) {

		String str;
		String id;

		ConstructorDoc[] constructors;
		MethodDoc[] methods;
		FieldDoc[] fields;
		AnnotationTypeElementDoc[] elements;

		int constructorCounter = 1;
		int methodCounter = 1;
		int fieldCounter = 1;

		if (type == XmlIdType.JAVA) {

			id = "class-" + cdoc.qualifiedName();
			id = createJavaId(id);
			map.put(cdoc.qualifiedName(), id);

		} else {

			id = "CLASS-" + index;
			id = createNumberedId(id);
			map.put(cdoc.qualifiedName(), id);
		}

		constructors = cdoc.constructors();

		for (int k = 0; k < constructors.length; k++) {

			str = constructors[k].getRawCommentText();

			if ((str != null) && !str.equals("")) {

				if (type == XmlIdType.JAVA) {

					id = "constructor-" + createMethodKey(constructors[k]);
					id = createJavaId(id);
					map.put(createMethodKey(constructors[k]), id);

				} else {

					id = "CONSTRUCTOR-" + index + "-" + constructorCounter++;
					id = createNumberedId(id);
					map.put(createMethodKey(constructors[k]), id);
				}
			}
		}

		methods = cdoc.methods();

		for (int k = 0; k < methods.length; k++) {

			str = methods[k].getRawCommentText();

			if ((str != null) && !str.equals("")) {

				if (type == XmlIdType.JAVA) {

					id = "method-" + createMethodKey(methods[k]);
					id = createJavaId(id);
					map.put(createMethodKey(methods[k]), id);

				} else {

					id = "METHOD-" + index + "-" + methodCounter++;
					id = createNumberedId(id);
					map.put(createMethodKey(methods[k]), id);
				}
			}
		}

		fields = cdoc.fields();

		for (int k = 0; k < fields.length; k++) {

			str = fields[k].getRawCommentText();

			if ((str != null) && !str.equals("")) {

				if (type == XmlIdType.JAVA) {

					id = "field-" + fields[k].qualifiedName();
					id = createJavaId(id);
					map.put(fields[k].qualifiedName(), id);

				} else {

					id = "FIELD-" + index + "-" + fieldCounter++;
					id = createNumberedId(id);
					map.put(fields[k].qualifiedName(), id);
				}
			}
		}

		if (cdoc.isAnnotationType()) {

			AnnotationTypeDoc atdoc = (AnnotationTypeDoc) cdoc;

			elements = atdoc.elements();

			for (int k = 0; k < elements.length; k++) {

				str = elements[k].getRawCommentText();

				if ((str != null) && !str.equals("")) {

					if (type == XmlIdType.JAVA) {

						id = "method-" + createMethodKey(elements[k]);
						id = createJavaId(id);
						map.put(createMethodKey(elements[k]), id);

					} else {

						id = "METHOD-" + index + "-" + methodCounter++;
						id = createNumberedId(id);
						map.put(createMethodKey(elements[k]), id);
					}
				}
			}
		}
	}
}
