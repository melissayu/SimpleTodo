package com.melmel.android.simpletodo;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by melmel on 2/13/2017.
 */
@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "MyDataBase";

    public static final int VERSION = 2;

    @Migration(version = 2, database = MyDatabase.class)
    public static class Migration2 extends AlterTableMigration<Task> {

        public Migration2(Class<Task> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "priority");
            addColumn(SQLiteType.TEXT, "dueDate");
        }
    }

}
