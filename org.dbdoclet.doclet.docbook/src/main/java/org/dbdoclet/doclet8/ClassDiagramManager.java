/*
 * ### Copyright (C) 2005-2009 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet8;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.doclet8.docbook.DbdScript;
import org.dbdoclet.doclet8.docbook.StrictSynopsis;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.svg.UmlClassDiagramCreator;
import org.dbdoclet.svg.shape.ClassBox;
import org.dbdoclet.svg.shape.Shape;
import org.dbdoclet.xiphias.ImageServices;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

/**
 * Die Klasse ClassDiagramManager erstellt ein UML Klassendiagramm aus einem
 * {@linkplain ClassDoc} Objekt.
 * 
 * @author michael
 * 
 */
public class ClassDiagramManager {

	private static Log logger = LogFactory.getLog(ClassDiagramManager.class);

	@Inject
	private DbdScript script;
	@Inject
	private StrictSynopsis synopsis;

	private int imageHeight;
	private int imageWidth;
	private final boolean showFullQualifiedName = false;

	public ArrayList<Type> getInheritancePath(ClassDoc doc) {

		if (doc == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		ArrayList<Type> list = new ArrayList<Type>();
		list.add(doc);

		int index = 0;

		if (doc.isInterface() == true) {

			Type[] types;

			while (doc != null) {

				logger.debug(String.valueOf(index++) + " doc=" + doc);

				types = doc.interfaceTypes();

				if (types != null && types.length > 0) {
					doc = types[0].asClassDoc();
				} else {
					doc = null;
				}

				if (doc != null) {
					list.add(doc);
				}
			}

		} else {

			Type superType = doc.superclassType();

			while (superType != null) {

				logger.debug(String.valueOf(index++) + " superType="
						+ superType);

				if (script.isClassDiagramIncludesObject() == false
						&& superType.qualifiedTypeName().equals(
								"java.lang.Object")) {
					break;
				}

				list.add(superType);

				superType = superType.asClassDoc().superclassType();

			}
		}

		return list;
	}

	public String createClassDiagram(ClassDoc cdoc, File outdir)
			throws DocletException {

		if (cdoc == null) {
			throw new IllegalArgumentException(
					"The argument cdoc must not be null!");
		}

		if (outdir == null) {
			throw new IllegalArgumentException(
					"The argument outdir must not be null!");
		}

		String fileName;
		File imageFile = null;

		try {

			String path = FileServices
					.appendPath(outdir, script.getImagePath());
			path = FileServices
					.appendPath(path, StringServices.replace(
							cdoc.qualifiedName(), ".", Sfv.FSEP));

			FileServices.createPath(path);

			UmlClassDiagramCreator ucdc = new UmlClassDiagramCreator();
			ucdc.setInterfaceBackgroundColor(Color.orange);
			ucdc.setFontSize(script.getClassDiagramFontSize());
			buildDiagram(ucdc, cdoc);

			ucdc.setMaxWidth(script.getClassDiagramWidth());
			ucdc.setMaxHeight(script.getClassDiagramHeight());
			ucdc.drawImage();

			fileName = FileServices.appendFileName(path, "ClassDiagram.svg");
			imageFile = new File(fileName);
			ucdc.save(imageFile);

			fileName = FileServices.appendFileName(path, "ClassDiagram.png");
			imageFile = new File(fileName);
			ucdc.saveAsPng(imageFile);

			imageWidth = ImageServices.getWidth(imageFile);
			imageHeight = ImageServices.getHeight(imageFile);

			String imagePath = script.getImagePath()
					+ Sfv.FSEP
					+ StringServices.replace(cdoc.qualifiedName(), ".",
							Sfv.FSEP) + Sfv.FSEP + "ClassDiagram";

			if (script.isAbsoluteImagePathEnabled() == true) {

				imagePath = FileServices.appendFileName(
						script.getDestinationDirectory(), imagePath);
				imageFile = new File(imagePath);
				imagePath = imageFile.toURI().toURL().toString();
			}

			return imagePath;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	/**
	 * Erzeugt die grafischen Elemente für die Klasse. Unterstützt werden die
	 * Typen Class, Interface und Annotation mit den Beziehungen Generalization
	 * und Realisation.
	 * 
	 * @param ucdc
	 * @param doc
	 */
	private void buildDiagram(UmlClassDiagramCreator ucdc, ClassDoc doc) {

		if (doc.isInterface()) {
			defineInterface(ucdc, doc);
		} else {
			defineClass(ucdc, doc);
		}
	}

	private void defineAttributes(UmlClassDiagramCreator ucdc, ClassDoc doc,
			ClassBox classBox) {
		FieldDoc[] fields = doc.fields();

		if (script.isClassDiagramContainsAttributes() && fields.length > 0) {

			ucdc.addLine(classBox);
			for (FieldDoc field : fields) {
				ucdc.addAttribute(classBox, String.format("%s %s: %s",
						createVisibilityIndicator(field), field.name(),
						synopsis.typeToString(field.type(), false)));
			}
		}
	}

	private void defineOperations(UmlClassDiagramCreator ucdc, ClassDoc doc,
			ClassBox classBox) {

		MethodDoc[] methods = doc.methods();
		ConstructorDoc[] constructors = doc.constructors();

		if (script.isClassDiagramContainsOperations()
				&& (methods.length > 0 || constructors.length > 0)) {

			ucdc.addLine(classBox);

			for (ConstructorDoc constructor : constructors) {
				ucdc.addMethod(classBox, String.format("%s %s(%s)",
						createVisibilityIndicator(constructor),
						constructor.name(),
						createOperationParameterList(constructor)));
			}

			for (MethodDoc method : methods) {
				ucdc.addMethod(classBox, String.format("%s %s(%s): %s",
						createVisibilityIndicator(method), method.name(),
						createOperationParameterList(method),
						synopsis.typeToString(method.returnType(), false)));
			}
		}
	}

	private String createVisibilityIndicator(MemberDoc field) {

		String indicator = "";

		if (field == null) {
			return indicator;
		}

		if (field.isPublic()) {
			return "+";
		}

		if (field.isProtected()) {
			return "#";
		}

		if (field.isPrivate()) {
			return "-";
		}

		if (field.isPackagePrivate()) {
			return "~";
		}

		return indicator;
	}

	private String createOperationParameterList(ExecutableMemberDoc method) {

		StringBuilder buffer = new StringBuilder();

		for (Parameter paramDoc : method.parameters()) {
			buffer.append(String.format("%s: %s,\n", paramDoc.name(),
					synopsis.typeToString(paramDoc.type(), false)));
		}

		String parametersAsText = buffer.toString();
		if (parametersAsText.length() > 0) {
			parametersAsText = StringServices
					.cutSuffix(parametersAsText, ",\n");
		}

		return parametersAsText.trim();
	}

	private ClassBox defineClass(UmlClassDiagramCreator ucdc, ClassDoc doc) {

		int row = 0;

		ArrayList<Type> inheritanceList = getInheritancePath(doc);
		Collections.reverse(inheritanceList);

		int numCol = 0;
		int index = 0;

		for (Type type : inheritanceList) {

			Type[] interfaceTypes = type.asClassDoc().interfaceTypes();

			if (interfaceTypes.length + 1 > numCol) {
				numCol = interfaceTypes.length + 1;
			}

			if (index == 0 && interfaceTypes.length > 0) {
				row = 1;
			}

			index++;
		}

		int middleCol = numCol / 2;

		index = 0;

		ClassBox classBox = null;
		ClassBox fromShape = null;
		ClassBox toShape = null;
		Shape shape = null;

		for (Type type : inheritanceList) {

			String className = synopsis.typeToString(type,
					showFullQualifiedName);

			ClassDoc cdoc = type.asClassDoc();

			if (cdoc.isAnnotationType()) {

				toShape = ucdc.addClassBox(row, middleCol, className,
						"annotation");

			} else if (cdoc.isInterface()) {

				toShape = ucdc.addClassBox(row, middleCol, className,
						"interface");

			} else if (cdoc.typeParameters() != null
					&& cdoc.typeParameters().length > 0) {

				ArrayList<String> templateParameters = new ArrayList<String>();

				for (TypeVariable typeParameter : cdoc.typeParameters()) {
					templateParameters.add(String.format("%s: %s",
							typeParameter.typeName(),
							synopsis.typeToString(typeParameter, false)));
				}

				logger.debug("className: " + className);
				toShape = ucdc.addParameterizedClassBox(row, middleCol,
						className, templateParameters);

			} else {

				logger.debug("className: " + className);
				toShape = ucdc.addClassBox(row, middleCol, className);
			}

			if (fromShape != null && toShape != null) {

				if (cdoc.isInterface()) {
					ucdc.addRealization(toShape, fromShape);
				} else {
					ucdc.addInheritance(toShape, fromShape);
				}
			}

			Type[] interfaceTypes = cdoc.interfaceTypes();

			int typeIndex = 1;

			for (Type interfaceType : interfaceTypes) {

				int remainder = typeIndex % 2;
				int offset = (typeIndex / 2) + remainder;

				if (remainder == 1) {
					offset *= -1;
				}

				String interfaceName = synopsis.typeToString(interfaceType,
						showFullQualifiedName);

				ClassDoc tcdoc = type.asClassDoc();
				AnnotationTypeDoc tadoc = type.asAnnotationTypeDoc();

				if (tadoc != null) {

					shape = ucdc.addClassBox(row - 1, middleCol + offset,
							interfaceName, "annotation");
					ucdc.addInheritance(toShape, shape);

				} else if (tcdoc != null && cdoc.isInterface() == false) {

					shape = ucdc.addClassBox(row - 1, middleCol + offset,
							interfaceName, "interface");
					ucdc.addRealization(toShape, shape);

				} else {

					shape = ucdc.addClassBox(row - 1, middleCol + offset,
							interfaceName);
					ucdc.addInheritance(toShape, shape);
				}

				typeIndex++;
			}

			fromShape = toShape;

			if (index == inheritanceList.size() - 1) {

				defineAttributes(ucdc, cdoc, fromShape);
				defineOperations(ucdc, cdoc, fromShape);
			}

			if (classBox == null) {
				classBox = fromShape;
			}

			row++;
			index++;
		}

		return classBox;
	}

	private ClassBox defineInterface(UmlClassDiagramCreator ucdc, ClassDoc doc) {

		int row = 0;

		ArrayList<Type> inheritanceList = getInheritancePath(doc);

		int numCol = 0;
		int index = 0;

		for (Type type : inheritanceList) {

			Type[] interfaceTypes = type.asClassDoc().interfaceTypes();

			if (interfaceTypes.length + 1 > numCol) {
				numCol = interfaceTypes.length + 1;
			}

			if (index == 0 && interfaceTypes.length > 0) {
				row = 1;
			}

			index++;
		}

		int middleCol = numCol / 2;

		row = index - 1;

		index = 0;

		ClassBox fromShape = null;
		ClassBox toShape = null;

		String interfaceName = synopsis
				.typeToString(doc, showFullQualifiedName);

		fromShape = ucdc
				.addClassBox(row, middleCol, interfaceName, "interface");

		row--;

		for (Type type : inheritanceList) {

			ClassDoc cdoc = type.asClassDoc();
			Type[] interfaceTypes = cdoc.interfaceTypes();

			int typeIndex = 1;

			for (Type interfaceType : interfaceTypes) {

				interfaceName = synopsis.typeToString(interfaceType,
						showFullQualifiedName);

				if (typeIndex == 1) {

					toShape = ucdc.addClassBox(row, middleCol, interfaceName,
							"interface");
 
				} else {

					int remainder = typeIndex % 2;
					int offset = (typeIndex / 2) + remainder;

					if (remainder == 1) {
						offset *= -1;
					}

					toShape = ucdc.addClassBox(row, middleCol + offset,
							interfaceName, "interface");
				}

				typeIndex++;

				if (fromShape != null && toShape != null) {
					ucdc.addInheritance(fromShape, toShape);
				}

			}


			if (index == 0) {
				
				defineAttributes(ucdc, cdoc, fromShape);
				defineOperations(ucdc, cdoc, fromShape);
			}

			fromShape = toShape;
			row--;
			index++;
		}

		return fromShape;
	}

	/**
	 * Liefert die Höhe des zuletzt erzeugten Klassendiagramms.
	 * 
	 * @return int
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * Liefert die Breite des zuletzt erzeugten Klassendiagramms.
	 * 
	 * @return int
	 */
	public int getImageWidth() {
		return imageWidth;
	}
}
