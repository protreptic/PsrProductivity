package ru.magnat.sfs.bom.query.measures;

import ru.magnat.sfs.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class TprEditPad extends RelativeLayout implements View.OnClickListener {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	public TprEditPad(Context context) {
		super(context);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mLayoutInflater.inflate(R.layout.trp_edit_pad_layout, this, true);
	} 
	
	public void setTprValueChangeListener(TprValueChangeListener listener) {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tpr_edit_pad_price_reduced: {
				//mTprValueChangeListener.onTprValueChange(mContext, TprType.PRICE_REDUCED, mPriceReduced.isChecked());
			} break;
			case R.id.tpr_edit_pad_price_label_presented: {
				//mTprValueChangeListener.onTprValueChange(mContext, TprType.PRICE_LABEL_PRESENTED, mPricelabelPresented.isChecked());
			} break;
		}
	}
	
}
