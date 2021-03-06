package com.qubaopen.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.qubaopen.R;
import com.qubaopen.customui.CircleImageView;
import com.qubaopen.daos.UserInfoDao;
import com.qubaopen.domain.UserInfo;
import com.qubaopen.provider.InternalStorageContentProvider;
import com.qubaopen.service.CropImageIntentService;
import com.qubaopen.settings.CurrentUserHelper;
import com.qubaopen.settings.SettingValues;
import com.qubaopen.utils.HttpClient;
import com.qubaopen.utils.RecToCircleTask;
import com.qubaopen.utils.ShareUtil;

import eu.janmuller.android.simplecropimage.CropImage;

public class MeFragment extends Fragment implements View.OnClickListener {
	private Activity mainActivity;
	private UserInfo userInfo;
	private UserInfoDao userInfoDao;
	/** 头像 */
	private CircleImageView headImageView;
	/** 设置 */
	private ImageView settings;
	/** 标题 */
	private TextView txtPageTitle;

	private Handler closePicImageDialogHander;

	// private MainMenuService service;

	private LinearLayout layoutMyprofile, layoutSuggestion, layoutAbout,
			layoutShareAppComp;

	private View scoreTheApp;

	private Intent intent;

	private TextView nickNameTextView;
	private String nickName;
	private String localNickName;
	private ImageButton editNickNameBtn;

	private TextView signatureHead;
	private TextView signatureTextView;
	private String signature;
	private String localSignature;

	private View rootView;

	private BroadcastReceiver mReceiver;

	static final int PICK_PIC_FROM_CAMERA_ACTION = 10;
	static final int PICK_PIC_FORM_GALLERY_ACTION = 20;
	static final int CROP_IMAGE_ACTION = 30;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainActivity = activity;
		userInfo = new UserInfo();
		userInfoDao = new UserInfoDao();
		userInfo = userInfoDao.getUserById(CurrentUserHelper
				.getCurrentUserId());
	}

	// 初始化
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.me_main, container, false);
			initView(rootView);

		} else {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		}

		return rootView;

	}

	private void initView(View view) {

		txtPageTitle = (TextView) view.findViewById(R.id.title_of_the_page);
		txtPageTitle.setText(this.getString(R.string.title_me));

		nickNameTextView = (TextView) view.findViewById(R.id.nickNameTextView);
		editNickNameBtn = (ImageButton) view.findViewById(R.id.editNickNameBtn);
		editNickNameBtn.setOnClickListener(new NicknameIconClickListener());

		signatureHead = (TextView) view.findViewById(R.id.tv_signature);
		signatureHead.setOnClickListener(new SignatureClickListener());

		signatureTextView = (TextView) view.findViewById(R.id.signature);
		signatureTextView.setOnClickListener(new SignatureClickListener());

		layoutMyprofile = (LinearLayout) view
				.findViewById(R.id.layoutMyprofile);
		layoutMyprofile.setOnClickListener(this);

		layoutShareAppComp = (LinearLayout) view
				.findViewById(R.id.layoutShareAppComp);
		layoutShareAppComp.setOnClickListener(this);

		layoutSuggestion = (LinearLayout) view
				.findViewById(R.id.layoutSuggestion);
		layoutSuggestion.setOnClickListener(this);

		layoutAbout = (LinearLayout) view.findViewById(R.id.layoutAbout);
		layoutAbout.setOnClickListener(this);

		settings = (ImageView) view.findViewById(R.id.shezhi);
		settings.setOnClickListener(this);

		scoreTheApp = view.findViewById(R.id.scoreTheApp);
		scoreTheApp.setOnClickListener(this);

		headImageView = (CircleImageView) view.findViewById(R.id.headIcon);

		headImageView.setOnClickListener(new ClickImageToChangeHeadIcon());
		String requestUrl = SettingValues.URL_PREFIX
				+ getString(R.string.URL_USER_INFO_ADD);
		if (userInfo != null) {
//			Log.i("userinfo", "find sqlite");
			if (StringUtils.isNotEmpty(userInfo.getNickName())) {
//				Log.i("userinfo", "find a nickname");
				localNickName = userInfo.getNickName();
				nickNameTextView.setText(localNickName);
				
			} else {
//				Log.i("userinfo", "find no nickname");
				new LoadDataTask1().execute(1, requestUrl, 0,
						HttpClient.TYPE_GET);
			}
			if (StringUtils.isNotEmpty(userInfo.getSignature())) {
				localSignature = userInfo.getSignature();
				signatureTextView.setText(localSignature);
			} else {
				new LoadDataTask1().execute(1, requestUrl, 0,
						HttpClient.TYPE_GET);
			}
			updateHeadIcon();

		} else {
			new LoadDataTask1().execute(1, requestUrl, 0,
					HttpClient.TYPE_GET);
		}

	}

	private void updateHeadIcon() {
		if (userInfo.getPicUrl() != null) {
			String frontStr = SettingValues.URL_PREFIX;
			frontStr = frontStr.substring(0,frontStr.length()-1);
			String headIcnUrl = frontStr
					+ userInfo.getPicUrl();
//			 Log.i("headIcon", headIcnUrl);

			File fileFolder = new File(
					Environment.getExternalStorageDirectory()
							+ SettingValues.PATH_USER_PREFIX);
			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}

			// Program exploit:if there is no external storage,program will
			// crash
			final String target = Environment.getExternalStorageDirectory()
					+ SettingValues.PATH_USER_PREFIX
					+ FilenameUtils.getName(userInfo.getPicUrl());
			File file = new File(target);
			if (!file.exists()) {
				FinalHttp fh = new FinalHttp();
				fh.download(headIcnUrl, null, target + ".temp", false,
						new AjaxCallBack<File>() {
							@Override
							public void onSuccess(File t) {
								if (isAdded()) {
									if (t.renameTo(new File(target))) {

										new RecToCircleTaskInQushejiao()
												.execute(target);
									}
								}
							}

						});
			} else {
				if (CurrentUserHelper.getBitmap() == null) {
					new RecToCircleTaskInQushejiao().execute(target);
				} else {
					headImageView.setImageBitmap(CurrentUserHelper.getBitmap());
					headImageView.setBorderWidth(1);
				}

			}
		}
	}

	private class NicknameIconClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			editNickNameBtn.setEnabled(false);
			intent = new Intent(mainActivity, ModifyNicknameActivity.class);
			localNickName = nickNameTextView.getText().toString();
			if (localNickName != null && !localNickName.equals("")) {
				intent.putExtra(ModifyNicknameActivity.INTENT_NICKNAME,
						localNickName);
			}
			startActivity(intent);
			editNickNameBtn.setEnabled(true);
		}

	}

	private class SignatureClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			signatureTextView.setEnabled(false);
			intent = new Intent(mainActivity, ModifySignatureActivity.class);
			localSignature = signatureTextView.getText().toString();
			if (localSignature != null && !localSignature.equals("")) {
				intent.putExtra(ModifySignatureActivity.INTENT_SIGNATURE,
						localSignature);
			}
			startActivity(intent);
			signatureTextView.setEnabled(true);
		}

	}

	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		Intent intent;
		switch (v.getId()) {
		case R.id.shezhi:
			intent = new Intent(mainActivity, MoreSetting.class);
			startActivity(intent);
			settings.setEnabled(true);
			break;
		case R.id.layoutMyprofile:
			intent = new Intent(mainActivity, UserInfoActivity.class);
			startActivity(intent);
			v.setEnabled(true);
			break;
		case R.id.layoutAbout:
			intent = new Intent(mainActivity, MoreAboutusActivity.class);
			startActivity(intent);
			v.setEnabled(true);
			break;
		// 打分，这里的Uri还是以前的，需要改
		case R.id.scoreTheApp:

			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=com.qubaopen"));

			if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
				startActivity(intent);
			} else {
				showToast(getString(R.string.toast_have_no_market));
			}
			v.setEnabled(true);
			break;
		case R.id.layoutSuggestion:
			intent = new Intent(mainActivity, MoreSuggestionActivity.class);
			startActivity(intent);
			v.setEnabled(true);
			break;
		case R.id.layoutShareAppComp:
			ShareUtil.showShare(getString(R.string.share_title_sharesoft),
					getString(R.string.share_content_sharesoft));
			v.setEnabled(true);
			break;
		default:
			break;

		}
	}

	// 头像转圆
	private class RecToCircleTaskInQushejiao extends
			AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
			Bitmap bitmap = BitmapFactory.decodeFile(urls[0]);
			return RecToCircleTask.transferToCircle(bitmap);
		}

		protected void onPostExecute(Bitmap result) {
			if (isAdded()) {
				CurrentUserHelper.saveBitmap(result);
				headImageView
						.setImageResource(R.drawable.head_white_ring_background);
				headImageView.setImageBitmap(result);
				headImageView.setBorderWidth(1);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		switch (requestCode) {
		case PICK_PIC_FROM_CAMERA_ACTION:
			if (resultCode == Activity.RESULT_OK) {
				startCropImage();
			}
			break;
		case PICK_PIC_FORM_GALLERY_ACTION:
			if (resultCode == Activity.RESULT_OK) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						InputStream inputStream;
						try {
							inputStream = mainActivity.getContentResolver()
									.openInputStream(data.getData());
							FileOutputStream fileOutputStream = new FileOutputStream(
									new File(SettingValues.TEMP_PHOTO_FILE_PATH));
							copyStream(inputStream, fileOutputStream);
							fileOutputStream.close();
							inputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						mainActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								startCropImage();
							}
						});
					}
				}).start();
			}
			break;

		case CROP_IMAGE_ACTION:
			if (resultCode == Activity.RESULT_OK) {

				Intent intent = new Intent(mainActivity,
						CropImageIntentService.class);
				intent.putExtra(CropImage.IMAGE_PATH,
						data.getStringExtra(CropImage.IMAGE_PATH));
				mainActivity.startService(intent);
				if (closePicImageDialogHander != null) {
					closePicImageDialogHander.sendEmptyMessage(0);
				}
			}
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mainActivity.unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUserInfo();
		IntentFilter intentFilter = new IntentFilter(
				CropImageIntentService.IMAGE_UPLOAD_DONE_RECEIVER);
		
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				String success = intent.getStringExtra("success");
//				 Log.i("upload", "成功？。。。。。。" + success);
				if (success.equals("1")) {
					String requestUrl = SettingValues.URL_PREFIX
							+ getString(R.string.URL_USER_INFO_ADD);
					new LoadDataTask1().execute(1, requestUrl, 1,
							HttpClient.TYPE_GET);
					showToast(getString(R.string.toast_upload_photo_success_tips));
				} else {
					showToast(getString(R.string.toast_upload_photo_fail_tips));
				}
			}
		};
		mainActivity.registerReceiver(mReceiver, intentFilter);

		StatService.onResume(this);
	}

	private class LoadDataTask1 extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject result = null;
			Integer syncType = (Integer) params[0];
			try {
				switch (syncType) {
				case 1:
					result = HttpClient.requestSync(params[1].toString(), null,
							(Integer) params[3]);
//					Log.i("userinfo", "个人资料。。。。。。" + result);
					result.put("syncType", syncType);
					result.put("isFirst", (Integer)params[2]);
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
					if (result.getString("success").equals("1")) {
						UserInfoDao userInfoDao = new UserInfoDao();
						userInfoDao.saveUserInfo(result);
						userInfo = userInfoDao.getCurrentUser();
						if (result.getInt("isFirst") == 0) {
							showToast(getString(R.string.toast_get_userinfo_success));
						}
						

						nickName = result.getString("nickName");
//						Log.i("nickname", "昵称......" + nickName);
						if (StringUtils.isNotEmpty(nickName)) {
							nickNameTextView.setText(nickName);
						} else {
							String phone = CurrentUserHelper.getCurrentPhone();
							String str = "手机用户" + phone.substring(7, 11);
							nickNameTextView.setText(str);
						}
						signature = result.getString("signature");
						signatureTextView.setText(signature);

						updateHeadIcon();

					} else {
						showToast(getString(R.string.toast_get_userinfo_failed));
					}
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}

	public void updateUserInfo() {
		userInfo = userInfoDao.getUserById(CurrentUserHelper
				.getCurrentUserId());
		if (userInfo != null) {
			if (userInfo.getNickName() != null) {
				localNickName = userInfo.getNickName();
				nickNameTextView.setText(localNickName);
			}
			if (userInfo.getSignature() != null) {
				localSignature = userInfo.getSignature();
				signatureTextView.setText(localSignature);
			}
		}
	}

	private void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	private void startCropImage() {
		Intent intent = new Intent(mainActivity, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH,
				SettingValues.TEMP_PHOTO_FILE_PATH);
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		startActivityForResult(intent, CROP_IMAGE_ACTION);
	}

	public Handler getClosePicImageDialogHander() {
		return closePicImageDialogHander;
	}

	private class ClickImageToChangeHeadIcon implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ SettingValues.PATH_USER_PREFIX);
			if (!file.exists()) {
				file.mkdirs();
			}
			final Dialog dialog = new Dialog(mainActivity,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.dialog_pick_head_image);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.dimAmount = 0.7f;
			dialog.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setAttributes(lp);
			Button cameraBtn = (Button) dialog
					.findViewById(R.id.dialogCameraBtn);
			Button galleryBtn = (Button) dialog
					.findViewById(R.id.dialogGalleryBtn);
			Button cancelBtn = (Button) dialog
					.findViewById(R.id.dialogCancelBtn);

			closePicImageDialogHander = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					dialog.dismiss();
				}
			};
			// 取消按钮
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();

				}
			});
			// 相机按钮
			cameraBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					try {
						Uri mImageCaptureUri = null;
						String state = Environment.getExternalStorageState();

						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mImageCaptureUri = Uri.fromFile(new File(
									SettingValues.TEMP_PHOTO_FILE_PATH));
						} else {
							mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
						}

						intent.putExtra(
								android.provider.MediaStore.EXTRA_OUTPUT,
								mImageCaptureUri);

						startActivityForResult(intent,
								PICK_PIC_FROM_CAMERA_ACTION);
					} catch (ActivityNotFoundException e) {

						e.printStackTrace();
					}

				}
			});
			// 相册按钮
			galleryBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");

					startActivityForResult(photoPickerIntent,
							PICK_PIC_FORM_GALLERY_ACTION);
				}
			});

			dialog.show();

		}
	}

	private void showToast(String content) {
		Toast.makeText(mainActivity, content, Toast.LENGTH_SHORT).show();
	}
}
