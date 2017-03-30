package com.predictext.biz;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.predictext.beans.Dictionary;
import com.predictext.beans.Thing;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bbva on 27/03/17.
 */
public class DictionaryBiz {


    private final static Logger LOGGER = Logger.getLogger(DictionaryBiz.class.getName());


    /**
     * Code snippet for creating a Document.
     * @return Document Created document.
     */
    public Document createDocument(Thing thingId, String words) {

        // [START create_document]


        Document doc = Document.newBuilder()
                .addField(Field.newBuilder().setName("word").setText(GenerateWords(words)))
                .addField(Field.newBuilder().setName("thingId").setText(thingId.getId().toString()))
                .build();

        // [END create_document]

        return doc;
    }


    public String GenerateWords(String phrase){

        List<String> preprocessString = Arrays.asList(phrase.toLowerCase().trim().split(" "));

        String result = "";

        for(String item : preprocessString) {
            for (int i = 0; i < item.length(); i++) {
                for (int j = i + 1; j <= item.length(); j++) {
                    result += " " +item.substring(i, j);
                }
            }
        }

        LOGGER.info("El resultado es: " + result);
        return result;
    }

}
