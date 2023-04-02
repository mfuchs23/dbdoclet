/*
 * ### Copyright (C) 2005-2008 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.statistic.ClassesPerPackage;
import org.dbdoclet.doclet.statistic.DirectKnownSubclasses;
import org.dbdoclet.doclet.statistic.LabeledInteger;
import org.dbdoclet.doclet.statistic.TotalsDiagram;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class StatisticData {

	private static Log logger = LogFactory.getLog(StatisticData.class);

	private TreeMap<Integer, ArrayList<String>> classesPerPackageMap;
	private HashMap<String, ArrayList<Type>> directKnownSubclassesMap;
	private TreeMap<String, TreeMap<String, ClassDoc>> pkgMap;
	private int totalClasses = 0;
	private int totalFields = 0;
	private int totalMethods = 0;
	private int totalPackagePrivateClasses = 0;
	private int totalPackagePrivateFields = 0;
	private int totalPackagePrivateMethods = 0;
	private int totalPackages = 0;
	private int totalPrivateFields = 0;
	private int totalPrivateMethods = 0;
	private int totalProtectedFields = 0;
	private int totalProtectedMethods = 0;
	private int totalPublicClasses = 0;
	private int totalPublicFields = 0;
	private int totalPublicMethods = 0;

	public StatisticData() throws IOException {

		try {
			System.setProperty("java.awt.headless", "true");
		} catch (Throwable oops) {
			logger.error("Can't set system property java.awt.headless", oops);
		}
	}

	public ClassesPerPackage getClassesPerPackageDiagram() throws IOException {

		// System.out.println("Creating classes per packages diagram...");

		int max = 20;
		int index = 0;
		Integer key;

		Iterator<Integer> iterator = classesPerPackageMap.keySet().iterator();
		ClassesPerPackage classesPerPackage = CDI
				.getInstance(ClassesPerPackage.class);

		while (iterator.hasNext()) {

			key = iterator.next();

			ArrayList<String> list = classesPerPackageMap.get(key);
			Collections.sort(list);

			for (String name : list) {

				index++;
				classesPerPackage.add(new LabeledInteger(key, name));

				if (index > max) {
					break;
				}
			}

			if (index > max) {
				break;
			}
		}

		return classesPerPackage;
	}

	public DirectKnownSubclasses getDirectKnownSubclassesDiagram()
			throws IOException {

		// System.out.println("Creating direct known subclasses diagram...");

		DirectKnownSubclasses directKnownSubclasses = CDI
				.getInstance(DirectKnownSubclasses.class);

		Iterator<String> iterator = directKnownSubclassesMap.keySet()
				.iterator();

		TreeMap<Integer, ArrayList<String>> topTen = new TreeMap<Integer, ArrayList<String>>(
				Collections.reverseOrder());

		ArrayList<Type> list;
		ArrayList<String> classnames;
		Integer count;
		int index;

		String name;

		while (iterator.hasNext()) {

			name = iterator.next();
			index = name.lastIndexOf('.');

			if (index != -1) {

				String pkgname = name.substring(0, index);

				if (pkgMap.get(pkgname) == null) {
					continue;
				}
			}

			list = directKnownSubclassesMap.get(name);

			if ((list != null) && (list.size() > 0)) {

				count = new Integer(list.size());
				classnames = topTen.get(count);

				if (classnames == null) {

					classnames = new ArrayList<String>();
					topTen.put(count, classnames);
				}

				classnames.add(name);
			}
		}

		int max = 10;
		index = 0;

		Iterator<Integer> topTenIterator = topTen.keySet().iterator();

		while (topTenIterator.hasNext()) {

			count = topTenIterator.next();

			classnames = topTen.get(count);

			Object[] sorted = classnames.toArray();
			Arrays.sort(sorted);

			for (int i = 0; i < sorted.length; i++) {

				name = (String) sorted[i];

				if ((name != null) && name.startsWith("java.lang")) {
					continue;
				}

				directKnownSubclasses.add(new LabeledInteger(count, name));
				index++;

				if (index > max) {
					break;
				}
			}

			if (index > max) {
				break;
			}
		}

		return directKnownSubclasses;
	}

	public ArrayList<Type> getSubclasses(String qualifiedName) {

		if (qualifiedName == null) {

			throw new IllegalArgumentException(
					"The argument qualifiedName must not be null!");
		}

		/*
		ArrayList<Type> subclasses = directKnownSubclassesMap
				.get(qualifiedName);

		if (subclasses == null) {
			subclasses = new ArrayList<Type>();
		}

		return subclasses;
		*/
		
		return new ArrayList<Type>();
	}

	public TotalsDiagram getTotalsDiagram() throws IOException {

		TotalsDiagram totals = CDI.getInstance(TotalsDiagram.class);
		totals.setTotalPackages(totalPackages);
		totals.setTotalClasses(totalClasses);
		totals.setTotalFields(totalFields);
		totals.setTotalMethods(totalMethods);
		totals.setTotalPackagePrivateClasses(totalPackagePrivateClasses);
		totals.setTotalPackagePrivateFields(totalPackagePrivateFields);
		totals.setTotalPackagePrivateMethods(totalPackagePrivateMethods);
		totals.setTotalPrivateFields(totalPrivateFields);
		totals.setTotalPrivateMethods(totalPrivateMethods);
		totals.setTotalProtectedFields(totalProtectedFields);
		totals.setTotalProtectedMethods(totalProtectedMethods);
		totals.setTotalPublicClasses(totalPublicClasses);
		totals.setTotalPublicFields(totalPublicFields);
		totals.setTotalPublicMethods(totalPublicMethods);

		return totals;
	}

	private void analyseClasses(TreeMap<String, ClassDoc> classMap) {

		if (classMap == null) {
			throw new IllegalArgumentException(
					"The argument classMap must not be null!");
		}

		totalClasses += classMap.size();

		ClassDoc cdoc;
		String className;

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			if (cdoc.isPublic()) {
				totalPublicClasses++;
			}

			if (cdoc.isPackagePrivate()) {
				totalPackagePrivateClasses++;
			}

			Type superclass = cdoc.superclassType();
			ArrayList<Type> subclasses;

			if (superclass != null) {

				subclasses = directKnownSubclassesMap.get(superclass
						.qualifiedTypeName());

				if (subclasses == null) {
					subclasses = new ArrayList<Type>();
					directKnownSubclassesMap.put(
							superclass.qualifiedTypeName(), subclasses);
				}

				subclasses.add(cdoc);
			}
		}

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			MethodDoc[] methods = cdoc.methods(false);
			totalMethods += methods.length;

			for (int j = 0; j < methods.length; j++) {

				if (methods[j].isPublic()) {
					totalPublicMethods++;
				}

				if (methods[j].isProtected()) {
					totalProtectedMethods++;
				}

				if (methods[j].isPackagePrivate()) {
					totalPackagePrivateMethods++;
				}

				if (methods[j].isPrivate()) {
					totalPrivateMethods++;
				}
			}
		}

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			FieldDoc[] fields = cdoc.fields(false);
			totalFields += fields.length;

			for (int j = 0; j < fields.length; j++) {

				if (fields[j].isPublic()) {
					totalPublicFields++;
				}

				if (fields[j].isProtected()) {
					totalProtectedFields++;
				}

				if (fields[j].isPackagePrivate()) {
					totalPackagePrivateFields++;
				}

				if (fields[j].isPrivate()) {
					totalPrivateFields++;
				}
			}
		}
	}

	public void init(TreeMap<String, TreeMap<String, ClassDoc>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException(
					"The field pkgMap must not be null!");
		}

		this.pkgMap = pkgMap;

		String pkgName;
		TreeMap<String, ClassDoc> classMap;

		totalPackages = 0;
		totalClasses = 0;
		totalMethods = 0;
		totalFields = 0;
		totalPublicClasses = 0;
		totalPublicMethods = 0;
		totalPublicFields = 0;

		classesPerPackageMap = new TreeMap<Integer, ArrayList<String>>(
				Collections.reverseOrder());
		directKnownSubclassesMap = new HashMap<String, ArrayList<Type>>();

		totalPackages = pkgMap.size();

		for (Iterator<String> iterator = pkgMap.keySet().iterator(); iterator
				.hasNext();) {

			pkgName = iterator.next();

			classMap = pkgMap.get(pkgName);
			Integer key = 0;

			if (classMap != null) {

				key = classMap.size();
				analyseClasses(classMap);
			}

			ArrayList<String> list = classesPerPackageMap.get(key);

			if (list == null) {

				list = new ArrayList<String>();
				classesPerPackageMap.put(key, list);
			}

			list.add(pkgName);
		}
	}
}