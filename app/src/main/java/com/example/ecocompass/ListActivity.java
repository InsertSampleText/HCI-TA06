package com.example.ecocompass;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ListActivity extends AppCompatActivity {

    ListView list;
    ArrayList<String> dynamicList;
    ArrayAdapter<String> listAdapter;
    Button buttonAdd, buttonRemove, buttonSort, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        // Handling system bars for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back Button Functionality
        backButton = findViewById(R.id.backArrowMyList);
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // ListView and Adapter setup
        list = findViewById(R.id.list);
        dynamicList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dynamicList);
        list.setAdapter(listAdapter);

        // Initialize list with default items
        dynamicList.add("Cardboard");
        dynamicList.add("Decor");
        dynamicList.add("Bottle 1");
        dynamicList.add("Bottle 2");
        listAdapter.notifyDataSetChanged();

        // Add Button
        buttonAdd = findViewById(R.id.addItem);
        buttonAdd.setOnClickListener(v -> {
            dynamicList.add("New Item");
            listAdapter.notifyDataSetChanged();
        });

        // Remove Button
        buttonRemove = findViewById(R.id.removeItem);
        buttonRemove.setOnClickListener(v -> {
            if (!dynamicList.isEmpty()) {
                int randomIndex = new Random().nextInt(dynamicList.size());
                dynamicList.remove(randomIndex);
                listAdapter.notifyDataSetChanged();
            }
        });

        // Sort Button
        buttonSort = findViewById(R.id.Sort);
        buttonSort.setOnClickListener(v -> {
            Collections.sort(dynamicList);
            listAdapter.notifyDataSetChanged();
        });
    }
}
