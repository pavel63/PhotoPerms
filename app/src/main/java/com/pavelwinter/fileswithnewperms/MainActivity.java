package com.pavelwinter.fileswithnewperms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

        private static final int PERMISSION_REQUEST_CODE = 2;

    Bitmap mBitmap;

    File file;
    //адрес файла
    File filePath=new File("/storage/emulated/0/CustomDir/filename.png");

        private ImageView mImageView;

        private static int TAKE_PICTURE = 1;




    //разрешения проверяем
    public boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    //всем скопом запрашиваем
    public void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

                if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)&&(!hasPermissions())) {
                    requestPerms();
                }

            setContentView(R.layout.activity_main);

            mImageView = (ImageView) findViewById(R.id.imageView);
        }

        public void onClick(View view) {
            getThumbnailPicture();

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == TAKE_PICTURE) {

                    if (data.hasExtra("data")) {
                        mBitmap  = data.getParcelableExtra("data");
                        // TODO Какие-то действия с миниатюрой
                        mImageView.setImageBitmap(mBitmap);
                        saveFullImage();
                    }
            }
        }

        private void getThumbnailPicture() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PICTURE);
        }


        //записывает файл в папку CustomDir
        private void saveFullImage() {

            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/CustomDir";
            File dir = new File(file_path);
            if (!dir.exists())

                //мтод создания директории
                dir.mkdirs();

            //  String format = new SimpleDateFormat("yyyyMMddHHmmss",
            //        java.util.Locale.getDefault()).format(new Date());

            file = new File(dir, "filename" + ".png");

           // Log.d("PLACE",file.toString());
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //этим кодом можем взять наше фото для деления с кем-то
        public void onClickGet(View v){

            Uri uri = Uri.fromFile(filePath);
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image*/");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(intent,"Sharing something"));

        }}

