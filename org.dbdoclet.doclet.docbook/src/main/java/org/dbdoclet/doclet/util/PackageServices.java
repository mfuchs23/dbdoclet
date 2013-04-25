/*
 * ### Copyright (C) 2001-2007 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.util;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;

import com.sun.javadoc.PackageDoc;


/**
 * The class <code>PackageServices</code> provides some static methods
 * for dealing with packages.
 *
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public class PackageServices {


    // private static Log logger = LogFactory.getLog(PackageServices.class);

    /**
     * The <code>findPackageDirectory</code> method returns the absolute path of a
     * package. Therefore all entries in the sourcepath are scanned. If the package can not be found
     * <code>null</code> is returned.
     *
     * @param pkg a <code>PackageDoc</code> value
     * @param sourcepath a <code>String</code> value
     * @return a <code>String</code> value
     */
    public static String findPackageDirectory(PackageDoc pkg, String sourcepath) {

        if (pkg == null) {

            throw new IllegalArgumentException("Parameter pkg is null!");
        }

        if (sourcepath == null) {

            throw new IllegalArgumentException("Parameter sourcepath is null!");
        }

        String psep = System.getProperty("path.separator");
        String fsep = System.getProperty("file.separator");

        File pkgdir;
        String path;

        String pkgname = pkg.name();
        pkgname = StringServices.replace(pkgname, ".", fsep);

        StringTokenizer stz = new StringTokenizer(sourcepath, psep);

        while (stz.hasMoreElements()) {

            path = stz.nextToken();
            path += (fsep + pkgname);
            pkgdir = new File(path);

            if (pkgdir.isDirectory()) {

                return pkgdir.getAbsolutePath();
            }
        }

        return null;
    }

    public static ArrayList<String> findDocFilesDirectories(PackageDoc pkg,
	        String sourcepath) {

        if (pkg == null) {
            throw new IllegalArgumentException("Parameter pkg is null!");
        }

        return findDocFilesDirectories(pkg.name(), sourcepath);
    }

    public static ArrayList<String> findDocFilesDirectories(String pkgName,
		        String sourcepath) {

        if (pkgName == null) {
            throw new IllegalArgumentException("Parameter pkgName is null!");
        }

        if (sourcepath == null) {
            sourcepath = ".";
        }

        String psep = System.getProperty("path.separator");
        String fsep = System.getProperty("file.separator");

        File docFilesDir;
        String path;


        pkgName = StringServices.replace(pkgName, ".", fsep) + fsep + "doc-files";

        ArrayList<String> list = new ArrayList<String>();

        StringTokenizer stz = new StringTokenizer(sourcepath, psep);

        while (stz.hasMoreElements()) {

            path = stz.nextToken();
            path = FileServices.appendPath(path, pkgName);
            docFilesDir = new File(path);

            if (docFilesDir.isDirectory()) {
                list.add(docFilesDir.getAbsolutePath());
            }
        }

        return list;
    }
}
