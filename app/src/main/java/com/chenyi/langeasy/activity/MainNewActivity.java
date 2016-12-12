package com.chenyi.langeasy.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.db.DBHelper;
import com.chenyi.langeasy.fragment.BookListFragment;
import com.chenyi.langeasy.fragment.CourseListFragment;
import com.chenyi.langeasy.fragment.MusicPlayerFragment;
import com.chenyi.langeasy.fragment.PlayListFragment;
import com.chenyi.langeasy.fragment.SettingFragment;
import com.chenyi.langeasy.fragment.WordLearningFragment;
import com.chenyi.langeasy.listener.ButtonPlayListListener;
import com.chenyi.langeasy.ui.MusicPlayerActivity;
import com.chenyi.langeasy.util.LogHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 *
 * @author guolin
 */
public class MainNewActivity extends BaseActivity implements OnClickListener, ButtonPlayListListener,
        MusicPlayerFragment.BtnLearningListener, PlayListFragment.OnSentenceSelectedListener, BookListFragment.OnItemSelectedListener, WordLearningFragment.OnPlayListener {

    private static final String TAG = LogHelper.makeLogTag(MainNewActivity.class);
    /**
     * 用于展示消息的Fragment
     */
    private MusicPlayerFragment playerFragment;
    private WordLearningFragment wlearningFragment;
    private BookListFragment bookListFragment;
    private CourseListFragment courseListFragment;

    /**
     * 用于展示联系人的Fragment
     */
    private PlayListFragment playListFragment;

    /**
     * 用于展示动态的Fragment
     */
    private SettingFragment settingFragment;

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    public ArrayList<Map<String, Object>> songsList = new ArrayList<>();
    private DBHelper mydb;

    public DBHelper getDBHelper() {
        return mydb;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_placeholder);

        initializeToolbar();

        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();


        mydb = new DBHelper(this);
        songsList = mydb.listSentence();

//        playerFragment = new MusicPlayerFragment();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
        setTabSelection(1);
        setTabSelection(5);
        setTabSelection(3);

        LogHelper.i(TAG, "Activity onCreate");
    }

    @Override
    protected void toFragment(String type) {
        LogHelper.i(TAG, "Fragment Type " + type);
        if ("listen".equals(type)) {
            setTabSelection(0);
        } else if ("learn".equals(type)) {
            setTabSelection(3);
        } else if ("booklist".equals(type)) {
            setTabSelection(4);
        } else if ("courselist".equals(type)) {
            setTabSelection(5);
        } else if ("playlist".equals(type)) {
            setTabSelection(1);
        }
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
    }

    @Override
    public void onClick(View v) {
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
//                messageImage.setImageResource(R.drawable.message_selected);
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
//                contactsImage.setImageResource(R.drawable.contacts_selected);
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
//                settingImage.setImageResource(R.drawable.setting_selected);
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
                // 当点击了消息tab时，改变控件的图片和文字颜色
//                wlearningImage.setImageResource(R.drawable.news_selected);
                if (wlearningFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    wlearningFragment = new WordLearningFragment();
                    transaction.add(R.id.content, wlearningFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(wlearningFragment);
                }
                break;
            case 4:
                if (bookListFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    bookListFragment = new BookListFragment();
                    transaction.add(R.id.content, bookListFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(bookListFragment);
                }
                break;
            case 5:
                if (courseListFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    courseListFragment = new CourseListFragment();
                    transaction.add(R.id.content, courseListFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(courseListFragment);
                }
                break;
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                break;
        }

        LogHelper.i(TAG, "setTabSelection " + index);
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
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
        if (wlearningFragment != null) {
            transaction.hide(wlearningFragment);
        }
        if (playListFragment != null) {
            transaction.hide(playListFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
        if (bookListFragment != null) {
            transaction.hide(bookListFragment);
        }
        if (courseListFragment != null) {
            transaction.hide(courseListFragment);
        }
    }

    @Override
    public void onSentenceSelected(int songIndex) {
        setTabSelection(3);
        wlearningFragment.playSong(songIndex);
    }

    @Override
    public void onBookSelected(String bookid) {
        setNavigationStatus("courselist");
        setTabSelection(5);
        courseListFragment.load(bookid);
    }

    @Override
    public void onPlay(int songIndex) {
        setNavigationStatus("listen");
        setTabSelection(0);
        playerFragment.lastPlayedAudioType = "sentence";
        playerFragment.playSong(songIndex);
    }

    @Override
    public void toLearning(int songIndex) {
        setNavigationStatus("learn");
        setTabSelection(3);
        wlearningFragment.playSong(songIndex);

    }

    @Override
    public void toList(int songIndex) {

    }

    @Override
    public void query(String condition) {
        setNavigationStatus("playlist");
        setTabSelection(1);
        playListFragment.query(condition);
    }

}
