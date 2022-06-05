package com.example.bookvibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.bookvibe.adapters.AdapterPdfUser;
import com.example.bookvibe.databinding.ActivityLibraryBinding;
import com.example.bookvibe.databinding.ActivityPdfListUserBinding;
import com.example.bookvibe.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*public class LibraryActivity extends AppCompatActivity {

    //view binding
    private ActivityLibraryBinding binding;

    //arraylist to hold list of data of type modelPdf
    private List<ModelPdf> pdfArrayList;
    //adapter
    private AdapterPdfUser adapterPdfUser;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth =  FirebaseAuth.getInstance();

        loadPdfList();

        //handle click, go to previous action
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        //init list
        pdfArrayList = new ArrayList<>();

        //load library from database
        //Users>userId>Library
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Library")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before clear list
                        pdfArrayList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            //we will only get the bookId here
                            String bookId = ""+dataSnapshot.child("bookId").getValue();

                            ModelPdf modelPdf = new ModelPdf();
                            modelPdf.setId(bookId);

                            pdfArrayList.add(modelPdf);
                        }

                        //setup adapter
                        adapterPdfUser = new AdapterPdfUser(LibraryActivity.this, pdfArrayList);
                        //set adapter to recycle view
                        binding.booRv.setAdapter(adapterPdfUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //6 55:50
    }*/
//}*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookvibe.databinding.ActivityDashboardUserBinding;
import com.example.bookvibe.fragments.BooksUserFragment;
import com.example.bookvibe.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    //view binding
    private ActivityLibraryBinding binding;

    //arraylist to hold list of data of type modelPdf
    private List<ModelPdf> pdfArrayList;
    //adapter
    private AdapterPdfUser adapterPdfUser;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        loadPdfList();

        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //search as and when type
                try {
                    adapterPdfUser.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //handle click, go to previous action
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        //init list
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Library")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            HashMap<String,String> values = (HashMap<String, String>) dataSnapshot.getValue();
                            String bookId = values.get("bookId");

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Books");
                            reference1.orderByChild("id").equalTo(bookId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot1: snapshot.getChildren()) {
                                                //get data
                                                ModelPdf model = dataSnapshot1.getValue(ModelPdf.class);
                                                //add to list
                                                pdfArrayList.add(model);

                                                Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        //setup adapter
                        adapterPdfUser = new AdapterPdfUser(LibraryActivity.this, pdfArrayList);
                        binding.bookRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //6 55:50
    }
}