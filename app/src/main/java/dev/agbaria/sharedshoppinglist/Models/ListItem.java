package dev.agbaria.sharedshoppinglist.Models;

/**
 * Created by ANDROID on 04/01/2017.
 */

public class ListItem {
    private String itemName;
    private boolean checked;
    private String userID;

    public ListItem() {
    }

    public ListItem(String itemName, boolean checked, String userID) {
        this.itemName = itemName;
        this.checked = checked;
        this.userID = userID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "tvItemName='" + itemName + '\'' +
                ", checked=" + checked +
                '}';
    }
}
