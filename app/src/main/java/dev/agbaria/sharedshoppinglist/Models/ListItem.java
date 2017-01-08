package dev.agbaria.sharedshoppinglist.Models;

/**
 * Created by ANDROID on 04/01/2017.
 */

public class ListItem {
    private String itemName;
    private boolean checked;

    public ListItem() {
    }

    public ListItem(String itemName, boolean checked) {
        this.itemName = itemName;
        this.checked = checked;
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

    @Override
    public String toString() {
        return "ListItem{" +
                "itemName='" + itemName + '\'' +
                ", checked=" + checked +
                '}';
    }
}
