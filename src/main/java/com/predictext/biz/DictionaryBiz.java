package com.predictext.biz;

import com.google.appengine.api.search.*;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.predictext.beans.Dictionary;
import com.predictext.beans.Thing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bbva on 27/03/17.
 */
public class DictionaryBiz {


    private final static Logger LOGGER = Logger.getLogger(DictionaryBiz.class.getName());


    public void createSearchableUserDoc(String id, String displayName) {
        List<String> substrings = buildAllSubstrings(displayName);
        String combinedString = combine(substrings, " ");
        // The input for this looks like "CHR CHRI CHRIS HRI HRIS" etc...
        createUserDocument(id, combinedString);
    }

    private List<String> buildAllSubstrings(String displayName) {
        List<String> substrings = new ArrayList<String>();
        for (String word : displayName.split(" ")) {
            int wordSize = 1;
            while (true) {
                for (int i = 0; i < word.length() - wordSize + 1; i++) {
                    substrings.add(word.substring(i, i + wordSize));
                }
                if (wordSize == word.length())
                    break;
                wordSize++;
            }
        }
        return substrings;
    }

    private String combine(List<String> strings, String glue) {
        int k = strings.size();
        if (k == 0)
            return null;
        StringBuilder out = new StringBuilder();
        out.append(strings.get(0));
        for (int x = 1; x < k; ++x)
            out.append(glue).append(strings.get(x));
        return out.toString();
    }

    private void createUserDocument(String id, String searchableSubstring) {
        Document.Builder docBuilder = Document
                .newBuilder()
                .addField(Field.newBuilder().setName("thingId").setText(id))
                .addField(
                        Field.newBuilder().setName("Display_Name")
                                .setText(searchableSubstring));

        addDocToIndex(docBuilder.build());

    }

    private void addDocToIndex(Document document) {

        Index index = getUserDocIndex();

        try {
            index.put(document);
        } catch (PutException e) {
            LOGGER.warning("Error putting document in index... trying again.");
            if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                index.put(document);
            }
        }
    }

    public static Index getUserDocIndex() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("USER_DOC_INDEX").build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        return index;
    }

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
