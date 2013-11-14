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
		return display.keyPress(event) || super.sendKeyEvent(event);
	}

	@Override
	public Editable getEditable() {
		return editable;
	}

}
