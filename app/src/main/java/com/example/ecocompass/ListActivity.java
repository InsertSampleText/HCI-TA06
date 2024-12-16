package com.example.ecocompass;

import android.app.AlertDialog;
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

public class ListActivity extends AppCompatActivity {

    ListView list;
    ArrayAdapter<item> listAdapter;
    Button buttonAdd, buttonRemove, buttonSort, backButton, buttonData;

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
        ArrayList<item> dynamicList = new ArrayList<item>();
        listAdapter = new ArrayAdapter<item>(this, android.R.layout.simple_list_item_1, dynamicList);
        list.setAdapter(listAdapter);

        // Initialize list with default items
        dynamicList.add(new item("Cardboard","It's boxes"));
        dynamicList.add(new item("Paper","The stuff you write on"));
        dynamicList.add(new item("Bottle 1,","It's a bottle"));
        dynamicList.add(new item("Bottle 2,","It's a really long bottle"));
        dynamicList.add(new item("Plastic","It's plastic"));
        listAdapter.notifyDataSetChanged();

        // Add Button
        buttonAdd = findViewById(R.id.addItem);
        buttonAdd.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Add Item");
            builder.setMessage("Enter the name and description of the item:");

            // Set up the input
            final android.widget.EditText inputName = new android.widget.EditText(this);
            final android.widget.EditText inputDescription = new android.widget.EditText(this);
            builder.setView(inputName);
            builder.setView(inputDescription);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                String name = inputName.getText().toString();
                String description = inputDescription.getText().toString();
                item item = new item(name, description);
                dynamicList.add(item);
                listAdapter.notifyDataSetChanged();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Remove Button
        buttonRemove = findViewById(R.id.removeItem);
        buttonData = findViewById(R.id.itemButton);

        //list selection
        list.setOnItemClickListener((parent, view, position, id) -> {

            //removes item from list
            buttonRemove.setOnClickListener(v -> {
                dynamicList.remove(position);
                listAdapter.notifyDataSetChanged();
            });


            //opens item data
            buttonData.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle(dynamicList.get(position).getName());
                builder.setMessage(dynamicList.get(position).getDescription());
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            });


        });

        // Sort Button
        buttonSort = findViewById(R.id.Sort);
        buttonSort.setOnClickListener(v -> {
            dynamicList.sort((item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
            listAdapter.notifyDataSetChanged();
        });


        //sources used
        /*
        https://developer.android.com/reference/java/util/List
        https://stackoverflow.com/questions/4540754/how-do-you-dynamically-add-elements-to-a-listview-on-android
        https://developer.android.com/reference/android/app/AlertDialog
        */


    }


    public class item {
        String  name;
        String description;

        public item(String n, String d) {
            name = n;
            description = d;;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
