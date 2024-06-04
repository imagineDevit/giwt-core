package io.github.imagineDevit.giwt.core.report;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.imagineDevit.giwt.core.utils.EnvVars;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static io.github.imagineDevit.giwt.core.utils.TextUtils.blue;
import static io.github.imagineDevit.giwt.core.utils.TextUtils.italic;

/**
 * This class is responsible for processing the report template and generating the report.
 * It uses the FreeMarker library to process the template and generate the report.
 * The report is generated in the target/giwtunit directory.
 * The report is generated in HTML format.
 *
 * @author Henri Joel SEDJAME
 * @since 0.0.1
 */
public class ReportProcessor {

    public static final String TARGET = "target";
    public static final String BUILD = "build";
    public static final String GIWT = "giwt";
    private final Template template;

    public ReportProcessor() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tempDirectory.getPath() + "/report.ftl");

        InputStream source = Objects.requireNonNull(ReportProcessor.class.getClassLoader().getResourceAsStream("report.ftl"));

        try (var target = new FileOutputStream(file)) {
            target.write(source.readAllBytes());
        }

        configuration.setDirectoryForTemplateLoading(tempDirectory);

        configuration.setDefaultEncoding("UTF-8");

        this.template = configuration.getTemplate("report.ftl");
    }


    public void process(TestCaseReport testCaseReport) throws Exception {

        var dataModel = Map.of("report", testCaseReport.toMap());

        var target = createGiwtDir();

        File file = new File("%s/report.html".formatted(target));
        template.process(dataModel, new FileWriter(file));

        System.out.println("------------------------------------------------------------------");
        System.out.println("Report generated: " + file.getAbsolutePath());
        System.out.println("------------------------------------------------------------------");
    }

    private String createGiwtDir() throws IOException {
        File target = new File(TARGET);
        File build = new File(BUILD);
        Path giwt;
        String result;

        if (target.exists() && target.isDirectory()) {
            giwt = new File(target, GIWT).toPath();
            result = TARGET + "/" + GIWT;
        } else if (build.exists() && build.isDirectory()) {
            giwt = new File(build, GIWT).toPath();
            result = BUILD + "/" + GIWT;
        } else {
            String buildDir = System.getenv(EnvVars.BUILD_DIR);
            if (buildDir != null) {
                giwt = new File(buildDir.trim(), GIWT).toPath();
                result = buildDir.trim() + "/" + GIWT;
            } else {
                throw new IOException("""
                        Failed to generate giwt report directory.
                        It seems that the target or build directory does not exist.
                        Please consider to specify environment variable : %s to specify the build directory.
                        """.formatted(italic(blue(EnvVars.BUILD_DIR))));
            }
        }

        if (!Files.exists(giwt)) {
            Files.createDirectory(giwt);
        }

        return result;
    }
}
