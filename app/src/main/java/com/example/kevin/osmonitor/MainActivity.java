package com.example.kevin.osmonitor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kevin.osmonitor.adapters.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

//    private final ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

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

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String process = (String) getListAdapter().getItem(position);
        int pid = processMap.get(process);

        Log.d(TAG, "on click: " + process);
//        Log.d(TAG, "This pid: " + pid);
//
        // Display data
        Toast.makeText(getApplicationContext(), "PID " + pid, Toast.LENGTH_LONG).show();
    }
}
