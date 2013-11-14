package org.polyml.androidpoly;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

public class DisplayInputConnection extends BaseInputConnection {
	DisplayView display;
	Editable editable;
	
	public DisplayInputConnection(View targetView, Editable editable) {
		super(targetView, true);
		display = (DisplayView)targetView;
		this.editable = editable;
	}

	@Override
	public boolean commitText(CharSequence text, int newCursorPosition) {
		editable.append(text);
		display.append(text);
		return true;
	}

	@Override
	public boolean sendKeyEvent(KeyEvent event) {
		display.setSelection(display.getText().length());
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_DEL:
				{
					int length = editable.length();
					if (length == 0)
						return true;
					editable.replace(length-1, length, "");
					return true;
				}
					
				case KeyEvent.KEYCODE_ENTER:
				{
					display.sendInputText(editable.toString());
					editable.clear();
					display.append("\n");
					return true;
				}
				
				default:
				{
					int ch = event.getUnicodeChar();
					if (ch < 256) {
						String s = Character.toString((char)ch);
						editable.append(s);
						display.append(s);
					}
					return true;
				}
			}
		}
		return super.sendKeyEvent(event);
	}

	@Override
	public Editable getEditable() {
		return editable;
	}

}
