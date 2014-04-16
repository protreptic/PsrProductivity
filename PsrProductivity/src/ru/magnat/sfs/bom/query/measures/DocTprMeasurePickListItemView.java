package ru.magnat.sfs.bom.query.measures;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoJournal;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLine;
import ru.magnat.sfs.bom.query.measures.TprValueChangeListener.TprType;
import ru.magnat.sfs.bom.ref.csku.RefCsku;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class DocTprMeasurePickListItemView extends GenericListItemView implements OnClickListener {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private DocTprMeasureEntity mDocTprMeasureEntity;
	private TprValueChangeListener mTprValueChangeListener;
	
	@SuppressWarnings("unchecked")
	public DocTprMeasurePickListItemView(Context context, OrmObject<?> list, ListView lv, DocTprMeasureEntity entity, TprValueChangeListener listener) {
		super(context, list, lv);
		
		mContext = MainActivity.getInstance();
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDocTprMeasureEntity = entity;
		mTprValueChangeListener = listener;
	}
	
	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.tpr_measure_list_item, this);
		return this;
	}

	private int getPhotoCount(DocTprMeasureEntity docTprMeasureEntity, Integer productId) {
		int result = 0;
		
		RefCsku refCsku = new RefCsku(mContext);
		RefCskuEntity cskuEntity = refCsku.FindById(productId);
		refCsku.close();

		// найти уже имеющиеся фотографии
		DocMarketingPhotoJournal docMarketingPhotoJournal = new DocMarketingPhotoJournal(mContext);
		DocMarketingPhotoEntity docMarketingPhotoEntity = null;
		
		docMarketingPhotoJournal.SetMasterDoc(mDocTprMeasureEntity);
		docMarketingPhotoJournal.Select(null, null, true, false);
		while (docMarketingPhotoJournal.Next()) {
			DocMarketingPhotoEntity temp = docMarketingPhotoJournal.Current();
			if (temp.MarketingObject == cskuEntity.Id) {
				docMarketingPhotoEntity = temp; 
				break;
			}
		}
		if (docMarketingPhotoEntity != null) {
			// выбрать их в массив и передать на вход фото модуля для отображения и редактирования
			DocMarketingPhotoLine docMarketingPhotoLines = (DocMarketingPhotoLine) docMarketingPhotoEntity.getLines(mContext);
			docMarketingPhotoLines.Select();
			
			result = (int) docMarketingPhotoLines.Count();
			docMarketingPhotoLines.close();
		}
	
		docMarketingPhotoJournal.close();		
		
		return result;
	}
	
	@Override
	public void fill() {
		TextView itemDescr = (TextView) findViewById(R.id.tpr_measure_list_item_product_descr);
		CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		ImageView imageButton = (ImageView) findViewById(R.id.tpr_measure_list_item_photo);
		TextView photoCount = (TextView) findViewById(R.id.tpr_measure_list_item_photo_count);
		
		QueryGetTprMeasurePickListEntity query = (QueryGetTprMeasurePickListEntity) _orm.Current();
		
		photoCount.setText(String.valueOf(getPhotoCount(mDocTprMeasureEntity, (int) query.Id)));
		
		checkBox1.setContentDescription(String.valueOf(query.Id));
		checkBox1.setOnClickListener(this);
		checkBox2.setContentDescription(String.valueOf(query.Id));
		checkBox2.setOnClickListener(this);
		imageButton.setContentDescription(String.valueOf(query.Id));
		imageButton.setOnClickListener(this);
		
		RefCsku refCsku = new RefCsku(mContext);
		RefCskuEntity cskuEntity = refCsku.FindById(query.Id);
		
		DocTprMeasureLine docTprMeasureLine = (DocTprMeasureLine) mDocTprMeasureEntity.getLines(mContext);
		DocTprMeasureLineEntity docTprMeasureLineEntity = docTprMeasureLine.getLine(mContext, cskuEntity);
		
		if (docTprMeasureLineEntity != null) {
			checkBox1.setChecked(docTprMeasureLineEntity.PriceReduced);
			checkBox2.setChecked(docTprMeasureLineEntity.PriceCardPromoted);
		}
	
		docTprMeasureLine.close();
		refCsku.close();
		
		itemDescr.setText(query.Descr);
		
		if (mDocTprMeasureEntity.getReadOnly()) {
			checkBox1.setEnabled(false);
			checkBox2.setEnabled(false);
			imageButton.setEnabled(false);
		}
	}

	@Override
	public void onClick(View view) {
		CheckBox checkBox;
		Integer productId;
		Boolean isChecked;
		
		switch (view.getId()) {
			case R.id.checkBox1:
				checkBox = (CheckBox) view;
				productId = Integer.valueOf(checkBox.getContentDescription().toString());
				isChecked = checkBox.isChecked();
				
				mTprValueChangeListener.onTprValueChange(mContext, TprType.PRICE_REDUCED, productId, isChecked);
			break;
			case R.id.checkBox2:
				checkBox = (CheckBox) view;
				productId = Integer.valueOf(checkBox.getContentDescription().toString());
				isChecked = checkBox.isChecked();
				
				mTprValueChangeListener.onTprValueChange(mContext, TprType.PRICE_LABEL_PRESENTED, productId, isChecked);
			break;
			case R.id.tpr_measure_list_item_photo:
				productId = Integer.valueOf(view.getContentDescription().toString());
				
				mTprValueChangeListener.onTprPhoto(mContext, productId);
			break;
		}
	}

}
