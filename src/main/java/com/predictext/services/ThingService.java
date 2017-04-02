package com.predictext.services;

import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.predictext.beans.Dictionary;
import com.predictext.beans.Thing;
import com.predictext.biz.BizResponse;
import com.predictext.constants.Params;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.predictext.biz.DictionaryBiz.getUserDocIndex;


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

        String queryString = ( id != null ? id.toLowerCase() : "").trim();
        List<Thing> things = new ArrayList<Thing>();

        LOGGER.info("la key es: " + id);
        // We get datastore user info and update language
        //We get datastore device info
        Query query = Query.newBuilder().build("Display_Name" + "=" + queryString);

        Index userDocIndex = getUserDocIndex();
        Results<ScoredDocument> matchingUsers = userDocIndex.search(query);

        for(ScoredDocument scoredDocument : matchingUsers){
            LOGGER.info("resultado: " + scoredDocument.getOnlyField("thingId").getText());
            things.add(ObjectifyService.ofy().load().key(Key.create(Thing.class, Long.parseLong(scoredDocument.getOnlyField("thingId").getText()))).now());
        }

        //List<Dictionary> dictionaries = ObjectifyService.ofy().load().group(Thing.class).type(Dictionary.class).filter("word >=", query).filter("word <", query + "\ufffd").limit(10).list();

        BizResponse response = new BizResponse(things);

        //return Response.ok().entity(response.toJsonExcludeFieldsWithoutExposeAnnotation()).build();
        return Response.ok().entity(response.toJsonExcludeFieldsWithoutExposeAnnotation()).build();
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
