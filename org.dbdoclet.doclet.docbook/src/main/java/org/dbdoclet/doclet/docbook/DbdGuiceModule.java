package org.dbdoclet.doclet.docbook;

import java.util.ResourceBundle;

import org.dbdoclet.doclet.AbstractDoclet;
import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.doclet.ReferenceManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.doclet.statistic.TotalsDiagram;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.trafo.script.Script;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class DbdGuiceModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(AbstractDoclet.class).to(DocBookDoclet.class).in(Scopes.SINGLETON);
		bind(DocletContext.class).in(Scopes.SINGLETON);
		bind(DocBookTagFactory.class).in(Scopes.SINGLETON);
		bind(MediaManager.class).toProvider(MediaManagerProvider.class).in(
				Scopes.SINGLETON);
		bind(ReferenceManager.class).in(Scopes.SINGLETON);
		bind(StatisticData.class).in(Scopes.SINGLETON);
		bind(ResourceBundle.class).toProvider(ResourceBundleProvider.class).in(
				Scopes.SINGLETON);
		bind(Script.class).in(Scopes.SINGLETON);
		bind(DbdScript.class).in(Scopes.SINGLETON);
		bind(Style.class).to(StyleTable.class).in(Scopes.SINGLETON);
		bind(TagManager.class).in(Scopes.SINGLETON);
		bind(DbdTransformer.class);
		bind(StrictSynopsis.class);
		bind(TotalsDiagram.class);
	}

}
