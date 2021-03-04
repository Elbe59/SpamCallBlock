package com.alioptak.spamcallblock.service;

import android.content.Context;
import android.util.Log;

import com.alioptak.spamcallblock.Singleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class StorageManager {

    private static String TAG = "StorageManager";
    private static String fileName = "config.txt";

    public static void writeStringAsFile(Context context, final ArrayList<String> fileContents) {
        try {
            PrintWriter out = new PrintWriter(new File(context.getFilesDir(), fileName));
            out.print("");
            if(fileContents !=null){
                for(String num : fileContents){
                    out.println(num + " ");
                }
            }
            out.println(Singleton.getInstance().getSTATUS_APPLICATION() + " ");
            out.close();
        } catch (IOException e) {
            Log.d(TAG, String.valueOf(e));
        }
    }

    public static ArrayList<String> readFileAsString(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line + " ");

        } catch (FileNotFoundException e) {
            Log.d(TAG, String.valueOf(e));
        } catch (IOException e) {
            Log.d(TAG, String.valueOf(e));
        }
        Set<String> set_blocked = new HashSet<String>(Arrays.asList(stringBuilder.toString().split(" ")));
        ArrayList<String> blocked = new ArrayList<String>(set_blocked);
        String status = blocked.get(blocked.size()-1);
        if(status.contentEquals("true")){
            Singleton.getInstance().setSTATUS_APPLICATION(true);
            Log.d(TAG, "true");
        }
        else{
            Singleton.getInstance().setSTATUS_APPLICATION(false);
            Log.d(TAG, "false");
        }
        blocked.remove(blocked.size()-1);
        Log.d(TAG, blocked.toString());
        return blocked;
    }

}
