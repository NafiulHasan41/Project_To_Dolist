package com.nafsoft.todolistapplication.activites;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.nafsoft.todolistapplication.R;
import com.nafsoft.todolistapplication.activites.CreateNoteActivity;
import com.nafsoft.todolistapplication.adapters.NotesAdapter;
import com.nafsoft.todolistapplication.database.NoteDataBase;
import com.nafsoft.todolistapplication.entities.Note;
import com.nafsoft.todolistapplication.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    //update code start

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    public static final int REQUEST_CODE_SELECT_IMAGE = 4;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 5;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition=-1;
    private AlertDialog dialogAddURL;


    //update code end

    EditText editText;
    Button button;
    ListView listView;
    ArrayList<String > itemList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    //add code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //update code start
        ImageView imageViewAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageViewAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        notesRecyclerView = findViewById(R.id.noteRecyclerView);

        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);


        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        EditText inputSearch = findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(noteList.size() != 0){

                    notesAdapter.searchNotes(editable.toString());

                }

            }
        });


        findViewById(R.id.imageAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );

            }
        });

        findViewById(R.id.imageAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int version = Build.VERSION.SDK_INT;

                if(version > 32)
                {

                    if(ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
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
                                MainActivity.this,
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



        findViewById(R.id.imageAddWebLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddURLDialog();
            }
        });




        //update code end

//        editText = findViewById(R.id.editText);
//        button = findViewById(R.id.button1);
//        listView = findViewById(R.id.list1);
//
//        itemList = FileHelper.readData(this);
//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,android.R.id.text1,itemList);
//
//        listView.setAdapter(arrayAdapter);
//
//
//
//
//
//
//
//
//        button.setOnClickListener(view -> {
//
//
//            if(TextUtils.isEmpty(editText.getText().toString()))
//            {
//                editText.setError("Please enter your item");
//                return;
//            }
//
//            String itemName = editText.getText().toString();
//
//            itemList.add(itemName);
//            editText.setText("");
//            FileHelper.writeData(itemList,getApplicationContext());
//            arrayAdapter.notifyDataSetChanged();
//
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
//                alert.setTitle("Delete");
//                alert.setMessage("Have you completed this item from the list ?");
//                alert.setCancelable(false);
//                alert.setPositiveButton("NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//                alert.setNegativeButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        itemList.remove(position);
//                        arrayAdapter.notifyDataSetChanged();
//                        FileHelper.writeData(itemList,getApplicationContext());
//                        Toast.makeText(MainActivity.this, "Congratulation!!You completed my DARE ", Toast.LENGTH_SHORT).show();
//                        if (mInterstitialAd != null) {
//                            mInterstitialAd.show(MainActivity.this);
//                        } else {
//                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                        }
//
//                    }
//                });
//                AlertDialog alertDialog = alert.create();
//                alertDialog.show();
//            }
//        });
//
//             banner_add();
//        interstitial_add();


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


    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);


    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted){


        class GetNoteTask extends AsyncTask<Void,Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDataBase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if(requestCode == REQUEST_CODE_SHOW_NOTE)
                {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {

                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);

                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {

                    noteList.remove(noteClickedPosition);


                    if(isNoteDeleted)
                    {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }
                    else {

                        noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }

                }

            }
        }

        new GetNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK)
        {
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {

            if(data != null)
            {
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }

        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {

            if(data != null)
            {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null)
                {
                    try {

                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions",true);

                        intent.putExtra("quickActionType","image");
                        intent.putExtra("imagePath",selectedImagePath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);


                    }catch (Exception e)
                    {

                        Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    private void showAddURLDialog()
    {
        if(dialogAddURL == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        Toast.makeText(MainActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {

                        Toast.makeText(MainActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions",true);

                        intent.putExtra("quickActionType","URL");
                        intent.putExtra("URL",inputURL.getText().toString());
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
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


    //    private void banner_add()
//    {
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//    }
//    private void interstitial_add()
//    {
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                        Log.i("TAG", "onAdLoaded");
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        Log.d("TAG", loadAdError.toString());
//                        mInterstitialAd = null;
//                    }
//                });
//
//    }
}