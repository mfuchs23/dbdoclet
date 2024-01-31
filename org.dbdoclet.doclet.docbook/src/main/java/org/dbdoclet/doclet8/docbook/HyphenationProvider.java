package org.dbdoclet.doclet8.docbook;

import org.dbdoclet.xiphias.Hyphenation;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class HyphenationProvider implements Provider<Hyphenation> {

	@Inject DbdScript script;
	
	@Override
	public Hyphenation get() {
		
		Hyphenation hyphenation = new Hyphenation();
		hyphenation.setHyphenationChar(script.getHyphenationChar());
		return hyphenation;
	}

}
