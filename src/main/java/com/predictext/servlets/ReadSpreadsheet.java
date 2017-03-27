package com.predictext.servlets;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.predictext.beans.Thing;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.googlecode.objectify.ObjectifyService;
import com.predictext.biz.DictionaryBiz;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReadSpreadsheet extends HttpServlet {

    private final static String APPLICATION_NAME = "predictext";
    private final static Logger LOGGER = Logger.getLogger(ReadSpreadsheet.class.getName());

    private final String CLIENT_ID = "predictext@appspot.gserviceaccount.com";

    // The name of the p12 file you created when obtaining the service account
    private final String P12FILE = "secret.p12";

    private final List<String> SCOPES = Arrays
            .asList(SheetsScopes.SPREADSHEETS);

    private static final DictionaryBiz DICTIONARY_BIZ = new DictionaryBiz();
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private GoogleCredential getCredentials() throws GeneralSecurityException, IOException, URISyntaxException {

        JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        URI keyURL = this.getClass().getClassLoader().getResource(P12FILE).toURI();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(CLIENT_ID)
                .setServiceAccountPrivateKeyFromP12File(
                        new File(keyURL))
                .setServiceAccountScopes(SCOPES).build();

        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public Sheets getSheetsService() throws IOException, GeneralSecurityException, URISyntaxException{

        JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = getCredentials();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Build a new authorized API client service.

        try {

            Sheets service = getSheetsService();


            String spreadSheetID= "11Flb1pXHBTqq4Sygf8_bqGP3Lgo8TuFrRaQA45w8Ijs";
            Integer sheetID = 123;
            String DateValue = "2015-07-13";

            Sheets.Spreadsheets.Values.Get spreadsheetRequest = service.spreadsheets().values().get(spreadSheetID, "data!A:B");

            ValueRange sheetResponse = spreadsheetRequest.execute();

            List<List<Object>> rows = sheetResponse.getValues();

            for(List<Object> row : rows){

                Thing thing = new Thing(row.get(0).toString(), row.get(1).toString());
                Key<Thing> thingKey = ObjectifyService.ofy().save().entity(thing).now();
                DICTIONARY_BIZ.GenerateWords(row.get(0).toString(), thing);

            }

        }catch (GeneralSecurityException e){
            throw new IOException(e.getCause());
        }catch (URISyntaxException e){
            throw new IOException(e.getCause());
        }
    }


}