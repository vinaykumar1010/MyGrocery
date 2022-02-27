package com.vinappstudio.mygrocerylist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    private TextView itemName;
    private TextView quantity;
    private TextView dateAdded;
    private int groceryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        itemName = findViewById(R.id.name_Detail_ID);
        quantity = findViewById(R.id.quantity_Detail_ID);
        dateAdded = findViewById(R.id.date_Detail_ID);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            itemName.setText(extras.getString("name"));
            quantity.setText(extras.getString("quantity"));
            dateAdded.setText(extras.getString("dateAdded"));
            groceryId = extras.getInt("id");

        }
    }
}