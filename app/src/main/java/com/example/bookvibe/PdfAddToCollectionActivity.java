package com.example.bookvibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookvibe.databinding.ActivityPdfAddToCollectionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PdfAddToCollectionActivity extends AppCompatActivity {

    //setup binding
    private ActivityPdfAddToCollectionBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    //arrayList to hold pdf collections
    private List<String> collectionTitleArrayList, collectionIdArrayList;

    //arrayList to hold pdf from library
    private List<String> bookTitleArrayList, bookIdArrayList;

    private static final int PDF_PICK_CODE = 1000;

    //TAG for debugging
    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddToCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCollections();
        loadBooksFromLibrary();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, pick collection
        binding.collectionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionPickDialog();
            }
        });

        //handle click, pick book
        binding.bookTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookPickDialog();
            }
        });

        //handle click, add to collection
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate data
                validateData();
            }
        });
    }

    private void validateData() {
        //Step 1: validate data
        Log.d(TAG, "validateData: validating data...");

        //validate data
        if (TextUtils.isEmpty(selectedCollectionTitle)) {
            Toast.makeText(this, "Pick Collection...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedBookTitle)) {
            Toast.makeText(this, "Pick Book...", Toast.LENGTH_SHORT).show();
        }
        else {
            //all data is valid, can upload now
            addBookToCollection();
        }
    }

    private void addBookToCollection() {

        //show progress
        progressDialog.setMessage("Adding to collection...");

        //set book in library collectionId
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Library").child(selectedBookId).child("collectionId").setValue(selectedCollectionId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Book added to collection");
                        Toast.makeText(PdfAddToCollectionActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to add to collection due to "+ e.getMessage());
                        Toast.makeText(PdfAddToCollectionActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //selected category id and category title
    private String selectedBookId;
    private String selectedBookTitle;
    private void bookPickDialog() {
        //first we need to get books from library from firebase => loadBooksFromLibrary();
        Log.d(TAG, "bookPickDialog: showing book pick dialog");

        //get string array of books from arraylist
        String[] bookArray = new String[bookTitleArrayList.size()];
        for(int i = 0; i < bookTitleArrayList.size(); i++) {
            bookArray[i] = bookTitleArrayList.get(i);
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Book from Library")
                .setItems(bookArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle item click
                        //get clicked item from list
                        selectedBookTitle = bookTitleArrayList.get(i);
                        selectedBookId = bookIdArrayList.get(i);
                        //set to category textview
                        binding.bookTv.setText(selectedBookTitle);

                        Log.d(TAG, "onClick: Selected Book"+selectedBookId+" "+selectedBookTitle);

                    }
                })
                .show();
    }

    //selected category id and category title
    private String selectedCollectionId;
    private String selectedCollectionTitle;
    private void collectionPickDialog() {
        //first we need to get collection from firebase => loadPdfCollections();
        Log.d(TAG, "collectionPickDialog: showing collection pick dialog");

        //get string array of collections from arraylist
        String[] collectionArray = new String[collectionTitleArrayList.size()];
        for(int i = 0; i < collectionTitleArrayList.size(); i++) {
            collectionArray[i] = collectionTitleArrayList.get(i);
        }

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Collection")
                .setItems(collectionArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle item click
                        //get clicked item from list
                        selectedCollectionTitle = collectionTitleArrayList.get(i);
                        selectedCollectionId = collectionIdArrayList.get(i);
                        //set to category textview
                        binding.collectionTv.setText(selectedCollectionTitle);

                        Log.d(TAG, "onClick: Selected Collection"+selectedCollectionId+" "+selectedCollectionTitle);

                    }
                })
                .show();
    }

    private void loadBooksFromLibrary() {
        Log.d(TAG, "loadBooksFromLibrary: Loading books in library");
        bookIdArrayList = new ArrayList<>();
        bookTitleArrayList = new ArrayList<>();

        //do reference to load collections...db > User > userId > Collections
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Library")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookIdArrayList.clear(); //clear before adding
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            //get id of book
                            String bookId = ""+dataSnapshot.child("bookId").getValue();
                            String bookTitle = ""+dataSnapshot.child("bookTitle").getValue();

                            //add to respective arraylist
                            bookIdArrayList.add(bookId);
                            bookTitleArrayList.add(bookTitle);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadPdfCollections() {
        Log.d(TAG, "loadPdfCollections: Loading pdf collections...");
        collectionTitleArrayList = new ArrayList<>();
        collectionIdArrayList = new ArrayList<>();

        //do reference to load collections...db > User > userId > Collections
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Collections")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        collectionIdArrayList.clear();
                        collectionTitleArrayList.clear(); //clear before adding
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            //get id and title of category
                            String collectionId = ""+dataSnapshot.child("id").getValue();
                            String collectionTitle = ""+dataSnapshot.child("collection").getValue();

                            //add to respective arraylist
                            collectionTitleArrayList.add(collectionTitle);
                            collectionIdArrayList.add(collectionId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}