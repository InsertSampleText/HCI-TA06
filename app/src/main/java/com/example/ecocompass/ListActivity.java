package com.example.ecocompass;

//
import android.os.Bundle;
//
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//
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
    Button buttonAdd, buttonRemove, buttonSort;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //setup
        list = findViewById(R.id.list);
        dynamicList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dynamicList);
        list.setAdapter(listAdapter);

        dynamicList.add("Cardboard");
        dynamicList.add("Decor");
        dynamicList.add("Bottle 1");
        dynamicList.add("Bottle 2");


        buttonAdd = findViewById(R.id.addItem);
        buttonAdd.setOnClickListener(v -> {
            dynamicList.add("New Item");
            list.setAdapter(listAdapter);

        });

        buttonRemove = findViewById(R.id.removeItem);
        buttonRemove.setOnClickListener(v -> {
            dynamicList.remove(new Random().nextInt(dynamicList.size()));
            list.setAdapter(listAdapter);
        });

        buttonSort = findViewById(R.id.Sort);
        buttonSort.setOnClickListener(v -> {
            Collections.sort(dynamicList);
            list.setAdapter(listAdapter);
        });

    }


}

