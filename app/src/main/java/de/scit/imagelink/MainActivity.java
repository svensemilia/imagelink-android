package de.scit.imagelink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.scit.imagelink.de.scit.imagelink.rest.ImageRestApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String imgBytes = "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";

    private final static int IMAGES_NUMBER_PORTRAIT = 3;
    private final static int IMAGES_NUMBER_LANDSCAPE = 6;

    private String continueToken = "";
    private TableLayout table;
    private List<String> base64Images;
    private List<String> subDirs;
    private int currentResolution;
    private String currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        table = findViewById(R.id.img_table);
        setSupportActionBar(toolbar);

        base64Images = new ArrayList<>(10);
        subDirs = new ArrayList<>(5);
        currentDir = "";
        getImages(currentDir);
        //addImagesToTable();
    }

    private void getImages(String album) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("TEST", "Width: " + metrics.widthPixels);
        Log.i("TEST", "Height: " + metrics.heightPixels);

        currentResolution = metrics.widthPixels / IMAGES_NUMBER_PORTRAIT;
        Log.i("TEST", "ImageWidth: " + currentResolution);

        if (album != currentDir){
            if (!currentDir.isEmpty()) {
                album = currentDir + "/" + album;
            }
            continueToken = "";
            currentDir = album;
            base64Images.clear();
            subDirs.clear();
        }
        try {
        ImageRestApi.getImages(album, continueToken, currentResolution, imageLoadHandler);
        } catch (IllegalStateException e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void addImagesToTable() {
        if (table.getChildCount() > 1) {
            table.removeViews(1,table.getChildCount() - 1);
        }
        Log.i("TEST", "Adding images: " + base64Images.size());
        int count = 0;
        TableRow newRow = new TableRow(this);
        byte[] decodedString;
        Bitmap decodedByte;
        TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams imageLayout;
        for(String dir : subDirs) {
            if (count % IMAGES_NUMBER_PORTRAIT == 0) {
                if (count != 0) {
                    newRow = new TableRow(this);
                }
                newRow.setLayoutParams(rowLayout);
                newRow.setPadding(5, 5, 5, 5);
            }

            TextView img = getDirView(dir, count);
            newRow.addView(img);

            if ((count % IMAGES_NUMBER_PORTRAIT == 2)) {
                table.addView(newRow);
            }
            count ++;
        }
        for (String base64 : base64Images) {
            if (count % IMAGES_NUMBER_PORTRAIT == 0) {
                if (count != 0) {
                    newRow = new TableRow(this);
                }
                newRow.setLayoutParams(rowLayout);
                newRow.setPadding(5, 5, 5, 5);
            }

            ImageView img = getImageView(base64, count);
            newRow.addView(img);

            if ((count % IMAGES_NUMBER_PORTRAIT == 2)) {
                table.addView(newRow);
            }
            count ++;
        }

        if (((count-1) % IMAGES_NUMBER_PORTRAIT != 2)) {
            table.addView(newRow);
        }

        if (continueToken != null && !continueToken.equals("")) {
            ImageView img = getAddImagesIcon(count % IMAGES_NUMBER_PORTRAIT);
            if (count % IMAGES_NUMBER_PORTRAIT != 2) {
                img.setPadding(0,0,5,0);
            }
            if (count % IMAGES_NUMBER_PORTRAIT == 0) {
                newRow = new TableRow(this);
                newRow.setLayoutParams(rowLayout);
                newRow.setPadding(5, 5, 5, 5);
                newRow.addView(img);
                table.addView(newRow);
            } else {
                newRow.addView(img);
            }
        }
    }

    private TextView getDirView(String dir, int count) {
        TextView view = new TextView(this);
        TableRow.LayoutParams imageLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        imageLayout.column = count % IMAGES_NUMBER_PORTRAIT;
        view.setLayoutParams(imageLayout);
        view.setText(dir);
        view.setPadding(0,3,3,0);
        view.setMinimumWidth(currentResolution);
        view.setMinimumHeight(currentResolution);
        view.setBackgroundColor(0xFF499EC5);
        view.setGravity(Gravity.CENTER);
        view.setOnClickListener(this);
        return view;
    }

    private ImageView getImageView(String base64, int count){
        ImageView img = new ImageView(this);
        img.setPadding(0,3,3,0);
        TableRow.LayoutParams imageLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        imageLayout.column = count % IMAGES_NUMBER_PORTRAIT;
        img.setLayoutParams(imageLayout);
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
        return img;
    }

    private ImageView getAddImagesIcon(int column) {
        ImageView img = new ImageView(this);
        TableRow.LayoutParams imageLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        imageLayout.column = column;
        img.setLayoutParams(imageLayout);
        img.setMinimumWidth(currentResolution);
        img.setMinimumHeight(currentResolution);
        Drawable dr = getResources().getDrawable(android.R.drawable.ic_input_add);
        img.setImageDrawable(dr);
        img.setOnClickListener(this);
        return img;
    }

    @Override
    public void onClick(View view) {
        Log.i("TEST", "OnClick");

        String album = "";
        if (view instanceof TextView) {
            album = ((TextView) view).getText().toString();
        } else {
            album = currentDir;
        }
        getImages(album);

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
        } else if (id == R.id.action_status) {
            Intent statusInt = new Intent(this, StatusActivity.class);
            startActivity(statusInt);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AsyncHttpResponseHandler imageLoadHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i("TEST", "OnSuccess JsonObj");
            try {
                continueToken = response.getString("ContinuationToken");
                JSONArray images = response.getJSONArray("Images");
                JSONArray dirs = response.getJSONArray("Dirs");
                JSONObject image = null;
                for (int i=0; i< images.length(); i++){
                    base64Images.add(images.getJSONObject(i).getString("Data"));
                }
                for (int i=0; i< dirs.length(); i++){
                    if (!subDirs.contains(dirs.getString(i))) {
                        subDirs.add(dirs.getString(i));
                    }
                }
                addImagesToTable();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
            if (response != null) {
                Log.i("TEST", response.toString());
            }
            Toast toast = Toast.makeText(MainActivity.this, "Content could not be loaded", Toast.LENGTH_SHORT);
            toast.show();
        }
    };
}
