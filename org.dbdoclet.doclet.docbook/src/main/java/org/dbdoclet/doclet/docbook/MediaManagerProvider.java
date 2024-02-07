package org.dbdoclet.doclet.docbook;

import java.util.ResourceBundle;

import org.dbdoclet.doclet.CDI;
import org.dbdoclet.doclet.ClassDiagramManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.doc.DocFormatter;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.doc.ReferenceManager;
import org.dbdoclet.doclet.doc.TagManager;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.xiphias.Hyphenation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class MediaManagerProvider implements Provider<MediaManager> {

	@Inject
	private DocManager docManager;
	@Inject
	private Hyphenation hyphenation;
	@Inject
	private ResourceBundle res;
	@Inject
	private DbdScript script;
	@Inject
	private StatisticData statisticData;
	@Inject
	private Style style;
	@Inject
	private DocBookTagFactory tagFactory;
	@Inject
	private ClassDiagramManager classDiagramManager;
	@Inject
	private DocFormatter docFormatter;
	@Inject
	private DbdTransformer transformer;
	
	@Override
	public MediaManager get() {

		String mgr = script.getDocumentElement();

		MediaManager mediaManager = new ArticleManager();

		if (mgr != null && mgr.equals("reference")) {
			// mediaManager = new RefentryManager();
		} else if (mgr != null && mgr.equalsIgnoreCase("article")) {
			mediaManager = new ArticleManager();
		} else if (mgr != null && mgr.equalsIgnoreCase("xmi")) {
			// mediaManager = new DodoManager();
		} else {
			mediaManager = new BookManager();
		}
		
		CDI.getInjector().injectMembers(mediaManager);
		
		mediaManager.setDocManager(docManager);
		mediaManager.setDocFormatter(docFormatter);
		mediaManager.setHyphenation(hyphenation);
		mediaManager.setResourceBundle(res);
		mediaManager.setScript(script);
		mediaManager.setStatisticData(statisticData);
		mediaManager.setStyle(style);
		mediaManager.setTagFactory(tagFactory);
		mediaManager.setClassDiagramManager(classDiagramManager);
		mediaManager.setHtmlDocBookTrafo(transformer);
		
		statisticData.setDocManager(docManager);
		
		return mediaManager;
	}

}
