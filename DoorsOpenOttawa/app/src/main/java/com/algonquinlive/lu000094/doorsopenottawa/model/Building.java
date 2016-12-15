package com.algonquinlive.lu000094.doorsopenottawa.model;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Building implements Serializable{
    private int buildingId;
    private String name;
    private String address;
    private String image;
    private String description;
    transient private Bitmap bitmap;
    private List<String> openHours;

    public Building(){
        openHours = new ArrayList<>();
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address + " Ottawa, Ontario";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOpenHours() {
        return openHours;
    }

    public void addDate(String date){
        openHours.add(date);
    }
}

