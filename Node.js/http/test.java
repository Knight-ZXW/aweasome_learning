package com.diyidan.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.diyidan.common.DydPreferenceManager;
import com.diyidan.util.ImageManager;
import com.diyidan.widget.floatingview.FloatingActionButton;
import com.diyidan.widget.floatingview.FloatingActionsMenu;

import org.apache.http.HttpStatus;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.diyidan.R;
import com.diyidan.adapter.NewPostAdapter;
import com.diyidan.adapter.NewPostAdapter.OnItemActionClickListner;
import com.diyidan.application.AppApplication;
import com.diyidan.common.Constants;
import com.diyidan.interfaces.INetworkHandler;
import com.diyidan.manager.ShareUtilManager;
import com.diyidan.manager.ShareUtilManager.ShareFinishHandler;
import com.diyidan.model.CatchDanmei;
import com.diyidan.model.ListJsonData;
import com.diyidan.model.JsonData;
import com.diyidan.model.Post;
import com.diyidan.model.SubArea;
import com.diyidan.model.User;
import com.diyidan.model.UserCheckInfo;
import com.diyidan.network.CollectNetwork2;
import com.diyidan.network.LikeNetwork2;
import com.diyidan.network.PostNetwork2;
import com.diyidan.network.UserCheckInfoNetwork2;
import com.diyidan.statistics.DydEventAgent;
import com.diyidan.statistics.EventConstants;
import com.diyidan.statistics.PageNameConstants;
import com.diyidan.statistics.SubjectConstants;
import com.diyidan.util.ImageLoaderUtil;
import com.diyidan.util.Utils;
import com.diyidan.utilbean.MusicPlayStatus;
import com.diyidan.utilbean.ShareQueue;
import com.diyidan.utilbean.ShareQueue.ShareItem;
import com.diyidan.widget.AuditDialog;
import com.diyidan.widget.CommonDialog;
import com.diyidan.widget.DrawableCenterTextView;
import com.diyidan.widget.PinkDialog;
import com.diyidan.widget.RoundImageViewByXfermode;
import com.diyidan.widget.ToastTools;
import com.diyidan.widget.pulltorefresh.PullToRefreshBase;
import com.diyidan.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.diyidan.widget.pulltorefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 个人中心页已发表或已收藏帖子的list界面
 */
public class ShowSubAreaPostsActivity extends BaseActivity implements OnClickListener, OnItemActionClickListner,
														INetworkHandler, AuditDialog.AuditDialogHandler,
														ShareFinishHandler {
	public static final int POST_TYPE_CREATED = 101;
	public static final int POST_TYPE_COLLECTED = 102;
	public static final int POST_TYPE_SHEQU = 103;
	public static final int POST_TYPE_DAILY_HOT = 104;
	
	private PullToRefreshListView mRefreshListView;
	private ListView mPostListView;
	private AuditDialog mAuditDialog;
//	private QuickReturnLayout quickReturnView;
	private NewPostAdapter mPostAdapter;
	private CommonDialog mPostDelDialog;
	private boolean isAlreadyInRefreshing = false;
	private User mUser;
	private boolean isOnlyShowAuditPosts = false;
	private SubArea mSubAreaInfo = null;
	private String mSubAreaName = null;
	private String mSubAreaDesString = null;
	private int mSubAreaFollowCount = 0;
	private int mSubAreaPostCount = 0;
	private String mSubAreaFollowCountString,mSubAreaPostCountString;
	private String mSubAreaUserState;
	private String mSubAreaUserLastCheck;
	private CatchDanmei mSubAreaCatchMe;
	private int nextPage = 1;
	private int curType = POST_TYPE_CREATED;
	private int mPostDelPosition = -1;
//	private Comparator<Post> mComparator;
	private static final int REQUEST_LIKE_A_POST = 113;
	private static final int REQUEST_UNLIKE_A_POST = 114;
	private static final int REQUEST_COLLECT_A_POST = 115;
	private static final int REQUEST_UNCOLLECT_A_POST = 116;
	private static final int REQUEST_AREA_DETAIL    = 117;
	private static final int REQUEST_DELETE_MY_POST    = 118;
	private static final int REQUEST_DELETE_A_POST = 119;
	private static final int REQUEST_LOCK_A_POST = 120;
	private static final int REQUEST_SWITCH_POST_TYPE = 121;
	private static final int REQUEST_CHANGE_POST_AREA = 122;
	private static final int REQUEST_FINISH_POST_AUDIT = 123;
	private static final int REQUEST_SWITCH_POST_CATEGORY = 124;
	private static final int REQUEST_SWITCH_POST_ORIGINAL = 125;
	private static final int REQUEST_SUPPORT_THIS_POST = 126;
	private static final int REQUEST_FOLLOW_SUBAREA = 127;
	private static final int REQUEST_SET_ACTIVE_TIME_A_POST = 128;
	private static final int REQUEST_CHECK_SUBAREA = 129;
	private static final int REQUEST_CATCH_ME = 130;//告知后台捕捉点击
	private static final int REQUEST_CATCH_ME_INFO = 131;//请求捕捉信息
	public static final int REQUEST_SUBAREA_DETAIL_CALL_BACK = 301;
	private static int 	MSG_CATCH_ME_DISAPPEAR = 1;

	private boolean isNoMorePosts = false;
	private int clickedLikePosition = 0, clickedCollectPosition = 0, mAuditPosition = -1;
	private Post mAuditPost;
	private boolean isForArea = false;
	private String category;
	private String mAreaInfo;
	private long mSubAreaId = -1L;
	private String mRequestFrom;
	private long currUserId;
	private ShareQueue mShareQueue;
	private ShareUtilManager mShareUtilManager;
	private RotateAnimation mRefreshAnim;
	private Handler mHandler;

	private FloatingActionButton floatLaunchMusicBtn;
	private FloatingActionButton floatLaunchMusicBtn2;
	private FloatingActionButton floatLaunchImgBtn;
	private FloatingActionButton floatLaunchImgBtn2;
	private FloatingActionButton floatLaunchVoteBtn;
	private FloatingActionsMenu quickReturnView;
	private TextView mSubAreaNameTv, mSubAreaDescription ;
	private RoundImageViewByXfermode mSubAreaIv;
	private LinearLayout mHeaderLayout;
	private boolean mHasHeader = false;
	protected long lastRequestedTime = 0L;
	private Runnable mRefreshAnimCancelRunnable,mCatchMeRunnable,mCatchMeVisibleRunnable,mCatchMeInvisibleRunnable;
	private TextView mSubAreaNumFollow;
	private TextView mSubAreaNumPost;
	private DrawableCenterTextView mJoinCheckButton;
	private List<User> masters,subMasters;
	private ImageView catchMeImage;
	private boolean isRequestCatchMeFirstTime = true;
	private boolean isDialogShowing = false;
	private long startTimeLong = -1L;
	private GifImageView mGigImageView;
	private GifDrawable gifDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_subarea);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_CATCH_ME_DISAPPEAR ){
					if(mGigImageView!=null){
						((RelativeLayout)findViewById(R.id.subarea_body)).removeView(mGigImageView);
						mGigImageView = null;
						gifDrawable = null;
					}
				}
			}
		};
		mRefreshAnimCancelRunnable = new Runnable() {
			@Override
			public void run() {
				mRefreshAnim.cancel();
			}
		};
		mCatchMeRunnable = new Runnable() {
			
			@Override
			public void run() {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_CATCH_ME_INFO).getCatchMe(mSubAreaInfo.getSubAreaName(),"2");
			}
		};
		mCatchMeVisibleRunnable = new Runnable() {
			
			@Override
			public void run() {
				if(catchMeImage!=null) catchMeImage.setVisibility(View.VISIBLE);
			}
		};
		
		mCatchMeInvisibleRunnable = new Runnable() {
			
			@Override
			public void run() {
				if(catchMeImage!=null && !isDialogShowing) ((RelativeLayout)findViewById(R.id.subarea_body)).removeView(catchMeImage);
				catchMeImage = null;
			}
		};
		
		mRefreshListView = (PullToRefreshListView) this.findViewById(R.id.post_subarea_quick_listview);
		quickReturnView = (FloatingActionsMenu) findViewById(R.id.subarea_quick_view);
		floatLaunchMusicBtn = (FloatingActionButton) findViewById(R.id.subarea_float_action_music);
		floatLaunchMusicBtn2 = (FloatingActionButton) findViewById(R.id.subarea_float_action_music_2);
		floatLaunchImgBtn = (FloatingActionButton) findViewById(R.id.subarea_float_action_img);
		floatLaunchImgBtn2 = (FloatingActionButton) findViewById(R.id.subarea_float_action_img_2);
		floatLaunchVoteBtn = (FloatingActionButton) findViewById(R.id.subarea_float_action_vote);

		mRefreshListView.setQuickReturnView(quickReturnView, 1);
		

		floatLaunchMusicBtn.setOnClickListener(this);
		floatLaunchImgBtn.setOnClickListener(this);
		floatLaunchImgBtn2.setOnClickListener(this);
		floatLaunchMusicBtn2.setOnClickListener(this);
		floatLaunchVoteBtn.setOnClickListener(this);

		mRefreshListView.setPullLoadEnabled(false);
		mRefreshListView.setScrollLoadEnabled(true);
		mRefreshListView.setPullRefreshEnabled(false);
		
		this.mUser = ((AppApplication) getApplication()).getUserInfo();

		mSubAreaId = getIntent().getLongExtra("subAreaId", -1L);
		mRequestFrom = getIntent().getExtras().getString("requestFrom");
		
		if (mSubAreaId <= 0L) {
			mAreaInfo = getIntent().getStringExtra("area");
			mSubAreaInfo = (SubArea)getIntent().getSerializableExtra("subAreaData");
			if (mSubAreaInfo != null){
				mSubAreaName = mSubAreaInfo.getSubAreaName();
				mSubAreaDesString = mSubAreaInfo.getSubAreaDescription();
			}
		}
		
		initNaviBar();
		mPostListView = mRefreshListView.getRefreshableView();
		mPostListView.setId(Constants.PULL_TO_REFRESH_LV_ID);
		initLaunchPostAndHeaderView();
		initListView();
		
		getPreviousPostsFromInternet();
	}
	
	private void initNaviBar() {
		// init navigation bar
//		navi = new NavigationBar(this);
//		navi.setVisibility(View.GONE);
//		((FrameLayout) findViewById(android.R.id.content)).addView(navi, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		navi.setLeftLayoutOnClickTransparent();
		navi.setLeftButtonClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
		if (navi !=null) {
	    	if (Utils.isViewReadyForAudit(mUser, mSubAreaName) && Utils.amIMasterOfThisArea(mSubAreaName) == false) {
	    		navi.setLeftAuditVisible(true, isOnlyShowAuditPosts, "审帖");
	    		navi.setLeftAuditClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (isAlreadyInRefreshing) return;
						isOnlyShowAuditPosts = !isOnlyShowAuditPosts;
						navi.setLeftAuditVisible(true, isOnlyShowAuditPosts, "审帖");
						mPostAdapter.clearAllPosts();
						mPostAdapter.notifyDataSetChanged();
						getLatestPostsFromInternet();
					}
				});
	    	}
	    	navi.setRightSecondPlaceVisible(true);
    		navi.setRightSecondPlaceDrawable(R.drawable.pic_icon_fresh);
    		navi.setRightSecondPlaceOnclick(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getLatestPostsFromInternet();
				}
			});
		}
		navi.setRightImage(R.drawable.subarea_detail);
		navi.setRightButtonOnClickTransparent();
		navi.setRightButtonVisible(true);
		

		// init refreshAnim
		mRefreshAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRefreshAnim.setInterpolator(new LinearInterpolator());
		mRefreshAnim.setDuration(1000);
		mRefreshAnim.setRepeatCount(Animation.INFINITE);
		navi.getRightSecondImage().setAnimation(mRefreshAnim);
		
		navi.setRightBtnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentDetail = new Intent(ShowSubAreaPostsActivity.this, ShowSubAreaDetailActivity.class);
				intentDetail.putExtra("masters", (Serializable) masters);
				intentDetail.putExtra("subMasters", (Serializable) subMasters);
				intentDetail.putExtra("subAreaUserState", mSubAreaUserState);
				intentDetail.putExtra("subAreaNme", mSubAreaName);
				intentDetail.putExtra("subAreaDesString", mSubAreaDesString);
				startActivityForResult(intentDetail, REQUEST_SUBAREA_DETAIL_CALL_BACK);
			}
		});
	}
	
	private void initListView() {
		mPostAdapter = new NewPostAdapter(
				this,
				null,
				null,
				this,
				ImageLoaderUtil.getDisplayImageOptions1(),ImageLoaderUtil.getDisplayImageOptions3(),
				NewPostAdapter.SHOW_ELAPSED_UPDATE_TIME);
	    mPostAdapter.setListView(mPostListView);
	    mPostAdapter.setLvHasViewHeader(mHasHeader);
	    if (mSubAreaInfo != null) mPostAdapter.setSubAreaInfo(mSubAreaName);

		if (PageNameConstants.FRAGMENT_ME.equals(mRequestFrom)) {
			mPostAdapter.setCurrentUserId(currUserId);
		}
		
		mPostListView.setAdapter(mPostAdapter);
		mPostAdapter.notifyDataSetChanged();
		mPostListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				onCommentClick((Post)mPostAdapter.getCommonPost(position), position);
			}
		});
		
		mRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				navi.setAlphaValue(0);
				getLatestPostsFromInternet();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				showMoreCachedResults();
			}
		});
		
		mRefreshListView.setNavi(navi);
		
	}
	
	private void initLaunchPostAndHeaderView()  {
		// init launch button
		if (mSubAreaId > 0 || (!Utils.isNull(mAreaInfo) && mSubAreaInfo != null)) {
			isForArea = true;
			navi.setBackTvVisible("", true);
			if("音乐区".equals(mAreaInfo)){
				mRefreshListView.setQuickReturnView(floatLaunchMusicBtn2, 1);
				floatLaunchMusicBtn2.setVisibility(View.VISIBLE);
				quickReturnView.setVisibility(View.GONE);
			}
			if("事务所".equals(mAreaInfo)){
				mRefreshListView.setQuickReturnView(floatLaunchImgBtn2, 1);
				floatLaunchImgBtn2.setVisibility(View.VISIBLE);
				quickReturnView.setVisibility(View.GONE);
			}
		}
		
		if (isForArea && mSubAreaInfo != null) {
			navi.setMidText(mSubAreaName);
			mHasHeader = true;
			initSubAreaHeaderView();
		}
	}
	
	private void getLatestPostsFromInternet() {
		if (isAlreadyInRefreshing) return;
		nextPage = 1;
		mPostListView.setSelection(0);
		mRefreshAnim.start();
		getPreviousPostsFromInternet();
	}
	

	private void initSubAreaHeaderView() {
		View headerView = View.inflate(this, R.layout.post_subarea_header, null);
		mHeaderLayout = (LinearLayout)headerView.findViewById(R.id.ll_subarea_header);
		mHeaderLayout.post(new Runnable() {
			@Override
			public void run() {
				//Log.e("liuxiao","height:"+mHeaderLayout.getHeight());
				//Log.e("liuxiao","width:"+mHeaderLayout.getWidth());
				if(DydPreferenceManager.getInstance(ShowSubAreaPostsActivity.this).getPreferencesBoolean(Constants.IS_USE_GLIDE,false)){
					Glide.with(ShowSubAreaPostsActivity.this)
							.load(mSubAreaInfo.getSubAreaImage()+Constants.UPYUN_IMAGE_BLUR)
							.asBitmap()
							.skipMemoryCache(true)
							.fitCenter()
							.diskCacheStrategy(DiskCacheStrategy.SOURCE)
							.into(new SimpleTarget<Bitmap>(mHeaderLayout.getWidth(), mHeaderLayout.getHeight()) {
								@Override
								public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
									mHeaderLayout.setBackgroundDrawable(new BitmapDrawable(resource));
								}
							});
				}
			}
		});
		mSubAreaIv = (RoundImageViewByXfermode)headerView.findViewById(R.id.iv_subarea_header_avatar);
		mSubAreaNameTv = (TextView)headerView.findViewById(R.id.tv_subarea_name);
//		mSubAreaInfoTv = (TextView)headerView.findViewById(R.id.tv_subarea_more_info);
		mJoinCheckButton = (DrawableCenterTextView) headerView.findViewById(R.id.join_subarea);
		mJoinCheckButton.setOnClickListener(this);
		mSubAreaDescription = (TextView)headerView.findViewById(R.id.tv_subarea_description);
		mSubAreaNumFollow = (TextView) headerView.findViewById(R.id.tv_subarea_num_follow);
		mSubAreaNumPost = (TextView) headerView.findViewById(R.id.tv_subarea_num_post);
		
		updateHeaderInfo();

		headerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Nothing did here, but it avoids clicking on headerview
				// being regarded as clicking pos 0 of listview
			}
		});
		if (mPostListView != null) mPostListView.addHeaderView(headerView);
	}
	
	private void updateHeaderInfo() {
		mSubAreaNameTv.setText(mSubAreaName);
		mSubAreaDescription.setText(mSubAreaDesString);
		mSubAreaIv.setType(RoundImageViewByXfermode.TYPE_ROUND);
		mSubAreaNumFollow.setText("成员: "+mSubAreaFollowCount);
		mSubAreaNumPost.setText("帖子: "+mSubAreaPostCount);
		if(DydPreferenceManager.getInstance(ShowSubAreaPostsActivity.this).getPreferencesBoolean(Constants.IS_USE_GLIDE,false)){
			ImageManager.glideLoadImage(ShowSubAreaPostsActivity.this,Utils.setImageUrlToTiny(mSubAreaInfo.getSubAreaImage()), mSubAreaIv, false);
			//背景的glide显示等后完全显示后执行
			//放到上面那个post里面
			/*Glide.with(ShowSubAreaPostsActivity.this)
					.load(mSubAreaInfo.getSubAreaImage()+Constants.UPYUN_IMAGE_BLUR)
					.asBitmap()
					.skipMemoryCache(true)
					.fitCenter()
					.diskCacheStrategy(DiskCacheStrategy.SOURCE)
					.into(new SimpleTarget<Bitmap>(mHeaderLayout.getWidth(), mHeaderLayout.getHeight()) {
						@Override
						public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
							mHeaderLayout.setBackgroundDrawable(new BitmapDrawable(resource));
						}
					});*/
		}else{
			ImageLoader.getInstance().displayImage(
					Utils.setImageUrlToTiny(mSubAreaInfo.getSubAreaImage()),
					mSubAreaIv, ImageLoaderUtil.getDisplayImageOptions4());
			ImageLoader.getInstance().loadImage(mSubAreaInfo.getSubAreaImage()+Constants.UPYUN_IMAGE_BLUR,ImageLoaderUtil.getDisplayImageOptions6(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							mHeaderLayout.setBackgroundDrawable(new BitmapDrawable(loadedImage));
						}
					});
		}
	}
	

	@Override
	public void onClick(View v) {
		if (((AppApplication) getApplication()).notifyMustLoginIfNecessary()) {
			Utils.jumpToLoginPageWithoutCleanData(ShowSubAreaPostsActivity.this);
			return;
		}
		switch (v.getId()) {
			case R.id.subarea_float_action_img:
			case R.id.subarea_float_action_img_2:
				Intent launchPost = new Intent(ShowSubAreaPostsActivity.this, LaunchPostActivity.class);
				launchPost.putExtra("isCurrentTagAll", true);
				if(mSubAreaName != null && mSubAreaName.equals("二次元动漫")){
					launchPost.putExtra("category", "音乐");
				}else{
					launchPost.putExtra("category", category);
					launchPost.putExtra("postTag", mSubAreaName);
					launchPost.putExtra("postArea", mSubAreaName);
				}
				launchPost.putExtra("type", PostDetailActivity.POST_TYPE_COMMUNITY_POST);
				launchPost.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(launchPost, LaunchPostActivity.CREATE_POST_CALLBACK);

				break;
			case R.id.subarea_float_action_music:
			case R.id.subarea_float_action_music_2:
				Intent launchMusicPost = new Intent(ShowSubAreaPostsActivity.this, LaunchMusicPostActivity.class);
				launchMusicPost.putExtra("postTag", mSubAreaName);
				launchMusicPost.putExtra("postArea", mSubAreaName);
				startActivityForResult(launchMusicPost, LaunchMusicPostActivity.CREATE_MUSIC_POST_CALLBACK);
				break;
			case R.id.subarea_float_action_vote:
				if (mUser.getUserLevel() < Constants.USER_MIN_LEVEL_FOR_VOTE_POST) {
					ToastTools.toast(ShowSubAreaPostsActivity.this, "投票功能公测中，" + Constants.USER_MIN_LEVEL_FOR_VOTE_POST + "级以上用户才能创建哟 (￣y▽￣)~*", Toast.LENGTH_LONG, true);
					return;
				}
				Intent launchVotePost = new Intent(ShowSubAreaPostsActivity.this, LaunchVotePostActivity.class);
				launchVotePost.putExtra("isCurrentTagAll", true);
				if(mSubAreaName != null && mSubAreaName.equals("二次元动漫")){
					launchVotePost.putExtra("category", "音乐");
				}else{
					launchVotePost.putExtra("category", category);
					launchVotePost.putExtra("postTag", mSubAreaName);
					launchVotePost.putExtra("postArea", mSubAreaName);
				}
				launchVotePost.putExtra("type", PostDetailActivity.POST_TYPE_COMMUNITY_POST);
				launchVotePost.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(launchVotePost, LaunchVotePostActivity.CREATE_VOTE_POST_CALLBACK);
				break;
			case R.id.join_subarea:
				if(mSubAreaUserState!=null){
					if(mSubAreaUserState.equals("unfollowed")){
						//关注
						new UserCheckInfoNetwork2(this, REQUEST_FOLLOW_SUBAREA).followSubArea(mSubAreaName);
					}else{
						if(mSubAreaUserLastCheck!=null){
							if(Utils.getCurrentDate().equals(mSubAreaUserLastCheck)){
								//已签到
								ToastTools.toastWithFrequencyLimited(ShowSubAreaPostsActivity.this, "大大已经签过到哟~", Toast.LENGTH_SHORT, false);
							}else{
								//签到
								new UserCheckInfoNetwork2(this, REQUEST_CHECK_SUBAREA).checkSubArea(mSubAreaName);
							}
						}else{
							new UserCheckInfoNetwork2(this, REQUEST_CHECK_SUBAREA).checkSubArea(mSubAreaName);
						}
					}
				}
				break;
			default:
				break;
		}

	}
	
	private void updateJoinCheckState(){
		if(mSubAreaUserState==null)return;
		if("unfollowed".equals(mSubAreaUserState)){
			//关注
			mJoinCheckButton.setText("加入");
			mJoinCheckButton.setVisibility(View.VISIBLE);
			Drawable drawable = getResources().getDrawable(R.drawable.subarea_join);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			mJoinCheckButton.setCompoundDrawables(drawable, null, null, null);
			mJoinCheckButton.setBackgroundResource(R.drawable.green_btn_bg);
			mJoinCheckButton.setTextColor(getResources().getColor(R.color.white));
		}else{
			if(mSubAreaUserLastCheck!=null){
				if(Utils.getCurrentDate().equals(mSubAreaUserLastCheck)){
					//已签到
					mJoinCheckButton.setVisibility(View.GONE);
//					mJoinCheckButton.setText("已签到");
//					Drawable drawable = getResources().getDrawable(R.drawable.subarea_checked);
//					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//					mJoinCheckButton.setCompoundDrawables(drawable, null, null, null);
//					mJoinCheckButton.setBackgroundResource(R.drawable.round_red_bg);
//					mJoinCheckButton.setTextColor(getResources().getColor(R.color.main_green));
				}else{
					//签到
					mJoinCheckButton.setVisibility(View.VISIBLE);
					mJoinCheckButton.setText("签到");
					Drawable drawable = getResources().getDrawable(R.drawable.subarea_check);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					mJoinCheckButton.setCompoundDrawables(drawable, null, null, null);
					mJoinCheckButton.setBackgroundResource(R.drawable.green_btn_bg);
					mJoinCheckButton.setTextColor(getResources().getColor(R.color.white));
				}
			} else {
				// 签到
				mJoinCheckButton.setVisibility(View.VISIBLE);
				mJoinCheckButton.setText("签到");
				Drawable drawable = getResources().getDrawable( R.drawable.subarea_check);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
				mJoinCheckButton.setCompoundDrawables(drawable, null, null,null);
				mJoinCheckButton.setBackgroundResource(R.drawable.green_btn_bg);
				mJoinCheckButton.setTextColor(getResources().getColor(R.color.white));
			}
		}
	}

	
	private void getPreviousPostsFromInternet() {
		if (isAlreadyInRefreshing) return;
		if (isForArea) {
			isAlreadyInRefreshing = true;
			
			if (mSubAreaId > 0L) {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_AREA_DETAIL).getPostsOfArea(
						mSubAreaId,
						nextPage,
						Constants.POST_REQUEST_NUM_FROM_INTERNET,
						isOnlyShowAuditPosts,true);
			} else if (!Utils.isNull(mSubAreaName)) {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_AREA_DETAIL).getPostsOfArea(
						mAreaInfo,
						mSubAreaName,
						nextPage,
						Constants.POST_REQUEST_NUM_FROM_INTERNET,
						isOnlyShowAuditPosts,true);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		navi.setAlphaValue(1);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data==null)return;
		if (requestCode == PostDetailActivity.POST_DETAIL_ACTIVITY_CALL_BACK && data != null) {
			boolean isPostDeleted = data.getBooleanExtra("isPostDeleted", false);
			boolean isTopItem = data.getBooleanExtra("isTopPost", false);
			int postPosition = data.getIntExtra("postPosition", Integer.MIN_VALUE);
			if (isPostDeleted) {
				mPostDelPosition = postPosition;
				removeAPost(mPostDelPosition);
			} else {
				boolean isModified = data.getBooleanExtra("isPostModified", false);
				if(isModified && postPosition >= 0 ){
					if(isTopItem){
						Post post = (Post) data.getSerializableExtra("post");
						mPostAdapter.updateTopPost(postPosition, post);
						mPostAdapter.notifyDataSetChanged();
					}else{
						Post post = (Post) data.getSerializableExtra("post");
						mPostAdapter.updatePost(postPosition, post);
						mPostAdapter.notifyDataSetChanged();
					}
				}

				MusicPlayStatus playStatus = (MusicPlayStatus)data.getSerializableExtra("musicPlayStatus");
				if (playStatus != null && playStatus.status != MusicPlayStatus.MUSIC_NEVER_STARTED) {
//					playStatus.playIdx = postPosition;
					mPostAdapter.setMusicPlayStatus(playStatus);
					mPostAdapter.notifyDataSetChanged();
				}
			}
			//如果超过下次请求的时间并且mHandler 与 catchMeRunnable 不为null时，重新请求
			if(mHandler!=null && mCatchMeRunnable!=null && mSubAreaCatchMe!=null && mSubAreaInfo!=null && startTimeLong > 0L){
				if(startTimeLong + mSubAreaCatchMe.getCatchDelay() < System.currentTimeMillis()){
					new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_CATCH_ME_INFO).getCatchMe(mSubAreaInfo.getSubAreaName(),"2");
				}
				
			}
			
		}else if (requestCode == LaunchPostActivity.CREATE_POST_CALLBACK
				|| requestCode == LaunchMusicPostActivity.CREATE_MUSIC_POST_CALLBACK
				|| requestCode == LaunchVotePostActivity.CREATE_VOTE_POST_CALLBACK) {
			boolean isPostNull = data.getBooleanExtra("isNull", true);
			if (!isPostNull) {
				Post createdPost = (Post) data.getSerializableExtra("post");
				mPostAdapter.insertOnePostToHead(createdPost);
				mPostAdapter.notifyDataSetChanged();
				mPostListView.setSelection(0);
			}
			List<ShareItem> itemList = (List<ShareItem>) data.getSerializableExtra("sharePostList");
			if (!Utils.isListEmpty(itemList)) {
				iterateShareItems(itemList);
			}
		}
		else if(requestCode ==  REQUEST_SUBAREA_DETAIL_CALL_BACK){
			String state = data.getStringExtra("subAreaUserState");
			if(state!=null && !state.equals(mSubAreaUserState)){
				mSubAreaUserState = state;
				updateJoinCheckState();
			}
		}
		else if (mShareUtilManager != null && mShareUtilManager.mTencent != null){
			if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE || requestCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE) {
				if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
//						Tencent.handleResultData(data, mShareUtilManager.listener);
					mShareUtilManager.mTencent.onActivityResultData(requestCode,resultCode,data,mShareUtilManager.mShareToQQRelatedListener);
				}
			}
		} 

	}
	
	public void iterateShareItems(List<ShareItem> itemList) {
		if (!Utils.isListEmpty(itemList)) {
			mShareQueue = new ShareQueue(itemList);
			if (mShareQueue.hasNext()) {
				mShareUtilManager = new ShareUtilManager();
				mShareUtilManager.setShareFinishHandler(ShowSubAreaPostsActivity.this);
				mShareQueue.shareNextItem(ShowSubAreaPostsActivity.this, mShareUtilManager);
			}
		}
	}

	@Override
	public void onZanClick(Post post, int position) {
		if (!isOperationFrequencyOkay()) return;
		if (((AppApplication) getApplication()).notifyMustLoginIfNecessary()) {
			Utils.jumpToLoginPageWithoutCleanData(ShowSubAreaPostsActivity.this);
			return;
		}
		
		clickedLikePosition = position;
		int type = PostDetailActivity.POST_TYPE_COMMUNITY_POST;
        if (Post.POST_TYPE_TRADE.equals(post.getPostType())) type = PostDetailActivity.POST_TYPE_TRADE_POST;
		if (post.isPostIsUserLikeIt()) {
			new LikeNetwork2(ShowSubAreaPostsActivity.this, REQUEST_UNLIKE_A_POST).unlikeAPost(post.getPostId(), type);
		} else {
			new LikeNetwork2(ShowSubAreaPostsActivity.this, REQUEST_LIKE_A_POST).likeAPost(post.getPostId(), type);
		}
	}
	
	@Override
	public void onCollectClick(Post post, int position) {
		if (!isOperationFrequencyOkay()) return;
		if (((AppApplication) getApplication()).notifyMustLoginIfNecessary()) {
			Utils.jumpToLoginPageWithoutCleanData(ShowSubAreaPostsActivity.this);
			return;
		}
		
		clickedCollectPosition = position;
		int type = PostDetailActivity.POST_TYPE_COMMUNITY_POST;
        if (Post.POST_TYPE_TRADE.equals(post.getPostType())) type = PostDetailActivity.POST_TYPE_TRADE_POST;
		
		if (post.isPostIsUserCollectIt()) {
			new CollectNetwork2(ShowSubAreaPostsActivity.this, REQUEST_UNCOLLECT_A_POST).uncollectAPost(post.getPostId(), type);
		} else {
			new CollectNetwork2(ShowSubAreaPostsActivity.this, REQUEST_COLLECT_A_POST).collectAPost(post.getPostId(), type);
		}
	}

	@Override
	public void onCommentClick(Post post, int position) {
		if (post == null || mPostAdapter == null) return;
		if (Post.POST_TYPE_LINK.equals(post.getPostType())) {
			Intent intent = new Intent(ShowSubAreaPostsActivity.this, CustomBrowserActivity.class);
			intent.putExtra("url", post.getPostLink());
			intent.putExtra("requestFrom", getPageName());
			startActivity(intent);
			return;
		}
		
//		int relPos = getRealClickedPosition(position); // do not need
		if (post.getPostVideo()!=null) mPostAdapter.setMusicPlayStatus(false);
		Intent intent = new Intent(ShowSubAreaPostsActivity.this, PostDetailActivity.class);
        intent.putExtra("post", post);
        intent.putExtra("areaName",mAreaInfo);
        intent.putExtra("subAreaName", mSubAreaName);
        boolean isCurTopPost = false;
        int tmpPos = position + mPostAdapter.getTopPostSize();
        if (tmpPos >=0 && tmpPos < mPostAdapter.getTopPostSize()) {
        	Post tPost = mPostAdapter.getTopPost(tmpPos);
        	if (tPost != null && post.getPostId() == tPost.getPostId()) {
        		isCurTopPost = true;
        	}
        }
        if (isCurTopPost) {
    		intent.putExtra("isTopPost", true);
            intent.putExtra("postPosition", tmpPos);
        } else {
    		intent.putExtra("isTopPost", false);
            intent.putExtra("postPosition", position);
        }
        int type = PostDetailActivity.POST_TYPE_COMMUNITY_POST;
        if (Post.POST_TYPE_TRADE.equals(post.getPostType())) type = PostDetailActivity.POST_TYPE_TRADE_POST;
        intent.putExtra("type", type);
		intent.putExtra("requestFrom", getPageName()); // ShowLatestPostActivity stands for many sources
        MusicPlayStatus playStatus = mPostAdapter.getMusicPlayStatus();
        intent.putExtra("isThisMusicChosen", playStatus.playIdx == position);
        intent.putExtra("musicPlayStatus", playStatus);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); // now onNewIntent() will be called when reuse PostDetailActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, PostDetailActivity.POST_DETAIL_ACTIVITY_CALL_BACK);
	}

	@Override
	public void onItemClick(Post post, int position) {
		onCommentClick(post, position);
	}

	@Override
	public void onPersonClick(Post post, int position) {
//		int relPos = getRealClickedPosition(position);
		Intent intent = new Intent(ShowSubAreaPostsActivity.this, MeActivity.class);
		intent.putExtra("userName", post.getPostAuthor().getNickName());
		intent.putExtra("userId", post.getPostAuthor().getUserId());
		intent.putExtra("userAvatar", post.getPostAuthor().getAvatar());
		intent.putExtra("userPoints", post.getPostAuthor().getUserExp());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void stopRefreshPostList() {
		mRefreshListView.onPullDownRefreshComplete();
		mRefreshListView.onPullUpRefreshComplete();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void networkCallback(Object jsonResult, int httpCode, int requestTag) {
		
		if (httpCode == HttpStatus.SC_FORBIDDEN) {
			((AppApplication)getApplication()).userNotLogin();
			return;
		}
		
		if (httpCode != HttpStatus.SC_OK) {
            Log.e("Volley", "HTTP Code " + httpCode + " catched in callback!!");
            String toastString;
            if (httpCode == HttpStatus.SC_CONFLICT) {
            	toastString = getString(R.string.parse_data_failed);
            } else if (httpCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				toastString = getString(R.string.error_occur_retry_later);
			} else toastString = getString(R.string.connect_to_server_failed);
			ToastTools.toastWithFrequencyLimited(ShowSubAreaPostsActivity.this, toastString, Toast.LENGTH_SHORT, true);
			return;
		}
		if (requestTag == REQUEST_FOLLOW_SUBAREA) {
			mSubAreaUserState = "followed";
			ToastTools.toast(this, "加入成功(ง •̀_•́)ง", Toast.LENGTH_SHORT, false);
			UserCheckInfo info = ((JsonData<UserCheckInfo>) jsonResult).getData();
			mSubAreaUserLastCheck = info.getUserLastCheckDate();
			updateJoinCheckState();
			return;
		}else if (requestTag == REQUEST_CHECK_SUBAREA) {
			UserCheckInfo info = ((JsonData<UserCheckInfo>) jsonResult).getData();
			int userExp = info.getUserExp();
			int userCandy = info.getUserCandy();
			if (userCandy > 0){
				showExperienceCount("连续签到第" + info.getUserContinuedCheckTime() + "天, 糖果值 +" + userCandy);
			} else if (userExp > 0){
				showExperienceCount("连续签到第" + info.getUserContinuedCheckTime() + "天, 经验值 +" + userExp);
			}else{
				if(info!=null && info.getUserContinuedCheckTime()>0){
					showExperienceCount("连续签到第" + info.getUserContinuedCheckTime()+ "天");
				}
			}
			mSubAreaUserLastCheck = Utils.getCurrentDate();
			updateJoinCheckState();
			return;
		} else if (requestTag == REQUEST_CATCH_ME) {
			final PinkDialog pinkDialog = new PinkDialog(ShowSubAreaPostsActivity.this);
			pinkDialog.show();
			pinkDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					isDialogShowing = false;
					if(catchMeImage!=null)((RelativeLayout)findViewById(R.id.subarea_body)).removeView(catchMeImage);
					catchMeImage = null;
					if(mGigImageView==null)mGigImageView = new GifImageView(ShowSubAreaPostsActivity.this);
					float density = Utils.getDensity(ShowSubAreaPostsActivity.this);
					int iconHeight = (int)(mSubAreaCatchMe.getCatchIconHeight()*density/2);
					int iconWidth = (int)(mSubAreaCatchMe.getCatchIconWidth()*density/2);
					LayoutParams layoutP = new LayoutParams(iconHeight,iconHeight);
					int left = (int)(Utils.getDeviceWidth(ShowSubAreaPostsActivity.this)*mSubAreaCatchMe.getCatchIconLeft() + iconWidth - iconHeight);
					int top = (int)(Utils.getDeviceHeight(ShowSubAreaPostsActivity.this)*mSubAreaCatchMe.getCatchIconTop());
					if(left <0 )left =0;
					if(top <0 ) top = 0;
					layoutP.setMargins(left,top , 0, 0);
					mGigImageView.setLayoutParams(layoutP);
					try {
						if(gifDrawable ==null) gifDrawable = new pl.droidsonroids.gif.GifDrawable(getResources(), R.drawable.catch_me_disappear);
						gifDrawable.setLoopCount(1);
						mGigImageView.setImageDrawable(gifDrawable);
						if(gifDrawable!=null)gifDrawable.start();
						((RelativeLayout)findViewById(R.id.subarea_body)).addView(mGigImageView);
						Thread gifThread= new Thread(new Runnable() {
							@Override
							public void run() {
								boolean isGifRunning = true;
								long mGifStart = android.os.SystemClock.uptimeMillis();
								while (isGifRunning) {
						                long now = android.os.SystemClock.uptimeMillis();
						                if (mGifStart > 0 && gifDrawable!=null && now - mGifStart > gifDrawable.getDuration()) {
						                    isGifRunning = false;
						                    Log.e("catchme", "diff" + (now - mGifStart ));
						                    Log.e("catchme", "duration" + gifDrawable.getDuration());
						                }
						            }
								mHandler.sendEmptyMessage(MSG_CATCH_ME_DISAPPEAR);
						        }
						});
						gifThread.start();
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			isDialogShowing = true;
			pinkDialog.setTitle(mSubAreaCatchMe.getCatchTitle()).setMessageText(mSubAreaCatchMe.getCatchContent()).setButton_OK_OnClickListener(new OnClickListener() {
				

				@Override
				public void onClick(View v) {
					pinkDialog.dismiss();
					isDialogShowing = false;
					if(catchMeImage!=null)((RelativeLayout)findViewById(R.id.subarea_body)).removeView(catchMeImage);
					catchMeImage = null;
					mGigImageView = new GifImageView(ShowSubAreaPostsActivity.this);
					float density = Utils.getDensity(ShowSubAreaPostsActivity.this);
					int iconHeight = (int)(mSubAreaCatchMe.getCatchIconHeight()*density/2);
					int iconWidth = (int)(mSubAreaCatchMe.getCatchIconWidth()*density/2);
					LayoutParams layoutP = new LayoutParams(iconHeight,iconHeight);
					int left = (int)(Utils.getDeviceWidth(ShowSubAreaPostsActivity.this)*mSubAreaCatchMe.getCatchIconLeft() + iconWidth - iconHeight);
					int top = (int)(Utils.getDeviceHeight(ShowSubAreaPostsActivity.this)*mSubAreaCatchMe.getCatchIconTop());
					if(left <0 )left =0;
					if(top <0 ) top = 0;
					layoutP.setMargins(left,top , 0, 0);
					mGigImageView.setLayoutParams(layoutP);
					try {
					 	if(gifDrawable==null)gifDrawable = new pl.droidsonroids.gif.GifDrawable(getResources(), R.drawable.catch_me_disappear);
						gifDrawable.setLoopCount(1);
						if(gifDrawable!=null)gifDrawable.start();
						mGigImageView.setImageDrawable(gifDrawable);
						((RelativeLayout)findViewById(R.id.subarea_body)).addView(mGigImageView);
						Thread gifThread= new Thread(new Runnable() {
							@Override
							public void run() {
								boolean isGifRunning = true;
								long mGifStart = android.os.SystemClock.uptimeMillis();
								while (isGifRunning) {
						                long now = android.os.SystemClock.uptimeMillis();
						                if (mGifStart > 0 && gifDrawable!=null && now - mGifStart > gifDrawable.getDuration()) {
						                    Log.e("catchme", "diff" + (now - mGifStart ));
						                    Log.e("catchme", "duration" + gifDrawable.getDuration());
						                    isGifRunning = false;
						                }
						            }
								mHandler.sendEmptyMessage(MSG_CATCH_ME_DISAPPEAR);
						        }
						});
						gifThread.start();
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			if(DydPreferenceManager.getInstance(ShowSubAreaPostsActivity.this).getPreferencesBoolean(Constants.IS_USE_GLIDE,false)){
				ImageManager.glideLoadImage(ShowSubAreaPostsActivity.this,mSubAreaCatchMe.getCatchTitleUrl(), pinkDialog.getHeaderDecorateImageView(),false);
			}else{
				ImageLoader.getInstance().displayImage(mSubAreaCatchMe.getCatchTitleUrl(), pinkDialog.getHeaderDecorateImageView());
			}
			return;
		}
		
		JsonData<ListJsonData> jResult = (JsonData<ListJsonData>)jsonResult;
		if (jResult.getCode() != Constants.CODE_SUCCESS) {
			Log.e("Volley", jResult.getMessage() == null ? "" : jResult.getMessage());
			ToastTools.toastWithFrequencyLimited(ShowSubAreaPostsActivity.this, jResult.getMessage(), Toast.LENGTH_SHORT, true);
			return;
		}
		
		if (jResult.getData().size() == 0  && requestTag == REQUEST_AREA_DETAIL) {
			isAlreadyInRefreshing = false;
			isNoMorePosts = true;
			mRefreshListView.setHasMoreData(false);
			mSubAreaFollowCount = jResult.getData().getSubAreaUserCount();
			mSubAreaPostCount = jResult.getData().getSubAreaPostCount();
			mSubAreaUserState = jResult.getData().getSubAreaUserStatus();
			mSubAreaUserLastCheck = jResult.getData().getSubAreaUserLastCheckDate();

			mSubAreaFollowCountString = jResult.getData().getSubAreaUserCountName();
			mSubAreaPostCountString = jResult.getData().getSubAreaPostCountName();
			if (nextPage == 1 && mRefreshAnim.hasStarted())  {
				mHandler.postDelayed(mRefreshAnimCancelRunnable, 1000);
			}
			if (nextPage == 1 ) {
				if (Utils.isNull(mSubAreaName)) {
					mSubAreaName = jResult.getData().getSubAreaName();
					mSubAreaDesString = jResult.getData().getSubAreaDescription();
					String imageUrl = jResult.getData().getSubAreaImage();
					mSubAreaInfo = new SubArea();
					mSubAreaInfo.setSubAreaImage(imageUrl);
					mSubAreaInfo.setSubAreaName(mSubAreaName);
					mSubAreaInfo.setSubAreaDescription(mSubAreaDesString);

					if (isForArea && mPostListView != null && mPostListView.getHeaderViewsCount() == 0) {
						navi.setMidText(mSubAreaName);
						mHasHeader = true;
						initSubAreaHeaderView();
					}
				} else {
					if (!Utils.isNull(mSubAreaFollowCountString)) Utils.setTextViewNumber(mSubAreaNumFollow, mSubAreaFollowCount,mSubAreaFollowCountString+ ": ");
					if (!Utils.isNull(mSubAreaPostCountString)) Utils.setTextViewNumber(mSubAreaNumPost, mSubAreaPostCount,mSubAreaPostCountString +": ");
				}
				updateJoinCheckState();
			}
			stopRefreshPostList();
			return;
		}

		if (requestTag == REQUEST_AREA_DETAIL) {
			List<Post> newPosts = jResult.getData().getPostList();
			List<Post> topPosts = jResult.getData().getTopPostList();
			masters  = jResult.getData().getMasterUserList();
			subMasters = jResult.getData().getSubMasterUserList();

			mSubAreaFollowCount = jResult.getData().getSubAreaUserCount();
			mSubAreaPostCount = jResult.getData().getSubAreaPostCount();
			mSubAreaUserState = jResult.getData().getSubAreaUserStatus();
			mSubAreaUserLastCheck = jResult.getData().getSubAreaUserLastCheckDate();
			mSubAreaFollowCountString = jResult.getData().getSubAreaUserCountName();
			mSubAreaPostCountString = jResult.getData().getSubAreaPostCountName();
			
			if (nextPage == 1) {
				if (Utils.isNull(mSubAreaName)) {
					mSubAreaName = jResult.getData().getSubAreaName();
					mSubAreaDesString = jResult.getData().getSubAreaDescription();
					String imageUrl = jResult.getData().getSubAreaImage();
					mSubAreaInfo = new SubArea();
					mSubAreaInfo.setSubAreaImage(imageUrl);
					mSubAreaInfo.setSubAreaName(mSubAreaName);
					mSubAreaInfo.setSubAreaDescription(mSubAreaDesString);

					if (isForArea && mPostListView != null && mPostListView.getHeaderViewsCount() == 0) {
						navi.setMidText(mSubAreaName);
						mHasHeader = true;
						initSubAreaHeaderView();
					}
				} else {
					if (!Utils.isNull(mSubAreaFollowCountString)) Utils.setTextViewNumber(mSubAreaNumFollow, mSubAreaFollowCount,mSubAreaFollowCountString+ ": ");
					if (!Utils.isNull(mSubAreaPostCountString)) Utils.setTextViewNumber(mSubAreaNumPost, mSubAreaPostCount,mSubAreaPostCountString +": ");
					updateJoinCheckState();
				}
				if (mRefreshAnim.hasStarted()) mHandler.postDelayed(mRefreshAnimCancelRunnable, 1000);
				if(isRequestCatchMeFirstTime) {
					new PostNetwork2(this, REQUEST_CATCH_ME_INFO).getCatchMe(mSubAreaInfo.getSubAreaName(),"2");
					isRequestCatchMeFirstTime = false;
				}
			}

			if(nextPage>1){
				mPostAdapter.appendPostsToTail(newPosts);
				mPostAdapter.appendTopPostsToTail(topPosts);
				mPostAdapter.notifyDataSetChanged();
			}else{
				mPostAdapter.clearAllPosts();
				mPostAdapter.clearAllTopPosts();
				mPostAdapter.appendPostsToTail(newPosts);
				mPostAdapter.appendTopPostsToTail(topPosts);
				mPostAdapter.notifyDataSetChanged();
			}
			
			nextPage++; // add the next page by 1
			if (newPosts.size() == 0) isNoMorePosts = true;
			isAlreadyInRefreshing = false;
			showMoreCachedResults();
			stopRefreshPostList();
		} else if (requestTag == REQUEST_CATCH_ME_INFO){
			mSubAreaCatchMe = jResult.getData().getCatchMe();
			if(mHandler!=null && mCatchMeInvisibleRunnable!=null)mHandler.removeCallbacks(mCatchMeInvisibleRunnable);
			if(mSubAreaCatchMe!=null) showCatchMe();
		}else if (requestTag == REQUEST_LIKE_A_POST) {
			mPostAdapter.likeThisPost(clickedLikePosition, true);
			ToastTools.toast(this, "点赞成功！", Toast.LENGTH_SHORT, false);
		} else if (requestTag == REQUEST_UNLIKE_A_POST) {
			mPostAdapter.likeThisPost(clickedLikePosition, false);
		} else if (requestTag == REQUEST_COLLECT_A_POST) {
			mPostAdapter.collectThisPost(clickedCollectPosition, true);
			ToastTools.toast(this, "收藏成功！", Toast.LENGTH_SHORT, false);
		} else if (requestTag == REQUEST_UNCOLLECT_A_POST) {
			mPostAdapter.collectThisPost(clickedCollectPosition, false);
		} else if (requestTag == REQUEST_DELETE_MY_POST) {
			removeAPost(mPostDelPosition);
		} else if (requestTag == REQUEST_DELETE_A_POST) {
			removeAPost(mAuditPosition);
		} else if (requestTag == REQUEST_FINISH_POST_AUDIT || requestTag == REQUEST_LOCK_A_POST
				|| requestTag == REQUEST_CHANGE_POST_AREA || requestTag == REQUEST_SET_ACTIVE_TIME_A_POST ) {
			List<Post> newPosts = jResult.getData().getPostList();
			ToastTools.toast(ShowSubAreaPostsActivity.this, "操作成功！", Toast.LENGTH_SHORT, false);
			if (Utils.isListEmpty(newPosts) == false) {
				mPostAdapter.updatePost(mAuditPosition, newPosts.get(0));
				mPostAdapter.notifyDataSetChanged();
			}
		} else if (requestTag == REQUEST_SWITCH_POST_TYPE) {
			removeAPost(mAuditPosition);
		}
	}
	
	private void removeAPost(int position) {
		mPostAdapter.removeAPost(position);
		mPostListView.setAdapter(mPostAdapter);
		mPostAdapter.setListView(mPostListView);
		mPostAdapter.notifyDataSetChanged();
		mPostListView.setSelection(position);
	}
	
	private void showCatchMe(){
		//解析时间，如果不在时间范围内退出
		long timeBeforeStart = mSubAreaCatchMe.getCatchStartTimeDelta();
		long timeBeforeEnd = mSubAreaCatchMe.getCatchEndTimeDelta();
		startTimeLong = System.currentTimeMillis();
		if(timeBeforeStart >0 &&  timeBeforeEnd > 0 || timeBeforeStart <= 0){
			if(catchMeImage!=null) {
				catchMeImage.setVisibility(View.VISIBLE);
			}else{
				catchMeImage = new ImageView(ShowSubAreaPostsActivity.this);
				if(DydPreferenceManager.getInstance(ShowSubAreaPostsActivity.this).getPreferencesBoolean(Constants.IS_USE_GLIDE,false)){
					ImageManager.glideLoadImage(ShowSubAreaPostsActivity.this,mSubAreaCatchMe.getCatchIconUrl(), catchMeImage,false);
				}else{
					ImageLoader.getInstance().displayImage(mSubAreaCatchMe.getCatchIconUrl(), catchMeImage);
				}
				((RelativeLayout)findViewById(R.id.subarea_body)).addView(catchMeImage);
			}
			float density = Utils.getDensity(this);
			int height = (int)(mSubAreaCatchMe.getCatchIconHeight()*density/2);
			int width = (int)(mSubAreaCatchMe.getCatchIconWidth()*density/2);
			LayoutParams layoutP = new LayoutParams(width,height);
			layoutP.setMargins((int)(mSubAreaCatchMe.getCatchIconLeft()*Utils.getDeviceWidth(this)), (int)(mSubAreaCatchMe.getCatchIconTop()*Utils.getDeviceHeight(this)), 0, 0);
			catchMeImage.setLayoutParams(layoutP);
			catchMeImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isDialogShowing)return;
					new UserCheckInfoNetwork2(ShowSubAreaPostsActivity.this, REQUEST_CATCH_ME).catchMe(mSubAreaCatchMe.getCatchToken());
				}
			});
			if(timeBeforeStart > 0){
				//如果暂时没出现，需要等会儿出现
				catchMeImage.setVisibility(View.GONE);
				//过会儿出现
				if(mHandler!=null && mCatchMeVisibleRunnable!=null) mHandler.postDelayed(mCatchMeVisibleRunnable, timeBeforeStart);
				//过会儿消失
				if(mSubAreaCatchMe.getCatchDelay() > timeBeforeEnd){
					//以免再次请求的弹妹被上一次的endtime干掉
					if(mHandler!=null && mCatchMeVisibleRunnable!=null) mHandler.postDelayed(mCatchMeInvisibleRunnable,  timeBeforeEnd);
				}
			}else if(timeBeforeStart <= 0 && timeBeforeEnd > 0){
				//已经显示了，从当前开始计算消失时间
				if( mSubAreaCatchMe.getCatchDelay() > timeBeforeEnd){
					//以免再次请求的弹妹被上一次的endtime干掉
					if(mHandler!=null && mCatchMeVisibleRunnable!=null) mHandler.postDelayed(mCatchMeInvisibleRunnable, timeBeforeEnd);
				}
			}
			//过会儿重新请求
			if(mSubAreaCatchMe.getCatchDelay() > 0 && mHandler!=null && mCatchMeVisibleRunnable!=null) mHandler.postDelayed(mCatchMeRunnable, mSubAreaCatchMe.getCatchDelay());
		}
	}
	
	private void showMoreCachedResults() {
		boolean result = mPostAdapter.showMoreCachedResult(); // already notified data changes
		stopRefreshPostList();
		if (!result) {
			if (isNoMorePosts) {
				mRefreshListView.setHasMoreData(false);
			} else {
				getPreviousPostsFromInternet();
			}
		} else {
			stopRefreshPostList();
		}
		
	}
	
	@Override
	public void onTagClick(Post post, int position) {
//		final int relPos  = getRealClickedPosition(position);
		Intent intent2 = new Intent(ShowSubAreaPostsActivity.this, SearchPostResultActivity.class);
		intent2.putExtra("tagName", post.getPostTagName());
		intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent2);
	}

	@Override
	public void onCategoryClick(Post post, int position) {
//		final int relPos  = getRealClickedPosition(position);
		Intent intent = new Intent(ShowSubAreaPostsActivity.this, ShowSubAreaPostsActivity.class);
		Bundle args =new Bundle();
		args.putString("category", post.getPostCategory());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtras(args);
		startActivity(intent);
	}

	@Override
	public void onMusicPlayClick(Post post, int position) {
		if (post == null) return;
		Map<String, String> params = new HashMap<String, String>();
		params.put("postId", "" + post.getPostId());
		DydEventAgent.getInstance(this).onEvent(getPageName(), SubjectConstants.BTN_MUSIC_PLAY, EventConstants.EVENT_CLICK, params);
	}

	@Override
	public void onItemDeleteClick(final Post post, final int position) {
//		final int relPos  = getRealClickedPosition(position);
		if (post == null || position < 0) return;
		
		mPostDelDialog = new CommonDialog(this);
		mPostDelDialog.show();
		mPostDelDialog.initPromptDialog("删帖将回收帖子经验和糖果数，确定要删除吗？");
		mPostDelDialog.setTitleColor();
		mPostDelDialog.setEditTextEnabled(false);
		mPostDelDialog.setButton_OK_OnClickListener("确认", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPostDelPosition = position;
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_DELETE_MY_POST).deleteMyPost(post.getPostId());
				mPostDelDialog.dismiss();
			}
		}).setButton_Cancel_OnClickListener("取消", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPostDelDialog.dismiss();
			}
		});
		
		
	}
	

	@Override
	public void onItemAuditClick(Post post, final int position) {
//		int relPos = getRealClickedPosition(position);
		mAuditPost = post;
		mAuditPosition = position;
		if (post != null) showAuditDialog();
	}
	
	private void showAuditDialog() {
		mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this);
		mAuditDialog.setPostUnlockTime(mAuditPost.getPostUnlockedTime());
		mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);
		mAuditDialog.show();
	}
	
	private void showAuditPostOriginalDialog() {
        mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_SWITCH_ORIGINAL, true);
        mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);

        mAuditDialog.show(); // show first
        mAuditDialog.updateDialogTitle(true, "是否原创");
        mAuditDialog.setOkayBtnText("确定");
        
        mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuditDialog != null && mAuditDialog.isShowing()) {
                    new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_SWITCH_POST_ORIGINAL).switchPostOriginal(
                        mAuditPost.getPostId(),
                        mAuditDialog.isSwitchPost2Original());
                    mAuditDialog.dismiss();
                }
            }
        });
    }

    private void showAuditSwitchCategoryDialog() {
        mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_SWITCH_CATEGORY, true);
        mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);

        mAuditDialog.show(); // show first
        mAuditDialog.updateDialogTitle(true, "所属版块");
        mAuditDialog.setOkayBtnText("确定");
        
        mAuditDialog.initPostCategoryListViewAdapter();
        
        mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuditDialog != null && mAuditDialog.isShowing()) {
                    int categoryPosition = -1;

                    List<Integer> selectedPosList = mAuditDialog.getListViewSelectedPositions();
                    if (Utils.isListEmpty(selectedPosList) == false) categoryPosition = selectedPosList.get(0);
                    if (categoryPosition < 0 || categoryPosition >= Constants.CATEGORY_NAMES.length) {
                        ToastTools.toast(ShowSubAreaPostsActivity.this, "还未选中版块喔", Toast.LENGTH_SHORT, true);
                    } else {
                        new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_SWITCH_POST_CATEGORY).switchPostCategory(
                                mAuditPost.getPostId(),
                                Constants.CATEGORY_NAMES[categoryPosition]);
                    }
                    
                    mAuditDialog.dismiss();
                }
            }
        });
    }
	
	private void showAuditDeleteDialog() {
		mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_DELETE_POST, true);
		mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
				ShowSubAreaPostsActivity.this,
				R.layout.simple_audit_list_item,
				R.id.text1,
				Constants.AUDIT_POST_DELETE_BRIEF_REASON);
		mAuditDialog.show(); // show first
		mAuditDialog.updateDialogTitle(true, "删除此帖");
		mAuditDialog.setListViewAdapter(modeAdapter);
		mAuditDialog.setOkayBtnText("确定");
		
		mAuditDialog.setListViewOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < 0 || position >= Constants.AUDIT_POST_DELETE_BRIEF_REASON.length) return;
				mAuditDialog.setReasonEditTextContent(
						Constants.AUDIT_POST_DELETE_COMMON_PREFIX
						+ Constants.AUDIT_POST_DELETE_BRIEF_REASON_TO_USER[position]
						+ Constants.AUDIT_POST_DELETE_COMMON_APPENDIX
						+ Constants.AUDIT_POST_DELETE_REASONS[position]);
			}
		});
		
		mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_DELETE_A_POST).deleteAuditPost(
						mAuditPost.getPostId(),
						mAuditDialog.getReasonEditTextString());
				if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			}
		});
	}
	
	private void showAuditPostMoveDialog() {
		mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_SWITCH_POST_TYPE, true);
		mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);

		mAuditDialog.show(); // show first
		mAuditDialog.updateDialogTitle(true, "帖子类型");
		mAuditDialog.setOkayBtnText("确定");
		
		mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAuditDialog != null && mAuditDialog.isShowing()) {
					if (Post.POST_TYPE_VOTE.equals(mAuditPost.getPostType())) {
						ToastTools.toast(ShowSubAreaPostsActivity.this, "投票帖暂时不支持移动喔(ಥ_ಥ)", Toast.LENGTH_SHORT, true);
					} else {
						new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_SWITCH_POST_TYPE).switchPostType(
								mAuditPost.getPostId(),
								mAuditDialog.isSwithPostType2Normal());
					}
					mAuditDialog.dismiss();
				}
			}
		});
	}
	
	private void showAuditUpdateAreaDialog() {
		final CommonDialog choosePostAreaDialog = new CommonDialog(ShowSubAreaPostsActivity.this);
		choosePostAreaDialog.show();
		
		choosePostAreaDialog.setTitle("选择分区")
		.setSubAreaAuditPick(0, 0)
		.setButton_Cancel_OnClickListener("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {
				choosePostAreaDialog.dismiss();
			}
		}).setButton_OK_OnClickListener("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Long> subAreaIdList = choosePostAreaDialog.getSelectedAuditSubAreaIds();
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_CHANGE_POST_AREA).updatePostAreas(
						mAuditPost.getPostId(),
						subAreaIdList);
				
				choosePostAreaDialog.dismiss();
			}
		});
	}
	
	private void showAuditLockDetailsDialog() {
		mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_LOCK_POST, true);
		mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);

		mAuditDialog.show(); // show first
		mAuditDialog.updateDialogTitle(true, "帖子锁定");
		mAuditDialog.setOkayBtnText("确定");
		mAuditDialog.initLockOperationListViewAdapter();
		
		mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAuditDialog != null && mAuditDialog.isShowing()) {
					List<Integer> selectedPosList = mAuditDialog.getListViewSelectedPositions();
					if (Utils.isListEmpty(selectedPosList)) {
						ToastTools.toast(ShowSubAreaPostsActivity.this, "请先选择操作选项喔|･ω･｀)", Toast.LENGTH_SHORT, true);
					}
					String lockType = Constants.AUDIT_LOCK_OPERATIONS_TYPE[selectedPosList.get(0)];
					new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_LOCK_A_POST).lockAuditPost(
							mAuditPost.getPostId(),
							lockType);
					mAuditDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onAuditItemClicked(int action,String subArea) {
		switch (action) {
		case AuditDialog.ON_POST_DELETE_ACTION_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditDeleteDialog();
			break;
		case AuditDialog.ON_POST_LOCK_ACTION_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditLockDetailsDialog();
			break;
		case AuditDialog.ON_SWITCH_TYPE_ACTION_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditPostMoveDialog();
			break;
		case AuditDialog.ON_CHANGE_AREA_ACTION_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditUpdateAreaDialog();
			break;
		case AuditDialog.ON_FINISH_AUDIT_ACTION_CLICKED:
		case AuditDialog.ON_OKAY_ACTION_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_FINISH_POST_AUDIT).finishPostAudit(
						mAuditPost.getPostId()
						);
				mAuditDialog.dismiss();
			}
			break;
		case AuditDialog.ON_SUPPORT_POST_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) {
				new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_SUPPORT_THIS_POST).supportThisPost(
						mAuditPost.getPostId()
						);
				mAuditDialog.dismiss();
			}
			break;
		case AuditDialog.ON_SWITCH_POST_ORIGINAL_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditPostOriginalDialog();
			break;
		case AuditDialog.ON_SWITCH_POST_CATEGORY_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditSwitchCategoryDialog();
			break;
		case AuditDialog.ON_POST_ACTIVIE_TIME_CLICKED:
			if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			showAuditInputActiveTimeDialog();
			break;
		default:
			break;
		}
	}
	
	private void showAuditInputActiveTimeDialog() {
		mAuditDialog = new AuditDialog(ShowSubAreaPostsActivity.this, AuditDialog.VIEW_TYPE_ADMIN_INPUT_ACTIVE_TIME, true);
		mAuditDialog.setAuditDialogHandler(ShowSubAreaPostsActivity.this);
		mAuditDialog.setPostUnlockTime(mAuditPost.getPostUnlockedTime());
		mAuditDialog.show(); // show first
		mAuditDialog.updateDialogTitle(true, "输入活跃时间");
		mAuditDialog.setOkayBtnText("确定");
		
		mAuditDialog.setOkayBtnOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String time = mAuditDialog.getReasonEditTextString();
				if(!Utils.isNull(time)){
					new PostNetwork2(ShowSubAreaPostsActivity.this, REQUEST_SET_ACTIVE_TIME_A_POST).setActiveTimeAuditPost(mAuditPost.getPostId(),time);
				}else{
					ToastTools.toast(ShowSubAreaPostsActivity.this, "请输入帖子活跃时间", Toast.LENGTH_SHORT, false);
					return;
				}
				if (mAuditDialog != null && mAuditDialog.isShowing()) mAuditDialog.dismiss();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//沉浸式状态栏动态alph
		mRefreshListView.setTintManager(tintManager);
		//
		if(mRefreshListView != null){
			mRefreshListView.computeNaviAlplaValue();
		}
	}

	private int getRealClickedPosition(int pos) {
		if (mHasHeader) return pos + 1;
		return pos;
	}
	
	@Override
	public void finish() {
		if(mHandler!=null ){
			if(mCatchMeRunnable!=null)mHandler.removeCallbacks(mCatchMeRunnable);
			if(mCatchMeVisibleRunnable!=null)mHandler.removeCallbacks(mCatchMeVisibleRunnable);
			if(mCatchMeInvisibleRunnable!=null)mHandler.removeCallbacks(mCatchMeInvisibleRunnable);
		}
		super.finish();
	}
	
	@Override
	public void onShareItemFinish() {
		if (mShareQueue != null && mShareQueue.hasNext()) {
			mShareQueue.shareNextItem(ShowSubAreaPostsActivity.this, mShareUtilManager);
		}
	}
	
	@Override
    public String getPageName() {
    	return PageNameConstants.ACTIVITY_SHOW_SUBAREA_POSTS;
	}
}

