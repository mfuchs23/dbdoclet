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
package org.dbdoclet.doclet8.docbook;

import org.dbdoclet.doclet8.DocletException;
import org.dbdoclet.tag.docbook.DocBookElement;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

public interface Style {

    public boolean addClassSynopsis(ClassDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addFieldSynopsis(FieldDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addInheritancePath(ClassDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addMethodSpecifiedBy(MethodDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addMemberSynopsis(ExecutableMemberDoc doc, DocBookElement parent) throws DocletException;

    public boolean addMetaInfo(Doc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addParamInfo(ExecutableMemberDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addSerialFieldsInfo(FieldDoc doc, DocBookElement parent)
	    throws DocletException;

    public boolean addThrowsInfo(ExecutableMemberDoc doc, DocBookElement parent)
	    throws DocletException;
}
