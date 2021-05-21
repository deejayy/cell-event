package com.android.cellevent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import android.media.AudioManager;
import android.content.Intent;
import android.content.SharedPreferences;

import android.telephony.TelephonyManager;
import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.cdma.CdmaCellLocation;

public class CellEventService extends Service {
	public static final String PREFOBJ = "CellEvent.Preferences";
	public static final String LOGSTR = "CellEventService";
	
	public int ringState;
	public int cellId;
	public int cellIdOld = 0;
	
	public int cellIdTest;
	
	SharedPreferences prefs;
	AudioManager am;

	ScheduledExecutorService timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGSTR, "Service created");
		timer = Executors.newSingleThreadScheduledExecutor();
		prefs = getSharedPreferences(PREFOBJ, 0);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
//		cellIdOld = getCellId();
		startTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(LOGSTR, "Timer stopped");
		timer.shutdown();
		Log.v(LOGSTR, "Restoring RINGER_MODE to " + Integer.toString(ringState));
		switchProfile(ringState);
		Log.v(LOGSTR, "Service destroyed");
	}

	@Override
	public void onStart(Intent intent, int startid) {
	}
	
	public void startTimer() {
		Log.v(LOGSTR, "Timer started");
		timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
				refreshCellInfo();
            }
        }, 0, 10, TimeUnit.SECONDS);
	}

	public int getProfileForCellId(int cellId, int currentProfileId) {
		int profileId = prefs.getInt(Integer.toString(cellId), currentProfileId);
		return profileId;
	}

	public void switchProfile(int profileId) {
		Log.v(LOGSTR, "Switching RINGER_MODE to " + Integer.toString(profileId));
		am.setRingerMode(profileId);
	}
	
	public int getCellId() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation location = telephonyManager.getCellLocation();
		GsmCellLocation gsmLocation = (GsmCellLocation) location;
		int cellId = gsmLocation.getCid();

		return cellId;
	}

	public void refreshCellInfo() {
		cellId = getCellId();
		Log.v(LOGSTR, Integer.toString(cellId));

		if (prefs.contains(Integer.toString(cellId)) && !(prefs.contains(Integer.toString(cellIdOld)))) {
			ringState = am.getRingerMode();
			Log.v(LOGSTR, "Saving RINGER_MODE (" + Integer.toString(ringState) + ")");
		}

		if (cellIdOld != cellId && prefs.contains(Integer.toString(cellId))) {
			switchProfile(getProfileForCellId(cellId, ringState));
		}

		if (!(prefs.contains(Integer.toString(cellId))) && prefs.contains(Integer.toString(cellIdOld))) {
			Log.v(LOGSTR, "Restoring RINGER_MODE to " + Integer.toString(ringState));
			switchProfile(ringState);
		}

		cellIdOld = cellId;
	}

}