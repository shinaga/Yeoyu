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
    ImageView imgUpload[] = new ImageView[3];
    static Uri file[] = new Uri[3];//Write클래스에서 사용할 변수

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
        imgUpload[0] = findViewById(R.id.imgUpload1);
        imgUpload[1] = findViewById(R.id.imgUpload2);
        imgUpload[2] = findViewById(R.id.imgUpload3);
    }
    private void imgClick() {
        for(int i=0;i<3;i++) {
            int finalI = i;//i를 인자값으로 주는 것이 불가능 해, 새로운 변수에 대입 함
            imgUpload[i].setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,finalI);
            });
        }
    }
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            file[requestCode] = data.getData();//Write클래스에서 사용할 변수
            InputStream in = getContentResolver().openInputStream(data.getData());
            Bitmap img = BitmapFactory.decodeStream(in);
            imgUpload[requestCode].setImageBitmap(img);//이미지 변경
            in.close();
        } catch (Exception e) {
            Toast.makeText(WriteActivity.this,"에러가 발생했습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}