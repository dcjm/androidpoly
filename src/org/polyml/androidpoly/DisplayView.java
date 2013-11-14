package org.polyml.androidpoly;

import android.text.ClipboardManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class DisplayView extends EditText {
	
	private Handler mainHandler;
	SpannableStringBuilder editable;

	public DisplayView(Context context) {
		super(context);
        init();
	}
    
    public DisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
   }

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        setFocusableInTouchMode(true);
        editable = (SpannableStringBuilder) Editable.Factory.getInstance().newEditable("");
    }
    
    public void setHandler(Handler handler) {
    	mainHandler = handler;
    }
    
    public void sendInputText(String s) {
    	if (mainHandler != null) {
    		Message msg = Message.obtain();
    		msg.what = 1;
    		msg.setTarget(mainHandler);
    		msg.obj = s;
    		msg.sendToTarget();
    	}
    }

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.actionLabel = null;
		outAttrs.label = "Test text";
		outAttrs.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
		outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE;//EditorInfo.IME_FLAG_FORCE_ASCII;
		return new DisplayInputConnection(this, editable);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		// We don't implement cut
		if (id == android.R.id.cut)
			return true;
		// If we are pasting, make sure it happens at the end
		if (id == android.R.id.paste) {
			setSelection(getText().length());
			// Add it to the text
			ClipboardManager clipBoard =
					(ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			CharSequence paste = clipBoard.getText();
			editable.append(paste);
		}
		return super.onTextContextMenuItem(id);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setSelection(getText().length());
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
					sendInputText(editable.toString());
					editable.clear();
					append("\n");
					return true;
				}
				
				default:
				{
					int ch = event.getUnicodeChar();
					if (ch > 0 && ch < 256) {
						String s = Character.toString((char)ch);
						editable.append(s);
						append(s);
						return true;
					}
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}


}
