package ru.magnat.sfs.widget;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PromotionIndicatorView extends RelativeLayout {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private ImageView mColorPoint;
	private TextView mDiscountPoint;
	
	public PromotionIndicatorView(Context context) {
		super(context, null, 0);
		
		init(context);
	}
	
	public PromotionIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		
		init(context);
	}
	
	public PromotionIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mLayoutInflater.inflate(R.layout.promotion_indicator_view, this);
		
		mColorPoint = (ImageView) findViewById(R.id.color_point);
		mDiscountPoint = (TextView) findViewById(R.id.discount_point); 
		//mDiscountPoint.setTypeface(SFSActivity.getInstance().getFonts().getTypeface("RobotoCondensed-Bold"));
	}
	
	public void setDiscountPointValue(Float discount) {
		String discountText = new String();
		if (discount > 0) {
			discountText = "-" + String.format("%.0f%% ", discount);
		} else {
			discountText = "+" + String.format("%.0f%% ", discount);
		}
		mDiscountPoint.setText(discountText); 
	}
	
	public void setDiscountPointDescription(String description) {
		setTag(description);
		setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View view) {
				String description = (String) view.getTag();
				if (description.isEmpty()) {
					return;
				}
				Dialogs.createDialog("", description, Command.NO_OP).show();
			}
		}); 
	}
	
	public void enabledDiscountPoint() {
		mDiscountPoint.setVisibility(View.VISIBLE);
		mColorPoint.setVisibility(View.GONE);
	}
	 
	public void disableDiscountPoint() {
		mDiscountPoint.setVisibility(View.GONE); 
	}
	
	public void hide() {
		mDiscountPoint.setVisibility(View.GONE); 
		mColorPoint.setVisibility(View.GONE);
	}
	
	
	public void enabledColorPoint() {
		mColorPoint.setVisibility(View.VISIBLE);
		mDiscountPoint.setVisibility(View.GONE);
 	}
	
	public void disableColorPoint() {
		mColorPoint.setVisibility(View.INVISIBLE);
 	}
	
	public void setColorPointMode(ColorPointMode mode) {
		int drawableMode = 0;
		switch (mode) {
			case GRAY: {
				drawableMode = R.drawable.point_gray;
			} break;
			case RED: {
				drawableMode = R.drawable.point_red;
			} break;
			case YELLOW: {
				drawableMode = R.drawable.point_yellow;
			} break;
			case GREEN: {
				drawableMode = R.drawable.point_green;
			} break;
			default: {
				throw new RuntimeException();
			}
		}
		
		Drawable drawable = getResources().getDrawable(drawableMode);
		mColorPoint.setImageDrawable(drawable);
	}
	
	public enum ColorPointMode {
		GRAY, RED, YELLOW, GREEN
	}
	
}
