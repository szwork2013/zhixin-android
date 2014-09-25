package com.zhixin.datasynservice;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhixin.R;
import com.zhixin.daos.SelfAnswerDao;
import com.zhixin.domain.SelfUserAnswer;
import com.zhixin.settings.SettingValues;
import com.zhixin.utils.HttpClient;

import android.content.Context;

public class SelfAnswerService {
	private Context context;
	private SelfAnswerDao selfAnswerDao;


	public SelfAnswerService(Context context) {
		this.context = context;
		selfAnswerDao = new SelfAnswerDao();

	}

	
	//提交自测答案， 返回一个答案对象 
	public SelfUserAnswer getAnswer(JSONObject requestParams) throws JSONException {
		String requestUrl = SettingValues.URL_PREFIX
				+ context.getString(R.string.URL_SELF_SUBMIT_QUESTION);

		int wjId = requestParams.getInt("selfId");

		JSONObject result = HttpClient.requestSync(requestUrl, requestParams,HttpClient.TYPE_POST_FORM);
		if (result != null
				&& (!result.has("success") || result.getString("success")
						.equals("1"))) {
			return selfAnswerDao.saveMyAnswer(result, wjId);
		} else if (result != null && result.getString("success").equals("0")) {
			return null;
		}
		return null;
	}


}
