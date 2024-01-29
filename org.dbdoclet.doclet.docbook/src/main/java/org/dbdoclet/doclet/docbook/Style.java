/*
 * ### Copyright (C) 2001-2003 Michael Fuchs ###
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Author: Michael Fuchs
 * E-Mail: mfuchs@unico-consulting.com
 *
 * RCS Information:
 * ---------------
 * Id.........: $Id: Style.java,v 1.1.1.1 2004/12/21 14:01:24 mfuchs Exp $
 * Author.....: $Author: mfuchs $
 * Date.......: $Date: 2004/12/21 14:01:24 $
 * Revision...: $Revision: 1.1.1.1 $
 * State......: $State: Exp $
 */
package org.dbdoclet.doclet.docbook;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.tag.docbook.DocBookElement;

public interface Style {

    public boolean addClassSynopsis(TypeElement typeElem, DocBookElement parent)
	    throws DocletException;

    public boolean addFieldSynopsis(VariableElement doc, DocBookElement parent)
	    throws DocletException;

    public boolean addInheritancePath(TypeElement doc, DocBookElement parent)
	    throws DocletException;

    public boolean addMethodSpecifiedBy(ExecutableElement doc, DocBookElement parent)
	    throws DocletException;

    public boolean addMemberSynopsis(ExecutableElement elem, DocBookElement parent) throws DocletException;

    public boolean addMetaInfo(Element memberElem, DocBookElement parent)
	    throws DocletException;

    public boolean addParamInfo(ExecutableElement doc, DocBookElement parent)
	    throws DocletException;

    public boolean addSerialFieldsInfo(VariableElement doc, DocBookElement parent)
	    throws DocletException;

    public boolean addThrowsInfo(ExecutableElement commentDoc, DocBookElement parent)
	    throws DocletException;
}
