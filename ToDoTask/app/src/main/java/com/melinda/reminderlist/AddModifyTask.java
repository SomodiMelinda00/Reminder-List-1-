package com.melinda.reminderlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddModifyTask extends AppCompatActivity {

    Calendar calendar;
    DBHelper mydb;

    Boolean isModify = false;
    String task_id;
    TextView toolbar_title;
    EditText edit_text;
    TextView dateText;
    Button save_btn;
    CheckBox important_check;
    int checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_modify_task);

        mydb = new DBHelper(getApplicationContext());
        calendar = new GregorianCalendar();
        toolbar_title = findViewById(R.id.toolbar_title);
        edit_text = findViewById(R.id.edit_text);
        dateText = findViewById(R.id.dateText);
        save_btn = findViewById(R.id.save_btn);
        important_check = findViewById(R.id.important_check);

        dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));


        Intent intent = getIntent();
        if (intent.hasExtra("isModify")) {
            isModify = intent.getBooleanExtra("isModify", false);
            task_id = intent.getStringExtra("id");
            init_modify();
        }


    }

    public void init_modify() {
        toolbar_title.setText("Modifică sarcina");
        save_btn.setText("Actualizează");
        LinearLayout deleteTask = findViewById(R.id.deleteTask);
        deleteTask.setVisibility(View.VISIBLE);
        Cursor task = mydb.getSingleTask(task_id);
        if (task != null) {
            task.moveToFirst();


            edit_text.setText(task.getString(1));

            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(iso8601Format.parse(task.getString(2)));
            } catch (ParseException e) {
            }

            dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));


        }

    }


    public void saveTask(View v) {
        if(important_check.isChecked())
        {
            checked = 1;

        }
        else {
            checked = 0;
        }
        if (edit_text.getText().toString().trim().length() > 0) {

            if (isModify) {
                mydb.updateTask(task_id, edit_text.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Sarcină actualizată.", Toast.LENGTH_SHORT).show();
            } else {
                mydb.insertTask(edit_text.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Sarcină adăugată.", Toast.LENGTH_SHORT).show();
            }
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Scrieți numele sarcinii!.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteTask(View v) {
        mydb.deleteTask(task_id);
        Toast.makeText(getApplicationContext(), "Acțiune eliminată", Toast.LENGTH_SHORT).show();
        finish();
    }


    public void chooseDate(View view) {
        final View dialogView = View.inflate(this, R.layout.date_picker, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Alege o dată");
        builder.setNegativeButton("Anulează", null);
        builder.setPositiveButton("Setează", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));

            }
        });
        builder.show();
    }
}
