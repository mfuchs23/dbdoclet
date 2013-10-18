package org.dbdoclet.doclet.docbook;

import javax.inject.Inject;
import javax.inject.Provider;

import org.dbdoclet.xiphias.Hyphenation;


public class HyphenationProvider implements Provider<Hyphenation> {

	@Inject DbdScript script;
	
	@Override
	public Hyphenation get() {
		
		Hyphenation hyphenation = new Hyphenation();
		hyphenation.setHyphenationChar(script.getHyphenationChar());
		return hyphenation;
	}

}
