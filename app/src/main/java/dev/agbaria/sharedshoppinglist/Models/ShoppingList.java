package dev.agbaria.sharedshoppinglist.Models;

import java.io.Serializable;

/**
 * Created by ANDROID on 28/12/2016.
 */

public class ShoppingList implements Serializable {
    private String listName;
    private String listOwner;
    private String description;
    private long creationTimeStamp;

    public ShoppingList() {

    }

    public ShoppingList(String listName, String listOwner, String description, long creationTimeStamp) {
        this.listName = listName;
        this.listOwner = listOwner;
        this.description = description;
        this.creationTimeStamp = creationTimeStamp;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListOwner() {
        return listOwner;
    }

    public void setListOwner(String listOwner) {
        this.listOwner = listOwner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(long creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }
}
