package com.example.bookvibe.models;

public class ModelCollection {
    //make sure to use same spellings for model variables as in firebase

    String id;
    String collection;
    String uid;
    long timestamp;

    //constructor empty required for firebase
    public ModelCollection() {
    }

    //parametrized constructor
    public ModelCollection(String id, String category, String uid, long timestamp) {
        this.id = id;
        this.collection = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    /*-----Getter/Setter------*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
