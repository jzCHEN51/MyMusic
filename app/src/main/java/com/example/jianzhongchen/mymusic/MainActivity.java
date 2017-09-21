package com.example.jianzhongchen.mymusic;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaTimestamp;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jianzhongchen.mymusic.adapter.MyAdapter;
import com.example.jianzhongchen.mymusic.constants.Constants;
import com.example.jianzhongchen.mymusic.service.MusicService;
import com.example.jianzhongchen.mymusic.utils.MediaUtils;
import com.example.jianzhongchen.mymusic.views.ScrollableViewGroup;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv_list;
    private ScrollableViewGroup svg_main;
    private TextView tv_currentTime;
    private TextView tv_totalTime;
    private TextView tv_minilrc;
    private ImageView iv_bottom_model;
    private ImageView iv_bottom_play;

    private String TAG = "MainActivity";
    private ImageButton ib_top_play;
    private ImageButton ib_top_list;
    private SeekBar sk_duration;


    private ImageButton ib_top_lrc;
    private ImageButton ib_top_volume;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MSG_ONPREPARED:
                    int currentPosition = msg.arg1;
                    int totalDuration = msg.arg2;
                    Log.d(TAG, "handleMessage: " + currentPosition);
                    tv_currentTime.setText(MediaUtils.duration2Str(currentPosition));
                    tv_totalTime.setText(MediaUtils.duration2Str(totalDuration));
                    sk_duration.setProgress(currentPosition);
                    sk_duration.setMax(totalDuration);
                    break;
                case Constants.MSG_COMPLETION:
                    if (MediaUtils.CURMODEL == Constants.MODEL_INORDER && MediaUtils.CURPOSITION < MediaUtils.songList.size()) {
                        iv_bottom_play.setImageResource(R.mipmap.img_playback_bt_play);
                        changeColorWhite();
                        MediaUtils.CURPOSITION++;
                        changeColorGreen();
                        startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                    } else if (MediaUtils.CURMODEL == Constants.MODEL_RANDOM) {
                        iv_bottom_play.setImageResource(R.mipmap.img_playback_bt_play);
                        Random random = new Random();
                        int position = random.nextInt(MediaUtils.songList.size());
                        changeColorWhite();
                        MediaUtils.CURPOSITION = position;
                        changeColorGreen();
                        startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                    } else if (MediaUtils.CURMODEL == Constants.MODEL_SINGLE) {
                        iv_bottom_play.setImageResource(R.mipmap.img_playback_bt_play);
                        startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                    } else if (MediaUtils.CURMODEL == Constants.MODEL_RECYCLE) {
                        iv_bottom_play.setImageResource(R.mipmap.img_playback_bt_play);
                        changeColorWhite();
                        MediaUtils.CURPOSITION = (MediaUtils.CURPOSITION + 1) % MediaUtils.songList.size();
                        changeColorGreen();
                        startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();

    }

    private void initView() {

        ib_top_play = (ImageButton) findViewById(R.id.ib_top_play);
        ib_top_list = (ImageButton) findViewById(R.id.ib_top_list);
        ib_top_lrc = (ImageButton) findViewById(R.id.ib_top_geci);
        ib_top_volume = (ImageButton) findViewById(R.id.ib_top_volume);
        lv_list = (ListView) findViewById(R.id.lv_list);
        svg_main = (ScrollableViewGroup) findViewById(R.id.svg_main);
        tv_currentTime = (TextView) findViewById(R.id.tv_currenttime_id);
        tv_totalTime = (TextView) findViewById(R.id.tv_totaltime_id);
        tv_minilrc = (TextView) findViewById(R.id.tv_lrc);
        iv_bottom_model = (ImageView) findViewById(R.id.iv_bottom_model);
        iv_bottom_play = (ImageView) findViewById(R.id.iv_bottom_play);
        sk_duration = (SeekBar) findViewById(R.id.sk_duration);

        findViewById(R.id.ib_top_play).setSelected(true);
    }

    private void initData() {

        MediaUtils.initSongList(this);
        lv_list.setAdapter(new MyAdapter(this));
    }

    private void initListener() {

        findViewById(R.id.ib_top_play).setOnClickListener(this);
        findViewById(R.id.ib_top_list).setOnClickListener(this);
        findViewById(R.id.ib_top_geci).setOnClickListener(this);
        findViewById(R.id.ib_top_volume).setOnClickListener(this);
        findViewById(R.id.ib_bottom_model).setOnClickListener(this);
        findViewById(R.id.ib_bottom_last).setOnClickListener(this);
        findViewById(R.id.ib_bottom_play).setOnClickListener(this);
        findViewById(R.id.ib_bottom_next).setOnClickListener(this);
        findViewById(R.id.ib_bottom_menu).setOnClickListener(this);

        svg_main.setOnCurrentViewChangedListener(new ScrollableViewGroup.OnCurrentViewChangedListener() {
            @Override
            public void onCurrentViewChanged(View view, int currentview) {
                Log.d(TAG, "onCurrentViewChanged: currentview");
                setTopSelected(myArray[currentview]);
            }
        });

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                changeColorWhite();
                MediaUtils.CURPOSITION = position;
                changeColorGreen();
                startMediaService("播放", MediaUtils.songList.get(position).path);
            }
        });
        sk_duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sk_duration.setProgress(seekBar.getProgress());
                startMediaService("拖动播放", seekBar.getProgress());
            }
        });
    }

    private void startMediaService(String option, String path) {
        Intent service = new Intent(MainActivity.this, MusicService.class);
        service.putExtra("option", option);
        service.putExtra("path", path);
        service.putExtra("messenger", new Messenger(handler));
        startService(service);
    }

    private void startMediaService(String option) {
        Intent service = new Intent(MainActivity.this, MusicService.class);
        service.putExtra("option", option);
        service.putExtra("messenger", new Messenger(handler));
        startService(service);
    }

    private void startMediaService(String option, int progress) {
        Intent service = new Intent(MainActivity.this, MusicService.class);
        service.putExtra("option", option);
        service.putExtra("progress", progress);
        service.putExtra("messenger", new Messenger(handler));
        startService(service);
    }


    int[] myArray = {R.id.ib_top_play, R.id.ib_top_list, R.id.ib_top_geci};

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_top_play:
                svg_main.setCurrentView(0);
                setTopSelected(R.id.ib_top_play);
                break;
            case R.id.ib_top_list:
                svg_main.setCurrentView(1);
                setTopSelected(R.id.ib_top_list);
                break;
            case R.id.ib_top_geci:
                svg_main.setCurrentView(2);
                setTopSelected(R.id.ib_top_geci);
                break;
            case R.id.ib_top_volume:
                break;
            case R.id.ib_bottom_model:
                if (MediaUtils.CURMODEL == Constants.MODEL_INORDER) {
                    MediaUtils.CURMODEL = Constants.MODEL_RANDOM;
                    iv_bottom_model.setImageResource(R.mipmap.icon_playmode_shuffle);
                    Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                } else if (MediaUtils.CURMODEL == Constants.MODEL_RANDOM) {
                    MediaUtils.CURMODEL = Constants.MODEL_SINGLE;
                    iv_bottom_model.setImageResource(R.mipmap.icon_playmode_single);
                    Toast.makeText(getApplicationContext(), "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (MediaUtils.CURMODEL == Constants.MODEL_SINGLE) {
                    MediaUtils.CURMODEL = Constants.MODEL_RECYCLE;
                    iv_bottom_model.setImageResource(R.mipmap.icon_playmode_repeat);
                    Toast.makeText(getApplicationContext(), "列表循环", Toast.LENGTH_SHORT).show();
                } else if (MediaUtils.CURMODEL == Constants.MODEL_RECYCLE) {
                    MediaUtils.CURMODEL = Constants.MODEL_INORDER;
                    iv_bottom_model.setImageResource(R.mipmap.icon_playmode_normal);
                    Toast.makeText(getApplicationContext(), "顺序播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_bottom_last:
                if (MediaUtils.CURPOSITION > 0) {
                    changeColorWhite();
                    MediaUtils.CURPOSITION--;
                    changeColorGreen();
                    startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                }
                break;
            case R.id.ib_bottom_play:
                if (MediaUtils.CURSTATE == Constants.STATE_STOP) {
                    startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                } else if (MediaUtils.CURSTATE == Constants.STATE_PLAY) {
                    startMediaService("暂停");
                    iv_bottom_play.setImageResource(R.mipmap.img_playback_bt_play);
                } else if (MediaUtils.CURSTATE == Constants.STATE_PAUSE) {
                    startMediaService("继续播放");
                    iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                }
                break;
            case R.id.ib_bottom_next:
                if (MediaUtils.CURMODEL == Constants.MODEL_INORDER && MediaUtils.CURPOSITION < MediaUtils.songList.size() - 1) {
                    changeColorWhite();
                    MediaUtils.CURPOSITION++;
                    changeColorGreen();
                    startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                }else if (MediaUtils.CURMODEL == Constants.MODEL_RANDOM){
                    changeColorWhite();
                    Random random = new Random();
                    int position = random.nextInt(MediaUtils.songList.size());
                    MediaUtils.CURPOSITION = position;
                    changeColorGreen();
                    startMediaService("播放",MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    iv_bottom_play.setImageResource(R.mipmap.appwidget_pause);
                }
                break;
            case R.id.ib_bottom_menu:
                break;
            default:
                break;


        }
    }

    public void setTopSelected(int selectedId) {
        ib_top_play.setSelected(false);
        ib_top_list.setSelected(false);
        ib_top_lrc.setSelected(false);
        ib_top_volume.setSelected(false);
        findViewById(selectedId).setSelected(true);

    }

    public void changeColorWhite() {
        TextView viewWithTag = (TextView) lv_list.findViewWithTag(MediaUtils.CURPOSITION);
        if (viewWithTag != null) {
            viewWithTag.setTextColor(Color.WHITE);
        }
    }

    public void changeColorGreen() {
        TextView viewWithTag = (TextView) lv_list.findViewWithTag(MediaUtils.CURPOSITION);
        if (viewWithTag != null) {
            viewWithTag.setTextColor(Color.GREEN);
        }
    }
}
