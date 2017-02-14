package com.melmel.android.simpletodo;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by melmel on 2/13/2017.
 */
@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "MyDataBase";

    public static final int VERSION = 1;

}
