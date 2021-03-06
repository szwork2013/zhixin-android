package com.qubaopen.customui;

import java.util.ArrayList;
import java.util.List;

import com.qubaopen.domain.Choices;
import com.qubaopen.domain.Options;
import com.qubaopen.domain.UserQuestionAnswer;
import com.qubaopen.enums.QuestionTypeEnums;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ShunxuViewGroup extends LinearLayout {
	private Context context;

	private ArrayList<ShunxuTitleItem> items;

	public ShunxuViewGroup(Context context, int belowWhat) {
		super(context);
		items = new ArrayList<ShunxuTitleItem>();

		this.context = context;
		setOrientation(HORIZONTAL);

		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.BELOW, belowWhat);

		this.setLayoutParams(layout);

	}

	@Override
	public void addView(View child) {
		super.addView(child);
		items.add((ShunxuTitleItem) child);
	}

	public void addText(Options choices, ShunxuItem shunxuItem) {
		for (ShunxuTitleItem anItem : items) {
			if (!anItem.hasValueYet()) {
				anItem.setCurrentChoices(choices);
				anItem.setBoundedItem(shunxuItem);
				break;
			}
		}

	}

	public List<UserQuestionAnswer> getAnswer() {
		List<UserQuestionAnswer> ans = null;
		if (!isAnswerEmpty()) {
			ans = new ArrayList<UserQuestionAnswer>();
			boolean once = true;

			UserQuestionAnswer userQuestionAnswer;
			for (ShunxuTitleItem anItem : items) {
				userQuestionAnswer = new UserQuestionAnswer();
				userQuestionAnswer.setOptionId(anItem.getCurrentChoices()
						.getOptionId());
				
				userQuestionAnswer.setQuestionId(anItem.getCurrentChoices()
						.getQuestionId());
				userQuestionAnswer.setQuestionType(QuestionTypeEnums.SHUNXU
						.getTypeCode());
				
				userQuestionAnswer.setOptionNum(anItem.getChoiceText());
				ans.add(userQuestionAnswer);
			}
		}
		return ans;

	}

	private boolean isAnswerEmpty() {
		for (ShunxuTitleItem anItem : items) {
			if (!anItem.hasValueYet()) {
				return true;
			}
		}
		return false;
	}

}
