package org.dbdoclet.doclet.option;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.Doclet.Option.Kind;

public class DocletOptions {

    private class Option implements Doclet.Option {
    
		private static final String MISSING_KEY = "<MISSING KEY>";
        private final Consumer<List<String>> processor;
        private final String[] names;
        private final String parameters;
        private final String description;
        private final int argCount;
        private final Kind kind;

        protected Option(String name, int argCount, Kind kind, Consumer<List<String>> processor) {
            this.processor = processor;
            this.names = name.trim().split("\\s+");
            this.description = resourceMsg(names[0] + ".description");
            this.parameters = resourceMsg(names[0] + ".parameters");
            this.argCount = argCount;
            this.kind = kind;
        }

        @Override
        public int getArgumentCount() {
            return argCount;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return kind;
        }

        @Override
        public List<String> getNames() {
            return Arrays.asList(names);
        }

        @Override
        public String getParameters() {
            return parameters;
        }

        @Override
        public boolean process(String option, List<String> arguments) {
            processor.accept(arguments);
            return true;
        }

        private String resourceMsg(String key) {
            try {
                String resourceKey = "doclet.usage." + key.toLowerCase(Locale.ENGLISH).replaceFirst("^-+", "");
                return res.getString(resourceKey);
            } catch (MissingResourceException mre) {
                return MISSING_KEY;
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(names);
        }
	}
    
	private Set<Doclet.Option> options = new LinkedHashSet<>();
    private String destinationDirectory;
	private ResourceBundle res;
	private String destinationFile;
	private String profile;
	private String title;
	private String encoding;
	private String sourcepath;
    
    public DocletOptions(ResourceBundle res) {
    	
    	this.res = res;
		options.add(new Option("-d -dir --destination-directory", 1, Kind.OTHER, args -> destinationDirectory = args.get(0)));
		options.add(new Option("-sourcepath", 1, Kind.OTHER, args -> sourcepath = args.get(0)));
		options.add(new Option("-e", 1, Kind.OTHER, args -> encoding = args.get(0)));
		options.add(new Option("-f", 1, Kind.OTHER, args -> destinationFile = args.get(0)));
		options.add(new Option("-p", 1, Kind.OTHER, args -> profile = args.get(0)));
		options.add(new Option("-t", 1, Kind.OTHER, args -> title = args.get(0)));
    }

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public String getDestinationFile() {
		return destinationFile;
	}

	public String getProfile() {
		return profile;
	}

	public String getSourcepath() {
		return sourcepath;
	}

	public String getTitle() {
		return title;
	}

	public Set<? extends Doclet.Option> getSupportedOptions() {
		return options;
	}

	public String getEncoding() {
		return encoding == null ? "UTF-8" : encoding;
	}
}
