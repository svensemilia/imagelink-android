package de.scit.imagelink;

import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
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
import de.scit.imagelink.cognito.AppHelper;
import de.scit.imagelink.cognito.CognitoSignIn;
import de.scit.imagelink.cognito.CognitoSignOut;
import de.scit.imagelink.cognito.SignInListener;
import de.scit.imagelink.common.ImageData;
import de.scit.imagelink.fragments.ImageFragment;
import de.scit.imagelink.rest.ImageRestApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SignInListener {

    private final static int IMAGES_NUMBER_PORTRAIT = 3;
    private final static int IMAGES_NUMBER_LANDSCAPE = 6;

    private String continueToken = "";
    private TableLayout table;
    private List<ImageData> images;
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

        images = new ArrayList<>(10);
        subDirs = new ArrayList<>(5);
        currentDir = "";

        AppHelper.init(this);
        CognitoSignIn.init(this);
        CognitoSignIn.registerListener(this);
        CognitoSignIn.signInLastUser();
        ImageRestApi.init(this);

        //wait for authentication
        //getImages(currentDir);

        //addImagesToTable();

    }

    @Override
    public void onSignInSuccess() {
        getImages(currentDir);
    }

    @Override
    public void onSignInFailure() {
        Toast toast = Toast.makeText(this, "SignIn failed", Toast.LENGTH_LONG);
        toast.show();
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
            images.clear();
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
        Log.i("TEST", "Adding images: " + images.size());
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
        int imageIndex = 0;
        for (ImageData imageData : images) {
            if (count % IMAGES_NUMBER_PORTRAIT == 0) {
                if (count != 0) {
                    newRow = new TableRow(this);
                }
                newRow.setLayoutParams(rowLayout);
                newRow.setPadding(5, 5, 5, 5);
            }

            ImageView img = getImageView(imageData.getBase64(), count, imageIndex);
            newRow.addView(img);

            if ((count % IMAGES_NUMBER_PORTRAIT == 2)) {
                table.addView(newRow);
            }
            count ++;
            imageIndex++;
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

    private ImageView getImageView(String base64, int count, int imageIndex){
        ImageView img = new ImageView(this);
        img.setPadding(0,3,3,0);
        TableRow.LayoutParams imageLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        imageLayout.column = count % IMAGES_NUMBER_PORTRAIT;
        img.setLayoutParams(imageLayout);
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
        img.setOnClickListener(this);
        img.setTag(R.id.tag_img_index, imageIndex);
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
        img.setTag(R.id.tag_load_chunk, "add");
        return img;
    }

    @Override
    public void onClick(View view) {
        String album = "";
        if (view instanceof TextView) {
            Log.i("TEST", "OnClick - Album");
            album = ((TextView) view).getText().toString();
        } else if (view instanceof ImageView){
            Object tag = view.getTag(R.id.tag_load_chunk);
            if (tag != null && tag instanceof String && ((String)tag).equals("add")) {
                album = currentDir;
                Log.i("TEST", "OnClick - next chunk " + tag);
            } else {
                // show image fragment
                tag = view.getTag(R.id.tag_img_index);
                if (tag == null) {
                    Log.i("TEST", "OnClick - image - Tag not present");
                    return;
                }

                Log.i("TEST", "OnClick - image");
                int index = (int) tag;
                startImageFragment(index);
                return;
            }
        }
        getImages(album);
    }

    private void startImageFragment(int imageIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //getContentResolver().ins
        ImageFragment frag = ImageFragment.getInstance(getContentResolver(), images, imageIndex);
        transaction.add(R.id.mainWindow, frag);
        transaction.addToBackStack(null);
        transaction.commit();
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
        } else if (id == R.id.action_logout) {
            CognitoSignOut.signOutUser();
            //redirect to start page or something?
            //TODO logout
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
                JSONArray imagesJson = response.getJSONArray("Images");
                JSONArray dirs = response.getJSONArray("Dirs");
                JSONObject image = null;
                String key;
                String content;
                String base64;
                for (int i=0; i< imagesJson.length(); i++){
                    base64 = imagesJson.getJSONObject(i).getString("Data");
                    content = imagesJson.getJSONObject(i).getString("Content");
                    key = imagesJson.getJSONObject(i).getString("Name");
                    images.add(new ImageData(key, base64, content));
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
                Log.i("TEST","Hello");
                Log.i("TEST", response.toString());
            }
            Toast toast = Toast.makeText(MainActivity.this, "Content could not be loaded", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                //sign in
                String user = data.getStringExtra(SignInActivity.EXTRA_INPUT_USER);
                String pwd = data.getStringExtra(SignInActivity.EXTRA_INPUT_PWD);
                CognitoSignIn.continueSignIn(user, pwd);
                break;
            default:
                Log.i("MAIN", "Requestcode not recognized");
        }
    }
}
