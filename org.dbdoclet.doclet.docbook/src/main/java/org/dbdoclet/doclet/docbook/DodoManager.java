/* 
 * ### Copyright (C) 2005-2008 Michael Fuchs ###
 * ### All Rights Reserved.                  ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.HashcodeServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.dbd.AssociationData;
import org.dbdoclet.tag.dbd.Associations;
import org.dbdoclet.tag.dbd.ClassData;
import org.dbdoclet.tag.dbd.Classes;
import org.dbdoclet.tag.dbd.ConstructorData;
import org.dbdoclet.tag.dbd.Constructors;
import org.dbdoclet.tag.dbd.DbdDocument;
import org.dbdoclet.tag.dbd.DbdElement;
import org.dbdoclet.tag.dbd.Dodo;
import org.dbdoclet.tag.dbd.FieldData;
import org.dbdoclet.tag.dbd.Fields;
import org.dbdoclet.tag.dbd.GeneralizationData;
import org.dbdoclet.tag.dbd.Generalizations;
import org.dbdoclet.tag.dbd.MethodData;
import org.dbdoclet.tag.dbd.Methods;
import org.dbdoclet.tag.dbd.PackageData;
import org.dbdoclet.tag.dbd.Packages;
import org.dbdoclet.tag.dbd.ParameterData;
import org.dbdoclet.tag.dbd.Parameters;
import org.dbdoclet.xiphias.NodeSerializer;
import org.dbdoclet.xiphias.XmlServices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;

public class DodoManager extends MediaManager {

	private static Log logger = LogFactory.getLog(DodoManager.class.getName());

	private ArrayList<GeneralizationData> generalizationList;
	private Map<String, AssociationData> associationMap;
	private Map<String, ClassData> classMap;
	private Map<String, PackageData> expPkgMap;
	private Map<String, String> idMap;
	private Packages packagesTag;

	public DodoManager() {
		super();
	}

	@Override
	public void process(RootDoc rootDoc) throws DocletException {

		try {

			expPkgMap = new TreeMap<String, PackageData>();
			classMap = new TreeMap<String, ClassData>();
			generalizationList = new ArrayList<GeneralizationData>();
			associationMap = new HashMap<String, AssociationData>();

			String fileName = script.getDestinationFile().getPath();
			String dirName = FileServices.getAbsoluteDirName(fileName);

			fileName = FileServices.appendFileName(dirName, "Model.dbdml");
			FileServices.delete(fileName);

			String encoding = script.getDestinationEncoding();

			DbdDocument doc = new DbdDocument();

			Dodo root = new Dodo();
			doc.setDocumentElement(root);

			packagesTag = new Packages();
			root.appendChild(packagesTag);

			TreeMap<String, ClassDoc> classMap;

			ClassDoc classDoc;

			PackageData packageTag;
			DbdElement packageParentTag;

			File file;
			String str;
			String pkgName;
			String className;

			idMap = initIdMap(fileName);

			for (Iterator<String> pkgIterator = pkgMap.keySet().iterator(); pkgIterator
					.hasNext();) {

				pkgName = pkgIterator.next();

				// println(ResourceServices.getString(res,"C_PROCESSING_PACKAGE"),
				// pkgName);

				String[] tokens = pkgName.split("\\.");

				packageTag = null;
				packageParentTag = packagesTag;

				str = "";

				for (int i = 0; i < tokens.length; i++) {

					str += tokens[i];

					packageTag = expPkgMap.get(str);

					if (packageTag == null) {

						packageTag = new PackageData();
						packageTag.setName(tokens[i]);
						packageTag.setQualifiedName(str);
						packageTag.setId(retrieveId(str));

						expPkgMap.put(str, packageTag);

						packageParentTag.appendChild(packageTag);
					}

					str += ".";
					packageParentTag = packageTag;
				}

				Classes classesTag = new Classes();

				packageTag.appendChild(classesTag);

				classMap = pkgMap.get(pkgName);

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					classDoc = classMap.get(className);

					writeClass(classesTag, classDoc);
				}
			}

			if (generalizationList.size() > 0) {

				Generalizations generalizations = new Generalizations();
				root.appendChild(generalizations);

				for (int i = 0; i < generalizationList.size(); i++) {
					generalizations.appendChild(generalizationList.get(i));
				}
			}

			if (associationMap.size() > 0) {

				Associations associations = new Associations();
				root.appendChild(associations);

				Collection<AssociationData> values = associationMap.values();

				for (Iterator<AssociationData> iterator = values.iterator(); iterator
						.hasNext();) {
					associations.appendChild(iterator.next());
				}
			}

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), encoding));

			NodeSerializer serializer = new NodeSerializer();
			serializer.write(doc.getDocumentElement(), writer);

			writer.close();

			String driver;
			HashMap<String, String> params;

			String xmiFileName = FileServices.appendFileName(dirName,
					"Model.xmi");
			file = new File(xmiFileName);

			if (file.exists()) {

				String backupFileName = FileServices.appendFileName(dirName,
						"ModelBackup.xmi");
				String contentFileName = FileServices.appendFileName(dirName,
						"Content.xmi");
				String modelFileName = FileServices.appendFileName(dirName,
						"UML.xmi");
				String newFileName = FileServices.appendFileName(dirName,
						"ModelNew.xmi");

				FileServices.copyFileToFile(file, new File(backupFileName));

				driver = getDriver("/xslt/xmi/Umbrello.xsl");

				XmlServices.xslt(fileName, driver,
						new HashMap<String, String>(), contentFileName,
						"UTF-8", true);

				driver = getDriver("/xslt/xmi/ExtractUmlModel.xsl");

				XmlServices.xslt(contentFileName, driver,
						new HashMap<String, String>(), modelFileName, "UTF-8",
						true);

				driver = getDriver("/xslt/xmi/ReplaceUmlModel.xsl");

				file = new File(modelFileName);
				params = new HashMap<String, String>();

				params.put("filename", file.toURI().toURL().toString());

				logger.debug("xslt -xsl ReplaceUmlModel.xsl" + " -in "
						+ xmiFileName + " -param filename="
						+ file.toURI().toURL().toString());

				XmlServices.xslt(xmiFileName, driver, params, newFileName,
						"UTF-8", true);

			} else {

				driver = getDriver("/xslt/xmi/Umbrello.xsl");

				XmlServices.xslt(fileName, driver,
						new HashMap<String, String>(), xmiFileName, "UTF-8",
						true);
			}

		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}

	private void writeClass(Classes classesTag, ClassDoc classDoc) {

		if (classesTag == null) {
			throw new IllegalArgumentException(
					"The argument classesTag must not be null!");
		}

		if (classDoc == null) {
			throw new IllegalArgumentException(
					"The argument classDoc must not be null!");
		}

		String qualifiedName;
		GeneralizationData generalization;

		String classId = retrieveId(classDoc.qualifiedName());
		String superId;

		ClassData classTag = new ClassData();
		classesTag.appendChild(classTag);

		classMap.put(classDoc.qualifiedName(), classTag);

		classTag.setName(classDoc.name());
		classTag.setQualifiedName(classDoc.qualifiedName());
		classTag.setId(retrieveId(classDoc.qualifiedName()));
		classTag.setVisibility(getVisibilityAsText(classDoc));

		if (classDoc.isAbstract()) {
			classTag.setAbstract(true);
		}

		if (classDoc.isInterface()) {
			classTag.setInterface(true);
		}

		if (classDoc.isException()) {
			classTag.setException(true);
		}

		Type superDoc = classDoc.superclassType();

		if (superDoc != null) {

			generalization = new GeneralizationData();

			superId = getId(superDoc.qualifiedTypeName());

			if (superId == null) {

				addUndocumentedClass(superDoc.qualifiedTypeName());
				superId = retrieveId(superDoc.qualifiedTypeName());
			}

			qualifiedName = "Generalization(" + String.valueOf(classId) + ","
					+ String.valueOf(superId) + ")";

			generalization.setId(retrieveId(qualifiedName));
			generalization.setQualifiedName(qualifiedName);
			generalization.setParentNode(Integer.parseInt(superId));
			generalization.setChild(Integer.parseInt(classId));
			generalizationList.add(generalization);
		}

		ClassDoc[] interfaces = classDoc.interfaces();

		for (int i = 0; i < interfaces.length; i++) {

			generalization = new GeneralizationData();
			qualifiedName = interfaces[i].qualifiedName();

			superId = retrieveId(qualifiedName);

			generalization.setId(retrieveId(qualifiedName));
			generalization.setName("implements");
			generalization.setQualifiedName(qualifiedName);
			generalization.setParentNode(Integer.parseInt(superId));
			generalization.setChild(Integer.parseInt(classId));

			generalizationList.add(generalization);
		}

		writeFields(classId, classTag, classDoc);
		writeConstructors(classId, classTag, classDoc);
		writeMethods(classId, classTag, classDoc);
		// writeImports(classId, classDoc);
	}

	private void writeFields(String classId, ClassData classTag,
			ClassDoc classDoc) {

		FieldDoc[] fields;

		if (classDoc.isEnum()) {
			fields = classDoc.enumConstants();
		} else {
			fields = classDoc.fields();
		}

		if (fields.length > 0) {

			AssociationData association;
			ClassDoc typeDoc;
			FieldData fieldTag;
			String qualifiedName;
			String typeName;
			Type type;
			String typeId;

			Fields fieldsTag = new Fields();
			classTag.appendChild(fieldsTag);

			for (int i = 0; i < fields.length; i++) {

				qualifiedName = fields[i].qualifiedName();

				type = fields[i].type();
				typeDoc = type.asClassDoc();
				typeId = null;

				if (typeDoc != null) {

					typeName = typeDoc.qualifiedName();
					typeId = getId(typeName);

					if (typeId == null) {

						addUndocumentedClass(typeName);
						typeId = retrieveId(typeName);
					}

				} else {
					typeName = "primitive";
				}

				if (typeDoc == null || typeName.startsWith("java.") == true) {

					fieldTag = new FieldData();
					fieldTag.setId(retrieveId(qualifiedName));
					fieldTag.setQualifiedName(qualifiedName);
					fieldTag.setName(fields[i].name());
					fieldTag.setType(fields[i].type().typeName());
					fieldTag.setVisibility(getVisibilityAsText(fields[i]));

					if (fields[i].isStatic()) {
						fieldTag.setStatic(true);
					}

					fieldsTag.appendChild(fieldTag);

				} else {

					association = new AssociationData();
					qualifiedName = "Association(shared,"
							+ String.valueOf(classId) + ","
							+ String.valueOf(typeId) + ")";

					association.setId(retrieveId(qualifiedName));
					association.setQualifiedName(qualifiedName);
					association.setAggregate(Integer.parseInt(classId));
					association.setPart(Integer.parseInt(typeId));
					association.setType("shared");

					associationMap.put(
							String.valueOf(classId) + "-"
									+ String.valueOf(typeId), association);
				}
			}
		}
	}

	private void writeConstructors(String classId, ClassData classTag,
			ClassDoc classDoc) {

		ConstructorDoc[] constructors = classDoc.constructors();

		if (constructors.length > 0) {

			ConstructorData constructorTag;
			String qualifiedName;

			Constructors constructorsTag = new Constructors();
			classTag.appendChild(constructorsTag);

			for (int i = 0; i < constructors.length; i++) {

				qualifiedName = constructors[i].qualifiedName();
				qualifiedName += parametersAsString(constructors[i]);

				constructorTag = new ConstructorData();
				constructorTag.setId(retrieveId(qualifiedName));
				constructorTag.setQualifiedName(qualifiedName);
				constructorTag.setName(constructors[i].name());

				constructorsTag.appendChild(constructorTag);

				writeParameters(constructorTag, constructors[i]);
			}
		}
	}

	private void writeMethods(String classId, ClassData classTag,
			ClassDoc classDoc) {

		MethodDoc[] methods = classDoc.methods();

		if (methods.length > 0) {

			MethodData methodTag;
			String qualifiedName;

			Methods methodsTag = new Methods();
			classTag.appendChild(methodsTag);

			for (int i = 0; i < methods.length; i++) {

				qualifiedName = methods[i].qualifiedName();
				qualifiedName += parametersAsString(methods[i]);

				methodTag = new MethodData();
				methodTag.setId(retrieveId(qualifiedName));
				methodTag.setQualifiedName(qualifiedName);
				methodTag.setName(methods[i].name());
				methodTag.setType(methods[i].returnType().typeName());
				methodTag.setVisibility(getVisibilityAsText(methods[i]));

				if (methods[i].isAbstract()) {
					methodTag.setAbstract(true);
				}

				if (methods[i].isStatic()) {
					methodTag.setStatic(true);
				}

				methodsTag.appendChild(methodTag);

				writeParameters(methodTag, methods[i]);
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private void writeImports(String classId, ClassDoc classDoc) {

		ClassDoc[] importedClasses = classDoc.importedClasses();

		if (importedClasses.length > 0) {

			String qualifiedName;
			AssociationData association;
			String typeId;

			for (int i = 0; i < importedClasses.length; i++) {

				qualifiedName = importedClasses[i].qualifiedName();
				typeId = retrieveId(qualifiedName);

				if (getAssociation(classId, typeId) == null) {

					association = new AssociationData();
					qualifiedName = "Association(none,"
							+ String.valueOf(classId) + ","
							+ String.valueOf(typeId) + ")";

					association.setId(retrieveId(qualifiedName));
					association.setQualifiedName(qualifiedName);
					association.setAggregate(Integer.parseInt(classId));
					association.setPart(Integer.parseInt(typeId));
					association.setPartNavigable(true);
					association.setType("none");

					associationMap.put(
							String.valueOf(classId) + "-"
									+ String.valueOf(typeId), association);
				}
			}
		}
	}

	private String getId(String name) {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name must not be null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("The field idMap must not be null!");
		}

		String id = idMap.get(name);
		return id;
	}

	private String retrieveId(String name) {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name must not be null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("The field idMap must not be null!");
		}

		String id = idMap.get(name);

		if (id == null) {

			id = String.valueOf(HashcodeServices.createHashcode(name));
			idMap.put(name, String.valueOf(id));
		}

		return id;
	}

	private AssociationData getAssociation(String id1, String id2) {

		AssociationData association;
		String key;

		key = id1 + "-" + id2;
		association = associationMap.get(key);

		if (association != null) {
			return association;
		}

		key = String.valueOf(id2) + "-" + String.valueOf(id1);
		association = associationMap.get(key);

		return association;
	}

	private String parametersAsString(ExecutableMemberDoc memberDoc) {

		if (memberDoc == null) {
			throw new IllegalArgumentException(
					"The argument memberDoc must not be null!");
		}

		String buffer = "(";
		Parameter[] parameters = memberDoc.parameters();

		for (int i = 0; i < parameters.length; i++) {
			buffer += parameters[i].type().qualifiedTypeName();

			if (i < parameters.length - 1) {
				buffer += ",";
			}
		}

		buffer += ")";

		return buffer;
	}

	private void writeParameters(DbdElement parent,
			ExecutableMemberDoc memberDoc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (memberDoc == null) {
			throw new IllegalArgumentException(
					"The argument memberDoc must not be null!");
		}

		String qualifiedName;
		Parameter[] parameters = memberDoc.parameters();

		if (parameters.length > 0) {

			Parameters parametersTag = new Parameters();
			parent.appendChild(parametersTag);

			ParameterData parameterTag;
			for (int i = 0; i < parameters.length; i++) {

				qualifiedName = memberDoc.qualifiedName() + "."
						+ parameters[i].name();

				parameterTag = new ParameterData();
				parameterTag.setId(retrieveId(qualifiedName));
				parameterTag.setQualifiedName(qualifiedName);
				parameterTag.setName(parameters[i].name());
				parameterTag.setType(parameters[i].type().typeName());

				parametersTag.appendChild(parameterTag);
			}
		}

	}

	private void addUndocumentedClass(String name) {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name must not be null!");
		}

		// logger.debug("Undokumentierte Klasse " + name);

		PackageData packageTag;
		DbdElement packageParentTag;

		ClassData classTag = classMap.get(name);

		if (classTag != null) {
			return;
		}

		String[] tokens = name.split("\\.");

		if (tokens.length == 0) {
			return;
		}

		packageTag = new PackageData();
		packageParentTag = packagesTag;

		String str = "";

		for (int i = 0; i < (tokens.length - 1); i++) {

			str += tokens[i];

			packageTag = expPkgMap.get(str);

			if (packageTag == null) {

				packageTag = new PackageData();
				packageTag.setName(tokens[i]);
				packageTag.setQualifiedName(str);
				packageTag.setId(retrieveId(str));

				expPkgMap.put(str, packageTag);

				packageParentTag.appendChild(packageTag);
			}

			str += ".";
			packageParentTag = packageTag;
		}

		Classes classesTag = packageTag.getClassesElement();

		if (classesTag == null) {
			classesTag = new Classes();
			packageTag.appendChild(classesTag);
		}

		classTag = new ClassData();
		classesTag.appendChild(classTag);

		classMap.put(name, classTag);

		classTag.setName(tokens[tokens.length - 1]);
		classTag.setQualifiedName(name);
		classTag.setId(retrieveId(name));
	}

	private String getDriver(String name) {

		URL driver = ResourceServices.getResourceAsUrl(name);

		if (driver != null) {
			return driver.toString();
		} else {
			logger.error("Can't find XSLT file " + name);
			return null;
		}
	}

	private HashMap<String, String> initIdMap(String fileName)
			throws IOException, SAXException, ParserConfigurationException {

		String className;
		String pkgName;
		TreeMap<String, ClassDoc> classMap;

		HashMap<String, String> idMap = new HashMap<String, String>();

		File file = new File(fileName);

		if (file.exists() == true) {

			Document doc = XmlServices.parse(file);
			Element elem = doc.getDocumentElement();
			initIdMapScan(elem, idMap);
		}

		for (Iterator<String> pkgIterator = pkgMap.keySet().iterator(); pkgIterator
				.hasNext();) {

			pkgName = pkgIterator.next();
			classMap = pkgMap.get(pkgName);

			for (Iterator<String> classIterator = classMap.keySet().iterator(); classIterator
					.hasNext();) {

				className = classIterator.next();
				if (idMap.get(className) == null) {
					idMap.put(className, String.valueOf(HashcodeServices
							.createHashcode(className)));
				}
			}
		}

		return idMap;
	}

	private void initIdMapScan(Element parent, HashMap<String, String> idMap) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (idMap == null) {
			throw new IllegalArgumentException(
					"The argument idMap must not be null!");
		}

		NodeList children = parent.getChildNodes();

		Node node;
		Element elem;

		String qualifiedName;
		String id;

		for (int i = 0; i < children.getLength(); i++) {

			node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				elem = (Element) node;
				qualifiedName = elem.getAttribute("qualified-name");
				id = elem.getAttribute("id");

				// System.out.println(elem + "[" + qualifiedName + "," + id +
				// "]");

				if ((id != null && id.length() > 0)
						&& (qualifiedName != null && qualifiedName.length() > 0)) {

					// System.out.println("Found " + qualifiedName + ":" + id);

					idMap.put(qualifiedName, id);
				}

				initIdMapScan(elem, idMap);
			}
		}
	}
}
