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
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

//    private final ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get running process
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        List<String> allProcesses = new ArrayList<String>();

        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            Log.d(TAG, "Current processes number" + runningProcesses.size());
            for (RunningAppProcessInfo runningProcess: runningProcesses) {
                allProcesses.add(runningProcess.processName);
            }

        }

        List<ActivityManager.RunningServiceInfo> runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        Log.d(TAG, "running services : " + runningServiceInfos.size());
        for (ActivityManager.RunningServiceInfo rsi: runningServiceInfos) {
            Log.d(TAG, "Service pid: " + rsi.pid + " process name: " + rsi.process);
            allProcesses.add(rsi.process);
        }

        setListAdapter(new ListAdapter(this, allProcesses));

    }

//    protected void onResume() {
//        super.onResume();
//
//        Log.d(TAG, "onResume");
//        //Get running process
//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
////        List<String> allProcesses = new ArrayList<String>();
//
//        if (runningProcesses != null && runningProcesses.size() > 0) {
//            // Set data to the list adapter
//            Log.d(TAG, "Resume Current processes number " + runningProcesses.size());
////            for (RunningAppProcessInfo runningProcess: runningProcesses) {
////                allProcesses.add(runningProcess.pid);
////            }
//            setListAdapter(new ListAdapter(this, runningProcesses));
//        }
//
//        List<ActivityManager.RunningServiceInfo> runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
//        Log.d(TAG, "Resume running services : " + runningServiceInfos.size());
//        for (ActivityManager.RunningServiceInfo rsi: runningServiceInfos) {
//            Log.d(TAG, "Resume Service pid: " + rsi.pid + " process name: " + rsi.process);
//        }
//    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        long send = 0;
        long received = 0;
        // Get UID of the selected process
//        int uid = ((RunningAppProcessInfo) getListAdapter().getItem(position)).uid;
//        int pid = ((RunningAppProcessInfo) getListAdapter().getItem(position)).pid;

        Log.d(TAG, "on click");
//        Log.d(TAG, "This pid: " + pid);
//
//        // Get traffic data
//        received = TrafficStats.getUidRxBytes(uid);
//        send = TrafficStats.getUidTxBytes(uid);
//
//        // Display data
//        Toast.makeText(getApplicationContext(), "UID " + uid + " details...\n send: " + send / 1000 + "kB" + " \n recived: " + received / 1000 + "kB", Toast.LENGTH_LONG).show();
    }
}
