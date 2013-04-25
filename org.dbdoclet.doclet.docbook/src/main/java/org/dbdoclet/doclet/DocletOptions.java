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
package org.dbdoclet.doclet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.option.BooleanOption;
import org.dbdoclet.option.DirectoryOption;
import org.dbdoclet.option.FileOption;
import org.dbdoclet.option.OptionException;
import org.dbdoclet.option.OptionList;
import org.dbdoclet.option.StringOption;

/**
 * The class <code>Options</code> provides access methods for the properties
 * defined in the properties file.
 * 
 * <p>
 * Properties:
 * </p>
 * 
 * <dl>
 * <dt><code>destination.file</code>
 * <dd>The destination file, *e.g. Reference.sgml. Any images are copied into
 * the subdirectory figures of the same directory.
 * </dl>
 * 
 * @author <a href ="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class DocletOptions {

	private static Log logger = LogFactory.getLog(DocletOptions.class);
	private OptionList optList;
	private DirectoryOption optDestDir;
	private FileOption optProfile;
	private StringOption optSourcepath;

	public DocletOptions(String[][] cmdline) {

		if (cmdline == null) {
			throw new IllegalArgumentException(
					" The argument cmdline must not be null!");
		}

		init(flattenArgs(cmdline));
	}

	public File getDestinationDirectory() {
		return optDestDir.getValue();
	}

	public ArrayList<String> getSourcepath() {
		return optSourcepath.getValues();
	}

	public String getDocumentationId() {
		return optList.getString("doclet.documentation.id");
	}

	public String getInstallationPath() throws IOException {

		URL url = getClass().getClassLoader().getResource("fo.xsl");

		if (url == null) {
			logger.fatal("java.class.path="
					+ System.getProperty("java.class.path"));
			throw new IllegalStateException(
					"Can't detect installation path (1)!");
		}

		String path = url.getFile();
		File dir = new File(path);

		dir = dir.getParentFile();
		if (dir == null) {
			throw new IllegalStateException(
					"Can't detect installation path (2)!");
		}

		dir = dir.getParentFile();
		if (dir == null) {
			throw new IllegalStateException(
					"Can't detect installation path (3)!");
		}

		if (dir.exists() == false) {
			throw new IllegalStateException(
					"Can't detect installation path (4)!");
		}

		path = dir.getCanonicalPath();
		logger.info("Installation path of dbdoclet is " + path);

		return path;
	}

	public File getProfile() {
		FileOption option = (FileOption) optList.getOption("profile");
		return option.getValue();
	}

	public ArrayList<String> getTagList() {

		StringOption option = (StringOption) optList.getOption("tag");
		return option.getValues();
	}

	private void init(OptionList optList) {

		if (optList == null) {
			throw new IllegalArgumentException(
					" The argument optList must not be null!");
		}

		optDestDir = new DirectoryOption();
		optDestDir.setShortName("d");
		optDestDir.setMediumName("dir");
		optDestDir.setMediumName("destination-directory");
		optDestDir.setDefault(new File("./dbdoclet"));
		optList.add(optDestDir);

		optProfile = new FileOption();
		optProfile.setShortName("p");
		optProfile.setMediumName("profile");
		optProfile.setLongName("profile");
		optProfile.isExisting(false);
		optList.add(optProfile);

		optSourcepath = new StringOption();
		optSourcepath.setDefault(".");
		optSourcepath.setMediumName("sourcepath");
		optList.add(optSourcepath);

		optList.add(new BooleanOption().setMediumName("breakiterator"));
		optList.add(new BooleanOption().setMediumName("help"));
		optList.add(new BooleanOption().setMediumName("package"));
		optList.add(new BooleanOption().setMediumName("private"));
		optList.add(new BooleanOption().setMediumName("protected"));
		optList.add(new BooleanOption().setMediumName("public"));
		optList.add(new BooleanOption().setMediumName("verbose"));
		optList.add(new StringOption().setMediumName("bootclasspath"));
		optList.add(new StringOption().setMediumName("classpath"));
		optList.add(new StringOption().setMediumName("doclet"));
		optList.add(new StringOption().setMediumName("docletpath"));
		optList.add(new StringOption().setMediumName("encoding"));
		optList.add(new StringOption().setMediumName("exclude"));
		optList.add(new StringOption().setMediumName("extdirs"));
		optList.add(new StringOption().setMediumName("locale"));
		optList.add(new StringOption().setMediumName("overview"));
		optList.add(new StringOption().setMediumName("source"));
		optList.add(new StringOption().setMediumName("subpackages"));

		if (optList.validate(true) == false) {
			throw new OptionException(optList.getError());
		}
	}

	private void init(String[] args) {

		optList = new OptionList(args);
		init(optList);
	}

	protected String[] flattenArgs(String[][] cmdline) {

		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < cmdline.length; i++) {

			for (int j = 0; j < cmdline[i].length; j++) {

				list.add(cmdline[i][j]);
			}
		}

		String[] args = new String[list.size()];

		int index = 0;

		for (Iterator<String> i = list.iterator(); i.hasNext();) {

			String arg = i.next();
			args[index++] = arg;
		}

		return args;
	}

	public OptionList getOptionList() {
		return optList;
	}

	public boolean isQuiet() {
		return false;
	}
}
