package com.cookandroid.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class WriteActivity extends AppCompatActivity {
    static EditText editTitle,editContent;
    Button btnWrite;
    private  final int GALLERY_CODE = 10;
    static ImageView imgUpload1;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();//액션바 숨기기

        editSet();
        btnSet();
        btnClick();
        imgSet();
        imgClick();
    }
    private void editSet() {
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
    }
    private void btnSet() {
        btnWrite = findViewById(R.id.btnWrite);
    }

    private void btnClick() {btnWrite.setOnClickListener(new Write(this));}
    private void imgSet() {
        imgUpload1 = findViewById(R.id.imgUpload1);
    }
    private void imgClick() {
        imgUpload1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, GALLERY_CODE);
        });
    }
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                imgUpload1.setImageBitmap(img);//이미지 변경
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}