package org.dbdoclet.doclet8.util;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.ResourceServices;

public class ReleaseServices {

	private static Log logger = LogFactory.getLog(ReleaseServices.class);

	Properties releaseProperties = null;

	public String getVersion() {
		return getReleaseProperties().getProperty("version");
	}

	private Properties getReleaseProperties() {

		if (releaseProperties == null) {
			
			releaseProperties = new Properties();
			
			try {
				releaseProperties.load(ResourceServices.getResourceAsStream("/release.properties"));
			} catch (Throwable oops) {
				logger.error("", oops);
			}
		}

		return releaseProperties;
	}

	public String getBuild() {
		return getReleaseProperties().getProperty("build");
	}

	public String getTimestamp() {
		return getReleaseProperties().getProperty("timestamp");
	}
}
