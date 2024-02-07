package org.dbdoclet.doclet.scanner;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementScanner9;

import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.doc.TagManager;

import com.google.inject.Inject;
import com.sun.source.doctree.DeprecatedTree;

public class DeprecatedScanner extends ElementScanner9<Void, Integer> {
	
	@Inject 
	private DocManager docManager;
	@Inject 
	private TagManager tagManager;
	
	private TreeSet<Element> set = new TreeSet<>((a,b) -> (docManager.getQualifiedName(a).compareTo(docManager.getQualifiedName(b))));
	private Set<? extends Element> elements;
	    

		public void setElements(Set<? extends Element> elements) {
			this.elements = elements;
		}
		
		public SortedSet<Element> getDeprecatedElements() {
	    
			if (isNull(elements)) {
				throw new IllegalStateException("The field elements must not be null!");
			}
			
			set.clear();
	    	scan(elements, 0);
	    	return set;
	    }
	    
		@Override
		public Void scan(Element elem, Integer depth) {

			DeprecatedTree deprecatedTag = tagManager.findDeprecatedTag(elem);
			Deprecated annotation = elem.getAnnotation(Deprecated.class);

			if (nonNull(deprecatedTag) || nonNull(annotation)) {
				set.add(elem);
			}
			
			return super.scan(elem, depth + 1);
		}

}
