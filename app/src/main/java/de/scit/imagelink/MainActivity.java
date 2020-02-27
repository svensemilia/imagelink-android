package de.scit.imagelink;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private String imgBytes = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ImageView img = findViewById(R.id.imageView);
        String strBase64 = imgBytes; //"data:image/jpeg;base64," +
        byte[] decodedString = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
