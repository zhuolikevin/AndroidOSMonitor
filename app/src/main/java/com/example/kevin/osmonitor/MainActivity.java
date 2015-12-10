package com.example.kevin.osmonitor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.MemoryInfo;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kevin.osmonitor.adapters.ListAdapter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

// private final ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

//    private HashMap<String, Integer> processMap = new HashMap<String, Integer>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get running process
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        if (runningProcesses != null && runningProcesses.size() > 0) {
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running (not a chance :))
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }

        // Get overall memory usage
        MemoryInfo mi = new MemoryInfo();
        manager.getMemoryInfo(mi);
        Log.d(TAG, "Available memory (MB): " + mi.availMem / 1048576L);
        Log.d(TAG, "Total memory (MB): " + mi.totalMem / 1048576L);

        float cpuUsage = readUsage();
        Log.d(TAG, "CPU usage: " + cpuUsage + "%");

        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            Log.d(TAG, load);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        float betterLevel = getBatteryLevel();
        Log.d(TAG, "Current battery remain: " + betterLevel + "%");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        String process = (String) getListAdapter().getItem(position);
        int pid = ((RunningAppProcessInfo)getListAdapter().getItem(position)).pid;

//        Log.d(TAG, "on click: " + process);

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int[] pids = {pid};
        Debug.MemoryInfo[] memoryInfos = manager.getProcessMemoryInfo(pids);
        int pss = memoryInfos[0].getTotalPss();
        int clean = memoryInfos[0].getTotalPrivateClean();
        int dirty = memoryInfos[0].getTotalPrivateDirty();
        double cpuUsage = calculateCPUUsagebyProcess(pid);

        String line1 = "PID: " + pid + "\n";
        String line2 = "CPU Usage: " + cpuUsage + "%\n";
        String line3 = "Memory: " + pss/1024 + "MB / " + clean/1024 + "MB / " + dirty/1024 + "MB\n";

        Toast.makeText(getApplicationContext(), line1 + line2 + line3, Toast.LENGTH_LONG).show();
    }

    private double calculateCPUUsagebyProcess(int pid) {
        try {
            RandomAccessFile uptimeReader = new RandomAccessFile("/proc/uptime", "r");
            String uptimeLoad = uptimeReader.readLine().split(" ")[0];
            float uptime = Float.parseFloat(uptimeLoad);
            Log.d(TAG, "" + uptime);

            RandomAccessFile reader = new RandomAccessFile("/proc/" + pid + "/stat", "r");
            String[] load = reader.readLine().split(" ");
            long utime = Long.parseLong(load[13]);
            long stime = Long.parseLong(load[14]);
            long starttime = Long.parseLong(load[21]);

            Log.d(TAG, "" + utime + " " + stime + " " + starttime);

            double seconds = uptime - (starttime / 100);
            Log.d(TAG, "seconds: " + seconds);
            double totalTime = utime + stime;
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

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
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

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }
}
