package br.com.ritmo78;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebResourceResponse;
import android.webkit.ValueCallback;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewFeature;
import org.json.JSONObject;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class MainActivity extends Activity {
  private static final String SITE = "https://luisroxxx.github.io/Ritimo78/";
  private static final String FALLBACK = "https://appassets.androidplatform.net/assets/web/index.html";
  private WebView web;
  private ValueCallback<Uri[]> imageCallback;
  private Uri cameraUri;
  private boolean fallbackUsed = false;
  private WebViewAssetLoader assetLoader;
  private static final int PICK = 77;
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.rgb(12,48,41));
    assetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
    web = new WebView(this); setContentView(web);
    WebSettings ws = web.getSettings();
    ws.setJavaScriptEnabled(true); ws.setDomStorageEnabled(true);
    ws.setAllowFileAccess(false); ws.setAllowContentAccess(true);
    ws.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
    if(WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)){
      WebViewCompat.addWebMessageListener(web,"Ritmo78Android",
        new HashSet<>(Arrays.asList("https://luisroxxx.github.io","https://appassets.androidplatform.net")),
        (v,message,origin,isMainFrame,reply)->{
          if(!isMainFrame || !(web.getUrl()!=null && (web.getUrl().startsWith(SITE)||web.getUrl().startsWith(FALLBACK)))) return;
          runOnUiThread(()->handle(message.getData()));
        });
    }
    web.setWebViewClient(new WebViewClient(){
      @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest req){return assetLoader.shouldInterceptRequest(req.getUrl());}
      @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req){
        Uri u=req.getUrl();
        if(u.toString().startsWith(SITE)||u.toString().startsWith("https://appassets.androidplatform.net/assets/web/")) return false;
        if(req.isForMainFrame()){
          try {startActivity(new Intent(Intent.ACTION_VIEW,u));} catch(Exception ignored){}
        }
        return true;
      }
      @Override public void onReceivedError(WebView v, WebResourceRequest req, WebResourceError err){
        if(req.isForMainFrame()&&!fallbackUsed){fallbackUsed=true;v.loadUrl(FALLBACK);}
      }
      @Override public void onReceivedHttpError(WebView v, WebResourceRequest req, WebResourceResponse resp){
        if(req.isForMainFrame()&&resp.getStatusCode()>=400&&!fallbackUsed){fallbackUsed=true;v.loadUrl(FALLBACK);}
      }
    });
    web.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params){
        if(imageCallback!=null) imageCallback.onReceiveValue(null);
        imageCallback=callback;
        try{
          Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
          gallery.setType("image/*");gallery.addCategory(Intent.CATEGORY_OPENABLE);
          Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          File f=File.createTempFile("ritmo78_",".jpg",getExternalCacheDir());
          cameraUri=FileProvider.getUriForFile(MainActivity.this,getPackageName()+".provider",f);
          camera.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
          camera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
          Intent chooser=Intent.createChooser(gallery,"Foto da refeição");
          chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{camera});
          startActivityForResult(chooser,PICK);
          return true;
        }catch(Exception ex){imageCallback.onReceiveValue(null);imageCallback=null;return false;}
      }
    });
    web.loadUrl(SITE);
  }
  private void handle(String raw){
    try{
      JSONObject msg=new JSONObject(raw);
      String action=msg.optString("action");
      if(action.equals("requestNotifications")){
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
          requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},100);
      }else if(action.equals("configureWaterReminders")){
        boolean active=msg.optBoolean("enabled");
        int interval=Math.max(60,Math.min(180,msg.optInt("minutes",90)));
        getPreferences(0).edit().putBoolean("water",active).putInt("interval",interval).apply();
        if(active && Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
          requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},100);
        scheduleWater(active,interval);
      }
    }catch(Exception ignored){}
  }
  private void scheduleWater(boolean enabled,int minutes){
    AlarmManager alarms=(AlarmManager)getSystemService(ALARM_SERVICE);
    Intent i=new Intent(this,WaterReceiver.class);
    PendingIntent p=PendingIntent.getBroadcast(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    alarms.cancel(p);
    if(enabled)alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+minutes*60000L,minutes*60000L,p);
  }
  @Override protected void onActivityResult(int req,int res,Intent data){
    super.onActivityResult(req,res,data);
    if(req==PICK && imageCallback!=null){
      Uri uri = res==RESULT_OK ? (data!=null&&data.getData()!=null?data.getData():cameraUri):null;
      imageCallback.onReceiveValue(uri==null?null:new Uri[]{uri});
      imageCallback=null;cameraUri=null;
    }
  }
  @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}
