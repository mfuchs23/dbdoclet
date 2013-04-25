/*
 * ### Copyright (C) 2001-2003 Michael Fuchs ###
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Author: Michael Fuchs
 * E-Mail: mfuchs@unico-consulting.com
 *
 * RCS Information:
 * ---------------
 * Id.........: $Id: RuntimeClassLoader.java,v 1.1.1.1 2004/12/21 14:01:01 mfuchs Exp $
 * Author.....: $Author: mfuchs $
 * Date.......: $Date: 2004/12/21 14:01:01 $
 * Revision...: $Revision: 1.1.1.1 $
 * State......: $State: Exp $
 */
package org.dbdoclet.doclet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.StringServices;

public class RuntimeClassLoader extends ClassLoader {

	private static Log logger = LogFactory.getLog(RuntimeClassLoader.class);
	private final HashMap<String, Class<?>> classMap;

	public RuntimeClassLoader() {

		classMap = new HashMap<String, Class<?>>();
	}

	@Override
	public InputStream getResourceAsStream(String name) {

		InputStream is = null;

		try {

			is = super.getResourceAsStream(name);
			logger.debug("Resource = " + name + ". InputStream = " + is);

			if (is == null) {

				String fsep = System.getProperty("file.separator");
				String psep = System.getProperty("path.separator");

				String classpath = System.getProperty("java.class.path");

				StringTokenizer stz = new StringTokenizer(classpath, psep);
				String path;
				File file;

				while (stz.hasMoreTokens()) {

					path = stz.nextToken();

					if (path.toLowerCase().endsWith(".jar")) {

						file = new File(path);

						if (file.exists() == false) {

							logger.warn("Jar archive " + file.getAbsolutePath()
									+ " doesn't exist.");

							continue;
						} // end of if ()

						JarFile jar = new JarFile(file);
						JarEntry entry = jar.getJarEntry(name);

						if (entry == null) {

							continue;
						}

						logger.debug("Found entry " + entry.getName()
								+ " in Jar archive " + file.getAbsolutePath()
								+ ".");

						return jar.getInputStream(entry);
					} else {

						file = new File(path + fsep + name);
						logger.debug("Searching for resource " + name
								+ " in path " + file.getAbsolutePath());

						if (file.exists()) {

							return new FileInputStream(file);
						}
					}
				}
			} // end of if ()
		} catch (Exception oops) {

			oops.printStackTrace();

			return null;
		} // end of catch

		return is;
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {

		return (loadClass(className, true));
	}

	@Override
	public synchronized Class<?> loadClass(String className, boolean resolveIt)
			throws ClassNotFoundException {

		logger.info("Loading class " + className + ".");

		try {

			Class<?> load;
			byte[] data;

			load = classMap.get(className);

			if (load != null) {

				return load;
			}

			try {

				load = super.findSystemClass(className);
				return load;

			} catch (ClassNotFoundException oops) {
				// Es geht weiter
			}

			data = getClass(className);

			if (data == null) {

				throw new ClassNotFoundException();
			}

			load = defineClass(className, data, 0, data.length);

			if (load == null) {
				throw new ClassFormatError();
			}

			if (resolveIt) {
				resolveClass(load);
			}

			// Add the class to the cache
			classMap.put(className, load);

			return load;
		} catch (Exception oops) {

			throw new ClassNotFoundException();
		} // end of catch
	}

	private byte[] getClass(String className) throws FileNotFoundException,
			IOException {

		String fsep = System.getProperty("file.separator");
		String psep = System.getProperty("path.separator");

		String fileName = StringServices.replace(className, ".", fsep)
				+ ".class";

		String classpath = System.getProperty("java.class.path");

		StringTokenizer stz = new StringTokenizer(classpath, psep);
		String path;
		File file;
		FileInputStream fis;

		byte[] data = null;
		int count = 0;
		int offset = 0;

		while (stz.hasMoreTokens()) {

			data = null;
			count = 0;
			offset = 0;

			path = stz.nextToken();

			if (path.toLowerCase().endsWith(".jar")) {

				file = new File(path);

				if (file.exists() == false) {

					logger.warn("Jar archive " + file.getAbsolutePath()
							+ " doesn't exist.");

					continue;
				} // end of if ()

				JarFile jar = new JarFile(file);
				JarEntry entry = jar.getJarEntry(fileName);

				if (entry == null) {

					continue;
				}

				logger.debug("Found entry " + entry.getName()
						+ " in Jar archive " + file.getAbsolutePath() + ".");

				int length = (int) entry.getSize();

				if (length <= 0) {

					logger.warn("Invalid length " + length
							+ " for jar archive entry " + entry.getName() + ".");

					continue;
				} // end of if ()

				data = new byte[length];

				InputStream is = jar.getInputStream(entry);

				while (count != -1) {

					count = is.read(data, offset, 4096);
					offset += count;
				}

				return data;
			} // end of if ()
			else {

				file = new File(path + fsep + fileName);
				logger.debug("Searching for class " + className + " in path "
						+ file.getAbsolutePath());

				if (file.exists()) {

					fis = new FileInputStream(file);
					data = new byte[(int) file.length()];

					while ((count = fis.read(data, offset, 4096)) != -1) {

						offset += count;
					} // end of while ()

					fis.close();
					return data;
				}
			}
		}

		return data;
	}
}
