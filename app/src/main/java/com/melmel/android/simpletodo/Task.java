package com.melmel.android.simpletodo;

import android.widget.DatePicker;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public void setPriority(int priorityLevel) {
        this.priority = priorityLevel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static String datePickerToString(DatePicker datePicker){
        int   day  = datePicker.getDayOfMonth();
        int   month= datePicker.getMonth();
        int   year = datePicker.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date dateRepresentation = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = sdf.format(dateRepresentation);

        return formattedDate;
    }

    public static Date parseDateString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setLenient(false);
        Date parsedDate = new Date();
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

}
