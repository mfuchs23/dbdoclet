package org.dbdoclet.doclet.statistic;

import java.io.IOException;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.jfree.data.category.DefaultCategoryDataset;

public class DirectKnownSubclasses extends Diagram {

	@Override
	public String getImageHref() {
		return super.getImagesRef() + "DirectKnownSubclasses.png";
	}

	@Override
	public void createDiagram() throws IOException {

		String image = FileServices.appendFileName(getImagePath(),
				"DirectKnownSubclasses.png");

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (LabeledInteger item : getItemList()) {
			dataset.addValue(item.getInteger(), "", item.getLabel());
		}

		createBarChart(ResourceServices.getString(res, "C_TOP_TEN") + " - "
				+ ResourceServices.getString(res, "C_DIRECT_KNOWN_SUBCLASSES"),
				dataset, image);

	}
}
