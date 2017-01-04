package dev.agbaria.sharedshoppinglist.Models;

import java.io.Serializable;

/**
 * Created by ANDROID on 28/12/2016.
 */

public class ShoppingList implements Serializable {
    private String listName;
    private String listOwner;

    public ShoppingList() {

    }

    public ShoppingList(String listName, String listOwner) {
        this.listName = listName;
        this.listOwner = listOwner;
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
}
