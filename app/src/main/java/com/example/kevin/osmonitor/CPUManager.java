package com.example.kevin.osmonitor;

import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CPUManager {
    private static final String CPU_UPTIME = "/proc/uptime";
    private static final String PROC_STAT = "/proc/stat";

    private static final String TAG = CPUManager.class.getSimpleName();

    public float readOverallCPUUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile(PROC_STAT, "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public double calculateCPUUsagebyProcess(int pid) {
        try {
            RandomAccessFile uptimeReader = new RandomAccessFile(CPU_UPTIME, "r");
            String uptimeLoad = uptimeReader.readLine().split(" ")[0];
            float uptime = Float.parseFloat(uptimeLoad);
            Log.d(TAG, "" + uptime);

            RandomAccessFile reader = new RandomAccessFile("/proc/" + pid + "/stat", "r");
            String[] load = reader.readLine().split(" ");
            long utime = Long.parseLong(load[13]);
            long stime = Long.parseLong(load[14]);
            long cutime = Long.parseLong(load[15]);
            long cstime = Long.parseLong(load[16]);
            long starttime = Long.parseLong(load[21]);

            Log.d(TAG, "" + utime + " " + stime + " " + starttime);

            double seconds = uptime - (starttime / 100);
            Log.d(TAG, "seconds: " + seconds);
            double totalTime = utime + stime + cutime + cstime;
            Log.d(TAG, "total time: " + totalTime);
            Log.d(TAG, "" + totalTime / 100);
            double cpuUsage = 100 * ((totalTime / 100) / seconds);
            Log.d(TAG, "" + cpuUsage);
            return cpuUsage;


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
