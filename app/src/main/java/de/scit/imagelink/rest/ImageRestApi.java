package de.scit.imagelink.rest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.scit.imagelink.cognito.AppHelper;
import de.scit.imagelink.common.DefaultResponseHandler;
import de.scit.imagelink.common.Preferences;
import de.scit.imagelink.common.Util;

public class ImageRestApi {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "ImageRestApi";
    private static final String API_GATEWAY = "https://4h6bxpz6e3.execute-api.eu-central-1.amazonaws.com/Dev";
    private static final String API_LAMBDA_STATUS = "/server/status";
    private static final String API_LAMBDA_ACTION = "/server/action";
    private static final String API_SERVER_HEALTH_CHECK = "/healthcheck";
    private static final String API_SERVER_IMAGES = "/images";
    private static final String API_SERVER_IMAGE = "/image";
    private static final String API_SERVER_POST_IMAGE = "/upload";

    private static String API_SERVER_IP = null;

    public static void init(Context ctx) {
        API_SERVER_IP = Preferences.getStringPreference(ctx, Preferences.PREFERENCE_SERVER_IP, null);
    }

    private static final AsyncHttpResponseHandler responseHandler = new DefaultResponseHandler();

    public static String getServerIP() {
        return API_SERVER_IP;
    }

    public static void setServerIP(String ip) {
        API_SERVER_IP = ip;
        Preferences.setPreference(null, Preferences.PREFERENCE_SERVER_IP, ip);
    }

    public static void getImages(String album, String continueToken, int pixelWidth, AsyncHttpResponseHandler handler) throws IllegalStateException {
        if (API_SERVER_IP == null || API_SERVER_IP.isEmpty()) {
            throw new IllegalStateException("Unknown Server IP");
        }

        Log.i(TAG, "getImages called");

        RequestParams params = new RequestParams();
        params.put("album", album);
        params.put("resolution", pixelWidth);
        params.put("continue", continueToken);
        Header[] headers = getHeaders(null);
        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_IMAGES);
        client.get(null, endpoint, headers, params, handler);
    }

    public static void getImage(String imageKey, AsyncHttpResponseHandler handler) throws IllegalStateException {
        if (API_SERVER_IP == null || API_SERVER_IP.isEmpty()) {
            throw new IllegalStateException("Unknown Server IP");
        }

        RequestParams params = new RequestParams();
        params.put("key", Util.getFilenameFromKey(imageKey)); //TODO fix that
        Header[] headers = getHeaders(null);
        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_IMAGE);
        client.get(null, endpoint, headers, params, handler);
    }

    public static void getServerState(AsyncHttpResponseHandler handler) {
        Log.i(TAG, "get server state called");
        RequestParams params = new RequestParams();
        Header[] headers = getHeaders(null);
        String endpoint = API_GATEWAY.concat(API_LAMBDA_STATUS);
        client.get(null, endpoint, headers, params, handler);
    }

    private static Header[] getHeaders(String contentType) {
        Header[] headers;
        if (contentType != null) {
            headers = new Header[2];
            headers[1] = new BasicHeader("Content-Type", contentType);
        } else {
            headers = new Header[1];
        }
        headers[0] = new BasicHeader("Authorization", AppHelper.getIdToken());
        return headers;
    }

    public static void postServerState(String action, AsyncHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "post server state called");

        Header[] headers = getHeaders("application/json");
        String endpoint = API_GATEWAY.concat(API_LAMBDA_ACTION);
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("action", action);
        StringEntity ent = new StringEntity( jsonobj.toString());
        ent.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(null, endpoint, headers, ent, "application/json", handler);
    }

    public static void postImages(ContentResolver resolver, List<Uri> imageUris, String album) throws IllegalStateException {
        if (API_SERVER_IP == null || API_SERVER_IP.isEmpty()) {
            throw new IllegalStateException("Unknown Server IP");
        }

        if(imageUris == null || imageUris.size() == 0){
            return;
        }
        Log.i(TAG, "postImages called");

        Header[] headers = getHeaders("multipart/form-data; boundary=&&");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        Log.i(TAG, album);


        try {
            if (album == null || album.equals("null")) {
                album = "";
            }
            builder.addTextBody("album", album);
            String filename;
            for(Uri uri : imageUris) {
                filename = getFileName(resolver, uri);
                Log.i(TAG, filename);
                builder.addBinaryBody("myFiles", resolver.openInputStream(uri), ContentType.create("image/jpeg") , filename);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        builder.setBoundary("&&");
        HttpEntity ent = builder.build();

        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_POST_IMAGE);
        client.post(null, endpoint, headers, ent, "multipart/form-data; boundary=&&", responseHandler);
    }

    private static String getFileName(ContentResolver resolver, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = resolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static void getAPIHealth(AsyncHttpResponseHandler handler) throws IllegalStateException {
        if (API_SERVER_IP == null || API_SERVER_IP.isEmpty()) {
            throw new IllegalStateException("Unknown Server IP");
        }
        Log.i(TAG, "health check called");

        RequestParams params = new RequestParams();
        Header[] headers = getHeaders(null);
        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_HEALTH_CHECK);
        client.get(null, endpoint, headers, params, handler);
    }

    private static String constructEndpoint(String ip, String resource) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(API_SERVER_IP);
        sb.append(":8080");
        sb.append(resource);
        return sb.toString();
    }
}
