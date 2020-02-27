package de.scit.imagelink.de.scit.imagelink.rest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.message.BasicHeader;

public class ImageRestApi {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String TAG = "ImageRestApi";
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

    public static void postImages(ContentResolver resolver, List<Uri> imageUris, String album){
        if(imageUris == null || imageUris.size() == 0){
            return;
        }
        Log.i(TAG, "postImages called");
        String token = "eyJraWQiOiJHbWNqRnB2WFFvbjBTVEdPcEdwRXdYTTBMXC9Nc0tlYmFTQ3REZ3ZZN2hwST0iLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoicWFBdFJtbnZsTmlYc0hkNGxrTXA2ZyIsInN1YiI6IjVlNmVjYzI3LWI5ZmUtNGI2MC1hMWI4LWI3M2JhMzVlOGYxNSIsImF1ZCI6IjNxbmdhZXFodDYxaWU2amh1dG52NjkxOGU3IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImV2ZW50X2lkIjoiMDBkMWEyOTYtNWY4MS00MWVlLThiZmMtNjhmY2VmN2FiZDgzIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1ODI3MDUxMzksImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX3Mwd1UybzN1biIsImNvZ25pdG86dXNlcm5hbWUiOiI1ZTZlY2MyNy1iOWZlLTRiNjAtYTFiOC1iNzNiYTM1ZThmMTUiLCJleHAiOjE1ODI3MDg3MzksImlhdCI6MTU4MjcwNTEzOSwiZW1haWwiOiJzdmVuX2NhcmxpbkBhcmNvci5kZSJ9.SgENx732rN163VzkDUtqKLy2S0TsVHYPQlFAc9SHyEkleoYrnjKpuZefkmWPPWLUrOnpDGb7uYsjyohRWQYZySF6PQAfxxpwmSLFqLgmElJka0jHYnUS8L0X-5uAo3kt_XPClgVOZ_LmSh8xX8_WQwg1yqKXd7os2W1UJYsw8VzzOG5rLRstoLw-VfADC77LIg6MVVbLA6o_MmFCrDiPWeqtZFBgb0N-iJfEKy_Y3Lv3JxsonRTPOOj3PMFlA5a5UYKXcoNEdJi2E7JcA1G6dnLaAd9v_AWjQ0tkpMl1v-w5vylZJxDiqASkqRViG9sOB7bme4e1W6qb4C8aq3RY2g";
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

        client.post(null, "http://54.93.33.97:8080/androidUpload", headers, ent, "multipart/form-data; boundary=&&", responseHandler);
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
