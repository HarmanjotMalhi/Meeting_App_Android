package com.example.scheduler.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.scheduler.R;
import com.example.scheduler.data.DataHelper;

public class CreateMeeting extends AppCompatActivity {

    private EditText meetingDate;
    private EditText meetingTime;
    private TextView displayContact;
    private static final int PICK_CONTACT_REQUEST = 1;
    private DataHelper dataHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dataHelper = DataHelper.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        Button contactButton = findViewById(R.id.contact);

        contactButton.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                openContacts();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        });

        EditText meeting_id = findViewById(R.id.id);
        EditText meeting_title = findViewById(R.id.meeting_title);
        EditText meeting_Date = findViewById(R.id.meeting_date);
        EditText meeting_Time = findViewById(R.id.meeting_time);

        meetingDate = findViewById(R.id.meeting_date);

        meetingTime = findViewById(R.id.meeting_time);

        displayContact = findViewById(R.id.displayContact);

        meetingDate.setOnClickListener(v -> showDatePicker());

        meetingTime.setOnClickListener(v -> showTimePicker());

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataHelper.insertMeeting(meeting_id.getText().toString(),meeting_title.getText().toString(),displayContact.getText().toString(),meeting_Date.getText().toString()+ " " + meeting_Time.getText().toString());
                finish();
            }
        });


    }




    @SuppressWarnings("deprecation")
    public void openContacts(){

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Display the selected contact
                displayContact.setText(contactNumber);
                cursor.close();
            }
        }
    }

    @SuppressLint("NewApi")
    private void showTimePicker(){

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                CreateMeeting.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        meetingTime.setText(hourOfDay + ":" + String.format("%02d", minute));
                    }
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }
    @SuppressLint("NewApi")
    private void showDatePicker(){

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" +
                            String.format("%02d", selectedMonth + 1) + "-" + // Add 1 to month (0-based)
                            String.format("%02d", selectedDay);
                    meetingDate.setText(selectedDate);
                    Log.d("date: ",selectedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}