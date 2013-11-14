package org.polyml.androidpoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import android.content.Context;

public class RunPolyProcess  {

	private OutStream outStream;
	private PrintStream writer;
	private Context context;

	public RunPolyProcess(OutStream outStream, Context context)
	{
		this.outStream = outStream;
		this.context = context;
	}

	// Actually create the process and handle output.  This is
	// run on the parent thread.
	private void runProcess()
	{
		// Start the thread to connect to the new process.
		try {
		    // Executes the command.  The simplest way to get the Poly executable included in the
			// APK is to pretend that it is a shared library and name it libXXX.so.
			String libPath = context.getFilesDir().getParentFile().getPath();
				
		    Process process =
		    		Runtime.getRuntime().exec(
		    				libPath + "/lib/libpolyexecutable.so -i");

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
