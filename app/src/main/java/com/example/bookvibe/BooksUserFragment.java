package com.example.bookvibe;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookvibe.adapters.AdapterPdfUser;
import com.example.bookvibe.databinding.FragmentBooksUserBinding;
import com.example.bookvibe.databinding.RowPdfUserBinding;
import com.example.bookvibe.models.ModelPdf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksUserFragment extends Fragment {

    //this we passed while creating instance of the fragment
    private String categoryId;
    private String category;
    private String uid;

    private List<ModelPdf> pdfArrayList;
    private AdapterPdfUser adapterPdfUser;

    //view binding
    private FragmentBooksUserBinding binding;

    private static final String TAG = "BOOKS_USER_TAG";

    public BooksUserFragment() {
        // Required empty public constructor
    }


    public static BooksUserFragment newInstance(String categoryId, String category, String uid) {
        BooksUserFragment fragment = new BooksUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate/bind the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(getContext()), container, false);

        Log.d(TAG, "onCreateView: Category: "+category);
       if(category.equals("All")) {
            //load all books
            loadAllBooks();
        }
        else if(category.equals("Most Viewed")) {
            //load most viewed
            loadMostViewedDownloadedBooks("viewsCount");

        }
        else if(category.equals("Most Downloaded")) {
            //load most downloaded
            loadMostViewedDownloadedBooks("downloadsCount");
        }
        else {
            //load selected category books
            loadCategorizeBooks();
        }
        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //called as and when user type any letter
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
        return binding.getRoot();
    }

    private void loadAllBooks() {
        //init list
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    //get data
                    ModelPdf modelPdf = dataSnapshot.getValue(ModelPdf.class);
                    //add to list
                    pdfArrayList.add(modelPdf);
                }
                //setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                //set adapter to recyclerview
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadMostViewedDownloadedBooks(String orderBy) {
        //init list
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild(orderBy).limitToLast(10) //load 10 most viewed or downloaded books
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    //get data
                    ModelPdf modelPdf = dataSnapshot.getValue(ModelPdf.class);
                    //add to list
                    pdfArrayList.add(modelPdf);
                }
                //setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                //set adapter to recyclerview
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadCategorizeBooks() {
        //init list
        pdfArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
        reference.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    //get data
                    ModelPdf modelPdf = dataSnapshot.getValue(ModelPdf.class);
                    //add to list
                    pdfArrayList.add(modelPdf);
                }
                //setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                //set adapter to recyclerview
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}