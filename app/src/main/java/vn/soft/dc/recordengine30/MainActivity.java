package vn.soft.dc.recordengine30;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soft.dc.recordengine.RecorderEngine;
import vn.soft.dc.recordengine.model.Preset;
import vn.soft.dc.recordengine30.popup.PopupChoosePresetFragment;

import static vn.soft.dc.recordengine.util.FileUtils.readRawTextFile;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;

    @BindView(R.id.etOffset)
    TextView etOffset;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.btnEnable)
    Button btnEnable;
    @BindView(R.id.btnAcoustic)
    Button btnAcoustic;
    @BindView(R.id.btnBolero)
    Button btnBolero;
    @BindView(R.id.btnMaster)
    Button btnMaster;
    @BindView(R.id.btnPopStar)
    Button btnPopStar;
    @BindView(R.id.btnPopStarFix)
    Button btnPopStarFix;
    @BindView(R.id.btnRap)
    Button btnRap;
    @BindView(R.id.btnStudio)
    Button btnStudio;
    @BindView(R.id.btnMedia)
    Button btnMedia;
    @BindView(R.id.btnInit)
    Button btnInit;
    @BindView(R.id.btnRelease)
    Button btnRelease;
    @BindView(R.id.btnKaraoke)
    Button btnKaraoke;
    @BindView(R.id.btnKaraokeRoom)
    Button btnKaraokeRoom;
    @BindView(R.id.btnPlay)
    Button btnPlay;
    @BindView(R.id.sbVolumeMusic)
    SeekBar sbVolumeMusic;
    @BindView(R.id.tvSystemStatus)
    TextView tvSystemStatus;
    @BindView(R.id.btnEffect)
    TextView btnEffect;

    private RecorderEngine mRecorderEngine;
    private List<Preset> mPresets;
    private Gson gson;
    private boolean isEnable;
    private MediaPlayer mMediaPlayer;
    private static final int MAX_VOLUME = 100;

    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMediaPlayer = MediaPlayer.create(this, R.raw.duyen_minh_beat);
        sbVolumeMusic.setMax(MAX_VOLUME);
        sbVolumeMusic.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mMediaPlayer.setVolume(0, 0);
        claimPermission();
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float log1 = (float) (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME));
            mMediaPlayer.setVolume(1 - log1, 1 - log1);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @OnClick({R.id.btnPlay, R.id.btnBack, R.id.btnEffect, R.id.btnKaraoke, R.id.btnKaraokeRoom, R.id.btnMedia, R.id.btnStart, R.id.btnStop, R.id.btnEnable, R.id.btnAcoustic, R.id.btnBolero, R.id.btnMaster, R.id.btnPopStar, R.id.btnPopStarFix, R.id.btnRap, R.id.btnStudio, R.id.btnRelease, R.id.btnInit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.start();
                }
                break;
            case R.id.btnMedia:
                mRecorderEngine.release();
                Intent intent = new Intent(getApplicationContext(), MediaActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnStart:
                mRecorderEngine.startRecord(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".wav");
                break;
            case R.id.btnStop:
                mRecorderEngine.stopRecord();
                break;
            case R.id.btnEnable:
                isEnable = !isEnable;
                mRecorderEngine.enableEffectVocal(isEnable);
                btnEnable.setText(isEnable ? "Mic On" : "Mic Off");
                mRecorderEngine.enablePlayback(isEnable);
                break;
            case R.id.btnAcoustic:
                mRecorderEngine.changeEffect(mPresets.get(0));
                break;
            case R.id.btnBolero:
                mRecorderEngine.changeEffect(mPresets.get(1));
                break;
            case R.id.btnMaster:
                mRecorderEngine.changeEffect(mPresets.get(2));
                break;
            case R.id.btnPopStar:
                mRecorderEngine.changeEffect(mPresets.get(3));
                break;
            case R.id.btnPopStarFix:
                mRecorderEngine.changeEffect(mPresets.get(4));
                break;
            case R.id.btnRap:
                mRecorderEngine.changeEffect(mPresets.get(5));
                break;
            case R.id.btnStudio:
                mRecorderEngine.changeEffect(mPresets.get(6));
                break;
            case R.id.btnRelease:
                Log.d("RecordEngine", "start release: " + System.currentTimeMillis());
                mRecorderEngine.release();
                Log.d("RecordEngine", "end release: " + System.currentTimeMillis());
                break;
            case R.id.btnInit:
                claimPermission();
                break;
            case R.id.btnKaraoke:
                mRecorderEngine.changeEffect(mPresets.get(7));
                break;
            case R.id.btnKaraokeRoom:
                mRecorderEngine.changeEffect(mPresets.get(8));
                break;
            case R.id.btnEffect:
                chooseEffect();
                break;
            case R.id.btnBack:
                MainActivity.this.finish();
                break;
        }
    }

    private void chooseEffect() {
        PopupChoosePresetFragment.newInstance().setOnPresetChoose(new PopupChoosePresetFragment.OnPresetChoose() {
            @Override
            public void onChoose(Preset preset) {
                onProcessPresetChoose(new Gson().fromJson(mReadJsonData(preset.getName()), Preset.class));
            }
        }).show(getSupportFragmentManager(), null);
    }

    private void onProcessPresetChoose(Preset preset) {
        String[] path = preset.getName().split("/");
        btnEffect.setText(path[path.length - 1]);
        mRecorderEngine.changeEffect(preset);
    }

    public String mReadJsonData(String path) {
        try {
            File f = new File(path);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        if (mRecorderEngine != null) {
            mRecorderEngine.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }

    private void claimPermission() {
        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            doCreate();
            return;
        }

        requestPermission(PERMISSION_CODE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @SuppressLint("SetTextI18n")
    private void doCreate() {
        gson = new Gson();
        mPresets = new ArrayList<>();
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.acoustic), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.bolero), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.master), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.pop_star), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.pop_star_fix), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.rap), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.studio), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.karaoke), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.karaoke_room), Preset.class));

        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
                buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            }
        }
        if (samplerateString == null) samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "512";
        int samplerate = Integer.parseInt(samplerateString);
        int buffersize = Integer.parseInt(buffersizeString);
        tvSystemStatus.setText("SampleRate: " + samplerate + "; BufferSize: " + buffersize);

        mRecorderEngine = new RecorderEngine(samplerate, buffersize < 32 ? buffersize : 32, onRecordEventListener);
        isEnable = false;
        btnEnable.setText(isEnable ? "Mic On" : "Mic Off");
    }

    private RecorderEngine.OnRecordEventListener onRecordEventListener = new RecorderEngine.OnRecordEventListener() {
        @Override
        public void onInitSuccess() {
            Toast.makeText(MainActivity.this, "Khởi tạo thành công", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFrequencyListener(final double freq) {
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    etOffset.setText(freq + "Hz");
                }
            });
        }
    };

    public boolean isPermissionGranted(String... permissions) {
        for (String value : permissions) {
            if (isPermissionGranted(value)) continue;
            return false;
        }
        return true;
    }

    public void requestPermission(int requestCode, String... permissions) {
        requestPermission(permissions, requestCode);
    }

    public void requestPermission(String[] permissionList, int codeRequest) {
        ActivityCompat.requestPermissions(this, permissionList, codeRequest);
    }

    public boolean isPermissionGranted(String permissions) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        claimPermission();
    }

}
