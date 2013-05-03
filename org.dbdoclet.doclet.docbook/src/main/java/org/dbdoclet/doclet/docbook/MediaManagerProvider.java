package org.dbdoclet.doclet.docbook;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Provider;

import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.doclet.ReferenceManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.tag.docbook.DocBookTagFactory;

public class MediaManagerProvider implements Provider<MediaManager> {

	@Inject
	DbdTransformer transformer;

	@Inject
	DocBookTagFactory tagFactory;

	@Inject
	ResourceBundle res;

	@Inject
	ReferenceManager referenceManager;

	@Inject
	DbdScript script;

	@Inject
	Style style;

	@Inject
	StatisticData statisticData;

	@Inject
	TagManager tagManager;

	@Inject
	DocletContext context;

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

		mediaManager.setResourceBundle(res);
		mediaManager.setReferenceManager(referenceManager);
		mediaManager.setScript(script);
		mediaManager.setStatisticData(statisticData);
		mediaManager.setStyle(style);
		mediaManager.setTagFactory(tagFactory);
		mediaManager.setTagManager(tagManager);
		mediaManager.setHtmlDocBookTrafo(transformer);
		mediaManager.setDocletContext(context);

		return mediaManager;
	}

}
