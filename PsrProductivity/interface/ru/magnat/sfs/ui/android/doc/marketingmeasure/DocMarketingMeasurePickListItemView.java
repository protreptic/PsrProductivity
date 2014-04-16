package ru.magnat.sfs.ui.android.doc.marketingmeasure;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureLineEntity;
import ru.magnat.sfs.bom.query.measures.QueryGetMarketingMeasurePickListEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.camera.PhotoGalleryActivity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.controls.InputValueControl;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class DocMarketingMeasurePickListItemView extends GenericListItemView {

	private Context mActivityContext;
	private LayoutInflater mLayoutInflater;
	private DocMarketingMeasureEntity mDocMarketingMeasureEntity;
	private DocMarketingMeasureLineEntity mDocMarketingMeasureLineEntity;
	private ImageView mPhoto;
	private TextView mQuestion;
	private TextView mAnswer;
	private TextView mPictureCount;
	private TextView mCommentCount;
	
	private Integer mRed;
	private Integer mGreen;
	
	@SuppressWarnings("unchecked")
	public DocMarketingMeasurePickListItemView(Context context, OrmObject<?> list, ListView lv, DocMarketingMeasureEntity doc) {
		super(context, list, lv);
		mActivityContext = MainActivity.getInstance();
		mRed = mActivityContext.getResources().getColor(R.color.red);
		mGreen = mActivityContext.getResources().getColor(R.color.green);
		mDocMarketingMeasureEntity = doc;
		mLayoutInflater = (LayoutInflater) mActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.marketing_measure_item_ii, this);
		return this;
	}
	
	@Override
	public void fill() {
		mPhoto = (ImageView) findViewById(R.id.marketing_measure_has_photo);
		mQuestion = (TextView) findViewById(R.id.marketing_measure_text);
		mAnswer = (TextView) findViewById(R.id.marketing_measure_result);
		mPictureCount = (TextView) findViewById(R.id.marketing_measure_picture_count);
		mCommentCount = (TextView) findViewById(R.id.marketing_measure_comment_count);
		
		QueryGetMarketingMeasurePickListEntity currentEntity = (QueryGetMarketingMeasurePickListEntity) _orm.Current();
		
		mQuestion.setText(currentEntity.marketingMeasureObjectDescr);
		
		DocMarketingMeasureLineEntity line = mDocMarketingMeasureEntity.getLine(getContext(), currentEntity.marketingMeasureObjectId);
		if (line != null) {
			mDocMarketingMeasureLineEntity = line;
			mAnswer.setText(line.getStringValue(mDocMarketingMeasureEntity.MarketingMeasure.UnitKind));
			String answer = mAnswer.getText().toString();
			if  (answer != null) {
				if (answer.equalsIgnoreCase("да")) {
					mAnswer.setTextColor(mGreen);
				}
				if (answer.equalsIgnoreCase("нет")) {
					mAnswer.setTextColor(mRed);
				}
			}
			
			if (line.Photo != null) {
				if (!line.Photo.isEmpty()) {
					mPhoto.setVisibility(View.VISIBLE);
					mPhoto.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mActivityContext, PhotoGalleryActivity.class);
							intent.putExtra("question", mQuestion.getText().toString());
							intent.putExtra("MarketingMeasureDocumentId", (int) mDocMarketingMeasureEntity.Id);
							intent.putExtra("MarketingObjectId", (int) mDocMarketingMeasureLineEntity.MarketingObject.Id);
							mActivityContext.startActivity(intent);
						} 
					});
					mPictureCount.setText(String.valueOf(line.Photo.split(";").length));
				} else {
					mPhoto.setVisibility(View.INVISIBLE);
				}
			}					
		}
	}
	
	@Override
	public void onValueChanged(Object sender, Object value) {
		if (sender instanceof InputValueControl) {
			mAnswer.setText(DocMarketingMeasureLineEntity.getStringValue(mDocMarketingMeasureEntity.MarketingMeasure.UnitKind, (Float) value));
		}

	}
}
