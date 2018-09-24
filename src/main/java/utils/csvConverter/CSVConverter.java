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

package utils.csvConverter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


@RequestScoped
public class CSVConverter {




    private final char DEFAULT_SEPARATOR = ',';
    private final char DEFAULT_QUOTE = '"';



    private List<List<String>> lines;



    public static void main(String[] args) {

        String csvFile = "/tmp/datapool.csv";

        CSVConverter c = new CSVConverter();
        System.out.println(c.readFile(csvFile, "windows-1253"));
    }

    public CSVConverter() {
    }
    public List<List<String>> getLines() {
        return lines;
    }

    public void getData() {
        System.out.println("Get Data");

        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target("http://localhost:8080/dfap-document/file/csv/datapool.csv/content");
        Invocation.Builder builder = target.request();
        String x = builder.get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        lines = new ArrayList<>();
        try {
            String y = mapper.readTree(x).get("content").asText();
            byte[] bytes = Base64.decodeBase64(y);
            String b = new String(bytes, Charset.forName("ISO-8859-1"));


            String[] sa = b.split("\n");

            for (String str: sa) {
                List<String> line = parseLine(str);
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(String filepath) {
        String[] charsetsToBeTested = {"UTF-8", "ISO-8859-1", "windows-1253", "ISO-8859-7"};
        for (String s: charsetsToBeTested) {
            int i = readFile(filepath, s);
            if (i > 0) {
                System.out.println(i);

                return s;
            }
        }
        return null;
    }

    public void readString(String file) {

        //String b = new String(file.getBytes());
        String b = new String(file.getBytes(Charset.forName("UTF-8")));
        lines = new ArrayList<>();
        String[] sa = b.split("\n");

        for (String str: sa) {
            List<String> line = parseLine(str);
            lines.add(line);
        }
    }

    public int readFile(String filepath, String charsetName) {
        File file = new File(filepath);
        lines = new ArrayList<>();

        Scanner scanner;
        int x = 0;
        try {
            scanner = new Scanner(file, charsetName);
            while (scanner.hasNext()) {

                List<String> line = parseLine(scanner.nextLine());
                lines.add(line);
                x++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return x;
    }
    public List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}