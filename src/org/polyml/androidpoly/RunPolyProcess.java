package org.polyml.androidpoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Field;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RunPolyProcess  {

	private OutStream outStream;
	private PrintStream writer;
	private PolyGUIActivity activity;
	private String heapSize;
	private Process process;

	public RunPolyProcess(PolyGUIActivity activity)
	{
		this.outStream = activity;
		this.activity = activity;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		heapSize = prefs.getString("heap_size", "100M");
	}

	// Actually create the process and handle output.  This is
	// run on the parent thread.
	private void runProcess()
	{
		// Start the thread to connect to the new process.
		try {
		    // Executes the command.  The simplest way to get the Poly executable included in the
			// APK is to pretend that it is a shared library and name it libXXX.so.
			String libPath = activity.getFilesDir().getParentFile().getPath();
				
		    process =
		    		Runtime.getRuntime().exec(
		    				libPath + "/lib/libpolyexecutable.so -i --maxheap " + heapSize);

		    Reader reader = new BufferedReader(
		            	new InputStreamReader(process.getInputStream()));
		    
		    writer = new PrintStream(process.getOutputStream(), true);
		    
		    // This thread reads from the Poly process until it terminates
		    int read;
		    char[] buffer = new char[4096];
		    while ((read = reader.read(buffer)) > 0) {
		    	outStream.write(new String(buffer, 0, read));
		    }
		    reader.close();
		    
		    // Waits for the command to finish.
		    process.waitFor();
		    // Terminate the GUI
		    activity.notifyDone();
		    
		} catch (IOException e) {
		    throw new RuntimeException(e);
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		}
	}
	
	// Handle input from the input box.
	public void handleInput(String s) {
		if (writer != null)
			writer.println(s);
	}
	
	public void sendInterrupt() {
		if (process != null) {
			// This is a mess - the pid is a hidden field.  Given that
			// Android provides sendSignal this is really an oversight.
			try {
				Field fs  = process.getClass().getDeclaredField("id");
				fs.setAccessible(true);
				int pid = fs.getInt(process);
				android.os.Process.sendSignal(pid, 2);
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}
	
	public void start()
	{
		// Begin the output thread.
		Thread thread = new Thread() {
			public void run() {
				runProcess();
			}
		};
		thread.start();
		
	}
}
