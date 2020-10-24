package com.coffice;

import android.app.Activity;
import android.view.WindowManager;
import android.os.HandlerThread;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Message;
import android.content.Context;


import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.Exception;
import java.lang.Runnable;

public class ScreenshotBlocker extends CordovaPlugin{
    private com.coffice.ScreenshotBlocker mContext;

    private static final String TAG = "ScreenshotBlocker";
    static ScreenshotBlocker instance = null;
    static CordovaWebView cordovaWebView;
    static CordovaInterface cordovaInterface;

   private ScreenShotContentObserver screenShotContentObserver;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Activity activity = this.cordova.getActivity();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        instance = this;
        cordovaWebView = webView;
        cordovaInterface = cordova;

        //setContentView(R.layout.activity_main);

        HandlerThread handlerThread = new HandlerThread("content_observer");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        screenShotContentObserver = new ScreenShotContentObserver(handler, activity.getApplicationContext()) {
            @Override
            protected void onScreenShot(String path, String fileName) {
                //File file = new File(path); //this is the file of screenshot image
                triggerJavascriptEvent("onTookScreenshot");
            }
        };


    }

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        mContext = this;

        if (action.equals("enable")) {
            mContext.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try{
                        // Allow to make screenshots removing the FLAG_SECURE
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                            mContext.cordova.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                        }
                        callbackContext.success("Success");
                    }catch(Exception e){
                        callbackContext.error(e.toString());
                    }
                }
            });

            return true;
        }else if (action.equals("disable")) {
            mContext.cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try{
                        // Allow to make screenshots removing the FLAG_SECURE
                        // Disable the creation of screenshots adding the FLAG_SECURE to the window
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                            mContext.cordova.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                    WindowManager.LayoutParams.FLAG_SECURE);
                        }
                        callbackContext.success("Success");
                    }catch(Exception e){
                        callbackContext.error(e.toString());
                    }
                }
            });
            return true;
        }
        else{
            return false;
        }

    }
    
    
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                screenShotContentObserver
        );
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        try {
            getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        try {
            getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //https://stackoverflow.com/questions/54939027/triggering-javascript-event-from-android how to trigger an event from android to JS
    private static void executeGlobalJavascript(final String jsString) {
        if (instance == null) {return;}
        instance.cordovaInterface.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.cordovaWebView.loadUrl("javascript:" + jsString);
                } catch (Exception e) {
                    Log.e(TAG, "Error executing javascript: "+ e.toString());
                }
            }
        });
    }

    public static void triggerJavascriptEvent(final String eventName){
        executeGlobalJavascript(String.format("document.dispatchEvent(new Event('%s'));", eventName));
    }

}
