/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.cli;

import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.util.Callback;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class HelpPrinter {

    private static final String HELP_FILE_EXT = ".help";

    public void print(CliToolConfig config, Terminal terminal) {
        print(config.toolType(), config.name(), terminal);
    }

    public void print(String toolName, CliToolConfig.Cmd cmd, Terminal terminal) {
        print(cmd.cmdType(), toolName + "-" + cmd.name(), terminal);
    }

    private static void print(Class clazz, String name, final Terminal terminal) {
        terminal.println(Terminal.Verbosity.SILENT, "");
        try (InputStream input = clazz.getResourceAsStream(name + HELP_FILE_EXT)) {
            Streams.readAllLines(input, new Callback<String>() {
                @Override
                public void handle(String line) {
                    terminal.println(Terminal.Verbosity.SILENT, line);
                }
            });
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        terminal.println(Terminal.Verbosity.SILENT, "");
    }
}
