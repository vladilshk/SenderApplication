package com.vovai.lab_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import com.vovai.lab_2.repostitory.RequestHistoryRepository;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private EditText linkEditText;
    private Button sendButton;
    private TextView textViewSender;

    String apiEndpoint = "https://remoteservice.onrender.com/v2/6";
    private Handler handler = new Handler();
    private static final int INTERVAL = 30 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkEditText = findViewById(R.id.editTextText);
        sendButton = findViewById(R.id.button2);
        textViewSender = findViewById(R.id.textViewSender);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = linkEditText.getText().toString().trim();
                if (!link.isEmpty()) {
                    sendPostRequest(link);
                } else {
                    Toast.makeText(MainActivity.this, "Введите ссылку", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button historyButton = findViewById(R.id.button3);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Открываем активность с историей запросов
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        autoSender.run();
    }

    private Runnable autoSender = new Runnable() {
        @Override
        public void run() {
            sendPostRequest(apiEndpoint);
            handler.postDelayed(this, INTERVAL);
        }
    };

    @SuppressLint("StaticFieldLeak")
    private void sendPostRequest(final String link) {

        Random random = new Random();
        double value = random.nextDouble();
        String jsonBody = "{\"value\": " + value + "}";

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);
                Request request = new Request.Builder()
                        .url(link)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    System.out.println("ResponseCode: " + response.code());
                    if (response.code() != 200){
                        throw new IOException();
                    } else {
                        showSnackbar("Данные успешно отправленны!", Color.GREEN, Color.WHITE, Snackbar.LENGTH_LONG);
                    }
                    RequestHistoryRepository repository = new RequestHistoryRepository(getApplicationContext());
                    SQLiteDatabase db = repository.getReadableDatabase();
                    Date date = new Date();
                    repository.insert(db, value, date.toString());
                    Cursor userCursor =  db.rawQuery("select * from "+ "requests", null);
                    System.out.println("Кол-во запросов в бд " + userCursor.getCount());
                    while(userCursor.getCount() > 100) {
                        repository.delete(db);
                        userCursor =  db.rawQuery("select * from "+ "requests", null);
                    }
                    userCursor.close();

                    return response.body().string();
                } catch (IOException e) {
                    showSnackbar("Что-то пошло не так, проверьте ссылку!", Color.GREEN, Color.WHITE, Snackbar.LENGTH_LONG);
                    return null;
                }
            }

        }.execute();
    }


    private void showSnackbar(String message, int backgroundColor, int textColor, int duration) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, duration);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(backgroundColor);
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(textColor);
        snackbar.show();
    }

}