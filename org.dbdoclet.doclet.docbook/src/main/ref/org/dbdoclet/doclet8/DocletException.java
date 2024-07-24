/* 
 * $Id$
 *
 * ### Copyright (C) 2005 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 *
 * RCS Information
 * Author..........: $Author$
 * Date............: $Date$
 * Revision........: $Revision$
 * State...........: $State$
 */
package org.dbdoclet.doclet8;

import java.util.Stack;


public class DocletException extends Exception {

    private static final long serialVersionUID = 1L;

    public DocletException(Exception oops) {
        super(oops);
    }

    public DocletException(String msg) {
        super(msg);
    }

    public DocletException(String msg, Throwable oops) {
        super(msg, oops);
    }

    public void printDetails() {

	Throwable oops = this;
	Stack<Throwable> stack = new Stack<Throwable>();
	
	while (oops != null) {
	    
	    stack.push(oops);
	    oops = oops.getCause();
	}
	
	int index = 1; 
	while (stack.empty() == false) {
	    
	    oops = stack.pop();
	    System.err.println("[Cause #" + index + " ========================================================]");
	    oops.printStackTrace();
	    index++;
	}
    }
}
/*
 * $Log$
 */
