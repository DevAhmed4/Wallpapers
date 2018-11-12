package com.devMohanned.devteam.wallpaper_app.OnePiece;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {
    ImageView imageView,imageView2;
    FloatingActionButton prevButton, nextButton, saveButton, shareButton, setButton;
    int instance_image = 0;
    AlertDialog SuccessSetDialog;
    boolean NetworkConnected = false;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1234;
    AlertDialog.Builder LeaveAppDialog,NoNetworkConnectionDialog;
    String file_Name;
    boolean mVisible;
    private View mControlsView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onBackPressed() {
        LeaveAppDialog = new AlertDialog.Builder(this);
        LeaveAppDialog.setTitle(R.string.wantToExite);
        LeaveAppDialog.setMessage(R.string.PlzShareOurApp);
        LeaveAppDialog.setCancelable(true);
        LeaveAppDialog.setPositiveButton(R.string.Exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        LeaveAppDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        LeaveAppDialog.setNeutralButton(R.string.ShareAppButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShareApp();
            }
        });
        LeaveAppDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            instance_image = savedInstanceState.getInt("instance_image");
        }
        //Hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mVisible = true;
        MobileAds.initialize(this, Utilities.app_id_num);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Utilities.Interstitial_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setVisibility(View.INVISIBLE);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        setButton = findViewById(R.id.setButton);
        mControlsView = findViewById(R.id.container);
        imageView.setImageResource(Utilities.IMAGES[instance_image]);
        //==========================================================================================
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        //==========================================================================================
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 500, 0, 0);
                //translateAnimation.setDuration(10);
                translateAnimation.reset();
                translateAnimation.cancel();
                //imageView.startAnimation(translateAnimation);
                if (instance_image != Utilities.IMAGES.length - 1) {
                    instance_image++;
                    imageView.setImageResource(Utilities.IMAGES[instance_image]);
                } else {
                    instance_image = 0;
                    imageView.setImageResource(Utilities.IMAGES[instance_image]);
                }
            }
        });
        //==========================================================================================
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateAnimation translateAnimation = new TranslateAnimation(0, -500, 0, 0);
                //translateAnimation.setDuration(10);
                translateAnimation.reset();
                translateAnimation.cancel();
                //imageView.startAnimation(translateAnimation);
                if (instance_image <= 0) {
                    instance_image = Utilities.IMAGES.length - 1;
                    imageView.setImageResource(Utilities.IMAGES[instance_image]);
                } else {
                    instance_image--;
                    imageView.setImageResource(Utilities.IMAGES[instance_image]);
                }
            }
        });
        //==========================================================================================
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButton.setClickable(false);
                shareButton.setClickable(false);
                saveButton.setClickable(false);
                setWall();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("MainActivity", "The interstitial wasn't loaded yet.");
                }
                setButton.setClickable(true);
                shareButton.setClickable(true);
                saveButton.setClickable(true);

            }
        });
        //==========================================================================================
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking network state
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are NetworkConnected to a network
                    NetworkConnected = true;
                } else
                    NetworkConnected = false;
                if (NetworkConnected) {
                    ///////////////Checking permissions before downloading ///////////////////////////////////
                    if ((int) Build.VERSION.SDK_INT >= 23) {
                        //get permission
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        }

                    }
                    //Call of Saving method
                    startSave();
                    //SnackBar Activation
                    Snackbar snackbar = Snackbar
                            .make(imageView, R.string.success_image_download, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG)
                            .setAction(R.string.View, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setType("image/*");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            });
                    //show the snackbar
                    snackbar.show();
                } else {
                    NoNetworkConnectionDialog = new AlertDialog.Builder(MainActivity.this);
                    NoNetworkConnectionDialog.setTitle(R.string.check_your_internet);
                    NoNetworkConnectionDialog.setMessage(R.string.TirnOnWifiOrData);
                    NoNetworkConnectionDialog.setCancelable(true);
                    NoNetworkConnectionDialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    NoNetworkConnectionDialog.show();

                }
            }
        });
        //==========================================================================================
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareTheImage();
                }
            });
        //==========================================================================================
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();// Code to be executed when an ad finishes loading.
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//               /* AdRequest.ERROR_CODE_INTERNAL_ERROR = 0;
//                AdRequest.ERROR_CODE_INVALID_REQUEST = 1;
//                AdRequest.ERROR_CODE_NETWORK_ERROR = 2;
//                AdRequest.ERROR_CODE_NO_FILL = 3;*/
//                Toast.makeText(MainActivity.this, "onAdFailedToLoad " + errorCode, Toast.LENGTH_SHORT).show();// Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                Toast.makeText(MainActivity.this, "onAdOpened", Toast.LENGTH_SHORT).show();// Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                Toast.makeText(MainActivity.this, "onAdLeftApplication", Toast.LENGTH_SHORT).show(); // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                Toast.makeText(MainActivity.this, "onAdClosed", Toast.LENGTH_SHORT).show();// Code to be executed when when the interstitial ad is closed.
//            }
//        });
    }

    private void toggle() {
        if(mVisible){
            hide();
        }else{
            show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("instance_image",instance_image);
    }

    private void hide() {
        TranslateAnimation set,download,share;
        set = new TranslateAnimation(0,0,0,500);
        set.setDuration(500);
        download = new TranslateAnimation(0,500,0,0);
        download.setDuration(500);
        share = new TranslateAnimation(0,-500,0,0);
        share.setDuration(500);

        setButton.startAnimation(set);
        shareButton.startAnimation(share);
        saveButton.startAnimation(download);

        set.setFillAfter(true);
        download.setFillAfter(true);
        share.setFillAfter(true);
        nextButton.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
        //Toast.makeText(this, "ssss", Toast.LENGTH_SHORT).show();
        //mControlsView.setVisibility(View.INVISIBLE);
        mVisible = false;
    }
    private void show() {

        TranslateAnimation set,download,share;
        set = new TranslateAnimation(0,0,500,0);
        set.setDuration(500);
        download = new TranslateAnimation(500,0,0,0);
        download.setDuration(500);
        share = new TranslateAnimation(-500,0,0,0);
        share.setDuration(500);

        setButton.startAnimation(set);
        shareButton.startAnimation(share);
        saveButton.startAnimation(download);
        //Toast.makeText(this, "show", Toast.LENGTH_SHORT).show();
        // Show the system bar
        mControlsView.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.VISIBLE);
        mVisible = true;
    }
    //==============================================================================================
    public void startSave() {
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        if (!file.exists() && !file.mkdirs()) {
            Toast.makeText(this, "Failed to create directory!!! Try again", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String data = simpleDateFormat.format(new Date());
        String name = "img" + data + ".jpg";

        file_Name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_Name);
        try {
            fileOutputStream = new FileOutputStream(file_Name);
            Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);

    }

    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Downloads_images");
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    //==============================================================================================
    private void setWall() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            // wallpaperManager.setBitmap(ViewToBitmap(imageView,imageView.getWidth(),imageView.getHeight()));
            //wallpaperManager.setResource(Utilities.IMAGES[instance_image]);
            wallpaperManager.setBitmap(viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight()));
            SuccessSetDialog = new AlertDialog.Builder(this).create();
            SuccessSetDialog.setTitle(getString(R.string.set_message_title));
            SuccessSetDialog.setMessage(getString(R.string.success_set_message));
            SuccessSetDialog.setButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SuccessSetDialog.dismiss();
                }
            });
            SuccessSetDialog.show();
            // Toast.makeText(this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "FAILED Set", Toast.LENGTH_SHORT).show();
        }
    }

    /////////////////////Save image & refresh gallery Methods///////////////////////////////
    public static Bitmap viewToBitmap(View view, int width, int hight) {
        Bitmap bitmap = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    //==============================================================================================
    public void ShareTheImage() {
        Bitmap bitmap = getBitmapFromView(imageView);
        try {
            File file = new File(this.getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    //==============================================================================================
    ////////////////////Share appa method///////////////////////////////////////////////////
    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.simple_wallpaper.devahmed.devteam.OnePiece");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.AppLik)));
    }

}