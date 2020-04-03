package de.scit.imagelink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.scit.imagelink.rest.ImageRestApi;


public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";
    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get intent, action and MIME type
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = findViewById(R.id.infoText);
        final TextView editView = findViewById(R.id.editText);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClick triggered");
                //ImageRestApi.get();
                String album = editView.getText().toString();
                try {
                ImageRestApi.postImages(getContentResolver(), imageUris, album);
                } catch (IllegalStateException e) {
                    Toast toast = Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
                //Environment.getE
                Log.i(TAG, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i(TAG, "Action: " + action + "; Type: " + type);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent, textView); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent, textView); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendImage(Intent intent, TextView textView) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Log.i(TAG, imageUri.getPath());
            textView.setText("Eine Datei zum Upload ausgewaehlt");
            // Update UI to reflect image being shared
            imageUris.clear();
            imageUris.add(imageUri);
        }
    }

    void handleSendMultipleImages(Intent intent, TextView textView) {
        ArrayList<Uri> ImageUrislocal = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            imageUris.clear();
            for(Uri uri : ImageUrislocal){
                Log.i(TAG, uri.getPath());
                imageUris.add(uri);
            }
            textView.setText(imageUris.size() + " Dateien zum Upload ausgewaehlt");
        }
    }
}
