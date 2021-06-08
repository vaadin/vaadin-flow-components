/*-
 * #%L
 * Vaadin Charts for Flow
 * %%
 * Copyright (C) 2021 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 * #L%
 */

package com.vaadin.flow.component.charts.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.util.ChartSerialization;

/**
 * <p>
 * Use instances of this class to generate SVG strings from chart
 * {@link Configuration} instances. You <b>must close the generator</b> when
 * you're done with it. You can use a try-with-resources block to close it
 * automatically. <b>You must have NodeJS installed for this to work</b>.
 * </p>
 * <br />
 * <p>
 * Example usage:
 * </p>
 *
 * <code>
 *  <pre>
 *  Configuration configuration = new Configuration();
 *  // ...
 *  try (SVGGenerator generator = new SVGGenerator()) {
 *      String svg = generator.generate(configuration);
 *  }
 *  </pre>
 * </code>
 *
 * @since 21.0
 */
public class SVGGenerator implements AutoCloseable {

    /**
     * Pathname to the internal exporter bundle file. We use it to copy its
     * contents to a temporary file that can be then accessed by a NodeJS
     * process.
     */
    private static final String INTERNAL_BUNDLE_PATH = "/META-INF/frontend/generated/jsdom-exporter-bundle.js";
    /**
     * String template for the script to be run with NodeJS to generate an svg
     * file which contents can be then read by this class.
     */
    private static final String SCRIPT_TEMPLATE = "const exporter = require('%s');\n"
            + "exporter({\n" + "chartConfiguration: %s,\n" + "outFile: '%s',\n"
            + "exportOptions: %s,\n" + "})";

    /**
     * Path to the temporary directory used to hold the temporary bundle file
     * and the temporary chart svg file.
     */
    private final Path tempDirPath;
    /**
     * Path to the temporary Javascript bundle file which contents are a copy of
     * the internal bundle file. This file can then be accessed by a NodeJS
     * process.
     */
    private final Path bundleTempPath;

    /**
     * Creates a new instance of {@link SVGGenerator} which allocates resources
     * used to transform a {@link Configuration} object to an SVG string.
     *
     * @throws IOException
     *             if there's any issue allocating resources needed.
     */
    public SVGGenerator() throws IOException {
        tempDirPath = Files.createTempDirectory("svg-export");
        bundleTempPath = tempDirPath.resolve("export-svg-bundle.js");
        Files.copy(getClass().getResourceAsStream(INTERNAL_BUNDLE_PATH),
                bundleTempPath);
    }

    @Override
    public void close() throws IOException {
        // cleanup by deleting all temp files
        Files.deleteIfExists(bundleTempPath);
        Files.deleteIfExists(tempDirPath);
    }

    /**
     * Generate an SVG string that can be used to render a chart with data from
     * a {@link Configuration} instance.
     *
     * @param chartConfiguration
     *            the {@link Configuration} with the chart's data.
     * @return an SVG string resulting from the {@link Configuration}.
     * @throws NullPointerException
     *             when passing a <code>null</code> configuration.
     * @throws IllegalStateException
     *             when called on a closed generator.
     * @throws IOException
     *             if anything happens using or allocating resources to
     *             virtually render the chart.
     * @throws InterruptedException
     *             if the rendering process gets interrupted.
     */
    public String generate(Configuration chartConfiguration)
            throws IOException, InterruptedException {
        return generate(chartConfiguration, null);
    }

    /**
     * Generate an SVG string that can be used to render a chart with data from
     * a {@link Configuration} instance.
     *
     * @param chartConfiguration
     *            the {@link Configuration} with the chart's data.
     * @param exportOptions
     *            optional exporting options to customize the result.
     * @return an SVG string resulting from the {@link Configuration},
     *         customized as per the {@link ExportOptions}.
     * @throws NullPointerException
     *             when passing a <code>null</code> configuration.
     * @throws IllegalStateException
     *             when called on a closed generator.
     * @throws IOException
     *             if anything happens using or allocating resources to
     *             virtually render the chart.
     * @throws InterruptedException
     *             if the rendering process gets interrupted.
     */
    public String generate(Configuration chartConfiguration,
            ExportOptions exportOptions)
            throws IOException, InterruptedException {
        if (isClosed()) {
            throw new IllegalStateException(
                    "This generator is already closed.");
        }
        Configuration config = Objects.requireNonNull(chartConfiguration,
                "Chart configuration must not be null.");
        String jsonConfig = ChartSerialization.toJSON(config);
        String jsonExportOptions = ChartSerialization.toJSON(exportOptions);
        Path chartFilePath = Files.createTempFile(tempDirPath, "chart", ".svg");
        String chartFileName = chartFilePath.toFile().getName();
        String command = String.format(SCRIPT_TEMPLATE,
                bundleTempPath.toAbsolutePath(), jsonConfig, chartFileName,
                jsonExportOptions);

        NodeRunner nodeRunner = new NodeRunner();
        nodeRunner.runJavascript(command);
        // when script completes, the chart svg file should exist
        try {
            return new String(Files.readAllBytes(chartFilePath));
        } finally {
            Files.delete(chartFilePath);
        }
    }

    /**
     * <p>
     * Check if this generator is closed.
     * </p>
     *
     * @return <code>true</code> if the generator is closed, <code>false</code>
     *         otherwise.
     */
    public boolean isClosed() {
        return !tempDirPath.toFile().exists();
    }
}