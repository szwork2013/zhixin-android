package com.qubaopen.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalDb;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import com.qubaopen.database.DbManager;
import com.qubaopen.domain.UserInfo;
import com.qubaopen.settings.CurrentUserHelper;

public class UserInfoDao {
	// 保存用户第一次登陆的信息
	public void saveUserForFirsttime(JSONObject jbo, String password)
			throws JSONException, ParseException {
		// Log.i("获取个人资料", jbo+"");
		// 用户名默认是手机号码
		Long userId = jbo.getLong("userId");

		UserInfo user = null;
		final FinalDb db = DbManager.getDatabase();
		// 检查用户信息是否存在
		if (db.tableExists(UserInfo.class)) {
			user = db
					.findUniqueByWhere(UserInfo.class, "userId=" + userId + "");
		}
		if (user == null) {
			user = new UserInfo();
			user.setUserId(userId);
			user.setPassword(password);
			user = saveUserForFirsttime_particial(jbo, user);
			// Log.i("userinfo", user + "");
			db.save(user);
		} else {
			user = saveUserForFirsttime_particial(jbo, user);
			user.setPassword(password);
			db.update(user);
		}

	}

	public void saveUserInfo(JSONObject jbo)
			throws JSONException, ParseException {
		long userId = CurrentUserHelper.getCurrentUserId();

		UserInfo user = null;
		final FinalDb db = DbManager.getDatabase();
		// 检查用户信息是否存在
		if (db.tableExists(UserInfo.class)) {
			user = db
					.findUniqueByWhere(UserInfo.class, "userId=" + userId + "");
		}
		if (user != null) {

			if (StringUtils.isNotEmpty(jbo.getString("avatarPath"))) {
				user.setPicUrl(jbo.getString("avatarPath"));
			}

			if (StringUtils.isNotEmpty(jbo.getString("name"))) {
				user.setName(jbo.getString("name"));
			} else {
				user.setName("");
			}
			// sex 0 for male 1 for female
			if (StringUtils.isNotEmpty(jbo.getString("sex"))) {
				user.setSex(jbo.getInt("sex"));
			} else {
				user.setSex(2);
			}

			if (StringUtils.isNotEmpty(jbo.getString("nickName"))) {
				user.setNickName(jbo.getString("nickName"));
			} else {
				user.setNickName("");
			}

			if (StringUtils.isNotEmpty(jbo.getString("signature"))) {
				user.setSignature(jbo.getString("signature"));
			} else {
				user.setSignature("");
			}

			if (StringUtils.isNotEmpty(jbo.getString("birthday"))) {
				user.setBirthDay(jbo.getString("birthday"));
			} else {
				user.setBirthDay("");
			}

			if (StringUtils.isNotEmpty(jbo.getString("IdCard"))) {
				user.setIdentityNumber(jbo.getString("IdCard"));
			} else {
				user.setIdentityNumber("");
			}

			// '0'=A型 '1'=B型 '2'=O型 '3'=AB型 '4'=其他
			if (StringUtils.isNotEmpty(jbo.getString("bloodType"))) {
				user.setBloodType(jbo.getInt("bloodType"));
			} else {
				user.setBloodType(4);
			}

			if (StringUtils.isNotEmpty(jbo.getString("district"))) {
				user.setDistrict(jbo.getString("district"));
			} else {
				user.setDistrict("");
			}

			if (StringUtils.isNotEmpty(jbo.getString("defaultAddress"))) {
				user.setAddress(jbo.getString("defaultAddress"));
			} else {
				user.setAddress("");
			}

			if (StringUtils.isNotEmpty(jbo.getString("email"))) {
				user.setEmail(jbo.getString("email"));
			} else {
				user.setEmail("");
			}

			DbManager.getDatabase().update(user);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void saveUserIndexInfo(JSONObject jbo) {
		UserInfo user = null;
		final FinalDb db = DbManager.getDatabase();
		user = db.findUniqueByWhere(UserInfo.class, "userId='"
				+ CurrentUserHelper.getCurrentUserId() + "'");

		try {
			if (StringUtils.isNotEmpty(jbo.getString("moodType"))) {
				user.setTodayMood(jbo.getInt("moodType"));
			}
			if (StringUtils.isNotEmpty(jbo.getString("userSelfTitle"))) {
				user.setDesignation(jbo.getString("userSelfTitle"));
			}
			if (StringUtils.isNotEmpty(jbo.getString("deduction"))) {
				user.setDeduction(jbo.getDouble("deduction"));
			}
			if (StringUtils.isNotEmpty(jbo.getString("resolution"))) {
				user.setResolution(jbo.getDouble("resolution"));
			}
			if (StringUtils.isNotEmpty(jbo.getString("isJoined"))) {
				user.setJoined(jbo.getBoolean("isJoined"));
			}
			if (StringUtils.isNotEmpty(jbo.getString("lastTime"))) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = dateFormat.parse(jbo.getString("lastTime"));
				user.setLastTime(date);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		DbManager.getDatabase().update(user);
	}

	private UserInfo saveUserForFirsttime_particial(JSONObject jbo,
			UserInfo user) throws JSONException, ParseException {

		if (jbo.has("phone") && StringUtils.isNotEmpty(jbo.getString("phone"))) {
			user.setUsername(jbo.getString("phone"));
		}

		if (jbo.has("thirdToken")
				&& StringUtils.isNotEmpty(jbo.getString("thirdToken"))) {
			user.setThirdToken(jbo.getString("thirdToken"));
		}

		if (jbo.has("thirdType")
				&& StringUtils.isNotEmpty(jbo.getString("thirdType"))) {
			user.setThirdType(jbo.getString("thirdType"));
		}

		if (jbo.has("avatarPath")
				&& StringUtils.isNotEmpty(jbo.getString("avatarPath"))) {
			user.setPicUrl(jbo.getString("avatarPath"));
		}

		if (jbo.has("name") && StringUtils.isNotEmpty(jbo.getString("name"))) {
			user.setName(jbo.getString("name"));
		} else {
			user.setName("");
		}
		// sex 0 for male 1 for female
		if (jbo.has("sex") && StringUtils.isNotEmpty(jbo.getString("sex"))) {
			user.setSex(jbo.getInt("sex"));
		} else {
			user.setSex(2);
		}

		if (jbo.has("nickName")
				&& StringUtils.isNotEmpty(jbo.getString("nickName"))) {
			user.setNickName(jbo.getString("nickName"));
		} else {
			user.setNickName("");
		}

		if (jbo.has("signature")
				&& StringUtils.isNotEmpty(jbo.getString("signature"))) {
			user.setSignature(jbo.getString("signature"));
		} else {
			user.setSignature("");
		}

		if (jbo.has("birthday")
				&& StringUtils.isNotEmpty(jbo.getString("birthday"))) {
			user.setBirthDay(jbo.getString("birthday"));
		} else {
			user.setBirthDay("");
		}

		// '0'=A型 '1'=B型 '2'=O型 '3'=AB型 '4'=其他
		if (jbo.has("bloodType")
				&& StringUtils.isNotEmpty(jbo.getString("bloodType"))) {
			user.setBloodType(jbo.getInt("bloodType"));
		} else {
			user.setBloodType(4);
		}

		if (jbo.has("district")
				&& StringUtils.isNotEmpty(jbo.getString("district"))) {
			user.setDistrict(jbo.getString("district"));
		} else {
			user.setDistrict("");
		}

		if (jbo.has("defaultAddress")
				&& StringUtils.isNotEmpty(jbo.getString("defaultAddress"))) {
			user.setAddress(jbo.getString("defaultAddress"));
		} else {
			user.setAddress("");
		}

		if (jbo.has("email") && StringUtils.isNotEmpty(jbo.getString("email"))) {
			user.setEmail(jbo.getString("email"));
		} else {
			user.setEmail("");
		}

		if (jbo.has("idCard")
				&& StringUtils.isNotEmpty(jbo.getString("idCard"))) {
			user.setIdentityNumber(jbo.getString("idCard"));
		} else {
			user.setIdentityNumber("");
		}

		return user;

	}

	public void saveUserInfoPassword(String phone, String password) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUsername(phone);
			user.setPassword(password);
			DbManager.getDatabase().save(user);
		} else {
			user.setPassword(password);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoEmail(String phone, String email) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUsername(phone);
			user.setEmail(email);
			DbManager.getDatabase().save(user);
		} else {
			user.setEmail(email);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoEmailById(long id, String email) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setEmail(email);
			DbManager.getDatabase().save(user);
		} else {
			user.setEmail(email);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoSex(String phone, Integer sex) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUsername(phone);
			user.setSex(sex);
			DbManager.getDatabase().save(user);
		} else {
			user.setSex(sex);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoSexById(long id, Integer sex) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setSex(sex);
			DbManager.getDatabase().save(user);
		} else {
			user.setSex(sex);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoBloodType(String phone, Integer type) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUsername(phone);
			user.setBloodType(type);
			DbManager.getDatabase().save(user);
		} else {
			user.setBloodType(type);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoBloodTypeById(long id, Integer type) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			;
			user.setBloodType(type);
			DbManager.getDatabase().save(user);
		} else {
			user.setBloodType(type);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoBirthDay(String phone, String date) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUsername(phone);
			user.setBirthDay(date);
			DbManager.getDatabase().save(user);
		} else {
			user.setBirthDay(date);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoBirthDayById(long id, String date) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setBirthDay(date);
			DbManager.getDatabase().save(user);
		} else {
			user.setBirthDay(date);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoNickNameById(long id, String nickName) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setNickName(nickName);
			DbManager.getDatabase().save(user);
		} else {
			user.setNickName(nickName);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoSignatureById(long id, String signature) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setSignature(signature);
			DbManager.getDatabase().save(user);
		} else {
			user.setSignature(signature);
			DbManager.getDatabase().update(user);
		}
	}

	public void saveUserInfoDefaultAddressById(long id, String defaultAddress) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user == null) {
			user = new UserInfo();
			user.setUserId(id);
			user.setAddress(defaultAddress);
			DbManager.getDatabase().save(user);
		} else {
			user.setAddress(defaultAddress);
			DbManager.getDatabase().update(user);
		}
	}

	public String getUserPasswordByPhone(String phone) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user != null) {
			return user.getPassword();
		} else {
			return null;
		}
	}

	public long getUserIdByPhone(String phone) {
		String sql = "select * from user_info where userId='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user != null) {
			return user.getUserId();
		} else {
			return 0;
		}
	}

	public UserInfo saveUserZhibiInfo(String phone, JSONObject result)
			throws JSONException {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		if (user != null) {
			if (!StringUtils.isEmpty(result.getString("picUrl"))) {
				user.setPicUrl(result.getString("picUrl"));
			}

			if (!StringUtils.isEmpty(result.getString("nickName"))) {
				user.setNickName(result.getString("nickName"));
			}

			if (!StringUtils.isEmpty(result.getString("coin"))) {
				user.setCoin(result.getInt("coin"));
			}

			if (!StringUtils.isEmpty(result.getString("unsend"))) {
				user.setUnsend(result.getInt("unsend"));
			}

			if (!StringUtils.isEmpty(result.getString("qiandaoId"))) {
				user.setQiandaoId(result.getInt("qiandaoId"));
			}
			DbManager.getDatabase().update(user);
		}
		return user;

	}

	public UserInfo getUserByphone(String phone) {
		String sql = "select * from user_info where username='" + phone + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		return user;
	}

	public UserInfo getUserById(Long id) {
		String sql = "select * from user_info where userId='" + id + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		return user;
	}

	public UserInfo getCurrentUser() {
		String sql = "select * from user_info where userId='"
				+ CurrentUserHelper.getCurrentUserId() + "'";
		UserInfo user = DbManager.getDatabase().findUniqueBySql(UserInfo.class,
				sql);
		return user;
	}

}
