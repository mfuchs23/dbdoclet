package org.dbdoclet.doclet8.docbook;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Provider;

import org.dbdoclet.doclet8.ReferenceManager;
import org.dbdoclet.doclet8.StatisticData;
import org.dbdoclet.doclet8.TagManager;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.xiphias.Hyphenation;

public class MediaManagerProvider implements Provider<MediaManager> {

	@Inject
	Hyphenation hyphenation;
	@Inject
	ReferenceManager referenceManager;
	@Inject
	ResourceBundle res;
	@Inject
	DbdScript script;
	@Inject
	StatisticData statisticData;
	@Inject
	Style style;
	@Inject
	DocBookTagFactory tagFactory;
	@Inject
	TagManager tagManager;
	@Inject
	DbdTransformer transformer;
	
	@Override
	public MediaManager get() {

		String mgr = script.getDocumentElement();

		MediaManager mediaManager;

		if (mgr != null && mgr.equals("reference")) {
			mediaManager = new RefentryManager();
		} else if (mgr != null && mgr.equalsIgnoreCase("article")) {
			mediaManager = new ArticleManager();
		} else if (mgr != null && mgr.equalsIgnoreCase("xmi")) {
			mediaManager = new DodoManager();
		} else {
			mediaManager = new BookManager();
		}
		
		mediaManager.setHyphenation(hyphenation);
		mediaManager.setResourceBundle(res);
		mediaManager.setReferenceManager(referenceManager);
		mediaManager.setScript(script);
		mediaManager.setStatisticData(statisticData);
		mediaManager.setStyle(style);
		mediaManager.setTagFactory(tagFactory);
		mediaManager.setTagManager(tagManager);
		mediaManager.setHtmlDocBookTrafo(transformer);

		return mediaManager;
	}

}
