/* 
 * ### Copyright (C) 2001-2007 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class MethodServices {

	private static Log logger = LogFactory.getLog(MethodServices.class);

	public static MethodDoc implementedMethod(MethodDoc methodDoc) {

		if (methodDoc == null) {

			throw new IllegalArgumentException(
					"The argument methodDoc must not be null!");
		}

		ClassDoc classDoc = methodDoc.containingClass();
		ClassDoc[] interfaces;
		MethodDoc[] methods;

		String methodName = methodDoc.name() + methodDoc.signature();
		;

		String interfaceMethodName;

		if (classDoc != null) {

			interfaces = classDoc.interfaces();

			for (int i = 0; i < interfaces.length; i++) {

				methods = interfaces[i].methods();

				for (int j = 0; j < methods.length; j++) {

					interfaceMethodName = methods[j].name()
							+ methods[j].signature();

					logger.debug("Method name=" + methodName);
					logger.debug("Interface method name=" + interfaceMethodName);

					if (methodName.equals(interfaceMethodName)) {

						return methods[j];
					}
				}
			}
		}

		return null;
	}
}
