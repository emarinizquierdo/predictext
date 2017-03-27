package com.predictext.biz;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.predictext.beans.Dictionary;
import com.predictext.beans.Thing;

import java.util.logging.Logger;

/**
 * Created by bbva on 27/03/17.
 */
public class DictionaryBiz {


    private final static Logger LOGGER = Logger.getLogger(DictionaryBiz.class.getName());

    public void GenerateWords(String phrase, Thing linkedId){

        String preprocessString = phrase.toLowerCase().trim();

        if(preprocessString.length() <= 3){

            Dictionary dictionary = new Dictionary(preprocessString, linkedId);
            ObjectifyService.ofy().save().entity(dictionary).now();

        }else {

            while (preprocessString.length() > 3) {

                Dictionary dictionary = new Dictionary(preprocessString, linkedId);
                ObjectifyService.ofy().save().entity(dictionary).now();

                preprocessString = preprocessString.substring(1);

            }

        }

    }

}
