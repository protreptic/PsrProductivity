package ru.magnat.sfs.ui.android.controls;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.OnValueChangedListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.view.View.OnClickListener;

public class ThreeLevelLogicPadLayout extends TableLayout implements OnClickListener, InputValueControl {

	public ThreeLevelLogicPadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.three_level_logic_pad, this, true);
		((ImageButton) findViewById(R.id.button_yes)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.button_no)).setOnClickListener(this);
		((Button) findViewById(R.id.button_na)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.button_erase)).setOnClickListener(this);
	}

	Float _value;

	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.button_yes:
			_value = (float) 1.0;
			break;
		case R.id.button_no:
			_value = (float) -1;
			break;
		case R.id.button_na:
			_value = (float) 0;
			break;

		case R.id.button_erase:
			_value = null;
			break;
		}
		fireEvent(_value);

	}

	private final Set<OnValueChangedListener> _eventListeners = new CopyOnWriteArraySet<OnValueChangedListener>();

	public void setOnValueChangedListener(OnValueChangedListener eventListener) {
		if (eventListener == null) {
			throw new NullPointerException();
		}
		_eventListeners.clear();
		_eventListeners.add(eventListener);

	}

	public void addOnValueChangedListener(OnValueChangedListener eventListener) {
		if (eventListener == null) {
			throw new NullPointerException();
		}
		_eventListeners.add(eventListener);
	}

	public void removeOnValueChangedListener(
			OnValueChangedListener eventListener) {
		if (eventListener == null) {
			throw new NullPointerException();
		}
		_eventListeners.remove(eventListener);
	}

	private void fireEvent(Float value) {
		for (OnValueChangedListener eventListener : _eventListeners) {
			eventListener.onValueChanged(this, value);
		}
	}

	public void resetValue(Object value) {

	}

}
