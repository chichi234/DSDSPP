package com.reb.dsd_spp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * File description
 *
 * @author Reb
 * @version 1.0
 * @date 2019-3-6 20:44
 * @package_name com.reb.bluetooth.db
 * @project_name DSD_SPP
 * @history At 2019-3-6 20:44 created by Reb
 */
public class SQLiteDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";

    public static final int DB_VERSION = 1;

    public static final String TABLE_DEVICE = "bondedDevice";

    private static final String DEVICES_CREATE_TABLE_SQL = "create table " + TABLE_DEVICE + "("
            + "mac_add varchar(17) primary key,"
            + "name varchar(32) not null,"
//            + "is_bond integer not null,"
            + "insert_time varchar(14) not null,"
            + "display_name varchar(32) ,"
            + "type integer default '1'"
            + ");";

    public SQLiteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEVICES_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertDevice(DeviceBond deviceBond) {
        ContentValues cv = deviceBond.convertContentValues();
        SQLiteDatabase database = getWritableDatabase();
        database.insert(TABLE_DEVICE, null, cv);
        database.close();
    }

    public List<DeviceBond> getDeviceBonds() {
        List<DeviceBond> deviceBonds = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_DEVICE, new String[]{"mac_add", "name", "insert_time", "type", "display_name"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DeviceBond deviceBond = new DeviceBond();
                deviceBond.setMac(cursor.getString(cursor.getColumnIndex("mac_add")));
                deviceBond.setInsert_time(cursor.getString(cursor.getColumnIndex("insert_time")));
                deviceBond.setName(cursor.getString(cursor.getColumnIndex("name")));
                deviceBond.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
                deviceBond.setType(cursor.getInt(cursor.getColumnIndex("type")));
                deviceBonds.add(deviceBond);
            }
            cursor.close();
        }
        database.close();
        return deviceBonds;
    }
}
