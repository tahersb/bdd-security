/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2014 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.jbehave;

import net.continuumsecurity.Config;
import net.continuumsecurity.steps.WrapUpScanSteps;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class StoryRunner extends JUnitStoryRunner {

    final CmdLineParser parser;

    @Option(name = "-story", usage = "Name of story meta-tag to run")
    private String storyName;

    @Option(name = "-id", usage = "ID of scenario meta-tag to run")
    private String idName;

    @Option(name = "-c")
    private boolean justRunConfig = false;

    @Option(name = "-h")
    private boolean help = false;

    public StoryRunner() {
        super();
        parser = new CmdLineParser(this);
    }

    /*
      * Add required meta filters to control which stories are run
      */
    public List<String> createFilters() {
        List<String> filters = new ArrayList<String>();
        //Enabling the Meta filters to allow single story/scenario execution
        if (storyName != null) {
            filters.add("+story "+storyName);
        }
        if (idName != null) {
            filters.add("+id "+idName);
        }
        filters.add("-skip");
        log.debug(" running with filters:");
        for (String filter : filters) {
            log.debug("\t"+filter);
        }
        return filters;
    }


    public void execute(String... argv) throws CmdLineException,IOException {
        parser.parseArgument(argv);

        if (help) {
            parser.setUsageWidth(Integer.MAX_VALUE);
            parser.printUsage(System.err);
            System.exit(0);
        }
        prepareReportsDir();
        List<String> filters = createFilters();
        configuredEmbedder().useMetaFilters(filters);

        if (justRunConfig) {
            try {
                log.debug("Running configuration stories");
                ConfigurationStoryRunner configRunner = new ConfigurationStoryRunner();
                configRunner.setFilters(filters);
                configRunner.run();
                log.debug("Configuration stories completed.");
            } catch (Throwable t) {
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                log.error("Configuration stories failed: " + t.getMessage());
                log.error("Halting execution");
                log.error(sw.toString());
                t.printStackTrace();
                System.exit(1);
            }
            System.exit(0);
        }

        try {
            run();
            log.debug("Completed StoryRunner.run() successfully.");
        } catch (Throwable e) {
            log.debug("Caught exception from StoryRunner.execute()");
            e.printStackTrace();
        } finally {
            new WrapUpScanSteps.wrapUp();
        }
        System.exit(0);
    }

    public static void main(String... argv) throws CmdLineException,IOException {
        StoryRunner storyRunner = new StoryRunner();
        storyRunner.execute(argv);
    }
}
