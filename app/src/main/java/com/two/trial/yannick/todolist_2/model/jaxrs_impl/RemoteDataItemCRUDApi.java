package com.two.trial.yannick.todolist_2.model.jaxrs_impl;

import com.two.trial.yannick.todolist_2.model.DataItem;

import org.jboss.resteasy.annotations.Body;

import java.net.Proxy;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/todos")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface RemoteDataItemCRUDApi {

    @POST
    public DataItem createDataItem(DataItem item);

    @GET
    public List<DataItem> readAllDataItems();

    @DELETE
    @Path("/{id}")
    public boolean deletaDataItem(@PathParam("id") long dataItemId);
}
