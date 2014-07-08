/*
 * $Id$
 *
 * ### Copyright (C) 2006 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.AbstractDoclet;
import org.dbdoclet.doclet.DocletOptions;
import org.dbdoclet.doclet.InstanceFactory;
import org.dbdoclet.doclet.util.PackageServices;
import org.dbdoclet.doclet.util.ReleaseServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.trafo.TrafoScriptManager;

import com.google.inject.Guice;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

/**
 * The class <code>DocBookDoclet</code> implements a javadoc doclet, which
 * creates DocBook XML..
 * 
 * @author <a href ="mailto:michael.fuchs@dbdoclet.org">Michael Fuchs</a>
 */
public final class DocBookDoclet extends AbstractDoclet {

	private static Log logger = LogFactory
			.getLog(DocBookDoclet.class.getName());

	public static void copyDocFiles(RootDoc root, String sourcepath,
			File outdir, DbdScript dbdScript) throws IOException {

		if (root == null) {
			throw new IllegalArgumentException("Parameter root is null!");
		}

		if (outdir == null) {
			throw new IllegalArgumentException("Parameter outdir is null!");
		}

		if (sourcepath == null) {
			sourcepath = ".";
		}

		if (dbdScript == null) {
			throw new IllegalStateException(
					"The field dbdScript must not be null!");
		}

		logger.debug("docfilespath=" + sourcepath);

		String fsep = System.getProperty("file.separator");

		PackageDoc[] pkgs = root.specifiedPackages();

		String srcdir;
		String destdir;

		ArrayList<String> dirs;
		Iterator<String> iterator;

		for (int i = 0; i < pkgs.length; i++) {

			logger.debug("pkg=" + pkgs[i]);

			dirs = PackageServices.findDocFilesDirectories(pkgs[i], sourcepath);
			iterator = dirs.iterator();

			while (iterator.hasNext()) {

				srcdir = iterator.next();
				logger.debug("srcdir=" + srcdir);

				destdir = FileServices.appendPath(outdir,
						dbdScript.getImagePath());
				destdir = FileServices.appendPath(destdir,
						StringServices.replace(pkgs[i].name(), ".", fsep));
				destdir = FileServices.appendPath(destdir, "doc-files");

				FileServices.copyDir(srcdir, destdir);
			}
		}

		ClassDoc[] cls = root.specifiedClasses();

		for (int i = 0; i < cls.length; i++) {

			logger.debug("cls=" + cls[i]);

			PackageDoc pkgDoc = cls[i].containingPackage();

			dirs = PackageServices.findDocFilesDirectories(pkgDoc, sourcepath);
			iterator = dirs.iterator();

			while (iterator.hasNext()) {

				srcdir = iterator.next();
				logger.debug("srcdir=" + srcdir);

				destdir = FileServices.appendPath(outdir,
						dbdScript.getImagePath());
				destdir = FileServices.appendPath(destdir,
						StringServices.replace(pkgDoc.name(), ".", fsep));
				destdir = FileServices.appendPath(destdir, "doc-files");

				FileServices.copyDir(srcdir, destdir);
			}
		}
	}

	/**
	 * The method <code>start</code> is called from within the Doclet API. It is
	 * the main entry point of the doclet. This method catches all Exceptions
	 * and prints a stacktrace if necessary.
	 */
	public static boolean start(RootDoc rootDoc) {

		MediaManager mediaManager = null;

		if (rootDoc == null) {
			return false;
		}

		try {

			InstanceFactory.setInjector(Guice
					.createInjector(new DbdGuiceModule()));

			ResourceBundle res = InstanceFactory
					.getInstance(ResourceBundle.class);
			DocBookDoclet doclet = InstanceFactory
					.getInstance(DocBookDoclet.class);

			doclet.setOptions(rootDoc.options());
			DocletOptions options = doclet.getOptions();

			DbdScript dbdScript = InstanceFactory.getInstance(DbdScript.class);

			File destDir = options.getDestinationDirectory();
			logger.info(String.format("destination directory=" + destDir));
			
			dbdScript.setOutputFile(new File(destDir, "Reference.xml"));
			dbdScript.setEncoding(options.getEncoding());
			
			File scriptFile = options.getProfile();
			logger.info("profile file=" + scriptFile);

			if (scriptFile != null) {

				if (scriptFile.exists()) {
					TrafoScriptManager mgr = new TrafoScriptManager();
					mgr.parseScript(dbdScript.getScript(), scriptFile);

				} else {
					logger.error(MessageFormat.format(ResourceServices
							.getString(res, "C_ERROR_FILE_NOT_FOUND"),
							scriptFile.getAbsolutePath()));
				}
			}

			if (options.getTitle() != null) {
				dbdScript.setTitle(options.getTitle());
			}
			
			logger.debug("destination-encoding = "
					+ dbdScript.getDestinationEncoding());

			doclet.println(ResourceServices
					.getString(res, "C_RUNNING_DBDOCLET"));
			doclet.println("Copyright (c) 2001-2014 Michael Fuchs");
			ReleaseServices releaseServices = new ReleaseServices();
			doclet.println("Version " + releaseServices.getVersion()
					+ " Build " + releaseServices.getBuild());

			File destPath = dbdScript.getDestinationDirectory();

			for (String sourcepath : options.getSourcepath()) {
				copyDocFiles(rootDoc, sourcepath, destPath, dbdScript);
			}

			if (dbdScript.isLinkSourceEnabled()) {
				LinkSourceManager lsm = InstanceFactory
						.getInstance(LinkSourceManager.class);
				lsm.createDocBook(rootDoc);
			}

			mediaManager = InstanceFactory.getInstance(MediaManager.class);
			mediaManager.writeContents(rootDoc);

			doclet.println(ResourceServices.getString(res, "C_FINISHED"));

		} catch (Throwable oops) {
			ExceptionHandler.handleException(oops);
		}

		return true;
	}
}
