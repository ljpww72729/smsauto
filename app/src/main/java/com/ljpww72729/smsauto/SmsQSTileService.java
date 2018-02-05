package com.ljpww72729.smsauto;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by LinkedME06 on 2/5/18.
 */

@TargetApi(Build.VERSION_CODES.N)
public class SmsQSTileService extends TileService {


    private static final String TAG = "SmsQSTileService";

    @Override
    public void onTileAdded() {
        Log.i(TAG, "onTileAdded: ");
    }

    @Override
    public void onStartListening() {
        Log.i(TAG, "onStartListening: ");
        WilddogSyncManager.syncConnected(this.getApplicationContext());
    }

    @Override
    public void onStopListening() {
        Log.i(TAG, "onStopListening: ");
    }

    @Override
    public void onClick() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tile_switch = sharedPreferences.getBoolean("tile_switch", false);
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_smsauto_24dp));
        tile.setLabel(getString(R.string.app_name));
        tile.setContentDescription(getString(R.string.app_name));
        if (tile_switch) {
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            tile.setState(Tile.STATE_ACTIVE);
        }
        sharedPreferences.edit().putBoolean("tile_switch", !tile_switch).apply();
        tile.updateTile();
    }

    @Override
    public void onTileRemoved() {
        Log.i(TAG, "onTileRemoved: ");
        super.onTileRemoved();
    }
}
