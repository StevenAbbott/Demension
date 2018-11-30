package com.example.sbabb.facial.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ModelManager {

    private static ModelManager mModelManager;
    private Context context;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDatabase;

    public static ModelManager get(Context context) {
        if (mModelManager == null) {
            mModelManager = new ModelManager(context);
        }

        return mModelManager;
    }

    private ModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.mStorage = FirebaseStorage.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    public FirebaseStorage getStorage() {
        return mStorage;
    }

    public FirebaseDatabase getDatabase() {
        return mDatabase;
    }

    public void createPersonssReference() {
        DatabaseReference personRef = mDatabase.getReference().child("persons");
    }

    public void createImageReference() {
        StorageReference storageRef = mStorage.getReference().child("images");
    }

    public UploadTask uploadPerson(Person person, Bitmap bitmap) {
        DatabaseReference personRef = mDatabase.getReference().child("persons").child(person.getKey());
        personRef.setValue(person);

        StorageReference imageRef = mStorage.getReference().child("images").child(person.getKey());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        return uploadTask;
    }

    public DatabaseReference getPersonRef(String personKey) {
        return mDatabase.getReference().child("persons").child(personKey);
    }

    public StorageReference getPersonImageRef(Person person) {
        return mStorage.getReference().child("images").child(person.getKey());
    }

    public Bitmap byteArrayToBitMap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public String generatePersonKey() {
        UUID id = UUID.randomUUID();
        String key = id.toString();
        return key;
    }
}
