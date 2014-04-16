package ru.magnat.sfs.ui.android.doc.order;

import java.util.Calendar;
import java.util.StringTokenizer;

import ru.magnat.sfs.android.R;
import android.R.color;
import android.content.Context;
import android.text.format.DateFormat;
import ru.magnat.sfs.android.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class DeliveryTimeChoiseDialogView extends RelativeLayout {

	private Context context;

	private View view;
	private LayoutInflater layoutInflater;

	private TextView tv1;
	private TextView tv2;
	private TimePicker tp1;
	private TimePicker tp2;

	private Calendar calendar;
	private TextView result;

	private String before;

	public DeliveryTimeChoiseDialogView(Context context, String before) {
		super(context);
		this.context = context;
		if (before == null || before.isEmpty()) {

			this.before = "Любое";
		} else {

			this.before = before;
		}
	}

	public View Inflate() {

		//context.setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		layoutInflater = LayoutInflater.from(this.context);

		view = layoutInflater.inflate(R.layout.delivery_time_choise_dialog_layout, null);
		view.setBackgroundColor(color.white);
		calendar = Calendar.getInstance();

		RadioButton rbBefore = (RadioButton) this.view.findViewById(R.id.rbBefore);
		rbBefore.setOnClickListener(listener);

		RadioButton rbAfter = (RadioButton) this.view.findViewById(R.id.rbAfter);
		rbAfter.setOnClickListener(listener);

		RadioButton rbBetween = (RadioButton) this.view.findViewById(R.id.rbBetween);
		rbBetween.setOnClickListener(listener);

		RadioButton rbAny = (RadioButton) this.view.findViewById(R.id.rbAny);
		rbAny.setOnClickListener(listener);

		result = (TextView) view.findViewById(R.id.result);

		tv1 = (TextView) view.findViewById(R.id.picture_gallery_comment_item_author);
		tv2 = (TextView) view.findViewById(R.id.picture_gallery_comment_item_date);
		tp1 = (TimePicker) view.findViewById(R.id.timePicker1);
		tp2 = (TimePicker) view.findViewById(R.id.timePicker2);

		rbBefore = (RadioButton) view.findViewById(R.id.rbBefore);
		rbAfter = (RadioButton) view.findViewById(R.id.rbAfter);
		rbBetween = (RadioButton) view.findViewById(R.id.rbBetween);
		rbAny = (RadioButton) view.findViewById(R.id.rbAny);

		tp1.setIs24HourView(true);
		tp1.setCurrentHour(10);
		tp1.setCurrentMinute(0);
		tp1.setOnTimeChangedListener(listener2);

		tp2.setIs24HourView(true);
		tp2.setCurrentHour(18);
		tp2.setCurrentMinute(0);
		tp2.setOnTimeChangedListener(listener2);

		StringTokenizer tokenizer = new StringTokenizer(this.before);

		if (tokenizer.countTokens() == 1) {

			type = 4;

			tv1.setVisibility(View.INVISIBLE);
			tp1.setVisibility(View.INVISIBLE);
			tv2.setVisibility(View.INVISIBLE);
			tp2.setVisibility(View.INVISIBLE);

			rbBefore.setChecked(false);
			rbAfter.setChecked(false);
			rbBetween.setChecked(false);
			rbAny.setChecked(true);

			tv1.setText("Любое");

			String result = "";

			result += tv1.getText();

			this.result.setText(result);

			return this.view;
		} else if (tokenizer.countTokens() == 2) {

			if (tokenizer.nextToken().equals("До")) {

				type = 1;

				StringTokenizer tt = new StringTokenizer(tokenizer.nextToken(),
						":");

				tv1.setVisibility(View.VISIBLE);
				tp1.setVisibility(View.VISIBLE);
				tv2.setVisibility(View.INVISIBLE);
				tp2.setVisibility(View.INVISIBLE);

				rbBefore.setChecked(true);
				rbAfter.setChecked(false);
				rbBetween.setChecked(false);
				rbAny.setChecked(false);

				tv1.setText("До");

				tp1.setIs24HourView(true);
				tp1.setCurrentHour(Integer.valueOf(tt.nextToken()));
				tp1.setCurrentMinute(Integer.valueOf(tt.nextToken()));

				calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

				String result = "";

				result += tv1.getText() + " "
						+ (String) DateFormat.format("kk:mm", calendar);

				this.result.setText(result);

				return this.view;
			} else {

				// if (tokenizer.nextToken().equals("После")) {

				type = 2;

				StringTokenizer tt = new StringTokenizer(tokenizer.nextToken(),
						":");

				tv1.setVisibility(View.VISIBLE);
				tp1.setVisibility(View.VISIBLE);
				tv2.setVisibility(View.INVISIBLE);
				tp2.setVisibility(View.INVISIBLE);

				rbBefore.setChecked(false);
				rbAfter.setChecked(true);
				rbBetween.setChecked(false);
				rbAny.setChecked(false);

				tv1.setText("После");

				tp1.setIs24HourView(true);
				tp1.setCurrentHour(Integer.valueOf(tt.nextToken()));
				tp1.setCurrentMinute(Integer.valueOf(tt.nextToken()));

				calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

				String result = "";

				result += tv1.getText() + " "
						+ (String) DateFormat.format("kk:mm", calendar);

				this.result.setText(result);

				return this.view;
			}
		} else if (tokenizer.countTokens() == 4) {

			type = 3;

			tokenizer.nextToken();

			StringTokenizer tt1 = new StringTokenizer(tokenizer.nextToken(),
					":");

			tokenizer.nextToken();

			StringTokenizer tt2 = new StringTokenizer(tokenizer.nextToken(),
					":");

			tv1.setVisibility(View.VISIBLE);
			tp1.setVisibility(View.VISIBLE);

			tv2.setVisibility(View.VISIBLE);
			tp2.setVisibility(View.VISIBLE);

			rbBefore.setChecked(false);
			rbAfter.setChecked(false);
			rbBetween.setChecked(true);
			rbAny.setChecked(false);

			tv1.setText("Между");

			tp1.setCurrentHour(Integer.valueOf(tt1.nextToken()));
			tp1.setCurrentMinute(Integer.valueOf(tt1.nextToken()));

			calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

			String result = "";

			result += tv1.getText() + " "
					+ (String) DateFormat.format("kk:mm", calendar) + " "
					+ tv2.getText() + " ";

			tp2.setCurrentHour(Integer.valueOf(tt2.nextToken()));
			tp2.setCurrentMinute(Integer.valueOf(tt2.nextToken()));

			calendar.set(Calendar.HOUR_OF_DAY, tp2.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp2.getCurrentMinute());

			result += (String) DateFormat.format("kk:mm", calendar);

			this.result.setText(result);

			return this.view;
		}

		return this.view;
	}

	public String getResult() {

		return this.result.getText().toString();
	}

	private int type = 0;

	public void update() {

		String result = "";

		switch (type) {
		case 1: {

			tv1.setVisibility(View.VISIBLE);
			tp1.setVisibility(View.VISIBLE);

			tv2.setVisibility(View.INVISIBLE);
			tp2.setVisibility(View.INVISIBLE);

			tv1.setText("До");

			calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

			result += tv1.getText() + " "
					+ (String) DateFormat.format("kk:mm", calendar);

			Log.d("", result);

			DeliveryTimeChoiseDialogView.this.result.setText(result);
		}
			break;
		case 2: {

			tv1.setVisibility(View.VISIBLE);
			tp1.setVisibility(View.VISIBLE);

			tv2.setVisibility(View.INVISIBLE);
			tp2.setVisibility(View.INVISIBLE);

			tv1.setText("После");

			calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

			result += tv1.getText() + " "
					+ (String) DateFormat.format("kk:mm", calendar);

			Log.d("", result);

			DeliveryTimeChoiseDialogView.this.result.setText(result);
		}
			break;
		case 3: {

			tv1.setVisibility(View.VISIBLE);
			tp1.setVisibility(View.VISIBLE);

			tv2.setVisibility(View.VISIBLE);
			tp2.setVisibility(View.VISIBLE);

			tv1.setText("Между");

			calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

			result += tv1.getText() + " "
					+ (String) DateFormat.format("kk:mm", calendar) + " "
					+ tv2.getText() + " ";

			calendar.set(Calendar.HOUR_OF_DAY, tp2.getCurrentHour());
			calendar.set(Calendar.MINUTE, tp2.getCurrentMinute());

			result += (String) DateFormat.format("kk:mm", calendar);

			Log.d("", result);

			DeliveryTimeChoiseDialogView.this.result.setText(result);
		}
			break;
		case 4: {

			tv1.setVisibility(View.INVISIBLE);
			tp1.setVisibility(View.INVISIBLE);

			tv2.setVisibility(View.INVISIBLE);
			tp2.setVisibility(View.INVISIBLE);

			tv1.setText("Любое");

			result = tv1.getText().toString();

			Log.d("", result);

			DeliveryTimeChoiseDialogView.this.result.setText(result);
		}
			break;
		}
	}

	private TimePicker.OnTimeChangedListener listener2 = new TimePicker.OnTimeChangedListener() {

		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

			update();
		}
	};

	private View.OnClickListener listener = new View.OnClickListener() {

		public void onClick(View v) {

			String result = "";

			switch (v.getId()) {
			case R.id.rbBefore: {

				type = 1;

				tv1.setVisibility(View.VISIBLE);
				tp1.setVisibility(View.VISIBLE);

				tv2.setVisibility(View.INVISIBLE);
				tp2.setVisibility(View.INVISIBLE);

				tv1.setText("До");

				calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

				result += tv1.getText() + " "
						+ (String) DateFormat.format("kk:mm", calendar);

				Log.d("", result);

				DeliveryTimeChoiseDialogView.this.result.setText(result);
			}
				break;
			case R.id.rbAfter: {

				type = 2;

				tv1.setVisibility(View.VISIBLE);
				tp1.setVisibility(View.VISIBLE);

				tv2.setVisibility(View.INVISIBLE);
				tp2.setVisibility(View.INVISIBLE);

				tv1.setText("После");

				calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

				result += tv1.getText() + " "
						+ (String) DateFormat.format("kk:mm", calendar);

				Log.d("", result);

				DeliveryTimeChoiseDialogView.this.result.setText(result);
			}
				break;
			case R.id.rbBetween: {

				type = 3;

				tv1.setVisibility(View.VISIBLE);
				tp1.setVisibility(View.VISIBLE);

				tv2.setVisibility(View.VISIBLE);
				tp2.setVisibility(View.VISIBLE);

				tv1.setText("Между");

				calendar.set(Calendar.HOUR_OF_DAY, tp1.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp1.getCurrentMinute());

				result += tv1.getText() + " "
						+ (String) DateFormat.format("kk:mm", calendar) + " "
						+ tv2.getText() + " ";

				calendar.set(Calendar.HOUR_OF_DAY, tp2.getCurrentHour());
				calendar.set(Calendar.MINUTE, tp2.getCurrentMinute());

				result += (String) DateFormat.format("kk:mm", calendar);

				Log.d("", result);

				DeliveryTimeChoiseDialogView.this.result.setText(result);
			}
				break;
			case R.id.rbAny: {

				type = 4;

				tv1.setVisibility(View.INVISIBLE);
				tp1.setVisibility(View.INVISIBLE);

				tv2.setVisibility(View.INVISIBLE);
				tp2.setVisibility(View.INVISIBLE);

				tv1.setText("Любое");

				result = tv1.getText().toString();

				Log.d("", result);

				DeliveryTimeChoiseDialogView.this.result.setText(result);
			}
				break;
			}
		}
	};
}
