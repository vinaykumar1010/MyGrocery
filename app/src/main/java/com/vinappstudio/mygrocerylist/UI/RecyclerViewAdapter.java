package com.vinappstudio.mygrocerylist.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vinappstudio.mygrocerylist.Data.DatabaseHandler;
import com.vinappstudio.mygrocerylist.DetailsActivity;
import com.vinappstudio.mygrocerylist.Model.Grocery;
import com.vinappstudio.mygrocerylist.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    // context field tat allow us to pass context field
    private Context context;
    private List<Grocery> groceryItems;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;


    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
        Log.d("RVA", " inside  RecyclerViewAdapter constructor" + groceryItems);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate list row here , then create a view , which then attach data to it
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        Log.d("RVA", " inside RecyclerViewAdapter.ViewHolder");

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        //  bind views  with the widget
        // this will take the position of the row  we are currently using or tapping or that is currently active
        Grocery grocery = groceryItems.get(position); // we are at position 0  or 1st obj or 1st row
        Log.d("RVA", " inside onBindViewHolder");

        Log.d("RVA", " after grocery" + groceryItems.get(position));


        holder.groceryItemName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDateItemAdded());

        Log.d("RVA", " at showing position" + grocery.getName());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;
            Log.d("RVA", " inside class View Holder Constructor");

            groceryItemName = view.findViewById(R.id.name_ID);
            quantity = view.findViewById(R.id.quantity_ID);
            dateAdded = view.findViewById(R.id.dateAdded_ID);

            editButton = view.findViewById(R.id.editButton_ID);
            deleteButton = view.findViewById(R.id.deleteButton_ID);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            // we also set internal view means whole row to onclick listner as well
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("id", grocery.getId());
                    intent.putExtra("date", grocery.getDateItemAdded());
                    context.startActivity(intent);


                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.editButton_ID:
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    editItem(grocery);
                    break;
                case R.id.deleteButton_ID:
                     position = getAdapterPosition();
                     grocery = groceryItems.get(position);
                    deleteItem(grocery.getId());

                    break;
            }
        }

        public void editItem(Grocery grocery) {
            alertDialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup, null);

            final EditText groceryItem = view.findViewById(R.id.groceryItem);
            final  EditText quantity = view.findViewById(R.id.groceryQty);
            final TextView title = view.findViewById(R.id.title);
            title.setText("Edit Grocery");
            Button saveButton = view.findViewById(R.id.saveButton_ID);
            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseHandler db = new DatabaseHandler(context);
                    // update item
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(quantity.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()) {
                        db.updateGrocery(grocery);
                        notifyItemRemoved(getAdapterPosition());
                        // this will notify other listeners and classes  , hey something changed
                        // , that way we need to change or restructure view so we can reflect the changes made
                    } else {
                        Snackbar.make(view, "Add Grocery and quantiy", Snackbar.LENGTH_LONG).show();
                    }
                    dialog.dismiss();

                }
            });


        }

        public void deleteItem(final int id) {
            // create an alert dialog
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_layout, null);

            Button noButton = view.findViewById(R.id.noButton_ID);
            Button yesButton = view.findViewById(R.id.yesButton_ID);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  delete item
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteGrocery(id);
                    // physically delete from recycler view
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });


        }


    }
}
