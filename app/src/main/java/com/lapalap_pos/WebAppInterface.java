package com.lapalap_pos;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by manishautomatic on 14/10/16.
 */
public class WebAppInterface {
    Context mContext;
    WebinterfaceCallaback lCallback=null;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c, WebinterfaceCallaback callbackinterface) {
        mContext = c;
        lCallback = callbackinterface;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void sendUserID(String userID){
        Toast.makeText(mContext, userID, Toast.LENGTH_SHORT).show();
       // AppInstance.getInstance().postIdToServer(userID);
    }

    @JavascriptInterface
    public void doMute(){
        lCallback.doMute();;
    }

    @JavascriptInterface
    public void weigh(){
        lCallback.weigh();;
    }

}
