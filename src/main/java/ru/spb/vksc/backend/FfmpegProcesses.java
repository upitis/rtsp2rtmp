package ru.spb.vksc.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by upitis on 12.01.2017.
 */
public class FfmpegProcesses {
    public static List<String> getFfmepgProcesses(){
        List<String> processes = new ArrayList<>();
        String[] cmd = {"/bin/bash", "-c", "ps -eo pid,cmd | grep \"[f]fmpeg -i rtsp\""};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = bri.readLine())!= null) {
                processes.add(s);
            }
            bri.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processes;
    }

    public static void killProcess(String pid){
        try {
            Process process = Runtime.getRuntime().exec("kill -9 "+ pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
