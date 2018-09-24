/*
 * Copyright 2018 Thomas Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;


public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static String CONFIG_PATH = "dfap-order.xml";
    private static String csvOrderFilepath;

    private static boolean loaded = false;

    static {
        System.out.println("STATIC");
        if (!loaded) {
            loadConfig();
        }
    }

    public static void loadConfig() {
        loaded = true;
        if (Configuration.class.getClassLoader().getResource(CONFIG_PATH) != null) {
            loadConfig(Objects.requireNonNull(Configuration.class.getClassLoader().getResource(CONFIG_PATH)));
        }
    }

    public static void loadConfig(URL url) {
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            logger.debug("", e);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            logger.info("Parsing 'dfap-order.xml'...");
            builder = factory.newDocumentBuilder();
            assert file != null;
            doc = builder.parse(file);

            doc.getDocumentElement().normalize();

            logger.info("Reading xml-document nodes...");
            csvOrderFilepath = doc.getElementsByTagName("csv-order-filepath").item(0).getTextContent();
            logger.debug("Listing all values:");
            logger.debug(csvOrderFilepath);
            logger.debug("Finished listing values.");


        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Configuration-Syntax is wrong.");
            logger.error("Cannot read 'dfap-order.xml'.", e);
        }
    }

    public static String getCsvOrderFilepath() {
        return csvOrderFilepath;
    }

    public static void setCsvOrderFilepath(String csvOrderFilepath) {
        Configuration.csvOrderFilepath = csvOrderFilepath;
    }
}
