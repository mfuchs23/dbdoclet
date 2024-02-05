package org.dbdoclet.doclet.doc;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.logging.Logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

import com.google.inject.Inject;

public class DocFormatter {

	private static Logger logger = Logger.getLogger(Logger.class.getName());

	@Inject
	private DocManager docManager;

	public String typeToString(TypeMirror type, boolean showFqn) {
		return typeToString(type, showFqn, true);
	}

	public String typeToString(TypeMirror type, boolean showFqn, boolean showBounds) {

		if (docManager.isPrimitiveType(type)) {
			return type.toString();
		}

		TypeKind kind = type.getKind();
		StringBuilder buffer = new StringBuilder();

		List<? extends AnnotationMirror> annotationMirrors = type.getAnnotationMirrors();
		for (AnnotationMirror am : annotationMirrors) {
			buffer.append(typeToString(am.getAnnotationType(), showFqn, showBounds));
			buffer.append(" ");
		}

		Element elem = docManager.getTypeUtils().asElement(type);
		if (nonNull(elem)) {
			if (showFqn && elem instanceof TypeElement) {
				buffer.append(((TypeElement) elem).getQualifiedName().toString());
			} else {
				buffer.append(elem.getSimpleName().toString());
			}
		}

		switch (kind) {
		case DECLARED:
			DeclaredType declaredType = (DeclaredType) type;
			for (TypeMirror tm : declaredType.getTypeArguments()) {
				buffer.append('<');
				buffer.append(typeToString(tm, showFqn, showBounds));
				buffer.append('>');
			}
			break;
		case TYPEVAR:
			TypeVariable typeVar = (TypeVariable) type;
			if (showBounds) {
				TypeMirror lowerBound = typeVar.getLowerBound();
				if (TypeKind.NULL != lowerBound.getKind()) {
					buffer.append(" super ");
					buffer.append(typeToString(lowerBound, showFqn, showBounds));
				}

				TypeMirror upperBound = typeVar.getUpperBound();
				if (TypeKind.NULL != upperBound.getKind()) {
					buffer.append(" extends ");
					buffer.append(typeToString(upperBound, showFqn, showBounds));
				}
			}
			break;
		case WILDCARD:
			WildcardType wildcardType = (WildcardType) type;
			buffer.append("?");
			TypeMirror lowerBound = wildcardType.getExtendsBound();
			if (nonNull(lowerBound)) {
				buffer.append(" extends ");
				buffer.append(typeToString(lowerBound, showFqn, showBounds));
			}

			TypeMirror upperBound = wildcardType.getSuperBound();
			if (nonNull(upperBound)) {
				buffer.append(" super ");
				buffer.append(typeToString(upperBound, showFqn, showBounds));
			}
			break;
		case ARRAY:
			ArrayType arrayType = (ArrayType) type;
			int count = 1;
			ArrayType inner = arrayType;
			while (TypeKind.ARRAY == inner.getComponentType().getKind()) {
				inner = (ArrayType) inner.getComponentType();
				count++;
			}			
			buffer.append(typeToString(inner.getComponentType(), showFqn, showBounds));
			for (int i=0; i<count; i++) {
				buffer.append("[]");
			}			
			break;
		case BOOLEAN:
		case BYTE:
		case CHAR:
		case DOUBLE:
		case ERROR:
		case EXECUTABLE:
		case FLOAT:
		case INT:
		case INTERSECTION:
		case LONG:
		case MODULE:
		case NONE:
		case NULL:
		case OTHER:
		case PACKAGE:
		case SHORT:
		case UNION:
		case VOID:
		default:
			logger.info(String.format("Unsopprted TypeKind: %s", kind));
			break;
		}

		return buffer.toString();
	}

	public String varArgsTypeToString(TypeMirror type, boolean showFqn, boolean showBounds) {
		String typeName = typeToString(type, showFqn, showBounds);
		return typeName.replace("[]", "...");
	}

	public String createMethodFlatSignature(ExecutableElement executableElement) {

		StringBuilder buffer = new StringBuilder();
		buffer.append('(');

		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), false, false));
			buffer.append(", ");
		}

		if (buffer.length() > 2) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}

		buffer.append(')');
		return buffer.toString();
	}

	public String createMethodPrettySignature(ExecutableElement executableElement) {
		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), false, false));
			buffer.append(" ");
			buffer.append(param.getSimpleName());
			buffer.append(", ");
		}
		if (buffer.length() > 2) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}
		buffer.append(')');
		return buffer.toString();
	}

	public String createMethodSignature(ExecutableElement executableElement) {

		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), true, false));
			buffer.append(", ");
		}

		if (buffer.length() > 2) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}

		buffer.append(')');
		return buffer.toString();
	}
}
