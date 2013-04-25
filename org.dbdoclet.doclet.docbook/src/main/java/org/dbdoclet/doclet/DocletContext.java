/* 
 * ### Copyright (C) 2001-2007 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet;

import java.io.File;

import org.dbdoclet.service.StringServices;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;


public class DocletContext {

    private ClassDoc classDoc;
    private ExecutableMemberDoc methodDoc;
    private FieldDoc fieldDoc;
    private PackageDoc packageDoc;
    private String comment;
    private String contextName;
    private boolean isOverview = false;

    public DocletContext() {
        super();
    }

    public DocletContext(String comment) {

        if (comment == null) {

            throw new IllegalArgumentException(
                "The argument comment must not be null!");
        }

        this.comment = comment;
    }

    public void isOverview(boolean isOverview) {

        this.isOverview = isOverview;
    }

    public boolean isOverview() {

        return isOverview;
    }

    public void setFieldDoc(ClassDoc classDoc, FieldDoc fieldDoc) {

        if (classDoc == null) {

            throw new IllegalArgumentException(
                "The argument classDoc must not be null!");
        }

        if (fieldDoc == null) {

            throw new IllegalArgumentException(
                "The argument fieldDoc must not be null!");
        }

        reset();

        this.classDoc = classDoc;
        this.fieldDoc = fieldDoc;

        setContextName("Field");
    }

    public void setMethodDoc(ClassDoc classDoc, ExecutableMemberDoc methodDoc) {

        if (classDoc == null) {

            throw new IllegalArgumentException(
                "The argument classDoc must not be null!");
        }

        if (methodDoc == null) {

            throw new IllegalArgumentException(
                "The argument methodDoc must not be null!");
        }

        reset();

        this.classDoc = classDoc;
        this.methodDoc = methodDoc;

        setContextName("Method");
    }

    public void setClassDoc(ClassDoc classDoc) {

        reset();

        this.classDoc = classDoc;
        setContextName("Class");
    }

    public void setPackageDoc(PackageDoc packageDoc) {

        reset();

        this.packageDoc = packageDoc;
        setContextName("Package");
    }

    public ClassDoc getClassDoc() {

        return classDoc;
    }

    public String getClassName() {

        if (classDoc == null) {

            throw new IllegalStateException(
                "The field classDoc must not be null!");
        }

        String name = classDoc.name();

        return name;
    }

    public PackageDoc getPackageDoc() {

        return packageDoc;
    }

    public String getPackagePath() {

        if (packageDoc == null) {

            throw new IllegalStateException(
                "The field packageDoc must not be null!");
        }

        String name = packageDoc.name();
        name = StringServices.replace(name, ".", File.separator);

        if (name.endsWith(File.separator) == false) {

            name += File.separator;
        }

        return name;
    }

    public void setComment(String comment) {

        this.comment = comment;
    }

    public void setContextName(String name) {

        if (name == null) {

            throw new IllegalArgumentException(
                "The argument name must not be null!");
        }

        contextName = name;
    }

    public String getContextName() {

	if (fieldDoc != null) {

            if (contextName != null) {
                return contextName + "-" + fieldDoc.qualifiedName();
            } else {
                return fieldDoc.qualifiedName();
            }
        }

        if (methodDoc != null) {
            if (contextName != null) {
                return contextName + "-" + methodDoc.qualifiedName();
            } else {
                return methodDoc.qualifiedName();
            }
        }

        if (classDoc != null) {
            if (contextName != null) {
                return contextName + "-" + classDoc.qualifiedName();
            } else {
                return classDoc.qualifiedName();
            }
        }

        if (packageDoc != null) {
            if (contextName != null) {
                return contextName + "-" + packageDoc.name();
            } else {
                return packageDoc.name();
            }
        }

        if (contextName != null) {
            return contextName;
        }

        return "UnknownContext";
    }

    public String toString() {

        String buffer = "[Context ";

        if (packageDoc != null) {
            buffer += (packageDoc.name() + " ");
        }

        if (classDoc != null) {
            buffer += (classDoc.name() + " ");
        }

        if (comment != null) {
            buffer += comment;
        }

        buffer += "] ";

        return buffer;
    }

    private void reset() {

        fieldDoc = null;
        methodDoc = null;
        classDoc = null;
        packageDoc = null;
        contextName = "";
        comment = "";
    }
}
