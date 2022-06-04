package com.example.bookvibe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.bookvibe.adapters.AdapterCategory;
import com.example.bookvibe.adapters.AdapterCollection;
import com.example.bookvibe.databinding.ActivityProfileBinding;
import com.example.bookvibe.models.ModelCategory;
import com.example.bookvibe.models.ModelCollection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;
    
    //firebase auth, fjr loading user data using user uid
    FirebaseAuth firebaseAuth;

    private static final String TAG = "PROFILE_TAG";

    //array list to store collection
    private List<ModelCollection> collectionArrayList;
    //adapter
    private AdapterCollection adapterCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();
        loadCollections();

        //handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle click, start collection add screen
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, CollectionAddActivity.class));
            }
        });
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info..."+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info of user from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();

                        //format date to dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        //set image, using glide ...

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCollections() {
        //init arraylist
        collectionArrayList = new ArrayList<>();

        //get all collection from firebase > Users > Collections
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Collections")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear arraylist before adding data into it
                collectionArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    //get data
                    ModelCollection modelCollection = dataSnapshot.getValue(ModelCollection.class);

                    //add to arraylist
                    collectionArrayList.add(modelCollection);
                }
                //setup adapter
                adapterCollection = new AdapterCollection(ProfileActivity.this, collectionArrayList);
                //set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCollection);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}