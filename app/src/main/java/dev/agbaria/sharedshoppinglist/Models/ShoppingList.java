package dev.agbaria.sharedshoppinglist.Models;

import java.io.Serializable;

/**
 * Created by agbaria on 28/12/2016.
 * ShoppingList POJO
 */

public class ShoppingList implements Serializable {
    private String listName;
    private String listOwner;
    private long creationTimeStamp;

    public ShoppingList() {

    }

    public ShoppingList(String listName, String listOwner, long creationTimeStamp) {
        this.listName = listName;
        this.listOwner = listOwner;
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

    public long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(long creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }
}
