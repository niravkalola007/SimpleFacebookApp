package com.example.android.simplefacebookapp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

public class MainActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private Button postImageBtn;
    private Button updateStatusBtn;
    private Button postVideo;

    private boolean pendingPublishReauthorization = false;

    private TextView userName;
    ProgressDialog progressDialog;
    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final ImageView imageProfile=(ImageView) findViewById(R.id.imageProfile);
        userName = (TextView) findViewById(R.id.user_name);

        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(final GraphUser user) {
                if (user != null) {
                    userName.setText("Hello, " + user.getName());


                    new AsyncTask<Void,Void,Void>(){
                        Bitmap bmp;
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                URL image_value = new URL("https://graph.facebook.com/"+ user.getId()+ "/picture?type=large");
                                Log.e("image_value:",image_value+"");
                                try {
                                    bmp = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.e("bmp:",bmp+"");
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            imageProfile.setImageBitmap(bmp);
                        }
                    }.execute();

                } else {
                    userName.setText("You are not logged");
                }

            }
        });

        postImageBtn = (Button) findViewById(R.id.post_image);
        postImageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
//                postImage();
                try {
                 progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Uploading video...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Bitmap photo = BitmapFactory.decodeResource(getResources(),
                        R.drawable.in); //This is the bitmap of your photo
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();




                final Session sessionfb = Session.getActiveSession();

                List<String> permissions = sessionfb.getPermissions();
                if (!permissions.contains("publish_actions")) {

                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                            MainActivity.this, Arrays.asList("publish_actions"))
                            ;
                    sessionfb.requestNewPublishPermissions(newPermissionsRequest);

                }


                    Bundle parameters = new Bundle();
//                    parameters.putString("description", "Hi nirav sample video");
//                    parameters.putString("description", "test");

                    parameters.putByteArray("source", byteArray);
//                    parameters.putByteArray("source", byteArray);


                    new Request(sessionfb, "1559395644274660/photos", parameters, HttpMethod.POST,
                            new Request.Callback() {
                                public void onCompleted(Response response) {
                                    progressDialog.dismiss();
                                    Log.e("facebook post response",
                                            response.toString());
//                                    postVideo();
                                }
                            }).executeAsync();

                } catch (Exception e){
                    e.printStackTrace();
                    Log.e("error",e+"");
                    progressDialog.dismiss();
                }


//                Bundle params = new Bundle();
//                params.putString("message", "picture caption");
//                params.putString("url", "https://camo.githubusercontent.com/2e5c9d7b6239afb43ade3e52e5156e33bf63dbc5/687474703a2f2f6934362e74696e797069632e636f6d2f32316b797769742e706e67");
///* make the API call */
//                new Request(
//                        Session.getActiveSession(),
////                        253451068097152
////                        1559395644274660
//                        "/253451068097152/photos",
//                        params,
//                        HttpMethod.POST,
//                        new Request.Callback() {
//                            public void onCompleted(Response response) {
//            /* handle the result */
//
//                                Log.e("response",response+"");
//
//                            }
//                        }
//                ).executeAsync();
            }
        });

        postVideo = (Button) findViewById(R.id.postVideo);
        postVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Uploading image...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.testfile);

                    InputStream iStream = getContentResolver().openInputStream(uri);
                    byte[] inputData = getBytes(iStream);


                    final Session sessionfb = Session.getActiveSession();

                    List<String> permissions = sessionfb.getPermissions();
                    if (!permissions.contains("publish_actions")) {

                        Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                                MainActivity.this, Arrays.asList("publish_actions"))
                                ;
                        sessionfb.requestNewPublishPermissions(newPermissionsRequest);

                    }


                    Bundle parameters = new Bundle();
//                    parameters.putString("description", "Hi nirav sample video");
//                    parameters.putString("description", "test");

                    parameters.putByteArray("source", inputData);
//                    parameters.putByteArray("source", byteArray);


                    new Request(sessionfb, "1559395644274660/videos", parameters, HttpMethod.POST,
                            new Request.Callback() {
                                public void onCompleted(Response response) {
                                    progressDialog.dismiss();
                                    Log.e("facebook post response",
                                            response.toString());
//                                    postVideo();
                                }
                            }).executeAsync();

                } catch (Exception e){
                    e.printStackTrace();
                    Log.e("error",e+"");
                    progressDialog.dismiss();
                }



//                String path=Environment.getExternalStorageDirectory()+"/Download/testing.mp4";
//
//                if (new File(path).exists()) {
//                    try {
//                        byte[] data = null;
//                        String dataPath = new File(path).getAbsolutePath();
//                        Log.e("", dataPath);
//                        String dataMsg = "It is the short movie created";
//                        Bundle param;
//                        InputStream is = null;
//                        try {
//                            is = new FileInputStream(dataPath);
//                            data = readBytes(is);
//                            param = new Bundle();
//                            // param.putString("filename", "" + new
//                            // File(path).getName());
//                            // param.putString("mimeType", "video/mp4");
//                            param.putString("message", dataMsg);
//                            param.putString("title", "title");
//                            param.putString("contentType", "video/quicktime");
//                            param.putByteArray("video.mov", data);
//                            Utility.mAsyncRunner.request("me/videos", param, "POST",
//                                    new FBRequestListener(), null);
//
//                            Toast.makeText(getContext(), "Uploading...",
//                                    Toast.LENGTH_SHORT).show();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(getContext(), "No videos found in these dates",
//                            Toast.LENGTH_SHORT).show();
//                }

            }
        });


        updateStatusBtn = (Button) findViewById(R.id.update_status);

        updateStatusBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });



        buttonsEnabled(false);
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                buttonsEnabled(true);
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                buttonsEnabled(false);
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    public void buttonsEnabled(boolean isEnabled) {
        postImageBtn.setEnabled(isEnabled);
        updateStatusBtn.setEnabled(isEnabled);
    }

//    public void postImage() {
//        if (checkPermissions()) {
//            Bitmap img = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.in);
//            Request uploadRequest = Request.newUploadPhotoRequest(
//                    Session.getActiveSession(), img, new Request.Callback() {
//                        @Override
//                        public void onCompleted(Response response) {
//                            Toast.makeText(MainActivity.this,
//                                    "Photo uploaded successfully",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
//            uploadRequest.executeAsync();
//        } else {
//            requestPermissions();
//        }
//    }

    public void postVideo() {
        File file=new File(Environment.getExternalStorageDirectory()+"/Download/testing.mp4");
        try {
            Request videoRequest = Request.newUploadVideoRequest(Session.getActiveSession(), file, new Request.Callback() {

                @Override
                public void onCompleted(Response response) {
                    // TODO Auto-generated method stub

                    if(response.getError()==null)
                    {
                        Toast.makeText(MainActivity.this, "Video Shared Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            videoRequest.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


//    public void postStatusMessage() {
//        if (checkPermissions()) {
//            Request request = Request.newStatusUpdateRequest(
//                    Session.getActiveSession(), message,
//                    new Request.Callback() {
//                        @Override
//                        public void onCompleted(Response response) {
//                            if (response.getError() == null)
//                                Toast.makeText(MainActivity.this,
//                                        "Status updated successfully",
//                                        Toast.LENGTH_LONG).show();
//                        }
//                    });
//            request.executeAsync();
//        } else {
//            requestPermissions();
//        }
//    }

//    public boolean checkPermissions() {
//        Session s = Session.getActiveSession();
//        if (s != null) {
//            return s.getPermissions().contains("publish_actions");
//        } else
//            return false;
//    }
//
//    public void requestPermissions() {
//        Session s = Session.getActiveSession();
//        if (s != null)
//            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
//                    this, PERMISSIONS));
//    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        buttonsEnabled(Session.getActiveSession().isOpened());
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
//        Session session = Session.getActiveSession();
//
//        if (session != null) {
//
//            // Check for publish permissions
//            List<String> permissions = session.getPermissions();
//            if (!isSubsetOf(PERMISSIONS, permissions)) {
//                pendingPublishReauthorization = true;
//                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
//                        this, PERMISSIONS);
//                session.requestNewPublishPermissions(newPermissionsRequest);
//                return;
//            }
//        }
    }

//    private boolean isSubsetOf(Collection<String> subset,
//                               Collection<String> superset) {
//        for (String string : subset) {
//            if (!superset.contains(string)) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }

}