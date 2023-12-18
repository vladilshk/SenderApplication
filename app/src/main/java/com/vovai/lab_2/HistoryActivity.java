package com.vovai.lab_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.vovai.lab_2.R;
import com.vovai.lab_2.repostitory.RequestHistoryRepository;

import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private RequestHistoryRepository historyRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        historyRepository = new RequestHistoryRepository(this);

        // Получаем данные из базы данных и отображаем их в ListView
        showHistory();
    }

    private void showHistory() {
        // Получаем историю из базы данных
        List<String> historyList = historyRepository.getHistory();
        Collections.reverse(historyList);
        // Отображаем историю в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        listViewHistory.setAdapter(adapter);
    }
}
