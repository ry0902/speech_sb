package com.bbnc.voice.tts;

import com.bbnc.voice.wav.ConvertUtils;
import com.iflytek.cloud.speech.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TtsTool {

    private static Logger LOGGER = LoggerFactory.getLogger(TtsTool.class);

    private Object lock = new Object();

    // 语音合成对象
    private SpeechSynthesizer mTts;

    private String ttsPcmDir;

    public TtsTool(String appId, String ttsPcmDir, int wordCount) {
        LOGGER.info("------Speech Utility init tts------");
        this.ttsPcmDir = ttsPcmDir;
        SpeechUtility.createUtility(SpeechConstant.APPID + "=" + appId);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer();
        if (mTts != null) {
            float vilocity = (float) (-2000 * (5.0 / wordCount) + 500);
            if(vilocity < 0) {
                vilocity = 0;
            }
            System.out.println("vilocity = " + vilocity);
            mTts.setParameter(SpeechConstant.SPEED, vilocity+"");//设置语速，范围 0~100
            mTts.setParameter(SpeechConstant.PITCH, "50");//设置语调，范围 0~100
            mTts.setParameter(SpeechConstant.VOLUME, "50");//设置音量，范围 0~100
            // 设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        } else {
            LOGGER.error("tts handler init fail");
        }
    }

    public String textToVoice(String waveName, String text) {
        try {
            String pcmPath = ttsPcmDir + waveName + ".pcm";
            // 设置合成音频保存位置（可自定义保存位置），默认不保存
            mTts.synthesizeToUri(text, pcmPath, mSynListener);

            synchronized (lock) {
                lock.wait();
            }

            return pcmPath;
        } catch (Exception e) {
            LOGGER.error("textToVoice get exception:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合成监听器
     */
    SynthesizeToUriListener mSynListener = new SynthesizeToUriListener() {

        public void onBufferProgress(int progress) {
            LOGGER.info("*************合成进度*************" + progress);

        }

        public void onSynthesizeCompleted(String uri, SpeechError error) {
            if (error == null) {
                LOGGER.info("*************合成成功*************");
                LOGGER.info("合成音频生成路径：" + uri);

                String newUri = uri.replace(".pcm",".wav");
                //因为科大讯飞转换为 .pcm 文件，这里自己转换成 .wav
                try {
                    ConvertUtils.convertPcm2Wav(uri, newUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("******合成失败*******" + error.getErrorCode()
                        + "*************");
            }
            synchronized (lock) {
                LOGGER.info("通知合成成功");
                lock.notify();
            }

        }


        @Override
        public void onEvent(int eventType, int arg1, int arg2, int arg3, Object obj1, Object obj2) {}

    };
}
