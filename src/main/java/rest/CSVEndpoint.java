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

package rest;

import controller.CSVWorker;
import controller.OrderController;
import de.iisys.smbcmis.extern.config.Configuration;
import de.iisys.smbcmis.extern.folderHandler.FolderHandler;
import de.iisys.smbcmis.extern.folderHandler.FolderHandlerForSMB;
import de.iisys.smbcmis.extern.model.DocumentFolder;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import utils.QLogger;
import utils.XLogger;
import utils.csvConverter.CSVConverter;
import utils.csvConverter.CSVFields;


import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Path("/csv")
public class CSVEndpoint {

    @Inject
    @XLogger
    private org.slf4j.Logger logger;




    @Inject
    private CSVConverter csv;

    @Inject
    private OrderController ctrl;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public  Response uploadFile(MultivaluedMap<String, String> att)  {
        logger.debug("CSVEndpoint.uploadFile");
        logger.debug("att = [" + att + "]");
        csv.readString(att.get("csvFile").get(0));
        ctrl.updateOrderFromCSV(csv.getLines());

        return Response.ok().build();
    }

    @Path("fields")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LinkedHashMap<CSVFields, Integer> getCSVOrder() {

        logger.debug("CSVEndpoint.getCSVOrder");

        LinkedHashMap<CSVFields, Integer> map = new LinkedHashMap<>();
        EnumSet.allOf(CSVFields.class).forEach(day -> map.put(day, day.getCode()));

        return map;
    }


}
