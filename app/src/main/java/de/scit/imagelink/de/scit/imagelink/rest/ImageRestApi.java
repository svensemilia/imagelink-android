package de.scit.imagelink.de.scit.imagelink.rest;

import android.content.ContentResolver;
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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ImageRestApi {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "ImageRestApi";
    private static final String API_GATEWAY = "https://4h6bxpz6e3.execute-api.eu-central-1.amazonaws.com/Dev";
    private static final String API_SERVER_STATUS = "/server/status";
    private static final String API_SERVER_ACTION = "/server/action";

    @Deprecated
    private static final String token = "eyJraWQiOiJHbWNqRnB2WFFvbjBTVEdPcEdwRXdYTTBMXC9Nc0tlYmFTQ3REZ3ZZN2hwST0iLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiaF83WTN6MW1CbnZtWTIyX2Z1QlFfQSIsInN1YiI6IjVlNmVjYzI3LWI5ZmUtNGI2MC1hMWI4LWI3M2JhMzVlOGYxNSIsImF1ZCI6IjNxbmdhZXFodDYxaWU2amh1dG52NjkxOGU3IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImV2ZW50X2lkIjoiZmVlNjJkYzYtNTM0Zi00MzE0LWE4MmEtZDg3ODc3OWY1NzAxIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1ODU1NjMxNjUsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX3Mwd1UybzN1biIsImNvZ25pdG86dXNlcm5hbWUiOiI1ZTZlY2MyNy1iOWZlLTRiNjAtYTFiOC1iNzNiYTM1ZThmMTUiLCJleHAiOjE1ODU1NjY3NjUsImlhdCI6MTU4NTU2MzE2NSwiZW1haWwiOiJzdmVuX2NhcmxpbkBhcmNvci5kZSJ9.ngG48cuZ5IwdUPZ6vR8BBo1YZsh1UyV3UvIMvjOiSH10S9wq_8jSRevu2QUncBUvytoIVMLasbHxg-JhcayerRYKMqp6EZd61kZMF8QfW5cFuxxOpLT85lSlgjvJzqaGPxh2HjCelTH8KJdc8NS7BtIadxAh51EgxIUUW8SwttDG65JoDTIQ37SptRdpgKpSG65OXThdvFRPeTwpiOw88G9OErdd85VRVFyrDG2-1Bbzf762T1Oimu_zM73Cm7KQjjaZhnkTrSFrwCc10eRcUsWS6f5-Q32M6y-QDr33hm2TM8X__xzeBfGFXViyjHLAiw1HsQ2RoSP5QHpU_ojC1Q";

    private static final AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.i(TAG, Integer.toString(statusCode));
            if(responseBody != null && responseBody.length > 0) {
                Log.i(TAG, new String(responseBody));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Log.i(TAG, Integer.toString(statusCode));
            if(responseBody != null && responseBody.length > 0){
                Log.i(TAG, new String(responseBody));
            }
            if(error != null){
                Log.e(TAG, error.getLocalizedMessage());
            }
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            Log.i(TAG, "Percentage " + (bytesWritten / totalSize));
        }
    };

    public static void getImages(String album, String continueToken, int pixelWidth, AsyncHttpResponseHandler handler) {
        Log.i(TAG, "getImages called");

        RequestParams params = new RequestParams();
        params.put("album", album);
        params.put("resolution", pixelWidth);
        params.put("continue", continueToken);
        client.addHeader("Authorization", token);
        client.get(null, "http://52.57.113.109:8080/images", params, handler);
    }

    public static void getServerState(AsyncHttpResponseHandler handler) {
        Log.i(TAG, "get server state called");

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        String endpoint = API_GATEWAY.concat(API_SERVER_STATUS);
        client.get(null, endpoint, params, handler);
    }

    public static void postServerState(String action, AsyncHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "post server state called");

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("Content-Type", "application/json");
        headers[1] = new BasicHeader("Authorization", token);

        String endpoint = API_GATEWAY.concat(API_SERVER_ACTION);
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("action", action);
        StringEntity ent = new StringEntity( jsonobj.toString());
        ent.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //List<NameValuePair> nameValuePairs = new ArrayList<>();
        //nameValuePairs.add(new BasicNameValuePair("action", action));
        //UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs);
        client.post(null, endpoint, headers, ent, "application/json", handler);
    }

    public static void postImages(ContentResolver resolver, List<Uri> imageUris, String album){
        if(imageUris == null || imageUris.size() == 0){
            return;
        }
        Log.i(TAG, "postImages called");
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("Content-Type", "multipart/form-data; boundary=&&");
        headers[1] = new BasicHeader("Authorization", token);
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

        client.post(null, "http://52.57.113.109:8080/androidUpload", headers, ent, "multipart/form-data; boundary=&&", responseHandler);
    }

    public static void get(){
        client.get("https://example.com", responseHandler);
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
}
