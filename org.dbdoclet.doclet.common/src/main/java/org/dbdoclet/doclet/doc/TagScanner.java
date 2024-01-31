package org.dbdoclet.doclet.doc;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTreeScanner;

public class TagScanner extends DocTreeScanner<BlockTagTree, Void> {

	@Override
	public ReturnTree visitReturn(ReturnTree returnTree, Void p) {
		return returnTree;
	}
}
