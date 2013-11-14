package org.polyml.androidpoly;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class MainActivity extends Activity implements OutStream, Callback {

	private DisplayView display;
//	private EditText inputText;
	private RunPolyProcess polyProcess;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		display = (DisplayView)findViewById(R.id.outputDisplay);
		handler = new Handler(this);
		display.setHandler(handler);
		polyProcess = new RunPolyProcess(this, this);
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
			
			default:
				return super.onOptionsItemSelected(item);
				
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showAbout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	// Called to append output from the Poly process.
	// Because this is called on another thread it must send a message
	// to do its work.
	public void write(String s) {
		Message msg = Message.obtain();
		msg.what = 0;
		msg.setTarget(handler);
		msg.obj = s;
		msg.sendToTarget();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what)
		{
		case 0:
			display.append((String)msg.obj);
			break;
			
		case 1:
			polyProcess.handleInput((String)msg.obj);
			break;
		}
		return false;
	}
}
