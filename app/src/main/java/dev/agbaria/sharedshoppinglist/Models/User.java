package dev.agbaria.sharedshoppinglist.Models;

/**
 * Created by ANDROID on 07/12/2016.
 */

public class User {
    private String name;
    private String email;
    private String pictureURL;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.pictureURL = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
            return email;
        }

    public void setEmail(String email) {
            this.email = email;
        }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
