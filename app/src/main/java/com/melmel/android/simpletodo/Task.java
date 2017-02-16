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

    @Column
    String title;

    @Column
    int priority;

    @Column
    String dueDate;

    @Override
    public String toString() {
        return this.description;
    }

    public final static int PRIORITY_HIGH = 3;
    public final static int PRIORITY_MEDIUM = 2;
    public final static int PRIORITY_LOW = 1;

    public void setDescription(String desc) {
        this.description = desc;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    public void setPriority(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setId(int id) {
        this.id = id;
    }

}
