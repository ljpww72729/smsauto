package com.ljpww72729.smsauto;

import android.app.Activity;
import android.view.View;

/**
 * Created by LinkedME06 on 1/27/18.
 */

public class UIUtils {
    /**
     * Retrieves the rootView of the specified {@link Activity}.
     */
    public static View getRootView(Activity activity) {
        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }


}
