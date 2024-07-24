package org.dbdoclet.doclet8.docbook;

import org.dbdoclet.xiphias.Hyphenation;
import org.dbdoclet.xiphias.dom.INodeVisitor;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class HyphenationVisitor implements INodeVisitor {

	@Override
	public void openTag(Node node) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void accept(Node node) throws Exception {
		if (node instanceof Text) {
			
			Text text = (Text) node;
			String buffer = text.getData();
			Hyphenation hyphenation = new Hyphenation();
			buffer = hyphenation.hyphenateAfter(buffer, "\\.");
			text.setData(buffer);
		}
	}

	@Override
	public void closeTag(Node node) throws Exception {
		// TODO Auto-generated method stub

	}

}
