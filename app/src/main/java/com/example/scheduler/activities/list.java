package com.example.scheduler.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scheduler.R;
import com.example.scheduler.data.DataHelper;
import com.example.scheduler.data.Meeting;
import com.example.scheduler.data.MeetingContract;

import java.util.ArrayList;
import java.util.List;

public class list extends AppCompatActivity {

    DataHelper dataHelper;
    ListView meetingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        dataHelper = DataHelper.getInstance(getApplicationContext());

        meetingList = findViewById(R.id.meetingListView);

        Intent intent = getIntent();
        if(intent.hasExtra("day")) {
            String id = intent.getStringExtra("day");
            listMeetingToday(id);
        }else{
            listAllMeetings();
        }
    }

    public void listAllMeetings(){
        SQLiteDatabase db = dataHelper.getReadableDatabase();

        List<Meeting> temp = dataHelper.getAllMeetings();

        ArrayList<Meeting> list = new ArrayList<>();

        for(Meeting i: temp){
            list.add(i);
        }

        ArrayAdapter<Meeting> adapter = new ArrayAdapter<>(
                this, // Context
                android.R.layout.simple_list_item_1, // Predefined layout
                list // Data source
        );

        meetingList.setAdapter(adapter);

    }

    @SuppressLint("NewApi")
    public void listMeetingToday(String day){

        SQLiteDatabase db = dataHelper.getReadableDatabase();

        ArrayList<Meeting> meetings = new ArrayList<>();

        String var = "";
        int i = 0;
        if(day.equals("tom")){
            i =1;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);      // Add 1 day to the current date

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Add 1 because months are 0-indexed
        int dey = calendar.get(Calendar.DAY_OF_MONTH);

        // Format as "yyyy-MM-dd"
        var = year + "-" +
                String.format("%02d", month) + "-" +
                String.format("%02d", dey);

        String query = "SELECT * FROM " + MeetingContract.TABLE_NAME +
                " WHERE substr(" + MeetingContract.COLUMN_DATE_TIME + ", 1, 10) = ?";

        Cursor cursor = db.rawQuery(query,new String[]{var});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String id = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_ID));
                @SuppressLint("Range")
                String title = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_TITLE));
                @SuppressLint("Range")
                String contact = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_CONTACT));
                @SuppressLint("Range")
                String dateTime = cursor.getString(cursor.getColumnIndex(MeetingContract.COLUMN_DATE_TIME));

                Meeting meeting = new Meeting(id, title, contact, dateTime);
                meetings.add(meeting);
            } while (cursor.moveToNext());

            cursor.close();
        }

        ArrayAdapter<Meeting> adapter = new ArrayAdapter<>(
                this, // Context
                android.R.layout.simple_list_item_1, // Predefined layout
                meetings // Data source
        );

        meetingList.setAdapter(adapter);
    }


}