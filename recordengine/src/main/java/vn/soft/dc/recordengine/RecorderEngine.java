package vn.soft.dc.recordengine;

import android.os.Environment;

import vn.soft.dc.recordengine.audio.calculators.AudioCalculator;
import vn.soft.dc.recordengine.model.Preset;

import static vn.soft.dc.recordengine.model.Preset.REVERB_DAMP;
import static vn.soft.dc.recordengine.model.Preset.REVERB_DRY;
import static vn.soft.dc.recordengine.model.Preset.REVERB_MIX;
import static vn.soft.dc.recordengine.model.Preset.REVERB_ROOMSIZE;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WET;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WIDTH;
import static vn.soft.dc.recordengine.util.FileUtils.short2byte;

/**
 * Created by Le Duc Chung on 2018-01-17.
 * on project 'RecordEngine30'
 */

public class RecorderEngine {

    private static final String LIB_CPP = "record-engine";
    private OnRecordEventListener onRecordEventListener;
    private AudioCalculator mAudioCalculator;
    private int thresholdAmp = 1200;

    @SuppressWarnings("unused")
    public void onInitDoneListener() {
        if (onRecordEventListener == null) return;
        onRecordEventListener.onInitSuccess();
    }

    public RecorderEngine(int sampleRate, int bufferSize) {
        mAudioCalculator = new AudioCalculator();
        FrequencyDomainWithRecorder(Environment.getExternalStorageDirectory() + "/record/" + System.currentTimeMillis() + ".wav", sampleRate, bufferSize);
    }

    @SuppressWarnings("unused")
    public void setThresholdAmp(int thresholdAmp) {
        this.thresholdAmp = thresholdAmp;
    }

    @SuppressWarnings("unused")
    public void onSampleRecordListener(final short[] sample) {
        if (onRecordEventListener == null) return;
        mAudioCalculator.setBytes(short2byte(sample));
        int amp = mAudioCalculator.getAmplitude();
        if (amp < thresholdAmp) return;
        onRecordEventListener.onFrequencyListener(mAudioCalculator.getFrequency(sample.length));
    }

    public void enableEffectVocal(boolean enable) {
        enableEffect(enable);
    }

    public void startRecord(String path) {
        startRecordFilePath(path);
    }

    public void stopRecord() {
        stopRecordFile();
    }

    public void changeEffect(Preset preset) {
        onFxReverbValue(REVERB_DRY, preset.getDryReverb());
        onFxReverbValue(REVERB_WET, preset.getWetReverb());
        onFxReverbValue(REVERB_MIX, preset.getMixReverb());
        onFxReverbValue(REVERB_WIDTH, preset.getWidthReverb());
        onFxReverbValue(REVERB_DAMP, preset.getDampReverb());
        onFxReverbValue(REVERB_ROOMSIZE, preset.getRoomsizeReverb());

        float dry = preset.getDryEcho();
        float wet = preset.getWetEcho();
        float bpm = preset.getBeatsEcho();
        float beats = preset.getBeatsEcho();
        float decay = preset.getDecayEcho();
        float mix = preset.getMixEcho();
        setEchoValue(dry, wet, bpm, beats, decay, mix);

        float compThreshold, compRatio, compRelease, compAttack, compWet;
        int compHP;

        compHP = preset.getHpCut();
        compThreshold = preset.getThresholdCompressor();
        compRatio = preset.getRatioCompressor();
        compRelease = preset.getReleaseCompressor();
        compAttack = preset.getAttackCompressor();
        compWet = preset.getDryCompressor();

        onCompressorValue(compWet, compRatio, compAttack, compRelease, compThreshold, compHP);

        onProcessBandEQ((preset.getValueEQ0())
                , (preset.getValueEQ1())
                , (preset.getValueEQ2())
                , (preset.getValueEQ3())
                , (preset.getValueEQ4())
                , (preset.getValueEQ5())
                , (preset.getValueEQ6())
                , (preset.getValueEQ7())
                , (preset.getValueEQ8())
                , (preset.getValueEQ9()));

        onBandValues(preset.getBass(), preset.getMid(), preset.getHi());
    }

    public void setOnRecordEventListener(OnRecordEventListener onRecordEventListener) {
        this.onRecordEventListener = onRecordEventListener;
    }

    private native void enableEffect(boolean enable);

    private native void FrequencyDomainWithRecorder(String path, int sampleRate, int bufferSize);

    @SuppressWarnings("unused")
    private native void startRecordFile();

    private native void startRecordFilePath(String pathTarget);

    @SuppressWarnings("unused")
    private native void startRecordWithOffset(int offset);

    private native void stopRecordFile();

    private native void setEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix);

    private native void onProcessBandEQ(float value0, float value1, float value2, float value3,
                                        float value4, float value5, float value6, float value7,
                                        float value8, float value9);

    private native void onBandValues(float low, float mid, float high);

    private native void onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                                          float threshold, float hpCutOffHz);

    @SuppressWarnings("SpellCheckingInspection")
    private native void onFxReverbValue(int param, float scaleValue);

    static {
        System.loadLibrary(LIB_CPP);
    }

    @SuppressWarnings("unused")
    public interface OnRecordEventListener {
        void onInitSuccess();

        void onFrequencyListener(double freq);
    }
}