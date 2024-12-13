package com.example.scheduler.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataHelper extends SQLiteOpenHelper {

   private static final String DATABASE_NAME = "meetings.db";
   private static final int DATABASE_VERSION = 1;

   private static final String TABLE_CREATE =
           "CREATE TABLE " + MeetingContract.TABLE_NAME + " (" +
                   MeetingContract.COLUMN_ID + " TEXT PRIMARY KEY, " +
                   MeetingContract.COLUMN_CONTACT + " TEXT, " +
                   MeetingContract.COLUMN_TITLE + " TEXT, " +
                   MeetingContract.COLUMN_DATE_TIME + " TEXT);";


   private static DataHelper instance;

   private DataHelper(Context context){
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   public static synchronized DataHelper getInstance(Context context){
       if(instance == null){
           instance = new DataHelper(context.getApplicationContext());
       }
       return instance;
   }

   public void onCreate(SQLiteDatabase db){
       db.execSQL(TABLE_CREATE);
   }
    public void insertMeeting(String id, String title, String contact, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String insertQuery = "INSERT INTO " + MeetingContract.TABLE_NAME +
                    " (" + MeetingContract.COLUMN_ID + ", " +
                    MeetingContract.COLUMN_CONTACT + ", " +
                    MeetingContract.COLUMN_TITLE + ", " +
                    MeetingContract.COLUMN_DATE_TIME + ") " +
                    "VALUES ('" + id + "', '" + title + "', '" + contact + "', '" + date + "');";

            db.execSQL(insertQuery);
        } catch (Exception e) {
            Log.e("DataHelper", "Error inserting meeting manually", e);
        }
    }



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, just drop the table and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + MeetingContract.TABLE_NAME);
        onCreate(db);
    }

    public void delete(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + MeetingContract.TABLE_NAME);  // Execute DELETE command
        db.close();
    }

    public void deleteSpecific(int year, int month, int day){

       Log.d("check","working");
        // Format as "yyyy-MM-dd"
        String var = year + "-" +
                String.format("%02d", month) + "-" +
                String.format("%02d", day);

        Log.d("date",var);
        printMeetingDates();

        String whereClause = "SUBSTR(date, 1, 10) = ?";
        String[] whereArgs = new String[] {var};

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("meeting_table",whereClause,whereArgs);
        Log.d("rowsDeleted",String.valueOf(rowsDeleted));

    }
    public void printMeetingDates() {
        SQLiteDatabase db = instance.getReadableDatabase();

        // Define the query to retrieve the date_time column
        String query = "SELECT " + MeetingContract.COLUMN_DATE_TIME + " FROM " + MeetingContract.TABLE_NAME;

        // Execute the query
        Cursor cursor = db.rawQuery(query, null);

        // Check if the cursor contains any data
        if (cursor.moveToFirst()) {
            do {
                // Extract the date_time column value
                @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_DATE_TIME));
                Log.d("MeetingDate", "Meeting Date: " + dateTime);
            } while (cursor.moveToNext());
        } else {
            Log.d("MeetingDate", "No meetings found in the table.");
        }

        // Close the cursor to release resources
        cursor.close();
    }

    public List<Meeting> getAllMeetings(){
       List<Meeting> meetings = new ArrayList<>();

       SQLiteDatabase db = this.getReadableDatabase();

       Cursor cursor = db.query(
               MeetingContract.TABLE_NAME,
               null,null,null,null,null,
               MeetingContract.COLUMN_DATE_TIME + " ASC"
       );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_TITLE));
                @SuppressLint("Range") String contact = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_CONTACT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_DATE_TIME));

                Meeting meeting = new Meeting(id, title, contact, date);
                meetings.add(meeting);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return meetings;
    }

    @SuppressLint("Range")
    public void updateMeetingDate(String oldDate, String newDate){
       SQLiteDatabase db = instance.getWritableDatabase();

        String query = "SELECT * FROM " + MeetingContract.TABLE_NAME +
                " WHERE SUBSTR(" + MeetingContract.COLUMN_DATE_TIME + ", 1, 10) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{oldDate});

        if(cursor != null && cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_ID));

                ContentValues values = new ContentValues();
                String newDateTime = newDate + cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_DATE_TIME)).substring(10);
                values.put(MeetingContract.COLUMN_DATE_TIME,newDateTime);

                String whereClause = MeetingContract.COLUMN_ID + " = ?";
                String[] whereArgs = new String[]{id}; // Update the row with this ID
                db.update(MeetingContract.TABLE_NAME, values, whereClause, whereArgs);
            }   while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}