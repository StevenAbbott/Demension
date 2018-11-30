package com.example.sbabb.facial.model;

public class Person {

    private String mKey, mName;

    public Person() {

    }

    public Person(String key) {
        this.mKey = key;
    }

    public Person(String key, String name) {
        this.mKey = key;
        this.mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPhotoFilename(int fileNumber) {
        return "IMG" + fileNumber+ "_" + getKey() + ".jpg";
    }
}
