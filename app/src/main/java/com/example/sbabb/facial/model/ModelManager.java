package com.example.sbabb.facial.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ModelManager {

    private FirebaseStorage storage;
    private FirebaseDatabase database;

    public ModelManager() {
        this.storage = FirebaseStorage.getInstance();
        this.database = FirebaseDatabase.getInstance();
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void createUsersReference() {
        DatabaseReference UserRef = database.getReference().child("users");
    }

    public void createImageReference() {
        StorageReference storageRef = storage.getReference().child("images");
    }

    public UploadTask uploadPerson(User user, Bitmap bitmap) {
        DatabaseReference userRef = database.getReference().child("users").child(user.getKey());
        userRef.setValue(user);

        StorageReference imageRef = storage.getReference().child("images").child(user.getKey());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        return uploadTask;
    }

    public DatabaseReference getUserRef(String userKey) {
        return database.getReference().child("users").child(userKey);
    }

    public StorageReference getUserImageRef(User user) {
        return storage.getReference().child("images").child(user.getKey());
    }

    public Bitmap byteArrayToBitMap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
