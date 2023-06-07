package org.dbdoclet.doclet.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner9;

public class ConstructorScanner extends ElementScanner9<Void, Integer> {
	
	    private HashSet<ExecutableElement> set = new HashSet<>();
		private Set<? extends Element> elements;
	    
	    public ConstructorScanner(Set<? extends Element> elements) {
			this.elements = elements;
	    }
	    
	    public ConstructorScanner(List<? extends Element> elements) {
	    	this.elements = new HashSet<>(elements);  
	    }

		public Set<ExecutableElement> getConstructorElements() {
	    	set.clear();
	    	scan(elements, 0);
	    	return set;
	    }
	    
		@Override
		public Void scan(Element elem, Integer depth) {

			ElementKind kind = elem.getKind();
			if (kind == ElementKind.CONSTRUCTOR) {
				set.add((ExecutableElement) elem);
			}

			return super.scan(elem, depth + 1);
		}

}
