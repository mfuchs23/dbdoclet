package org.dbdoclet.doclet.scanner;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.ElementScanner9;

public class PackageScanner extends ElementScanner9<Void, Integer> {
	
	    private HashSet<PackageElement> pkgSet = new HashSet<>();
		private Set<? extends Element> elements;
	    
	    public PackageScanner(Set<? extends Element> elements) {
			this.elements = elements;
	    }
	    
	    public Set<PackageElement> getPackageElements() {
	    	pkgSet.clear();
	    	scan(elements, 0);
	    	return pkgSet;
	    }
	    
		@Override
		public Void scan(Element elem, Integer depth) {

			ElementKind kind = elem.getKind();
			if (kind == ElementKind.PACKAGE) {
				pkgSet.add((PackageElement) elem);
			}

			return super.scan(elem, depth + 1);
		}

}
