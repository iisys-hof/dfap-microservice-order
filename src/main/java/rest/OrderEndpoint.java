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

import controller.MachineController;
import controller.OrderController;
import de.iisys.smbcmis.extern.config.Configuration;
import entities.Machine;
import entities.Ordering;

import org.slf4j.Logger;
import utils.XLogger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@RequestScoped
public class OrderEndpoint {

    @Inject
    @XLogger
    private Logger logger;

    @Inject
    private OrderController orderController;

    @Inject
    private MachineController machineController;

    @GET
    @Path("/orders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ordering> getAllOrders() {
        logger.debug("OrderEndpoint.getAllOrders");
        return  orderController.getAllOrders();
    }

    @GET
    @Path("/order/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Ordering getOrderById(@PathParam("orderId") long orderId) {
        logger.debug("OrderEndpoint.getOrderById");
        logger.debug("orderId = [" + orderId + "]");

        Ordering e = orderController.getOrder(orderId);
        e.setFinishedQuantity((double) 0);
        return  e;
    }

    @PUT
    @Path("/order")
    public Response updateOrder(Ordering e) {
        logger.debug("OrderEndpoint.updateOrder");

        orderController.updateOrder(e);
        return Response.ok().build();
    }
    @PUT
    @Path("/order/{orderId}/activeState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setOrderActiveState(@PathParam("orderId") long orderId, Boolean activeState) {
        logger.debug("OrderEndpoint.setOrderActiveState");
        logger.debug("orderId = [" + orderId + "], activeState = [" + activeState + "]");

        orderController.setOrderActiveState(orderId, activeState);
        return Response.ok().build();
    }



    @GET
    @Path("/order/{orderId}/tool/versions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getToolVersions(@PathParam("orderId") long orderId) {
        logger.debug("OrderEndpoint.getToolVersions");
        logger.debug("orderId = [" + orderId + "]");

        return  orderController.getToolVersionsForOrder(orderId);
    }

    @PUT
    @Path("/order/{orderId}/tool/version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateToolVersions(@PathParam("orderId") long orderId, String version) {
        logger.debug("OrderEndpoint.updateToolVersions");
        logger.debug("orderId = [" + orderId + "], version = [" + version + "]");

        orderController.updateVersionForOrder(version, orderId);
        return Response.ok().build();
    }

    @GET
    @Path("/machines/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Machine> getAllMachines() {
        logger.debug("OrderEndpoint.getAllMachines");

        return machineController.getAllMachines();
    }

    @GET
    @Path("/machine/{machineId}/orders/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ordering> getOrdersForMachine(@PathParam("machineId") int machineId) {
        logger.debug("OrderEndpoint.getOrdersForMachine");
        logger.debug("machineId = [" + machineId + "]");

        return  orderController.getOrdesForMachine(machineId);
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTest() {
        logger.debug("getTest");

        return Configuration.getSmbDomain();

    }

}


