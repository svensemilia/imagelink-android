package de.scit.imagelink.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.scit.imagelink.R;
import de.scit.imagelink.common.ImageData;
import de.scit.imagelink.rest.ImageRestApi;

public class ImageFragment extends Fragment implements View.OnTouchListener {

    private List<ImageData> images;
    private int imageIndex;
    private ImageView imgView;

    private static ImageFragment fragment;

    public static ImageFragment getInstance(List<ImageData> images, int imageIndex) {
        if (fragment == null) {
            fragment = new ImageFragment(images, imageIndex);
        }
        if (images.size() > fragment.getImages().size()) {
            fragment.addAdditionalImages(images);
        }
        fragment.setImageIndex(imageIndex);
        return fragment;
    }

    // call this, if you navigate into a different image folder
    public static void clear() {
        fragment = null;
    }

    private ImageFragment(List<ImageData> images, int imageIndex) {
        this.images = images;
        this.imageIndex = imageIndex;
    }

    private List<ImageData> getImages() {
        return images;
    }

    private void setImageIndex(int index) {
        imageIndex = index;
    }

    private void addAdditionalImages(List<ImageData> images) {
        int startIndex = this.images.size();
        for (int i = startIndex; i < images.size(); i++) {
            this.images.add(images.get(i));
        }
    }

    private void displayImage() {
        ImageData img = images.get(imageIndex);
        if (!img.isOriginal()) {
            //load original media
            Log.i("ImageFragment", "Load image with key " + img.getKey());
            ImageRestApi.getImage(img.getKey(), imageLoadHandler);
            // TODO store both: original and scaled??
            return;
        }

        byte[] decodedString = Base64.decode(img.getOriginalBase64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgView.setImageBitmap(decodedByte);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ImageFragment", "OnCreateView called");
        View layout =  inflater.inflate(R.layout.fragment_image, container, false);
        imgView = layout.findViewById(R.id.ogImage);
        View con = layout.findViewById(R.id.fragmentContainer);
        con.setOnTouchListener(this);
        displayImage();
        return layout;
    }

    private AsyncHttpResponseHandler imageLoadHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i("TEST", "OnSuccess JsonObj");
            try {
                String base64 = response.getString("Data");
                images.get(imageIndex).setOriginalBase64(base64);
                displayImage();
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
            Toast toast = Toast.makeText(null, "Image could not be loaded", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("Fragment", "Motion: " + motionEvent.getRawX() + "; " + motionEvent.getRawY() +
                MotionEvent.actionToString(motionEvent.getAction()));
        return false;
    }
}
