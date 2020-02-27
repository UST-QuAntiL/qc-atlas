/*
 *  /*******************************************************************************
 *  * Copyright (c) 2020 University of Stuttgart
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 */

package org.planqk.quality.knowledge.prolog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jpl7.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to access and change the local Prolog knowledge base.
 */
public class PrologKnowledgeBaseHandler {

    final private static Logger LOG = LoggerFactory.getLogger(PrologKnowledgeBaseHandler.class);

    // path to store the files for the local knowledge base
    private static String basePath = System.getProperty("java.io.tmpdir") + "quality";

    protected static void activatePrologFile(String fileName) {
        String activateQuery = "consult('" + basePath + File.separator + fileName + ".pl').";

        // replace backslashs if running on windows as JPL cannot handle this
        activateQuery = activateQuery.replace("\\", "/");

        Query query = new Query(activateQuery);
        LOG.debug("File: {} consulting finished with {}", fileName, query.hasSolution());
    }

    /**
     * Write a Prolog file with the given content to the local directory
     *
     * @param content  the Prolog content to write to the file
     * @param fileName the name of the file to create
     * @throws IOException is thrown in case the writing fails
     */
    protected static void persistPrologFile(String content, String fileName) throws IOException {
        File file = new File(basePath + File.separator + fileName + ".pl");
        try {
            File dir = new File(basePath);
            if (!dir.exists()) dir.mkdirs();
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new IOException("Could not write facts to prolog file: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate the facts and rules contained in the given Prolog file and delete the file.
     *
     * @param fileName the name of the Prolog file
     */
    protected static void deletePrologFile(String fileName) {
        // TODO: deactivate Prolog file in knowledge base
        File file = new File(basePath + File.separator + fileName + ".pl");
        LOG.debug("Deleting prolog file successful: {}", file.delete());
    }
}
