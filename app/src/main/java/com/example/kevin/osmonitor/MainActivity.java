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

import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get running process
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        if (runningProcesses != null && runningProcesses.size() > 0) {
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }

        // Get overall memory usage
        MemoryInfo mi = new MemoryInfo();
        manager.getMemoryInfo(mi);
        Log.d(TAG, "Available memory (MB): " + mi.availMem / 1048576L);
        Log.d(TAG, "Total memory (MB): " + mi.totalMem / 1048576L);

        CPUManager cpuManager = new CPUManager();
        float cpuUsage = cpuManager.readOverallCPUUsage();
        Log.d(TAG, "CPU usage: " + cpuUsage + "%");

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

        CPUManager cpuManager = new CPUManager();
        double cpuUsage = cpuManager.calculateCPUUsagebyProcess(pid);

        String line1 = "PID: " + pid + "\n";
        String line2 = "CPU Usage: " + cpuUsage + "%\n";
        String line3 = "Memory: " + pss/1024 + "MB / " + clean/1024 + "MB / " + dirty/1024 + "MB\n";

        Toast.makeText(getApplicationContext(), line1 + line2 + line3, Toast.LENGTH_LONG).show();
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
