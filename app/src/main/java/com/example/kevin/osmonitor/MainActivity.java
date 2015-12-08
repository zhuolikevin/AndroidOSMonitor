package com.example.kevin.osmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.MemoryInfo;
import android.app.ListActivity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kevin.osmonitor.adapters.ListAdapter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

// private final ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

    private HashMap<String, Integer> processMap = new HashMap<String, Integer>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get running process
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        List<String> allProcesses = new ArrayList<String>();

        if (runningProcesses != null && runningProcesses.size() > 0) {
            Log.d(TAG, "Current processes number" + runningProcesses.size());
            for (RunningAppProcessInfo runningProcess: runningProcesses) {
                allProcesses.add(runningProcess.processName);
                processMap.put(runningProcess.processName, runningProcess.pid);
            }

        }

        List<ActivityManager.RunningServiceInfo> runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        Log.d(TAG, "running services : " + runningServiceInfos.size());
        for (ActivityManager.RunningServiceInfo rsi: runningServiceInfos) {
            Log.d(TAG, "Service pid: " + rsi.pid + " process name: " + rsi.process);
            if (processMap.get(rsi.process) == null) {
                allProcesses.add(rsi.process);
            }
            processMap.put(rsi.process, rsi.pid);
        }

        setListAdapter(new ListAdapter(this, allProcesses));

        // Get overall memory usage
        MemoryInfo mi = new MemoryInfo();
        manager.getMemoryInfo(mi);
        Log.d(TAG, "Available memory (MB): " + mi.availMem / 1048576L);
        Log.d(TAG, "Total memory (MB): " + mi.totalMem / 1048576L);

        float cpuUsage = readUsage();
        Log.d(TAG, "CPU usage: " + cpuUsage);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String process = (String) getListAdapter().getItem(position);
        int pid = processMap.get(process);

        Log.d(TAG, "on click: " + process);
//        Log.d(TAG, "This pid: " + pid);

//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        Toast.makeText(getApplicationContext(), "PID " + pid, Toast.LENGTH_LONG).show();
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
}
