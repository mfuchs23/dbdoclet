/*
 * ### Copyright (C) 2005-2009 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.docbook.DbdScript;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.svg.UmlClassDiagramCreator;
import org.dbdoclet.svg.shape.ClassBox;
import org.dbdoclet.svg.shape.Shape;
import org.dbdoclet.xiphias.ImageServices;

import com.google.inject.Inject;

/**
 * Die Klasse ClassDiagramManager erstellt ein UML Klassendiagramm aus einem
 * {@linkplain TypeElement} Objekt.
 * 
 * @author michael
 * 
 */
public class ClassDiagramManager {

	private static Log logger = LogFactory.getLog(ClassDiagramManager.class);

	@Inject
	private DbdScript script;
	@Inject
	private DocManager docManager;

	private int imageHeight;
	private int imageWidth;
	private final boolean showFullQualifiedName = false;


	public ArrayList<TypeElement> getInheritancePath(TypeElement doc) {

		if (doc == null) {
			throw new IllegalArgumentException("Parameter doc is null!");
		}

		ArrayList<TypeElement> list = new ArrayList<>();
		list.add(doc);

		int index = 0;

		if (docManager.isInterface(doc)) {

			while (doc != null) {

				logger.debug(String.valueOf(index++) + " doc=" + doc);

				List<? extends TypeMirror> types = doc.getInterfaces();
				if (types.size()> 0) {
					doc = (TypeElement) docManager.getTypeUtils().asElement(types.get(0));
					list.add(doc);
				} else {
					doc = null;
				}
			}

		} else {

			TypeElement superType = docManager.getSuperclass(doc);

			while (superType != null) {

				logger.debug(String.valueOf(index++) + " superType="
						+ superType);

				if (script.isClassDiagramIncludesObject() == false
						&& docManager.getQualifiedName(superType).equals(
								"java.lang.Object")) {
					break;
				}

				list.add(superType);
				superType = docManager.getSuperclass(superType);
			}
		}

		return list;
	}

	public String createClassDiagram(TypeElement cdoc, File outdir)
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
							docManager.getQualifiedName(cdoc), ".", Sfv.FSEP));

			FileServices.createPath(path);

			UmlClassDiagramCreator ucdc = new UmlClassDiagramCreator();
			ucdc.setInterfaceBackgroundColor(Color.orange);
			ucdc.setFont(script.getClassDiagramFontFamily(), script.getClassDiagramFontSize());
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
					+ StringServices.replace(docManager.getQualifiedName(cdoc), ".",
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
	private void buildDiagram(UmlClassDiagramCreator ucdc, TypeElement doc) {

		if (docManager.isInterface(doc)) {
			defineInterface(ucdc, doc);
		} else {
			defineClass(ucdc, doc);
		}
	}

	private void defineAttributes(UmlClassDiagramCreator ucdc, TypeElement doc,
			ClassBox classBox) {
		
		Set<VariableElement> fields = docManager.getFieldElements(doc);

		if (script.isClassDiagramContainsAttributes() && fields.size() > 0) {

			ucdc.addLine(classBox);
			for (var field : fields) {
				ucdc.addAttribute(classBox, String.format("%s %s: %s",
						createVisibilityIndicator(field), docManager.getName(field),
						docManager.typeToString(field.asType(), false)));
			}
		}
	}

	private void defineOperations(UmlClassDiagramCreator ucdc, TypeElement classElem,
			ClassBox classBox) {

		Set<ExecutableElement> methods = docManager.getMethodElements(classElem);
		Set<ExecutableElement> constructors = docManager.getConstructorElements(classElem);

		if (script.isClassDiagramContainsOperations()
				&& (methods.size() > 0 || constructors.size() > 0)) {

			ucdc.addLine(classBox);

			for (var constructor : constructors) {
				ucdc.addMethod(classBox, String.format("<<Create>> %s %s(%s)",
						createVisibilityIndicator(constructor),
						docManager.getName(classElem),
						createOperationParameterList(constructor)));
			}

			for (var method : methods) {
				ucdc.addMethod(classBox, String.format("%s %s(%s): %s",
						createVisibilityIndicator(method), docManager.getName(method),
						createOperationParameterList(method),
						docManager.typeToString(method.getReturnType(), false)));
			}
		}
	}

	private String createVisibilityIndicator(Element field) {

		String indicator = "";

		if (field == null) {
			return indicator;
		}

		if (docManager.isPublic(field)) {
			return "+";
		}

		if (docManager.isProtected(field)) {
			return "#";
		}

		if (docManager.isPrivate(field)) {
			return "-";
		}

		if (docManager.isPackagePrivate(field)) {
			return "~";
		}

		return indicator;
	}

	private String createOperationParameterList(ExecutableElement method) {

		StringBuilder buffer = new StringBuilder();

		int index = 0;
		List<? extends VariableElement> parameters = method.getParameters();
		for (var paramDoc : parameters) {
			
			String type = docManager.typeToString(paramDoc.asType(), false);
			if (method.isVarArgs() && index == parameters.size()-1) {
				type = docManager.varArgsTypeToString(paramDoc.asType(), false);
			}
			
			buffer.append(String.format("%s: %s,\n", docManager.getName(paramDoc), type));
			index++;
		}

		String parametersAsText = buffer.toString();
		if (parametersAsText.length() > 0) {
			parametersAsText = StringServices
					.cutSuffix(parametersAsText, ",\n");
		}

		return parametersAsText.trim();
	}

	private ClassBox defineClass(UmlClassDiagramCreator ucdc, TypeElement doc) {

		int row = 0;

		ArrayList<TypeElement> inheritanceList = getInheritancePath(doc);
		Collections.reverse(inheritanceList);

		int numCol = 0;
		int index = 0;

		for (var type : inheritanceList) {

			List<? extends TypeMirror> interfaceTypes = type.getInterfaces();

			if (interfaceTypes.size() + 1 > numCol) {
				numCol = interfaceTypes.size() + 1;
			}

			if (index == 0 && interfaceTypes.size() > 0) {
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

		for (TypeElement type : inheritanceList) {

			String className = docManager.typeToString(type.asType(),
					showFullQualifiedName);

			if (docManager.isAnnotationType(type)) {

				toShape = ucdc.addClassBox(row, middleCol, className,
						"annotation");

			} else if (docManager.isInterface(type)) {

				toShape = ucdc.addClassBox(row, middleCol, className,
						"interface");

			} else if (type.getTypeParameters().size() > 0) {

				ArrayList<String> templateParameters = new ArrayList<String>();

				for (TypeParameterElement typeParameter : type.getTypeParameters()) {
					templateParameters.add(String.format("%s: %s",
							typeParameter.getSimpleName(),
							docManager.typeToString(typeParameter.asType(), false)));
				}

				logger.debug("className: " + className);
				toShape = ucdc.addParameterizedClassBox(row, middleCol,
						className, templateParameters);

			} else {

				logger.debug("className: " + className);
				toShape = ucdc.addClassBox(row, middleCol, className);
			}

			if (fromShape != null && toShape != null) {

				if (docManager.isInterface(type)) {
					ucdc.addRealization(toShape, fromShape);
				} else {
					ucdc.addInheritance(toShape, fromShape);
				}
			}

			List<? extends TypeMirror> interfaceTypes = type.getInterfaces();

			int typeIndex = 1;

			for (TypeMirror interfaceType : interfaceTypes) {

				int remainder = typeIndex % 2;
				int offset = (typeIndex / 2) + remainder;

				if (remainder == 1) {
					offset *= -1;
				}

				String interfaceName = docManager.typeToString(interfaceType,
						showFullQualifiedName);

				if (docManager.isAnnotationType(type)) {

					shape = ucdc.addClassBox(row - 1, middleCol + offset,
							interfaceName, "annotation");
					ucdc.addInheritance(toShape, shape);

				} else if (docManager.isInterface(type) && docManager.isInterface(doc) == false) {

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

				defineAttributes(ucdc, doc, fromShape);
				defineOperations(ucdc, doc, fromShape);
			}

			if (classBox == null) {
				classBox = fromShape;
			}

			row++;
			index++;
		}

		return classBox;
	}

	private ClassBox defineInterface(UmlClassDiagramCreator ucdc, TypeElement doc) {

		int row = 0;

		ArrayList<TypeElement> inheritanceList = getInheritancePath(doc);

		int numCol = 0;
		int index = 0;

		List<? extends TypeMirror> interfaceTypes;
		for (TypeElement type : inheritanceList) {

			interfaceTypes = type.getInterfaces();

			if (interfaceTypes.size() + 1 > numCol) {
				numCol = interfaceTypes.size() + 1;
			}

			if (index == 0 && interfaceTypes.size() > 0) {
				row = 1;
			}

			index++;
		}

		int middleCol = numCol / 2;

		row = index - 1;

		index = 0;

		ClassBox fromShape = null;
		ClassBox toShape = null;

		String interfaceName = docManager.typeToString(doc.asType(), showFullQualifiedName);
		fromShape = ucdc.addClassBox(row, middleCol, interfaceName, "interface");

		row--;

		for (TypeElement type : inheritanceList) {

			interfaceTypes = type.getInterfaces();
			int typeIndex = 1;

			for (TypeMirror interfaceType : interfaceTypes) {

				interfaceName = docManager.typeToString(interfaceType,
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
				defineAttributes(ucdc, doc, fromShape);
				defineOperations(ucdc, doc, fromShape);
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
