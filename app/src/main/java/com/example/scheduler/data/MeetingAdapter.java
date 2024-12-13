package com.example.scheduler.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.scheduler.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MeetingAdapter extends ArrayAdapter<Meeting> {

    private Context context;
    private List<Meeting> meetings;

    public MeetingAdapter(Context context, List<Meeting> meetings) {
        super(context, 0, meetings);
        this.context = context;
        this.meetings = meetings;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        Meeting meeting = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meeting, parent, false);
        }

        // Get the views for title, contact, and date
        TextView titleTextView = convertView.findViewById(R.id.meetingTitle);
        TextView contactTextView = convertView.findViewById(R.id.meetingContact);
        TextView dateTextView = convertView.findViewById(R.id.meetingDate);

        titleTextView.setText(meeting.getTitle());
        contactTextView.setText(meeting.getContact());

        Log.d("date: " , meeting.getDate());
        String formattedDate = formatDate(meeting.getDate());
        dateTextView.setText(formattedDate);

        return convertView;
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy HH:mm");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
}
