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

package controller;

import config.Configuration;
import de.iisys.smbcmis.extern.fileHandler.FileHandler;
import de.iisys.smbcmis.extern.fileHandler.FileHandlerForSMB;
import de.iisys.smbcmis.extern.folderHandler.FolderHandler;
import de.iisys.smbcmis.extern.folderHandler.FolderHandlerForSMB;
import de.iisys.smbcmis.extern.model.DocumentFile;
import de.iisys.smbcmis.extern.model.DocumentFolder;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import utils.XLogger;
import utils.csvConverter.CSVConverter;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class CSVWorker {

    @Inject
    @XLogger
    Logger logger;

    @Inject
    private CSVConverter csv;

    @Inject
    private OrderController ctrl;

    private AtomicBoolean busy = new AtomicBoolean(false);
    private int x = 0;
    @Lock(LockType.READ)
    public void doTimerWork() throws InterruptedException {
        System.out.println("WORKER");
        if (!busy.compareAndSet(false, true)) {
            return;
        }
        try {
            logger.info("WORKER " + x++);
            FileHandler fh = new FileHandlerForSMB();
            DocumentFile file = fh.getDocumentContent(Configuration.getCsvOrderFilepath());
            logger.info(file.getTitle());
            String content = new String(Base64.decodeBase64(file.getContent()));
            csv.readString(content);

            ctrl.updateOrderFromCSV(csv.getLines());
            System.out.println(new Date());
        } finally {
            busy.set(false);
        }
    }

}
