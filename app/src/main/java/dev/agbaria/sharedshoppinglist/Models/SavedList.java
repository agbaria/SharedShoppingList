package dev.agbaria.sharedshoppinglist.Models;

/**
 * Created by agbaria on 24/01/2017.
 * SavedList POJO
 */

public class SavedList {

    private String listName;

    public SavedList() {
    }

    public SavedList(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
