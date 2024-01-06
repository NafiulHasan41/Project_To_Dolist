package com.nafsoft.todolistapplication.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nafsoft.todolistapplication.R;
import com.nafsoft.todolistapplication.database.NoteDataBase;
import com.nafsoft.todolistapplication.entities.Note;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle,inputNoteSubtitle,inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private String selectNoteColor;
    private String selectImagePath;


    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textWebURL=findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURl);


        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectNoteColor = "#333333";

        selectImagePath = "";

        if(getIntent().getBooleanExtra("isViewOrUpdate",false))
        {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              imageNote.setImageBitmap(null);
              imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectImagePath= "";


            }
        });


        if(getIntent().getBooleanExtra("isFromQuickActions",false))
        {
            String type = getIntent().getStringExtra("quickActionType");
            if(type != null)
            {
                if(type.equals("image"))
                {
                    selectImagePath = getIntent().getStringExtra("imagePath");
                    imageNote.setImageBitmap(BitmapFactory.decodeFile(selectImagePath));
                    imageNote.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                } else if (type.equals("URL")) {

                    textWebURL.setText(getIntent().getStringExtra("URL"));
                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }

    initCustomization();
    setSubtitleIndicatorColor();




    }

    private void setViewOrUpdateNote()
    {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
             imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
             imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
             selectImagePath = alreadyAvailableNote.getImagePath();
        }

        if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
             textWebURL.setText(alreadyAvailableNote.getWebLink());
             layoutWebURL.setVisibility(View.VISIBLE);

        }
    }

    private void saveNote()
    {
        if(inputNoteTitle.getText().toString().trim().isEmpty())
        {
            inputNoteTitle.setError("Note title can't be empty");
            inputNoteTitle.requestFocus();
            return;
        }
        else if(inputNoteSubtitle.getText().toString().trim().isEmpty())
        {
            inputNoteSubtitle.setError("Note subtitle can't be empty");
            inputNoteSubtitle.requestFocus();
            return;
        }
        else if(inputNoteText.getText().toString().trim().isEmpty())
        {
            inputNoteText.setError("Note  can't be empty");
            inputNoteText.requestFocus();
            return;
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectNoteColor);
        note.setImagePath(selectImagePath);

        if(layoutWebURL.getVisibility() == View.VISIBLE)
        {
            note.setWebLink(textWebURL.getText().toString());
        }

        if (alreadyAvailableNote !=null)
        {
            note.setId(alreadyAvailableNote.getId());
        }

       @SuppressLint("StaticFieldLeak")
       class SaveNoteTask extends AsyncTask<Void,Void,Void>{

           @Override
           protected Void doInBackground(Void... voids) {
               NoteDataBase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
               return null;
           }

           @Override
           protected void onPostExecute(Void aVoid) {
               super.onPostExecute(aVoid);

               Intent intent = new Intent();
               setResult(RESULT_OK,intent);
               finish();
           }
       }

       new SaveNoteTask().execute();
    }

    private void initCustomization()
    {
        final LinearLayout customizationLinearLayout = findViewById(R.id.noteCustomization);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(customizationLinearLayout);

        customizationLinearLayout.findViewById(R.id.textCustomization).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = customizationLinearLayout.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = customizationLinearLayout.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = customizationLinearLayout.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = customizationLinearLayout.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = customizationLinearLayout.findViewById(R.id.imageColor5);

        customizationLinearLayout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        customizationLinearLayout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        customizationLinearLayout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });



        customizationLinearLayout.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        customizationLinearLayout.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });


        if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() !=null && !alreadyAvailableNote.getColor().trim().isEmpty() )
        {
            switch (alreadyAvailableNote.getColor())
            {
                case "#FDBE3B":
                    customizationLinearLayout.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    customizationLinearLayout.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    customizationLinearLayout.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    customizationLinearLayout.findViewById(R.id.viewColor5).performClick();
                    break;

            }
        }


        customizationLinearLayout.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                int version = Build.VERSION.SDK_INT;

                if(version > 32)
                {

                    if(ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(
                                CreateNoteActivity.this,
                                new String[] {Manifest.permission.READ_MEDIA_IMAGES},
                                REQUEST_CODE_STORAGE_PERMISSION
                        );

                    }
                    else
                    {
                        selectImage();
                    }
                }
                else
                {
                    if(ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(
                                CreateNoteActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_STORAGE_PERMISSION
                        );

                    }
                    else
                    {
                        selectImage();
                    }
                }





            }
        });


        customizationLinearLayout.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });

        if(alreadyAvailableNote != null)
        {
            customizationLinearLayout.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            customizationLinearLayout.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();

                }
            });

        }


    }

    private void showDeleteNoteDialog()
    {
        if(dialogDeleteNote == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() !=null)
            {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }


            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>
                    {

                        @Override
                        protected Void doInBackground(Void... voids) {

                            NoteDataBase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);

                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancelNote1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteNote.dismiss();
                }
            });

        }

        dialogDeleteNote.show();

    }

    private void setSubtitleIndicatorColor()
    {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));
    }

    public void selectImage(){

        int version = Build.VERSION.SDK_INT;
        Intent intent;

        if(version > 32)
        {
             intent = new Intent( MediaStore.ACTION_PICK_IMAGES);
        }
        else
        {
             intent = new Intent( Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        }



        if(intent.resolveActivity(getPackageManager()) != null){

            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else
            {
                Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK)
        {
            if(data!=null)
            {

                Uri selectedImageUri = data.getData();

                if(selectedImageUri != null)
                {
                    try {

                      InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                       Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        int newWidth = 400; // Set your desired width
//                        int newHeight = 600; // Set your desired height
//                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
//                        imageNote.setImageBitmap(resizedBitmap);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);



                        selectImagePath = getPathFromUri(selectedImageUri);






                    }catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri)
    {
        String filePath;

        Cursor cursor = getContentResolver().
                query(contentUri,null,null,null,null);
       if(cursor == null)
       {
           filePath = contentUri.getPath();

       }else {
           cursor.moveToFirst();
           int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
           filePath = cursor.getString(index);
           cursor.close();
       }
       return filePath;
   }

   private void showAddURLDialog()
   {
       if(dialogAddURL == null)
       {
           AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
           View view = LayoutInflater.from(this).inflate(
                   R.layout.layout_add_url,
                   (ViewGroup) findViewById(R.id.layoutAddUriContainer)
           );
           builder.setView(view);
           dialogAddURL = builder.create();
           if(dialogAddURL.getWindow() !=null)
           {
               dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
           }

           final EditText inputURL = view.findViewById(R.id.inputURL);
           inputURL.requestFocus();

           view.findViewById(R.id.textADD).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(inputURL.getText().toString().trim().isEmpty()){
                       Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                   } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {

                       Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();

                   }
                   else
                   {

                           textWebURL.setText(inputURL.getText().toString());
                           layoutWebURL.setVisibility(View.VISIBLE);
                           dialogAddURL.dismiss();


                   }
               }
           });

           view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   dialogAddURL.dismiss();
               }
           });

       }
       dialogAddURL.show();
   }
    
}