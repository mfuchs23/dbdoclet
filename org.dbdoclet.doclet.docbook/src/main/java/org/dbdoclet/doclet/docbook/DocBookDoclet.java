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
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner9;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.AbstractDoclet;
import org.dbdoclet.doclet.DeprecatedDocletOptions;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.option.DocletOptions;
import org.dbdoclet.doclet.CDI;
import org.dbdoclet.doclet.util.PackageServices;
import org.dbdoclet.doclet.util.ReleaseServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.trafo.TrafoConstants;
import org.dbdoclet.trafo.TrafoScriptManager;
import org.dbdoclet.trafo.script.Script;

import com.google.inject.Guice;
import com.sun.javadoc.RootDoc;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTreeScanner;
import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * The class <code>DocBookDoclet</code> implements a javadoc doclet, which
 * creates DocBook XML from javadoc comments.
 * 
 * @author <a href ="mailto:michael.fuchs@dbdoclet.org">Michael Fuchs</a>
 */
public final class DocBookDoclet extends AbstractDoclet {

	private static Log logger = LogFactory.getLog(DocBookDoclet.class.getName());
	private DocletOptions options;
	private ResourceBundle res;

	public DocBookDoclet() {
		CDI.setInjector(Guice.createInjector(new DbdGuiceModule()));
		res = CDI.getInstance(ResourceBundle.class);
		options = new DocletOptions(res);
	}
	
    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }
	/**
	 * The method <code>copyDocFiles</code> copies all files, which are located in
	 * doc-files directories, from the source path to the destination path.
	 * 
	 * @param root
	 * @param sourcepath
	 * @param outdir
	 * @param dbdScript
	 * @throws IOException
	 */
	public static void copyDocFiles(DocManager dm, String sourcepath, File outdir, DbdScript dbdScript)
			throws IOException {

		if (dm == null) {
			throw new IllegalArgumentException("Parameter DocManager is null!");
		}

		if (outdir == null) {
			throw new IllegalArgumentException("Parameter outdir is null!");
		}

		if (sourcepath == null) {
			sourcepath = ".";
		}

		if (dbdScript == null) {
			throw new IllegalStateException("The field dbdScript must not be null!");
		}

		logger.debug("docfilespath=" + sourcepath);

		String fsep = System.getProperty("file.separator");

		Set<PackageElement> pkgs = dm.getPackageElements();

		String srcdir;
		String destdir;

		ArrayList<String> dirs;
		Iterator<String> iterator;

		for (PackageElement pkg : pkgs) {

			logger.debug("pkg=" + pkg);

			dirs = PackageServices.findDocFilesDirectories(pkg, sourcepath);
			iterator = dirs.iterator();

			while (iterator.hasNext()) {

				srcdir = iterator.next();
				logger.debug("srcdir=" + srcdir);

				destdir = FileServices.appendPath(outdir, dbdScript.getImagePath());
				destdir = FileServices.appendPath(destdir, StringServices.replace(pkg.getQualifiedName().toString(), ".", fsep));
				destdir = FileServices.appendPath(destdir, "doc-files");

				FileServices.copyDir(srcdir, destdir);
			}
		}

		Set<TypeElement> cls = dm.getClassElements();

		for (TypeElement classElem : cls) {

			PackageElement pkg = dm.containingPackage(classElem);

			dirs = PackageServices.findDocFilesDirectories(pkg, sourcepath);
			iterator = dirs.iterator();

			while (iterator.hasNext()) {

				srcdir = iterator.next();
				logger.debug("srcdir=" + srcdir);

				destdir = FileServices.appendPath(outdir, dbdScript.getImagePath());
				destdir = FileServices.appendPath(destdir, StringServices.replace(pkg.getQualifiedName().toString(), ".", fsep));
				destdir = FileServices.appendPath(destdir, "doc-files");

				FileServices.copyDir(srcdir, destdir);
			}
		}
	}


	@Override
	public Set<? extends Option> getSupportedOptions() {
		return options.getSupportedOptions();
	}

	@Override
	public boolean run(DocletEnvironment environment) {

		try {

			DbdScript dbdScript = CDI.getInstance(DbdScript.class);

			String filename = options.getDestinationFile();
			File destFile = null;
			if (filename != null) {
				logger.info(String.format("destination file (ignore destination directory)=" + filename));
				destFile = new File(filename);
			} else {
				String destDir = options.getDestinationDirectory();
				logger.info(String.format("destination directory=" + destDir));
				destFile = new File(destDir, "Reference.xml");
			}

			dbdScript.setOutputFile(destFile);
			dbdScript.setEncoding(options.getEncoding());
			dbdScript.setDocumentElement("article");
			
			Script script = dbdScript.getScript();

			filename = options.getProfile();
			if (filename!= null) {
				
				File scriptFile = new File(filename);
				logger.info("Using profile file " + scriptFile.getCanonicalPath());
				if (scriptFile.exists()) {

					TrafoScriptManager mgr = new TrafoScriptManager();
					mgr.parseScript(script, scriptFile);

				} else {
					logger.error(MessageFormat.format(ResourceServices.getString(res, "C_ERROR_FILE_NOT_FOUND"),
							scriptFile.getAbsolutePath()));
				}
			}

			if (script.getParameter(TrafoConstants.SECTION_DOCBOOK, TrafoConstants.PARAM_IMAGE_PATH) == null) {
				dbdScript.setImagePath("img/");
			}

			if (options.getTitle() != null) {
				dbdScript.setTitle(options.getTitle());
			}

			File destPath = dbdScript.getDestinationDirectory();

			println(ResourceServices.getString(res, "C_RUNNING_DBDOCLET"));
			println("Copyright (c) 2001-2023 Michael Fuchs");
			ReleaseServices releaseServices = new ReleaseServices();
			println("Version " + releaseServices.getVersion() + " Build " + releaseServices.getBuild());
			println(String.format("destination-directory: %s", destPath));

			DocManager docManager = CDI.getInstance(DocManager.class);
			docManager.setDocletEnvironment(environment);
			docManager.setReporter(reporter);

			for (String sourcepath : options.getSourcepath().split(File.pathSeparator)) {
				copyDocFiles(docManager, sourcepath, destPath, dbdScript);
			}

			/*
			if (dbdScript.isLinkSourceEnabled()) {
			 	LinkSourceManager lsm = InstanceFactory.getInstance(LinkSourceManager.class);
			 	lsm.createDocBook(docManager);
			}
			*/
			
			MediaManager mediaManager = CDI.getInstance(MediaManager.class);
			mediaManager.writeContents();

			println(ResourceServices.getString(res, "C_FINISHED"));
			return true;
		
		} catch (Throwable oops) {
			ExceptionHandler.handleException(oops);
			return false;
		}
	}

	class ShowElements extends ElementScanner9<Void, Integer> {

		final PrintStream out;
		private DocTrees trees;

		ShowElements(DocTrees trees, PrintStream out) {
			this.trees = trees;
			this.out = out;
		}

		void show(Set<? extends Element> elements) {
			scan(elements, 0);
		}

		@Override
		public Void scan(Element e, Integer depth) {
			ElementKind kind = e.getKind();
			if (kind == ElementKind.PACKAGE) {
				out.println("========================== PACKAGE " + e.toString());
			}
			DocCommentTree dcTree = trees.getDocCommentTree(e);
			String indent = "  ".repeat(depth);
			if (dcTree != null) {
				out.println(indent + "| " + e.getKind() + " " + e);
			}
			if (dcTree != null) {
				new ShowDocTrees(out).scan(dcTree, depth + 1);
			}
			return super.scan(e, depth + 1);
		}
	}

	/**
	 * A scanner to display the structure of a documentation comment.
	 */
	class ShowDocTrees extends DocTreeScanner<Void, Integer> {
		final PrintStream out;

		ShowDocTrees(PrintStream out) {
			this.out = out;
		}

		@Override
		public Void scan(DocTree t, Integer depth) {
			String indent = "  ".repeat(depth);
			out.println(indent + "# " + t.getKind() + " " + t.toString().replace("\n", "\n" + indent + "#    "));
			return super.scan(t, depth + 1);
		}
	}
}
