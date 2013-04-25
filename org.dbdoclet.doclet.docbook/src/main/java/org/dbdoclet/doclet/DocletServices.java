package org.dbdoclet.doclet;

import org.dbdoclet.xiphias.XmlServices;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class DocletServices {

    public static String typeToString(Type type, boolean showFullQualifiedName) {

	return typeToString(type, showFullQualifiedName, 0);
    }

    public static String typeToString(Type type, boolean showFullQualifiedName, int level) {

	if (type == null) {
	    return "";
	}

	StringBuilder buffer = new StringBuilder();

	TypeVariable typeVariable = type.asTypeVariable();

	buffer.append(getTypeNameWithDimension(type, showFullQualifiedName));

	if (typeVariable != null && level == 0) {
	    processBounds(buffer, typeVariable.bounds(), "extends", showFullQualifiedName, level);
	}

	ParameterizedType pType = type.asParameterizedType();

	if (pType != null) {

	    Type[] arguments = pType.typeArguments();

	    if (arguments != null && arguments.length > 0) {

		buffer.append('<');

		boolean first = true;

		for (Type argument : arguments) {

		    if (first == false) {
			buffer.append(", ");
		    }

		    buffer.append(typeToString(argument, showFullQualifiedName, level + 1));
		    first = false;
		}

		buffer.append('>');
	    }
	}

	WildcardType wType = type.asWildcardType();

	if (wType != null) {
	    processBounds(buffer, wType.superBounds(), "super", showFullQualifiedName, level);
	    processBounds(buffer, wType.extendsBounds(), "extends", showFullQualifiedName, level);
	}

	String typeName = buffer.toString();
	typeName = XmlServices.makeWrapable(typeName, ".");
	
	return typeName;
    }

    public static void processBounds(StringBuilder buffer, Type[] bounds, String kind, boolean showFullQualifiedName,
	    int level) {

	if (kind == null) {
	    throw new IllegalArgumentException("The argument kind must not be null!");
	}

	kind = kind.trim();

	if (bounds != null && bounds.length > 0) {

	    boolean first = true;

	    for (Type bound : bounds) {

		buffer.append(first ? " " + kind + " " : " & ");

		if (kind.equals("extends") && level == 0) {
		    buffer.append(typeToString(bound, showFullQualifiedName, level + 1));
		} else {
		    buffer.append(getTypeNameWithDimension(bound, showFullQualifiedName));
		}

		first = false;
	    }
	}
    }

    public static String getTypeNameWithDimension(Type type, boolean showFullQualifiedName) {

	StringBuilder buffer = new StringBuilder();

	if (showFullQualifiedName == true) {
	    buffer.append(type.qualifiedTypeName());
	} else {
	    buffer.append(type.simpleTypeName());
	}

	buffer.append(type.dimension());

	return buffer.toString();
    }

    public static void printType(Type type) {
	
	StringBuilder buffer = new StringBuilder();
	
	buffer.append("============================================\n");
	
	buffer.append("Type=");
	buffer.append(type);
	buffer.append('\n');
	
	ClassDoc classDoc = type.asClassDoc();
	buffer.append("ClassDoc=");
	buffer.append(classDoc);
	buffer.append('\n');
	
	if (classDoc != null) {
	
	    buffer.append("isAbstract=");
	    buffer.append(classDoc.isAbstract());
	    buffer.append('\n');
	}
	
	buffer.append("TypeVariable=");
	buffer.append(type.asTypeVariable());
	buffer.append('\n');

	buffer.append("ParameterizedType=");
	buffer.append(type.asParameterizedType());
	buffer.append('\n');

	buffer.append("WildcardType=");
	buffer.append(type.asWildcardType());
	buffer.append('\n');

	buffer.append("AnnotationTypeDoc=");
	buffer.append(type.asAnnotationTypeDoc());
	buffer.append('\n');

	System.out.println(buffer.toString());
    }


}
