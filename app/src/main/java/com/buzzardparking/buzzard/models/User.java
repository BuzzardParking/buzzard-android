package com.buzzardparking.buzzard.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.parceler.Parcel;

import java.util.List;

/**
 * User model
 */
@Table(name = "Users")
@Parcel(analyze={User.class})
public class User extends Model {
    @Column(name = "userId")
    public String userId;

    @Column(name = "name")
    public String name;

   @Column(name = "preferExternalNavigation")
    public boolean preferExternalNavigation;

    public User() {
        super();
    }

    public User(String name) {
        this.name = name;
    }

    public boolean doesPreferExternalNavigation() {
        return preferExternalNavigation;
    }

    public void setPreferExternalNavigation(boolean preferExternalNavigation) {
        this.preferExternalNavigation = preferExternalNavigation;
    }

    public static List<User> getAll() {
        return new Select()
                .from(User.class)
                .execute();
    }

}
