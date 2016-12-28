package dev.agbaria.sharedshoppinglist.Models;

/**
 * Created by ANDROID on 28/12/2016.
 */

public class ShoppingList {
    private String listID;
    private String listName;
    private String listOwner;

    public ShoppingList() {

    }

    public ShoppingList(String listID, String listName, String listOwner) {
        this.listID = listID;
        this.listName = listName;
        this.listOwner = listOwner;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
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
