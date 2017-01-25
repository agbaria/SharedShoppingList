package dev.agbaria.sharedshoppinglist.Models;

import java.io.Serializable;

/**
 * Created by ANDROID on 25/01/2017.
 */

public class MySharedList implements Serializable {

    private String listName;
    private long joinDate;

    public MySharedList() {
    }

    public MySharedList(String listName, long joinDate) {
        this.listName = listName;
        this.joinDate = joinDate;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }
}
