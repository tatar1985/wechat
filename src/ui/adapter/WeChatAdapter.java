package ui.adapter;

import im.model.HistoryChatBean;

import java.util.List;

import tools.Logger;
import ui.adapter.FriendCardAdapter.CellHolder;

import bean.JsonMessage;
import bean.UserDetail;
import bean.UserInfo;
import cn.sharesdk.wechat.friends.Wechat;

import com.donal.wechat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import config.ApiClent;
import config.ApiClent.ClientCallback;
import config.CommonValue;
import config.Constant;
import config.WCApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeChatAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<HistoryChatBean> inviteUsers;
	private Context context;
	private OnClickListener contacterOnClick;

	static class CellHolder {
		TextView alpha;
		ImageView avatarImageView;
		TextView titleView;
		TextView desView;
		TextView paopao;
		TextView newDate;
	}
	
	public WeChatAdapter(Context context, List<HistoryChatBean> inviteUsers) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.inviteUsers = inviteUsers;
	}

	public void setNoticeList(List<HistoryChatBean> inviteUsers) {
		this.inviteUsers = inviteUsers;
	}

	@Override
	public int getCount() {
		return inviteUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return inviteUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HistoryChatBean notice = inviteUsers.get(position);
		CellHolder cell = null;
		if (convertView == null) {
			cell = new CellHolder();
			convertView = mInflater.inflate(R.layout.friend_card_cell, null);
			cell.alpha = (TextView) convertView.findViewById(R.id.alpha);
			cell.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImageView);
			cell.titleView = (TextView) convertView.findViewById(R.id.title);
			cell.desView = (TextView) convertView.findViewById(R.id.des);
			cell.paopao = (TextView) convertView.findViewById(R.id.paopao);
			cell.newDate = (TextView) convertView.findViewById(R.id.role);
			convertView.setTag(cell);
		} else {
			cell = (CellHolder) convertView.getTag();
		}
		String jid = notice.getFrom();
		Logger.i(jid);
		cell.desView.setTag(jid);
		getUserInfo(jid, cell, notice);
		convertView.setOnClickListener(contacterOnClick);

		return convertView;
	}

	public void setOnClickListener(OnClickListener contacterOnClick) {

		this.contacterOnClick = contacterOnClick;
	}
	
	private void getUserInfo(final String userId, final CellHolder holder, final HistoryChatBean notice) {
		final Integer ppCount = notice.getNoticeSum();
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		String apiKey = sharedPre.getString(Constant.APIKEY, null);
		ApiClent.getUserInfo(apiKey, userId.split("@")[0], new ClientCallback() {
			
			@Override
			public void onSuccess(Object data) {
				UserDetail userInfo = (UserDetail) data;
				holder.titleView.setText(userInfo.userDetail.nickName);
				ImageLoader.getInstance().displayImage(CommonValue.BASE_URL+userInfo.userDetail.userHead, holder.avatarImageView, CommonValue.DisplayOptions.default_options);
				String content = notice.getContent();
				JsonMessage msg = JsonMessage.parse(content);
				holder.desView.setText(msg.text);
				holder.newDate.setText(notice.getNoticeTime().substring(5, 16));
		
				if (ppCount != null && ppCount > 0) {
					holder.paopao.setText(ppCount + "");
					holder.paopao.setVisibility(View.VISIBLE);
		
				} else {
					holder.paopao.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onFailure(String message) {
			
			}
			
			@Override
			public void onError(Exception e) {
				
			}
		});
	}
}