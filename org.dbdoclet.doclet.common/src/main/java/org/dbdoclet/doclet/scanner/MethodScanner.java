package org.dbdoclet.doclet.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner9;

public class MethodScanner extends ElementScanner9<Void, Integer> {
	
	    private TreeSet<ExecutableElement> set = new TreeSet<>((a,b) -> (a.getSimpleName().toString().compareTo(b.getSimpleName().toString())));
		private Set<? extends Element> elements;
	    
	    public MethodScanner(Set<? extends Element> elements) {
			this.elements = elements;
	    }
	    
	    public MethodScanner(List<? extends Element> elements) {
	    	this.elements = new HashSet<>(elements);  
	    }

		public SortedSet<ExecutableElement> getMethodElements() {
	    	set.clear();
	    	scan(elements, 0);
	    	return set;
	    }
	    
		@Override
		public Void scan(Element elem, Integer depth) {

			ElementKind kind = elem.getKind();
			if (kind == ElementKind.METHOD) {
				set.add((ExecutableElement) elem);
			}

			return super.scan(elem, depth + 1);
		}

}
