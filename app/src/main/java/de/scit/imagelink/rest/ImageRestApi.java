package de.scit.imagelink.rest;

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
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import de.scit.imagelink.common.DefaultResponseHandler;

public class ImageRestApi {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "ImageRestApi";
    private static final String API_GATEWAY = "https://4h6bxpz6e3.execute-api.eu-central-1.amazonaws.com/Dev";
    private static final String API_LAMBDA_STATUS = "/server/status";
    private static final String API_LAMBDA_ACTION = "/server/action";
    private static final String API_SERVER_HEALTH_CHECK = "/healthcheck";
    private static final String API_SERVER_IMAGES = "/images";
    private static final String API_SERVER_POST_IMAGE = "/androidUpload";

    private static String API_SERVER_IP = null;

    @Deprecated
    private static final String token = "eyJraWQiOiJHbWNqRnB2WFFvbjBTVEdPcEdwRXdYTTBMXC9Nc0tlYmFTQ3REZ3ZZN2hwST0iLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiUUItM2NXUl81a0VnYVV3bU5Vc1owdyIsInN1YiI6IjVlNmVjYzI3LWI5ZmUtNGI2MC1hMWI4LWI3M2JhMzVlOGYxNSIsImF1ZCI6IjNxbmdhZXFodDYxaWU2amh1dG52NjkxOGU3IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImV2ZW50X2lkIjoiMGVmZmRjNDgtOWZiYy00N2FiLWE0MjItZmE3MjczZWM2MWJiIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1ODU2NDcxMDcsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX3Mwd1UybzN1biIsImNvZ25pdG86dXNlcm5hbWUiOiI1ZTZlY2MyNy1iOWZlLTRiNjAtYTFiOC1iNzNiYTM1ZThmMTUiLCJleHAiOjE1ODU2NTA3MDcsImlhdCI6MTU4NTY0NzEwNywiZW1haWwiOiJzdmVuX2NhcmxpbkBhcmNvci5kZSJ9.uWKkSO0ynSzxQ_y6TX59MDvlt4uCE3ceCDZo7pZdBYuzzc7CrJKfaL3kwH2qDRaQyMBPP84qJ9TU2b4WgJhRBmQbmJ3utCxXy-bhdSVsk59bax2tCM-O2E10qO1CvnuHzbzVLhBjZDF7p8cA-sZ0d6_znwQdDEXzGIgQImg7zvJRM0LkpplhrcF3Ruktdx66VQH1g0auIJRVgMs9DZhUvSEQhzlQl3hO-nK6PLiTURP6V2phdAGFAYSOA2OSTjUyn_8UC3hPhXHNAgXXzn7bFBCFkZIHbS3NhSFYuyn2P-NZiIEnc40cWief5GTQrHxxxjzJyssdYyZ2h9USUT2dyA";

    private static final AsyncHttpResponseHandler responseHandler = new DefaultResponseHandler();

    public static String getServerIP() {
        return API_SERVER_IP;
    }

    public static void setServerIP(String ip) {
        API_SERVER_IP = ip;
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
        client.addHeader("Authorization", token);
        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_IMAGES);
        client.get(null, endpoint, params, handler);
    }

    public static void getServerState(AsyncHttpResponseHandler handler) {
        Log.i(TAG, "get server state called");

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        String endpoint = API_GATEWAY.concat(API_LAMBDA_STATUS);
        client.get(null, endpoint, params, handler);
    }

    public static void postServerState(String action, AsyncHttpResponseHandler handler) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "post server state called");

        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("Content-Type", "application/json");
        headers[1] = new BasicHeader("Authorization", token);

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
        client.addHeader("Authorization", token);
        String endpoint = constructEndpoint(API_SERVER_IP, API_SERVER_HEALTH_CHECK);
        client.get(null, endpoint, params, handler);
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
