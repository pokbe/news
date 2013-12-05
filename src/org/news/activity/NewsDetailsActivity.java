package org.news.activity;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import org.news.service.SyncHttp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class NewsDetailsActivity extends Activity{
	
	private ViewFlipper mNewsBodyFlipper;
	private LayoutInflater mNewsBodyInflater;
	private int mCount=0;
	private int mPosition = 0;
	private ArrayList<HashMap<String, Object>> mNewsData;
	private int mNid;
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_details);
		
		Button newsDetailsTitlebarPre = (Button)findViewById(R.id.newsdetails_titlebar_previous);
		NewsDetailsTitleBarOnClickListener newsDetailsTitleBarOnClickListener = new NewsDetailsTitleBarOnClickListener();
		newsDetailsTitlebarPre.setOnClickListener(newsDetailsTitleBarOnClickListener);
		Button newsDetailsTitlebarNext = (Button)findViewById(R.id.newsdetails_titlebar_next);
		newsDetailsTitlebarNext.setOnClickListener(newsDetailsTitleBarOnClickListener);
		
		//获取信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		//新闻标题
		String categoryName = bundle.getString("categoryName");
		TextView titleBarTitle = (TextView) findViewById(R.id.newsdetails_titlebar_title);
		titleBarTitle.setText(categoryName);
		//获取位置
		mPosition = bundle.getInt("position");
		//获取新闻信息
		Serializable s  = bundle.getSerializable("newsDate");
		mNewsData = ((ArrayList<HashMap<String, Object>>) s);
		HashMap<String, Object> hashMap = mNewsData.get(mPosition);
		//新闻编号
		mNid = (Integer)hashMap.get("nid");
		
		mNewsBodyInflater = getLayoutInflater();
		View newsBodyLayout = mNewsBodyInflater.inflate(R.layout.newsbody, null);
		TextView newsTitle = (TextView)newsBodyLayout.findViewById(R.id.news_body_title);
		newsTitle.setText(hashMap.get("newslist_item_title").toString());
		TextView newsPtimeAndSource = (TextView)newsBodyLayout.findViewById(R.id.news_body_ptime_source);
		newsPtimeAndSource.setText(hashMap.get("newslist_item_datatime").toString() + "    " + hashMap.get("newslist_item_author").toString());
		TextView newsDetails = (TextView)newsBodyLayout.findViewById(R.id.news_body_details);
		newsDetails.setText(Html.fromHtml(getNewsBody()));
		
		mNewsBodyFlipper = (ViewFlipper)findViewById(R.id.news_body_flipper);
		mNewsBodyFlipper.addView(newsBodyLayout);
	}
	
	class NewsDetailsTitleBarOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			//上一条新闻
			case R.id.newsdetails_titlebar_previous:
				showPrevious();
				break;
			//下一条新闻
			case R.id.newsdetails_titlebar_next:
				showNext();
			default:
				break;
			}
			
		}
	}
	
	private void showNext()
	{
		//设置下一屏动画
		mNewsBodyFlipper.setInAnimation(this, R.anim.push_left_in);
		mNewsBodyFlipper.setOutAnimation(this, R.anim.push_left_out);
		//动态创建新闻视图
		mCount++;
		//由于每一次放到Flipper中的视图是不同的对象，因此必须重新new一个newsBodyLayout
		View newsBodyLayout = mNewsBodyInflater.inflate(R.layout.newsbody, null);
		TextView newsTitle = (TextView)newsBodyLayout.findViewById(R.id.news_body_title);
		newsTitle.setText("新闻标题"+mCount);
		TextView newsPtimeAndSource = (TextView)newsBodyLayout.findViewById(R.id.news_body_ptime_source);
		newsPtimeAndSource.setText("来源：sina      2012-03-12 10:21:22");
		TextView newsDetails = (TextView)newsBodyLayout.findViewById(R.id.news_body_details);
		newsDetails.setText(Html.fromHtml(getNewsBody()));
		//添加触摸时间
		mNewsBodyFlipper.addView(newsBodyLayout);
		//显示下一屏
		mNewsBodyFlipper.showNext();
	}

	private void showPrevious()
	{
		//设置上一屏动画
		mNewsBodyFlipper.setInAnimation(this, R.anim.push_right_in);// 定义下一页进来时的动画
		mNewsBodyFlipper.setOutAnimation(this, R.anim.push_right_out);// 定义当前页出去的动画
		//显示上一屏
		mNewsBodyFlipper.showPrevious();
	}
	
	private String getNewsBody()
	{
		String retStr = "网络连接失败，请稍后再试";
		SyncHttp syncHttp = new SyncHttp();
		String url = "http://10.0.2.2:8080/web/getNews";
		String params = "nid=" + mNid;
		try
		{
			String retString = syncHttp.httpGet(url, params);
			JSONObject jsonObject = new JSONObject(retString);
			//获取返回码，0表示成功
			int retCode = jsonObject.getInt("ret");
			if (0 == retCode)
			{
				JSONObject dataObject = jsonObject.getJSONObject("data");
				JSONObject newsObject = dataObject.getJSONObject("news");
				retStr = newsObject.getString("body");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return retStr;
	}

}

