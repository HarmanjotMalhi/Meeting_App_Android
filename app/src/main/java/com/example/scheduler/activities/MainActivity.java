package com.example.scheduler.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scheduler.R;
import com.example.scheduler.data.DataHelper;
import com.example.scheduler.data.DateUtils;
import com.example.scheduler.data.Meeting;
import com.example.scheduler.data.MeetingAdapter;
import com.example.scheduler.data.MeetingContract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    DataHelper dataHelper;
    ListView meetingList;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dataHelper = DataHelper.getInstance(getApplicationContext());
        meetingList = findViewById(R.id.meetingListView);

        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the Toolbar as the ActionBar
        setSupportActionBar(toolbar);

        // Enable the back button in the Toolbar (Up button)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button list = findViewById(R.id.listAll);

        list.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                listAllMeetings();
            }
        });

        CalendarView calendarView = findViewById(R.id.calendarView);



        calendarView.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {

                        listMeetings(year,month+1,day);
                        TextView delete = findViewById(R.id.delete_text);

                        delete.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dataHelper.deleteSpecific(year,month+1,day);
                                listMeetings(year,month+1,day);
                            }
                        });

                        Button push = findViewById(R.id.push);

                        push.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                push(year,month+1,day);
                            }
                        });


                    }
                }
        );
        long selectedDateMillis = calendarView.getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDateMillis);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.create_meeting) {
            Intent intent = new Intent(this, CreateMeeting.class);
            startActivity(intent);  // Make sure to start the activity
            return true;
        }
        else if (item.getItemId() == R.id.clearAll){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear All Meetings");
            builder.setMessage("Do you want to proceed?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            builder.setPositiveButton("Yes", (dialog, which) -> {
                dataHelper.delete();
                Toast.makeText(this, "All meetings have been deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(this, "No meetings deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void listAllMeetings(){
        SQLiteDatabase db = dataHelper.getReadableDatabase();

        List<Meeting> temp = dataHelper.getAllMeetings();

        ArrayList<Meeting> list = new ArrayList<>();

        for(Meeting i: temp){
            list.add(i);
        }

        ArrayAdapter<Meeting> adapter = new MeetingAdapter(
                this, // Context
                list // Data source
        );

        meetingList.setAdapter(adapter);

    }

    @SuppressLint("NewApi")
    public void listMeetings(int year, int month, int dey){

        SQLiteDatabase db = dataHelper.getReadableDatabase();

        ArrayList<Meeting> meetings = new ArrayList<>();

        String var = "";

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

        ArrayAdapter<Meeting> adapter = new MeetingAdapter(
                this, // Context
                meetings // Data source
        );

        meetingList.setAdapter(adapter);
    }

    @SuppressLint("NewApi")
    public void push(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        Calendar nextWeekday = DateUtils.getNextWeekday(calendar);


        dataHelper.deleteSpecific(nextWeekday.get(Calendar.YEAR),(nextWeekday.get(Calendar.MONTH) + 1),nextWeekday.get(Calendar.DAY_OF_MONTH));

        String oldDate = year + "-" +
                String.format("%02d", month) + "-" +
                String.format("%02d", day);

        String newDate = nextWeekday.get(Calendar.YEAR) + "-" +
                String.format("%02d", (nextWeekday.get(Calendar.MONTH))+1) + "-" +
                String.format("%02d", nextWeekday.get(Calendar.DAY_OF_MONTH));

        Log.d("old date: ", oldDate);
        Log.d("new date: ", newDate);

        dataHelper.updateMeetingDate(oldDate,newDate);
        dataHelper.deleteSpecific(year,month,day);
    }

}







