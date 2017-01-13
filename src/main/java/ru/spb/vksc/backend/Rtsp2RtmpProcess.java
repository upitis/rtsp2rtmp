package ru.spb.vksc.backend;

import java.io.IOException;

/**
 * Created by upitis on 11.01.2017.
 */
public class Rtsp2RtmpProcess {
    private Process process;
    private String rtspUrl;
    private String rtmpUrl;

    public Rtsp2RtmpProcess(String rtspUrl, String rtmpUrl) {
        this.rtspUrl = rtspUrl;
        this.rtmpUrl = rtmpUrl;
        start();
    }

    private void start() {
        if (process == null) {
            try {
//                process = Runtime.getRuntime().exec("cmd /c hostname");
//                String[] cmd = {"/bin/sh", "-c", "ffmpeg -i rtsp://root:TANDBERG@192.168.110.204/axis-media/media.amp -rtsp_transport tcp -vcodec copy -f flv -r 25 -s 1920x1080 -acodec aac rtmp://10.128.2.102:1935/live/vksstream", "&"};
                String[] cmd = {"/bin/bash", "-c", "ffmpeg -i "+rtspUrl+" -rtsp_transport tcp -vcodec copy -f flv -r 25" +
                        " -s 1920x1080 -acodec aac "+rtmpUrl+" </dev/null >/dev/null 2>/dev/null &"};
                process = Runtime.getRuntime().exec(cmd);

            } catch (IOException e) {
            }
        }
    }

}
