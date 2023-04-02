package org.dbdoclet.doclet.scanner;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner9;

public class ClassScanner extends ElementScanner9<Void, Integer> {
	
	    private HashSet<TypeElement> set = new HashSet<>();
		private Set<? extends Element> elements;
	    
	    public ClassScanner(Set<? extends Element> elements) {
			this.elements = elements;
	    }
	    
	    public Set<TypeElement> getClassElements() {
	    	set.clear();
	    	scan(elements, 0);
	    	return set;
	    }
	    
		@Override
		public Void scan(Element elem, Integer depth) {

			ElementKind kind = elem.getKind();
			if (kind == ElementKind.CLASS) {
				set.add((TypeElement) elem);
			}

			return super.scan(elem, depth + 1);
		}

}
