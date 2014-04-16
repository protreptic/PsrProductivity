package ru.magnat.sfs.ui.android.controls;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OnValueChangedListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;

public class NumericPadLayout extends TableLayout implements InputValueControl {
	final ActionCallback _callback = new ActionCallback();

	public NumericPadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.numeric_pad, this, true);
	
		((Button) findViewById(R.id.button_0)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_1)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_2)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_3)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_4)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_5)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_6)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_7)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_8)).setOnClickListener(_callback);
		((Button) findViewById(R.id.button_9)).setOnClickListener(_callback);
		((ImageButton) findViewById(R.id.button_calc)).setOnClickListener(_callback);
		((ImageButton) findViewById(R.id.button_erase)).setOnClickListener(_callback);
	}
	
	class ActionCallback implements OnClickListener {
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.button_calc) {
				if (!_firstClick)
					onCommit(_value);
				else
					onCommit(null);
				resetValue(_value);
				return ;
			}

			switch (id) {
			case R.id.button_0:
				_value = _value * 10;
				break;
			case R.id.button_1:
				_value = _value * 10 + 1;
				break;
			case R.id.button_2:
				_value = _value * 10 + 2;
				break;
			case R.id.button_3:
				_value = _value * 10 + 3;
				break;
			case R.id.button_4:
				_value = _value * 10 + 4;
				break;
			case R.id.button_5:
				_value = _value * 10 + 5;
				break;
			case R.id.button_6:
				_value = _value * 10 + 6;
				break;
			case R.id.button_7:
				_value = _value * 10 + 7;
				break;
			case R.id.button_8:
				_value = _value * 10 + 8;
				break;
			case R.id.button_9:
				_value = _value * 10 + 9;
				break;
			case R.id.button_erase:
				_value = 0;
				break;
			}
			fireEvent(_value);
			_firstClick = false;
		}
	}
	
	boolean _firstClick = true;
	int _value;

	public void resetValue(Object value) {
		_firstClick = true;
		_value = (Integer) value;
	}

	private final Set<OnValueChangedListener> _eventListeners = new CopyOnWriteArraySet<OnValueChangedListener>();

	public void setOnValueChangedListener(OnValueChangedListener eventListener) {
		if (eventListener == null) {
			return;
		}
		_eventListeners.clear();
		_eventListeners.add(eventListener);

	}

	public void addOnValueChangedListener(OnValueChangedListener eventListener) {
		if (eventListener == null) {
			return;
		}
		_eventListeners.add(eventListener);
	}

	public void removeOnValueChangedListener(
			OnValueChangedListener eventListener) {
		if (eventListener == null) {
			return;
		}
		_eventListeners.remove(eventListener);
	}

	class ValueChangedDispatch extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			for (OnValueChangedListener eventListener : _eventListeners) {
				eventListener.onValueChanged(this, params[0]);
			}
			return null;
		}
	}

	private void fireEvent(int value) {
		// new ValueChangedDispatch().execute(value);
		for (OnValueChangedListener eventListener : _eventListeners) {
			eventListener.onValueChanged(this, value);
		}
	}

	private final Set<IEventListener> _eventCommitListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnCommitListener(OnCommitListener eventListener) {
		EventListenerSubscriber.setListener(_eventCommitListeners,
				eventListener);
	}

	public void addOnCommitListener(OnCommitListener eventListener) {
		EventListenerSubscriber.addListener(_eventCommitListeners,
				eventListener);
	}

	public void onCommit(Integer value) {

		for (IEventListener eventListener : _eventCommitListeners)
			((OnCommitListener) eventListener).onCommit(this, value);
	}

}
