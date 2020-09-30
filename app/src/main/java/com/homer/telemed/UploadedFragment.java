package com.homer.telemed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UploadedFragment extends Fragment implements ImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dBListener;

    private List<Upload> uploads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploaded, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        uploads = new ArrayList<Upload>();

        imageAdapter = new ImageAdapter(getActivity(), uploads);

        recyclerView.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener(UploadedFragment.this);

        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        dBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    if(upload.getUserID() == LoginActivity.jsonID) {
                        uploads.add(upload);
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getActivity(), "Normal click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = uploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = storage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Item was not deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dBListener);
    }
}