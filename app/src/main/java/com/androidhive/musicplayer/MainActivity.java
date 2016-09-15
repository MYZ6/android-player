package com.androidhive.musicplayer;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 *
 * @author guolin
 */
public class MainActivity extends Activity implements OnClickListener ,PlayListFragment.OnSentenceSelectedListener {

    /**
     * 用于展示消息的Fragment
     */
    private MusicPlayerFragment playerFragment;

    /**
     * 用于展示联系人的Fragment
     */
    private PlayListFragment playListFragment;

    /**
     * 用于展示动态的Fragment
     */
    private SettingFragment settingFragment;


    /**
     * 消息界面布局
     */
    private View playerLayout;

    /**
     * 联系人界面布局
     */
    private View slistLayout;

    /**
     * 动态界面布局
     */
    private View settingLayout;

    /**
     * 在Tab布局上显示消息图标的控件
     */
    private ImageView messageImage;

    /**
     * 在Tab布局上显示联系人图标的控件
     */
    private ImageView contactsImage;

    /**
     * 在Tab布局上显示动态图标的控件
     */
    private ImageView settingImage;


    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        playerLayout = findViewById(R.id.player_layout);
        slistLayout = findViewById(R.id.slist_layout);
        settingLayout = findViewById(R.id.setting_layout);
        messageImage = (ImageView) findViewById(R.id.message_image);
        contactsImage = (ImageView) findViewById(R.id.contacts_image);
        settingImage = (ImageView) findViewById(R.id.setting_image);
        playerLayout.setOnClickListener(this);
        slistLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.slist_layout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.setting_layout:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                messageImage.setImageResource(R.drawable.message_selected);
                if (playerFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    playerFragment = new MusicPlayerFragment();
                    transaction.add(R.id.content, playerFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(playerFragment);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                contactsImage.setImageResource(R.drawable.contacts_selected);
                if (playListFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    playListFragment = new PlayListFragment();
                    transaction.add(R.id.content, playListFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(playListFragment);
                }
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                settingImage.setImageResource(R.drawable.setting_selected);
                if (settingFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(settingFragment);
                }
                break;
            case 3:
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        messageImage.setImageResource(R.drawable.message_unselected);
        contactsImage.setImageResource(R.drawable.contacts_unselected);
        settingImage.setImageResource(R.drawable.setting_unselected);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (playerFragment != null) {
            transaction.hide(playerFragment);
        }
        if (playListFragment != null) {
            transaction.hide(playListFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
    }

    @Override
    public void onSentenceSelected(int songIndex) {
        setTabSelection(0);
        playerFragment.playSong(songIndex);
    }
}
