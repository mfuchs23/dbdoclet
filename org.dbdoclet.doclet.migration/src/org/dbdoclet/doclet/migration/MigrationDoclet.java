package org.dbdoclet.doclet.migration;

 
import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

import org.dbdoclet.doclet.doc.DocManager;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
 
/**
 * A minimal doclet that just prints out the names of the
 * selected elements.
 */
public class MigrationDoclet implements Doclet {
    
    private Reporter reporter;

	@Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
 
    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Collections.emptySet();
    }
 
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
 
    private static final boolean OK = true;
 
    private void pln(String text) {
    	System.out.println(text);
    }
    
    @Override
    public boolean run(DocletEnvironment env) {
        env.getSpecifiedElements()
                .forEach(System.out::println);
        DocManager dm = new DocManager();
        DocTrees docTrees = env.getDocTrees();
        for (PackageElement pkg : dm.getPackageElements()) {
        	DocCommentTree dcTree = docTrees.getDocCommentTree(pkg);
        	pln("Package: " + pkg.toString());	
        	if (nonNull(dcTree)) {
        		pln("Comment: " + dcTree.toString());	
        	}
        	for (Element typeElem : pkg.getEnclosedElements()) {
        		pln("Type: " + typeElem.toString());
        		pln("TypeMirror: " + typeElem.asType().toString());
        		dcTree = docTrees.getDocCommentTree(typeElem);
        		if (nonNull(dcTree)) {
        			pln("Comment: " + dcTree.toString());	
        			pln("Body: " + dcTree.getBody());	
        			pln("BlockTags: " + dcTree.getBlockTags());	
        		}
        		for (Element memberElem : typeElem.getEnclosedElements()) {
        			pln("Member: " + memberElem.toString());
        			dcTree = docTrees.getDocCommentTree(memberElem);
        			if (nonNull(dcTree)) {
        				pln("Comment: " + dcTree.toString());	
        				pln("Body: " + dcTree.getBody());	
        				pln("BlockTags: " + dcTree.getBlockTags());	
        				for (DocTree dtree : dcTree.getBlockTags()) {
        					if (dtree instanceof SeeTree) {
        						SeeTree seeTree = (SeeTree) dtree;
								pln("SeeTree: " + seeTree.getReference());
        					}
        				}
        			}
        		}
        	}
        }
        return OK;
    }
}
