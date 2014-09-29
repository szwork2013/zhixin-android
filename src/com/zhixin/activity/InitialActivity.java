package com.zhixin.activity;

import java.io.IOException;

import com.baidu.mobstat.StatService;
import com.zhixin.R;
import com.zhixin.datasynservice.RegistService;
import com.zhixin.service.GetAddressIntentService;
import com.zhixin.settings.CurrentUserHelper;
import com.zhixin.settings.PhoneHelper;
import com.zhixin.settings.SettingValues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import cn.jpush.android.api.InstrumentedActivity;
import cn.sharesdk.framework.ShareSDK;


/**
 * @author Administrator
 *标志Logo
 */
public class InitialActivity extends InstrumentedActivity {

	boolean isFirstIn = false;

	private RegistService registService;

	private String currentPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_initial);

		// share sdk inital
		ShareSDK.initSDK(this);

		registService = new RegistService(this);

		// determine screen size in dp
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		float density = getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		float dpWidth = outMetrics.widthPixels / density;
		
		PhoneHelper.savePhoneDensity(density);
		PhoneHelper.savePhoneHeight(outMetrics.heightPixels);
		PhoneHelper.savePhoneWidth(outMetrics.widthPixels);
		
		Log.i("phone","density" + String.valueOf(density));
		Log.i("phone","height" + String.valueOf(dpHeight));
		Log.i("phone","width" + String.valueOf(dpWidth));
		Log.i("phone", "sdk version" + android.os.Build.VERSION.SDK_INT);

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				
				Boolean isAddressSaved = false;
					SharedPreferences sharedPref = InitialActivity.this
							.getSharedPreferences(
									SettingValues.FILE_NAME_SETTINGS,
									Context.MODE_PRIVATE);
					isAddressSaved = sharedPref.getBoolean(
							SettingValues.KEY_CURRENT_ADDRESS_SAVED, false);
				
				return isAddressSaved;
			}

			@Override
			protected void onPostExecute(Boolean params) {
				if (!params) {
					Intent intent = new Intent(InitialActivity.this,
							GetAddressIntentService.class);
					InitialActivity.this.startService(intent);
				}

				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						SharedPreferences sharedPref = InitialActivity.this
								.getSharedPreferences(
										SettingValues.FILE_NAME_SETTINGS,
										Context.MODE_PRIVATE);
						isFirstIn = sharedPref.getBoolean(
								SettingValues.APP_FIRSTTIME_IN, true);
//						isFirstIn=true;
						currentPhone = CurrentUserHelper.getCurrentPhone();
						return null;
					}

					@Override
					protected void onPostExecute(Void params) {
						judgeWhichToStart();
					}
				}.execute();

			}
		}.execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	public void onStart() {
		super.onStart();

	}


	private void judgeWhichToStart() {

		if (isFirstIn) {
			Intent intent = new Intent(InitialActivity.this,
					FristInIntroduceActivity.class);
			startActivity(intent);
			return;
		} else {
			if (currentPhone != null) {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			} else {
				startLoginActivity();
			}
		}
	}
	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

}
