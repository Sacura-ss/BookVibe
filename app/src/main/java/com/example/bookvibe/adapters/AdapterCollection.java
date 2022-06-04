package com.example.bookvibe.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookvibe.databinding.RowCategoryBinding;
import com.example.bookvibe.filters.FilterCollection;
import com.example.bookvibe.models.ModelCategory;
import com.example.bookvibe.models.ModelCollection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.HolderCollection> implements Filterable {
    private Context context;
    public List<ModelCollection> collectionArrayList;
    public List<ModelCollection> filterList;

    //view binding
    private RowCategoryBinding binding;

    //instance of our filter class
    private FilterCollection filter;

    public AdapterCollection(Context context, List<ModelCollection> collectionArrayList) {
        this.context = context;
        this.collectionArrayList = collectionArrayList;
        this.filterList = collectionArrayList;
    }

    @NonNull
    @Override
    public AdapterCollection.HolderCollection onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCollection(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCollection.HolderCollection holder, int position) {
        //get data
        ModelCollection model = collectionArrayList.get(position);
        String id = model.getId();
        String collection = model.getCollection();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.collectionTv.setText(collection);

        //handle click, delete collection
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this collection?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //begin delete
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void deleteCategory(ModelCollection model, AdapterCollection.HolderCollection holder) {
        //get id of category to delete
        String id = model.getId();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //Firebase DB > Users > collections
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Collections").child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //delete successfully
                        Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to delete
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return collectionArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new FilterCollection(filterList, this);
        }
        return filter;
    }

    /*View holder class to hold UI views for row_category.xml*/
    class HolderCollection extends RecyclerView.ViewHolder {

        //ui views of row_category.xml
        TextView collectionTv;
        ImageButton deleteBtn;

        public HolderCollection(@NonNull CardView itemView) {
            super(itemView);
            //init ui views
            collectionTv = binding.categoryTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
