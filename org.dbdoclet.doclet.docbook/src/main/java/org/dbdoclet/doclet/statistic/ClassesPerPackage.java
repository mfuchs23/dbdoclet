package org.dbdoclet.doclet.statistic;

import java.io.IOException;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.jfree.data.category.DefaultCategoryDataset;

public class ClassesPerPackage extends Diagram {

	@Override
	public String getImageHref() {
		return super.getImagesRef() + "ClassesPerPackage.png";
	}

	@Override
	public void createDiagram() throws IOException {

		String image = FileServices.appendFileName(getImagePath(),
				"ClassesPerPackage.png");

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (LabeledInteger item : getItemList()) {

			String label = item.getLabel();

			if (label.length() > 12) {
				label = "... " + label.substring(label.length() - 9);
			}

			dataset.addValue(item.getInteger(), "", label);
		}

		createBarChart(
				ResourceServices.getString(res, "C_CLASSES_PER_PACKAGE"),
				dataset, image);
	}
}
