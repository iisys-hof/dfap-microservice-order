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


import daos.ordering.JPAOrderingDao;
import daos.tool.JPAToolDao;
import entities.*;
import org.slf4j.Logger;
import utils.XLogger;
import utils.csvConverter.CSVFields;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequestScoped
public class OrderController {

    @Inject
    @XLogger
    private Logger logger;
    @Inject
    private JPAOrderingDao orderingDao;
    @Inject
    private JPAToolDao toolDao;

    public List<String> getToolVersionsForOrder(long orderId) {
        Ordering oe = this.getOrder(orderId);

        return toolDao.readAllVersionsOfTool(oe.getToolId().getName());
    }

    public List<Ordering> getAllOrders() {
        return orderingDao.readAllOrders();
    }

    public Ordering getOrder(long orderId) {
        return orderingDao.readOrdering(orderId);
    }

    public List<Ordering> getOrdesForMachine(long machineId) {
        return orderingDao.getOrderingsForMachine(machineId);
    }

    public void updateOrderFromCSV(List<List<String>> entries) {
        logger.debug("OrderController.updateOrderFromCSV");
        HashMap<String, Ordering> orderings = new HashMap<>();

        for(List<String> e : entries) {
            Ordering o = new Ordering();
            String key = e.get(CSVFields.ORDERID.getCode());

            if(orderings.containsKey(key)) {
                OrderingPartsList part = new OrderingPartsList();
                part.setComponent(e.get(CSVFields.PARTS_LIST.getCode()));
                part.setDescription(e.get(CSVFields.PARTS_LIST_DESCRIPTION.getCode()));
                part.setQuantity(Double.valueOf(e.get(CSVFields.PARTS_LIST_QUANTITY.getCode()).replaceAll(",",".")));
                part.setUnit(e.get(CSVFields.PARTS_LIST_UNIT.getCode()));
                part.setOrderingId(orderings.get(key));
                orderings.get(key).getOrderingPartsListList().add(part);
                continue;
            }

            orderings.put(key, o);
            o.setOrderingId(Long.valueOf(e.get(CSVFields.ORDERID.getCode()).substring(1)));

            Article article = new Article();
            article.setName(e.get(CSVFields.ARTICLE.getCode()).trim());
            article.setDescription(e.get(CSVFields.DESCRIPTION.getCode()));
            o.setArticleId(article);

            o.setTotalQuantity(Double.valueOf(e.get(CSVFields.QUANTITY.getCode()).replaceAll(",",".")));
            o.setTypeOfContainer(e.get(CSVFields.TYPE_OF_CONTAINER.getCode()));
            o.setQuantityPerContainer(Double.valueOf(e.get(CSVFields.QUANTITY_PER_CONTAINER.getCode()).replaceAll(",",".")));
            o.setPiecesPerContainer(Math.round(Float.valueOf(e.get(CSVFields.PIECES_PER_CONTAINER.getCode()).replaceAll(",","."))));
            o.setQuantityOfContainer(Double.valueOf(e.get(CSVFields.QUANTITY_OF_CONTAINER.getCode()).replaceAll(",",".")));
            o.setShortage(Double.valueOf(e.get(CSVFields.SHORTAGE.getCode()).replaceAll(",",".")));

            Machine machine = new Machine();
            machine.setName(e.get(CSVFields.MACHINE.getCode()).trim());
            o.setMachineId(machine);

            o.setLengthOfArticle(Math.round(Float.valueOf(e.get(CSVFields.LENGTH_OF_ARTICLE.getCode()).replaceAll(",","."))));
            o.setPrintDescription(e.get(CSVFields.PRINT_DESCRIPTION.getCode()));
            o.setProfileBody(e.get(CSVFields.PROFILE_BODY.getCode()));
            o.setProfileGasket(e.get(CSVFields.PROFILE_GASKET.getCode()));
            o.setProfileGasketQuantity(Math.round(Integer.valueOf(e.get(CSVFields.PROFILE_GASKET_QUANTITY.getCode()).replaceAll(",","."))));

            Tool tool = new Tool();
            tool.setName(e.get(CSVFields.TOOL.getCode()).trim());
            o.setToolId(tool);

            o.setSchufoO(e.get(CSVFields.SCHUFO_O.getCode()));
            o.setSchufoU(e.get(CSVFields.SCHUFO_U.getCode()));

            List<OrderingPartsList> parts = new ArrayList<>();
            OrderingPartsList part = new OrderingPartsList();
            part.setComponent(e.get(CSVFields.PARTS_LIST.getCode()).trim());
            part.setDescription(e.get(CSVFields.PARTS_LIST_DESCRIPTION.getCode()).trim());
            part.setQuantity(Double.valueOf(e.get(CSVFields.PARTS_LIST_QUANTITY.getCode()).replaceAll(",",".")));
            part.setUnit(e.get(CSVFields.PARTS_LIST_UNIT.getCode()));
            part.setOrderingId(o);
            parts.add(part);
            o.setOrderingPartsListList(parts);
        }

        //orderingDao.updateFromCSV(orderings.get("M908040"));

        orderings.forEach((k,v)->logger.debug("Item : "  + " Count : " + v));

        orderings.forEach((k,v)-> orderingDao.updateFromCSV(v));

    }

    public void updateOrder(Ordering e) {
        orderingDao.updateOrdering(e);
    }

    public void updateVersionForOrder(String version, long orderId) {
        Ordering oe = this.getOrder(orderId);


        Tool te = toolDao.findToolForNameAndVersion(oe.getToolId().getName(), version);
        if(te != null) {
            System.out.println("TOOL version known");
            System.out.println(te.getToolId());
            orderingDao.updateOrdering(oe, te);
        }
        else {
            System.out.println("TOOL version unknown");

            Tool nte = new Tool();
            nte.setName(oe.getToolId().getName());
            nte.setVersion(version);
            toolDao.storeTool(nte);
            System.out.println(nte.getToolId());
            oe.setToolId(nte);
            orderingDao.updateOrdering(oe, nte);
        }
    }

    public void setOrderActiveState(long orderId, Boolean activeState) {
        Ordering order = orderingDao.readOrdering(orderId);

        List<Ordering> orders= orderingDao.getOrderingsForMachine(order.getMachineId().getMachineId());
        for (Ordering o: orders) {
            o.setOrderActive(0);
            updateOrder(o);
        }
        order.setOrderActive(activeState ? 1 : 0);
        System.out.println(order.getOrderActive());

        updateOrder(order);

    }
}
