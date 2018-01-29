package com.asg.ashish.privacybrowser;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static com.asg.ashish.privacybrowser.R.drawable.paperdesign;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    private TextView glow;
    private ProgressBar progress;
    AutoCompleteTextView Address;
    private ShareActionProvider mShareActionProvider;
    static String searchengine, homepage, theme;
    SharedPreferences mPreferences, tPreferences;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = findViewById(R.id.web);

        Address = findViewById(R.id.Address);
        glow = findViewById(R.id.textViewstart);


        Intent intent = getIntent();
        Uri uri = intent.getData();
        try {

            String url = uri.toString();
            if (Patterns.WEB_URL.matcher(url).matches()) {
                web.setVisibility(View.VISIBLE);
                glow.setVisibility(View.INVISIBLE);
                Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
                if (!url.startsWith("http")) url = "http://" + url;
                web.loadUrl(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




        glow.setShadowLayer(20, 0, 0, Color.WHITE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        tPreferences = getSharedPreferences("maindatafortheme", MODE_PRIVATE);
        theme = tPreferences.getString("thememain", null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewGroup.LayoutParams params = Address.getLayoutParams();
            final int width = params.width;
            tPreferences = getSharedPreferences("Width", MODE_PRIVATE);
            SharedPreferences.Editor tpreferencesEditor = tPreferences.edit();
            tpreferencesEditor.putInt("width", width);
            tpreferencesEditor.apply();
        }

        themesetter();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, WEB);


        //WebSettings webSettings = web.getSettings();
        web.getSettings().setJavaScriptEnabled(true);

        web.getSettings().setSupportZoom(true);       //Zoom Control on web
        web.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
        web.getSettings().setAllowFileAccess(true);
        web.addJavascriptInterface(new MyJavaScriptInterface(), "android");
        web.getSettings().setGeolocationEnabled(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.setWebViewClient(new WebViewClientDemo());
        web.setWebChromeClient(new WebChromeClientDemo());

        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);
        CookieManager.getInstance().acceptThirdPartyCookies(web);
        CookieManager.getInstance().acceptCookie();


        haveFineLocationPermission();
        progress = findViewById(R.id.progress);

        mPreferences = getSharedPreferences("maindata", MODE_PRIVATE);
        searchengine = mPreferences.getString("pos2", "https://www.google.com/search?q=");
        mPreferences = getSharedPreferences("maindata", MODE_PRIVATE);
        homepage = mPreferences.getString("Homepos", "www.google.com");


        Address.setOnEditorActionListener(new EditText.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                String query;

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (isConnect()) {
                        try {

                            query = Address.getText().toString();
                            if (!query.equals("")) {
                                web.setVisibility(View.VISIBLE);
                                glow.setVisibility(View.INVISIBLE);

                                if (!Patterns.WEB_URL.matcher(query).matches()) {

                                    query = searchengine + query;

                                    web.loadUrl(query);
                                } else if (query.startsWith("https") || query.startsWith("http")) {
                                    web.loadUrl(query);

                                } else {
                                    query = "http://" + query;
                                    web.loadUrl(query);
                                }

                                Address.setCursorVisible(false);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                assert imm != null;
                                imm.hideSoftInputFromWindow(Address.getWindowToken(), 0);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Check Ur Connection", Toast.LENGTH_LONG).show();
                    }
                    handled = true;
                }
                return handled;
            }
        });


        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (haveStoragePermission()) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setMimeType(mimeType);
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    assert dm != null;
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                }
            }
        });


        Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address.setCursorVisible(true);
                Address.setAdapter(adapter);

                if (Address.getText().toString().equals(web.getTitle()))
                    Address.setText(web.getUrl());

            }
        });


        Address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Address.setText(web.getTitle());
            }
        });


    }


    public void showMenu(View v) {

        PopupMenu popup = new PopupMenu(this, v);
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reload:
                        web.reload();
                        return true;

                    case R.id.forward:
                        if (web.canGoForward()) {
                            web.goForward();
                        }
                        return true;
                    case R.id.action_settings:
                        Intent settings = new Intent("Settings");
                        startActivity(settings);
                        return true;
                    case R.id.share:
                        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);


                        String sharebody = web.getUrl();
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharebody);
                        startActivity(Intent.createChooser(shareIntent, "Share via"));


                        return true;

                    case R.id.menuhomepage:
                        //web.setWebViewClient(new WebViewClientDemo());
                        //web.setWebChromeClient(new WebChromeClientDemo());
                        web.loadUrl("http://" + homepage);
                        glow.setVisibility(View.INVISIBLE);
                        web.setVisibility(View.VISIBLE);
                        return true;


                    default:
                        return MainActivity.super.onOptionsItemSelected(item);
                }
            }
        });
        popup.inflate(R.menu.menu_main);
        popup.show();


    }

    private static final String[] WEB = new String[]{
            "www.amazon.com", "www.android.com", "www.bing.com", "www.craiglist.com", "www.diply.com", "www.ebay.com", "www.facebook.com", "www.flipkart.com", "www.google.com", "www.huffingtonpost.com", "www.hotmail.com", "www.imgur.com",
            "www.jcpenney.com", "www.kohls.com", "www.live.com", "www.msn.com", "www.netflix.com", "www.outbrain.com", "www.outlook.com", "www.pinterest.com", "www.qvc.com",
            "www.reddit.com", "www.slickdeals.com", "www.snapdeal.com", "www.twitter.com", "www.theguardian.com", "www.theverge.com", "www.techsavvydotcom.wordpress.com", "www.usps.com", "www.verizonwireless.com", "www.wikipedia.org", "www.xfinity.com", "www.youtube.com", "www.zillow.com",
            "amazon.com", "android.com", "bing.com", "craiglist.com", "diply.com", "ebay.com", "facebook.com", "flipkart.com", "google.com", "huffingtonpost.com", "hotmail.com", "imgur.com",
            "jcpenney.com", "kohls.com", "live.com", "msn.com", "netflix.com", "outbrain.com", "outlook.com", "pinterest.com", "qvc.com",
            "reddit.com", "slickdeals.com", "snapdeal.com", "twitter.com", "theguardian.com", "theverge.com", "techsavvydotcom.wordpress.com", "usps.com", "verizonwireless.com", "wikipedia.org", "xfinity.com", "youtube.com", "zillow.com"
    };

    /*public boolean checkIntenta(View view){
        Intent intenta = getIntent();
        Uri launchapp = Uri.parse(intenta.getPackage());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, launchapp);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if(isIntentSafe){
            startActivity(webIntent);
        }
        return true;
    }*/

    public void clear(View view) {
        Address.setText(null);
        Address.setCursorVisible(true);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display = getWindowManager().getDefaultDisplay();
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            int screenWidth = display.getWidth();
            ViewGroup.LayoutParams params = Address.getLayoutParams();
            params.width = screenWidth - 200;
            Address.setLayoutParams(params);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //int screenWidth = display.getWidth();
            ViewGroup.LayoutParams params = Address.getLayoutParams();
            /*params.width = screenWidth-230;

            Address.setLayoutParams(params);*/
            tPreferences = getSharedPreferences("Width", MODE_PRIVATE);
            int width = tPreferences.getInt("width", 220);
            params.width = width;
            Address.setLayoutParams(params);


        }
    }


    @Override

    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        web = findViewById(R.id.web);

        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
//If there is history, then the canGoBack method will return ‘true’//
            return true;
        }


//If the button that’s been pressed wasn’t the ‘Back’ button, or there’s currently no
//WebView history, then the system should resort to its default behavior and return
//the user to the previous Activity//
        else
            return super.onKeyDown(keyCode, event);
    }

    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }


    public void haveFineLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
        }
    }

    private boolean isConnect() {
        ConnectivityManager c = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert c != null;
        NetworkInfo activeNetwork = c.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();

    }


    public class WebViewClientDemo extends WebViewClient {

        ProgressBar progress = findViewById(R.id.progress);




        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress.setProgress(0);
            view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
            Address.setText(web.getTitle());
            Address.setCursorVisible(false);
            Address.setAdapter(null);


        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(0);
            Address.setText(web.getTitle());

        }

    }

    private class WebChromeClientDemo extends WebChromeClient {
        public void onProgressChanged(WebView view, int prog) {
            if(android.os.Build.VERSION.SDK_INT >= 11){
                // will update the "progress" propriety of seekbar until it reaches progress

                ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress",prog);
                animation.setDuration(500); // 0.5 second
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }
            else
                progress.setProgress(prog);
            //progress.setProgress(prog);

        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }






       /* public void WebChromeClientDemo(){
            Intent intent = getIntent();
            String appPackageName = intent.getPackage();
            //Toast.makeText(this,appPackageName,Toast.LENGTH_LONG).show();
            if((appPackageName!=null)&&(appPackageName!="com.asg.ashish.privacybrowser")) {

                // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        }*/








}


    class MyJavaScriptInterface {
        @JavascriptInterface
        public void onUrlChange(String url) {
            Log.d("hydrated", "onUrlChange" + url);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        web.clearHistory();
        web.clearCache(true);
        web.clearFormData();
        web.clearMatches();
        web.clearSslPreferences();
        web.clearFocus();
        clearCookiesAndCache(this);
        tPreferences = getSharedPreferences("maindatafortheme",MODE_PRIVATE);
        SharedPreferences.Editor tpreferencesEditor = tPreferences.edit();
        tpreferencesEditor.putString("thememain",theme);
        tpreferencesEditor.apply();

    }

    @Override
    protected void onStop() {
        super.onStop();



        mPreferences = getSharedPreferences("maindata", MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("pos2", searchengine);
        preferencesEditor.apply();


        //*****For Homepage******
        mPreferences = getSharedPreferences("maindata", MODE_PRIVATE);
        preferencesEditor.putString("Homepos", homepage);
        preferencesEditor.apply();

        //******For Theme********
        tPreferences = getSharedPreferences("maindatafortheme",MODE_PRIVATE);
        SharedPreferences.Editor tpreferencesEditor = tPreferences.edit();
        tpreferencesEditor.putString("thememain",theme);
        tpreferencesEditor.apply();





    }

    public void clearCookiesAndCache(Context context){
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
        else {
            cookieManager.removeAllCookie();
        }
    }
    public void themesetter(){
        final RelativeLayout layout1 = findViewById(R.id.layout1);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        if(Objects.equals(theme, "Material Red"))
            layout1.setBackgroundColor(Color.RED);
        else if(Objects.equals(theme, "Cyan"))
            layout1.setBackgroundColor(Color.CYAN);
        else if(Objects.equals(theme, "Material Dark"))
            layout1.setBackgroundColor(Color.DKGRAY);
        else if(Objects.equals(theme, "Paper Design"))
            {
            layout1.setBackground(getResources().getDrawable(paperdesign));
            //Toast.makeText(this,"In Setter",Toast.LENGTH_SHORT).show();
        }
        else
            layout1.setBackground(wallpaperDrawable);
    }

    @Override
    protected void onStart(){
        super.onStart();
        themesetter();


    }
    @Override
    protected void onResume(){
        super.onResume();
        themesetter();

    }


}

