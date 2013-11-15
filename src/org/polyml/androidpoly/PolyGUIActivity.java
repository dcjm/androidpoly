package org.polyml.androidpoly;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PolyGUIActivity extends Activity implements OutStream, Callback {

	private DisplayView display;
	private RunPolyProcess polyProcess;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		display = (DisplayView)findViewById(R.id.outputDisplay);
		handler = new Handler(this);
		display.setHandler(handler);
		polyProcess = new RunPolyProcess(this);
		polyProcess.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.about_poly:
				showAbout();
				return true;
				
			case R.id.settings:
			{
				startActivity(new Intent(this, PolySettings.class));
				return true;
			}
			
			case R.id.interrupt:
			{
				if (polyProcess != null)
					polyProcess.sendInterrupt();
				return true;
			}
			
			default:
				return super.onOptionsItemSelected(item);
				
		}
	}

	private void showAbout() {
		String version;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e) {
			version = "Unknown";
		}
		
		String text = new String("Poly/ML for Android version" + version + "\n\n");
		text += getString(R.string.all_about_poly);
		View about = getLayoutInflater().inflate(R.layout.aboutpoly, null);
		TextView aboutText = (TextView)about.findViewById(R.id.aboutText);
		aboutText.setBackgroundColor(Color.WHITE);
		aboutText.setText(text);
		Linkify.addLinks(aboutText, Linkify.ALL);
		Builder dialogue = new AlertDialog.Builder(this);
		dialogue.setTitle("About" + getString(R.string.app_name));
		dialogue.setCancelable(true);
		dialogue.setIcon(R.drawable.ic_launcher);
		dialogue.setPositiveButton("OK", null);
		dialogue.setView(about);
		dialogue.show();
	}
	
	private static final int WRITE_TEXT = 0;
	public static final int HANDLE_INPUT = 1;
	private static final int POLY_PROCESS_DIED = 2;

	// Called to append output from the Poly process.
	// Because this is called on another thread it must send a message
	// to do its work.
	public void write(String s) {
		Message msg = Message.obtain();
		msg.what = WRITE_TEXT;
		msg.setTarget(handler);
		msg.obj = s;
		msg.sendToTarget();
	}
	
	// Called when the Poly process has terminated
	public void notifyDone() {
		Message msg = Message.obtain();
		msg.what = POLY_PROCESS_DIED;
		msg.setTarget(handler);
		msg.sendToTarget();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what)
		{
		case WRITE_TEXT:
			display.append((String)msg.obj);
			break;
			
		case HANDLE_INPUT:
			if (polyProcess != null)
				polyProcess.handleInput((String)msg.obj);
			break;
			
		case POLY_PROCESS_DIED:
			finish();
		}
		return false;
	}
}
