package com.ljpww72729.smsauto;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by LinkedME06 on 2/6/18.
 */

public class SmsAutoAccessibilityService extends AccessibilityService {

    private static final String TAG = "SmsAutoAccessibilitySer";
    private boolean sharedClicked = false;


    @Override
    protected void onServiceConnected() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
//        info.notificationTimeout = 100;
//        setServiceInfo(info);
//        info.packageNames = new String[]{"com.teamviewer.quicksupport.market"};
//        setServiceInfo(info);
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.i(TAG, "onAccessibilityEvent: " + event.getEventType());

        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                Log.i(TAG, "onAccessibilityEvent: TYPE_WINDOWS_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.i(TAG, "onAccessibilityEvent: TYPE_WINDOW_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i(TAG, "onAccessibilityEvent: TYPE_WINDOW_CONTENT_CHANGED");
                break;
        }

        // get the source node of the event
        AccessibilityNodeInfo nodeInfo = event.getSource();

        // Use the event and node information to determine
        // what action to take

        List<AccessibilityNodeInfo> mainClientIdList = nodeInfo.findAccessibilityNodeInfosByViewId("com.teamviewer.quicksupport.market:id/main_client_id");
        if (mainClientIdList != null && mainClientIdList.size() > 0) {
            if (mainClientIdList.get(0).getText().toString().length() > 3) {

            }
        }
        List<AccessibilityNodeInfo> sendIdButtonList = nodeInfo.findAccessibilityNodeInfosByViewId("com.teamviewer.quicksupport.market:id/main_send_id_button");
        if (sendIdButtonList != null && sendIdButtonList.size() > 0) {
            if (!sharedClicked) {
                Log.i(TAG, "onAccessibilityEvent: click");
                sendIdButtonList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                sharedClicked = true;
            }
        }
        List<AccessibilityNodeInfo> allowBtnList = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (allowBtnList != null && allowBtnList.size() > 0) {
            Log.i(TAG, "onAccessibilityEvent: allowBtn");
            allowBtnList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            sharedClicked = true;
        }
        // recycle the nodeInfo object
        nodeInfo.recycle();
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt: ");
    }
}
