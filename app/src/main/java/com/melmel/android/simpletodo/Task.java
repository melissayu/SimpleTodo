package com.melmel.android.simpletodo;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by melmel on 2/13/2017.
 */
@Table(database = MyDatabase.class)
public class Task extends BaseModel {

    @Column
    @PrimaryKey (autoincrement=true)
    @Unique
    int id;

    @Column
    String description;

//    @Column
//    int priority;

    @Override
    public String toString() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setId(int id) {
        this.id = id;
    }

}
