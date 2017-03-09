package com.chenyi.langeasy.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.Utilities;
import com.chenyi.langeasy.db.DBHelper;
import com.chenyi.langeasy.fragment.BookListFragment;
import com.chenyi.langeasy.fragment.BookTypeListFragment;
import com.chenyi.langeasy.fragment.CourseListFragment;
import com.chenyi.langeasy.fragment.HistoryFragment;
import com.chenyi.langeasy.fragment.MusicPlayerFragment;
import com.chenyi.langeasy.fragment.PassListFragment;
import com.chenyi.langeasy.fragment.PlayListFragment;
import com.chenyi.langeasy.fragment.QueueFragment;
import com.chenyi.langeasy.fragment.QueueRecordFragment;
import com.chenyi.langeasy.fragment.SettingFragment;
import com.chenyi.langeasy.fragment.WordLearningFragment;
import com.chenyi.langeasy.listener.FragmentExchangeListener;
import com.chenyi.langeasy.ui.MusicPlayerActivity;
import com.chenyi.langeasy.util.CSVUtil;
import com.chenyi.langeasy.util.LogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 *
 * @author guolin
 */
public class MainNewActivity extends BaseActivity implements OnClickListener, FragmentExchangeListener,
        PlayListFragment.OnSentenceSelectedListener, BookTypeListFragment.OnItemSelectedListener, BookListFragment.OnItemSelectedListener, WordLearningFragment.OnPlayListener {

    private static final String TAG = LogHelper.makeLogTag(MainNewActivity.class);
    /**
     * 用于展示消息的Fragment
     */
    private MusicPlayerFragment playerFragment;
    private WordLearningFragment wlearningFragment;
    private BookListFragment bookListFragment;
    private BookTypeListFragment bookTypeListFragment;
    private CourseListFragment courseListFragment;
    private HistoryFragment historyFragment;
    private PassListFragment passListFragment;
    private QueueFragment queueFragment;
    private QueueRecordFragment queueRecordFragment;

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

//        ShareSDK.initSDK(this);
        initializeToolbar();

        // 初始化布局元素
        initViews();
        fragmentManager = getFragmentManager();


        mydb = new DBHelper(this);
        songsList = mydb.listSentence("");

//        playerFragment = new MusicPlayerFragment();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
        setTabSelection(1);
//        try {
//            Thread.sleep(1200);// waiting playlist filter finished
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        setTabSelection(4);
        setTabSelection(5);
        setTabSelection(7);
        setTabSelection(10);// init queue record page
//        setTabSelection(3);

        LogHelper.i(TAG, "Activity onCreate");
    }

    @Override
    protected void toFragment(String type) {
        LogHelper.i(TAG, "Fragment Type " + type);
        if ("listen".equals(type)) {
            setTabSelection(0);
        } else if ("learn".equals(type)) {
            setTabSelection(3);
        } else if ("booktype_list".equals(type)) {
            setTabSelection(6);
        } else if ("booklist".equals(type)) {
            setTabSelection(4);
        } else if ("courselist".equals(type)) {
            setTabSelection(5);
        } else if ("playlist".equals(type)) {
            setTabSelection(1);
        } else if ("history".equals(type)) {
            setTabSelection(7);
        } else if ("passlist".equals(type)) {
            setTabSelection(8);
        } else if ("queue".equals(type)) {
            setTabSelection(9);
        } else if ("queue_record".equals(type)) {
            setTabSelection(10);
        } else if ("setting".equals(type)) {
            setTabSelection(2);
        }
    }

    @Override
    protected void handle(String type) {
        if ("setting".equals(type)) {
            showShare();
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
    public void setTabSelection(int index) {
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
                if (playListFragment == null) {
                    playListFragment = new PlayListFragment();
                    transaction.add(R.id.content, playListFragment);
                } else {
                    transaction.show(playListFragment);
                }
                break;
            case 2:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
            case 3:
                if (wlearningFragment == null) {
                    wlearningFragment = new WordLearningFragment();
                    transaction.add(R.id.content, wlearningFragment);
                } else {
                    transaction.show(wlearningFragment);
                }
                break;
            case 6:
                if (bookTypeListFragment == null) {
                    bookTypeListFragment = new BookTypeListFragment();
                    transaction.add(R.id.content, bookTypeListFragment);
                } else {
                    transaction.show(bookTypeListFragment);
                }
                break;
            case 4:
                if (bookListFragment == null) {
                    bookListFragment = new BookListFragment();
                    transaction.add(R.id.content, bookListFragment);
                } else {
                    transaction.show(bookListFragment);
                }
                break;
            case 5:
                if (courseListFragment == null) {
                    courseListFragment = new CourseListFragment();
                    transaction.add(R.id.content, courseListFragment);
                } else {
                    transaction.show(courseListFragment);
                }
                break;
            case 7:
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                    transaction.add(R.id.content, historyFragment);
                } else {
                    transaction.show(historyFragment);
                }
                break;
            case 8:
                if (passListFragment == null) {
                    passListFragment = new PassListFragment();
                    transaction.add(R.id.content, passListFragment);
                } else {
                    transaction.show(passListFragment);
                }
                break;
            case 9:
                if (queueFragment == null) {
                    queueFragment = new QueueFragment();
                    transaction.add(R.id.content, queueFragment);
                } else {
                    transaction.show(queueFragment);
                }
                break;
            case 10:
                if (queueRecordFragment == null) {
                    queueRecordFragment = new QueueRecordFragment();
                    transaction.add(R.id.content, queueRecordFragment);
                } else {
                    transaction.show(queueRecordFragment);
                }
                break;
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                break;
        }

        LogHelper.i(TAG, "setTabSelection " + index);
//        transaction.addToBackStack(null);
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
        if (bookTypeListFragment != null) {
            transaction.hide(bookTypeListFragment);
        }
        if (bookListFragment != null) {
            transaction.hide(bookListFragment);
        }
        if (courseListFragment != null) {
            transaction.hide(courseListFragment);
        }
        if (historyFragment != null) {
            transaction.hide(historyFragment);
        }
        if (passListFragment != null) {
            transaction.hide(passListFragment);
        }
        if (queueFragment != null) {
            transaction.hide(queueFragment);
        }
        if (queueRecordFragment != null) {
            transaction.hide(queueRecordFragment);
        }
    }

    @Override
    public void onSentenceSelected(int songIndex) {
        setNavigationStatus("learn");
        setTabSelection(3);
        wlearningFragment.playSong(songIndex);
    }

    @Override
    public void onBooktypeSelected(String booktype) {
        setNavigationStatus("booklist");
        setTabSelection(4);
        bookListFragment.load(booktype);
    }

    @Override
    public void onBookSelected(String bookid) {
        setNavigationStatus("courselist");
        setTabSelection(5);
        courseListFragment.load(bookid);
    }

    public int findIndex(Integer sentenceid) {
        for (Map<String, Object> sentence : songsList) {
            int sid = (int) sentence.get("sentenceid");
            if (sid == sentenceid) {
                return (int) sentence.get("index");
            }
        }
        return 0;
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

    @Override
    public void hquery(String condition) {
        setNavigationStatus("history");
        setTabSelection(7);
        historyFragment.query(condition);
    }

    public void stopPlay() {
        playerFragment.pausePlayer();
    }


    public interface PlayListResetCallback {
        void afterReset();
    }

    public void toQueueRecord(Integer queueId) {
        setNavigationStatus("queue_record");
        setTabSelection(10);
        queueRecordFragment.load(queueId);
    }

    public void h2learn(final Integer sentenceid) {
        PlayListResetCallback prCallback = new PlayListResetCallback() {
            @Override
            public void afterReset() {
                int songIndex = findIndex(sentenceid);
                toLearning(songIndex);
            }
        };
        playListFragment.reset(prCallback);

    }

    public void remember() {
        playListFragment.remember();
    }

    public void showShare() {
        List<List<String>> recordList = mydb.backupPlayRecord();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = "langeasy_bak-" + sdf.format(new Date()) + ".csv";
        File csvFile = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = "/sdcard/langeasy/bak/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
//                csvFile = File.createTempFile(fileName, ".csv");
                csvFile = new File(path + fileName);
                FileWriter writer = new FileWriter(csvFile);
                //for header
                CSVUtil.writeLine(writer, Arrays.asList("Index", "WordId", "Word", "SentenceId", "PlayCount", "PlayTime"));
                for (List<String> recordData : recordList) {
                    CSVUtil.writeLine(writer, recordData);

                    //try custom separator and quote.
                    //CSVUtil.writeLine(writer, list, '|', '\"');
                }

                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(csvFile));
//        sendIntent.setType("text/plain");
        sendIntent.setType("text/csv");
//        startActivity(sendIntent);
        startActivity(Intent.createChooser(sendIntent, "Share Via"));
    }

    public void showShare2() {
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
//        oks.setTitle("标题");
//        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("ShareSDK");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//// 启动分享GUI
//        oks.show(this);
    }
}
