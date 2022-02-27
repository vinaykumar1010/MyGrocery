package com.vinappstudio.mygrocerylist;

import android.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.vinappstudio.mygrocerylist.Data.DatabaseHandler;
import com.vinappstudio.mygrocerylist.Model.Grocery;

import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem;
    private EditText quantity;
    private Button saveButton;
    private FloatingActionButton fab;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate our database
        db = new DatabaseHandler(getApplicationContext());
        byPassActivity();
      ///  Log.d("MA", " getGroceriesCount " + db.getGroceriesCount());

        fab = findViewById(R.id.fab);
        saveButton = findViewById(R.id.saveButton_ID);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", " clicked fab");
                createPopupDialog();

            }
        });

    }

    private void createPopupDialog() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        groceryItem = view.findViewById(R.id.groceryItem);
        quantity = view.findViewById(R.id.groceryQty);
        saveButton = view.findViewById(R.id.saveButton_ID);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo : save to db
                // Todo : go to next screen

                if (!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()) {
                    saveGroceryToDB(view);
                }
            }
        });
    }

    private void saveGroceryToDB(View v) {
        Grocery grocery = new Grocery();
        String newGrocery = groceryItem.getText().toString();
        String newGroceryQuantity = quantity.getText().toString();
        grocery.setName(newGrocery);
        grocery.setQuantity(newGroceryQuantity);

        // save to DB
        db.addGrocery(grocery);
        // snackbar is similar to toast
        Snackbar.make(v, "Item Saved", Snackbar.LENGTH_LONG).show();
        Log.d("Item Added ID: ", String.valueOf(db.getGroceriesCount()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this , ListActivity.class));
            }
        },1200);
    }

    public void  byPassActivity(){
        //check if database is empty, if not then
        // we go  listActivity and show all added item
        Log.d("MA", " inside bypass " + db.getGroceriesCount());

        if(db.getGroceriesCount() > 0 ){
            startActivity(new Intent(MainActivity.this , ListActivity.class));
            finish();
        }
        }

}