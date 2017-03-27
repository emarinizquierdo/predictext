package com.predictext.beans;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;

import java.lang.String;


/**
 * The @Entity tells Objectify about our entity.  We also register it in {@link OfyHelper}
 * Our primary key @Id is set automatically by the Google Datastore for us.
 *
 * We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks.  Objectify, unlike the AppEngine library requires that you specify the fields you
 * want to index using @Index.  Only indexing the fields you need can lead to substantial gains in
 * performance -- though if not indexing your data from the start will require indexing it later.
 *
 * NOTE - all the properties are PUBLIC so that can keep the code simple.
 **/
@Entity
@Cache
public class Dictionary {

    @Id
    @Expose
    private Long id;

    @Index
    @Expose
    private String word;

    private Ref<Thing> thingRef;

    @IgnoreSave
    @Expose
    private Thing thing;

    public Dictionary(){}

    public Dictionary(String pword, Thing plinkedId){

        this();
        word = pword;
        setThingRef(plinkedId);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Ref<Thing> getThingRef() {
        return this.thingRef;
    }

    public void setThingRef(Thing thing) {
        this.thingRef = Ref.create(thing);
    }

    @OnLoad
    void defer(){
        this.thing = this.thingRef.get();
    }

}