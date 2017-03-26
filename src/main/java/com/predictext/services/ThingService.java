package com.predictext.services;

import com.predictext.beans.Thing;
import com.predictext.biz.BizResponse;
import com.predictext.constants.Params;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by edu on 5/03/16.
 */
@Path("/things")
public class ThingService {

    private final static Logger LOGGER = Logger.getLogger(ThingService.class.getName());

    /**
     * Gets a user information.
     * @return {@link Response} Response in Json with the rating information.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + Params.CHARSET_UTF8)
    public Response getAll(
            @Context HttpServletRequest request) {

        LOGGER.info("response");

        // We get datastore user info and update language
        //We get datastore device info
        List<Thing> things = ObjectifyService.ofy().load().type(Thing.class).list();
                //.filter("userId", user.id).list();

        BizResponse response = new BizResponse(things);

        //return Response.ok().entity(response.toJsonExcludeFieldsWithoutExposeAnnotation()).build();
        return Response.ok().entity(response.toJson()).build();
    }

    /**
     * Gets a user information.
     * @return {@link Response} Response in Json with the rating information.
     */
    @GET
    @Path("/search" + Params.URL_ID)
    @Produces(MediaType.APPLICATION_JSON + Params.CHARSET_UTF8)
    public Response get(
            @Context HttpServletRequest request,
            @PathParam(Params.PARAM_ID) String id) {

        LOGGER.info("response");

        LOGGER.info("la key es: " + id);
        // We get datastore user info and update language
        //We get datastore device info
        List<Thing> things = ObjectifyService.ofy().load().type(Thing.class).filter("name >=", id).filter("name <", id + "\ufffd").list();

        BizResponse response = new BizResponse(things);

        //return Response.ok().entity(response.toJsonExcludeFieldsWithoutExposeAnnotation()).build();
        return Response.ok().entity(response.toJson()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + Params.CHARSET_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDevice(
            @Context HttpServletRequest request,
            ThingPOJO json) throws IOException {

        // We get datastore user info and update language
        Thing thing = new Thing(json.name, json.description);

        ObjectifyService.ofy().save().entity(thing).now();

        BizResponse response = new BizResponse(thing);
        return Response.ok().entity(response.toJson()).build();

    }

    @XmlRootElement
    static public class ThingPOJO {

        public Long id;
        public String name;
        public String description;
        public Boolean status;
        public Double lat = 40.4165000;
        public Double lng = -3.7025600;

        public ThingPOJO() {} // constructor is required

    }


}
