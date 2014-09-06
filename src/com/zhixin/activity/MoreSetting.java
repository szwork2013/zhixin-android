package com.zhixin.activity;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mobstat.StatService;
import com.zhixin.R;
import com.zhixin.daos.UserSettingsDao;
import com.zhixin.dialog.TimePickerDialog;
import com.zhixin.domain.UserSettings;
import com.zhixin.settings.CurrentUserHelper;
import com.zhixin.settings.SettingValues;
import com.zhixin.utils.HttpClient;

public class MoreSetting extends FragmentActivity implements
		View.OnClickListener, DialogInterface.OnDismissListener {
	private TextView txtPageTitle;
	private ImageButton iBtnPageBack;

	private ToggleButton tBtnNewMessage_MoreOption;
	private TextView txtReceiveTime_MoreOption;
	private ToggleButton tBtnEconomize_MoreOption;
	private ToggleButton tBtnOpenToFriendAnswer_MoreOption;

	private Button logOutBtn;

	private TimePickerDialog timePickerDialog;
	private Activity _this;

	private long userId;
	private String startTime;
	private String endTime;
	private String receiveTime = "";
	private Boolean push;
	private Boolean saveFlow;
	private Boolean publicAnswersToFriend;

	private String localStartTime;
	private String localEndTime;
	private Boolean localPush;
	private Boolean localSaveFlow;
	private Boolean localPublicAnswersToFriend;

	private UserSettingsDao userSettingsDao;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				tBtnNewMessage_MoreOption.setChecked(push);
				tBtnEconomize_MoreOption.setChecked(saveFlow);
				tBtnOpenToFriendAnswer_MoreOption
						.setChecked(publicAnswersToFriend);
				if (startTime == null || startTime.equals("")) {
					startTime = localStartTime;
				}
				if (endTime == null || endTime.equals("")) {
					endTime = localEndTime;
				}
				receiveTime = startTime + "-" + endTime;
				txtReceiveTime_MoreOption.setText(receiveTime);

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting);
		_this = this;

		initView();

		String requestUrl = SettingValues.URL_PREFIX
				+ getString(R.string.URL_USER_SETTING);
		new LoadDataTask().execute(1, requestUrl, null, HttpClient.TYPE_GET);

	}

	private void initView() {
		txtPageTitle = (TextView) this.findViewById(R.id.title_of_the_page);
		iBtnPageBack = (ImageButton) this.findViewById(R.id.backup_btn);
		iBtnPageBack.setOnClickListener(this);
		txtPageTitle.setText(this.getString(R.string.title_more_option));

		logOutBtn = (Button) this.findViewById(R.id.logOutBtn);
		logOutBtn.setOnClickListener(this);

		tBtnNewMessage_MoreOption = (ToggleButton) this
				.findViewById(R.id.tBtnNewMessage_MoreOption);
		tBtnNewMessage_MoreOption.setOnClickListener(this);
		tBtnEconomize_MoreOption = (ToggleButton) this
				.findViewById(R.id.tBtnEconomize_MoreOption);
		tBtnEconomize_MoreOption.setOnClickListener(this);
		tBtnOpenToFriendAnswer_MoreOption = (ToggleButton) this
				.findViewById(R.id.tBtnOpenToFriendAnswer_MoreOption);
		tBtnOpenToFriendAnswer_MoreOption.setOnClickListener(this);
		txtReceiveTime_MoreOption = (TextView) this
				.findViewById(R.id.txtReceiveTime_MoreOption);
		txtReceiveTime_MoreOption.setOnClickListener(this);
		tBtnNewMessage_MoreOption
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						tBtnNewMessage_MoreOption.setChecked(isChecked);
						push = isChecked;
					}
				});
		tBtnEconomize_MoreOption
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						tBtnEconomize_MoreOption.setChecked(isChecked);
						saveFlow = isChecked;
					}
				});
		tBtnOpenToFriendAnswer_MoreOption
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						tBtnOpenToFriendAnswer_MoreOption.setChecked(isChecked);
						publicAnswersToFriend = isChecked;
					}
				});

		UserSettings us = new UserSettings();
		userSettingsDao = new UserSettingsDao();
		us = userSettingsDao.getUserSettings(CurrentUserHelper
				.getCurrentUserId());

		if (us != null) {

			localPush = us.isPublicAnswersToFriend();
			localStartTime = us.getStatTime();
			localEndTime = us.getEndTime();
			localSaveFlow = us.isSaveFlow();
			localPublicAnswersToFriend = us.isPublicAnswersToFriend();
		} else {

			localPush = true;
			localStartTime = "09:00";
			localEndTime = "22:00";
			localSaveFlow = true;
			localPublicAnswersToFriend = false;
		}
		tBtnNewMessage_MoreOption.setChecked(localPush);
		tBtnEconomize_MoreOption.setChecked(localSaveFlow);
		tBtnOpenToFriendAnswer_MoreOption
				.setChecked(localPublicAnswersToFriend);
		receiveTime = localStartTime + "-" + localEndTime;
		txtReceiveTime_MoreOption.setText(receiveTime);

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		switch (v.getId()) {
		case R.id.backup_btn:
			String requestUrl1 = SettingValues.URL_PREFIX
					+ getString(R.string.URL_MORE_SETTINGS);
			if (push != null && startTime != null && endTime != null) {
				if (push != localPush || !startTime.equals(localStartTime)
						|| !endTime.equals(localEndTime)) {

					requestUrl1 += "?isPush=" + push.toString();

					if (startTime.equals("")) {
						requestUrl1 += "&startTime=" + localStartTime;
					} else {
						requestUrl1 += "&startTime=" + startTime;
					}
					if (endTime.equals("")) {
						requestUrl1 += "&endTime=" + localEndTime;
					} else {
						requestUrl1 += "&endTime=" + endTime;
					}

				} else {
					requestUrl1 += "?isPush=" + localPush.toString();
					requestUrl1 += "&startTime=" + localStartTime;
					requestUrl1 += "&endTime=" + localEndTime;
				}
				new LoadDataTask().execute(2, requestUrl1, null,
						HttpClient.TYPE_PUT_FORM);
				try {
					JSONObject params1 = new JSONObject();
					if (push == null) {
						params1.put("isPush", localPush.toString());
					} else {
						params1.put("isPush", push.toString());
					}
					if (startTime == null || startTime.equals("")) {
						params1.put("startTime", localStartTime);
					} else {
						params1.put("startTime", startTime);
					}
					if (endTime == null || endTime.equals("")) {
						params1.put("endTime", localEndTime);
					} else {
						params1.put("endTime", endTime);
					}

					userSettingsDao = new UserSettingsDao();

					userSettingsDao.saveUserSettingsFront(params1, userId);
					;
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			if (publicAnswersToFriend != localPublicAnswersToFriend
					|| saveFlow != localSaveFlow) {

				try {
					JSONObject params2 = new JSONObject();

					params2.put("id", userId);
					if (publicAnswersToFriend == null) {
						params2.put("publicAnswersToFriend",
								localPublicAnswersToFriend);
					} else {
						params2.put("publicAnswersToFriend",
								publicAnswersToFriend);
					}
					if (saveFlow == null) {
						params2.put("saveFlow", localSaveFlow);
					} else {
						params2.put("saveFlow", saveFlow);
					}

					String requestUrl2 = SettingValues.URL_PREFIX
							+ getString(R.string.URL_USER_INFO_STATE);
					new LoadDataTask().execute(3, requestUrl2, params2,
							HttpClient.TYPE_POST_FORM);

					userSettingsDao = new UserSettingsDao();
					userSettingsDao.saveUserSettingsBehind(params2, userId);

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			this.onBackPressed();
			v.setEnabled(true);
			break;

		case R.id.txtReceiveTime_MoreOption:

			timePickerDialog = new TimePickerDialog(this,
					android.R.style.Theme_Translucent_NoTitleBar, startTime,
					endTime);
			Window window = timePickerDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.dimAmount = 0.7f;
			timePickerDialog.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setAttributes(lp);
			timePickerDialog.setOnDismissListener(this);

			if (!timePickerDialog.isShowing()) {
				timePickerDialog.show();
			}
			break;
		case R.id.logOutBtn:
			logOut();
			break;
		default:
			break;

		}
		v.setEnabled(true);
	}

	private void logOut() {
		CurrentUserHelper.clearCurrentPhone();
		Intent intent = new Intent(MoreSetting.this, InitialActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();
		HttpClient.clearHttpCache();
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

	private class LoadDataTask extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject result = null;

			Integer syncType = (Integer) params[0];
			try {
				switch (syncType) {
				case 1:
					// null。。。。传参方式是get
					// (Integer)params[3]对应上面的HttpClient.TYPE_POST
					result = HttpClient.requestSync(params[1].toString(), null,
							(Integer) params[3]);
					result.put("syncType", syncType);
					break;
				case 2:
					result = HttpClient.requestSync(params[1].toString(), null,
							(Integer) params[3]);
					result.put("syncType", syncType);
					break;
				case 3:
					result = HttpClient.requestSync(params[1].toString(),
							params[2], (Integer) params[3]);
					result.put("syncType", syncType);
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				Integer syncType = result.getInt("syncType");
				switch (syncType) {
				case 1:
					if (result != null
							&& result.getString("success").equals("1")) {
						// 。。。。。。。。。
						Toast.makeText(_this, "获取设置成功！", Toast.LENGTH_SHORT)
								.show();
						userId = result.getLong("id");
						push = result.getBoolean("push");
						startTime = result.getString("startTime");
						endTime = result.getString("endTime");
						saveFlow = result.getBoolean("saveFlow");
						publicAnswersToFriend = result
								.getBoolean("publicAnswersToFriend");
						Message msg = Message.obtain();
						msg.what = 0;
						handler.sendMessage(msg);
						try {
							userSettingsDao = new UserSettingsDao();
							Log.i("获取设置", result + "");
							userSettingsDao.saveUserSettings(result, userId);
							;
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(_this, "获取设置失败！", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				case 2:
					if (result != null && result.getInt("success") == 1) {
						// 。。。。。。。。。

						Toast.makeText(_this, "前二修改成功！", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(_this, "修改前二失败！", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				case 3:
					if (result != null && result.getInt("success") == 1) {
						// 。。。。。。。。。

						Toast.makeText(_this, "后二设置成功！", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(_this, "修改后二失败！", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (timePickerDialog.isLeaveWithConfirm()) {
			StringBuffer startTimeBuffer = new StringBuffer();
			startTimeBuffer.append(timePickerDialog.getStartHour() < 10 ? "0"
					+ String.valueOf(timePickerDialog.getStartHour()) : String
					.valueOf(timePickerDialog.getStartHour()));
			startTimeBuffer.append(":");
			startTimeBuffer.append(timePickerDialog.getStartMinute() < 10 ? "0"

			+ String.valueOf(timePickerDialog.getStartMinute()) : String
					.valueOf(timePickerDialog.getStartMinute()));

			startTime = startTimeBuffer.toString();

			StringBuffer endTimeBuffer = new StringBuffer();
			endTimeBuffer.append(timePickerDialog.getEndHour() < 10 ? "0"
					+ String.valueOf(timePickerDialog.getEndHour()) : String
					.valueOf(timePickerDialog.getEndHour()));
			endTimeBuffer.append(":");
			endTimeBuffer.append(timePickerDialog.getEndMinute() < 10 ? "0"

			+ String.valueOf(timePickerDialog.getEndMinute()) : String
					.valueOf(timePickerDialog.getEndMinute()));
			endTime = endTimeBuffer.toString();
			txtReceiveTime_MoreOption.setText(startTime + "-" + endTime);
		}
	}

}
