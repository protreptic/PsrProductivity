/**
 * 
 */
package ru.magnat.sfs.ui.android.doc.marketingmeasure;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnPhotoReceivedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OnValueChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureJournal;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureLineEntity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoJournal;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLine;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLineEntity;
import ru.magnat.sfs.bom.query.measures.QueryGetMarketingMeasurePickList;
import ru.magnat.sfs.bom.query.measures.QueryGetMarketingMeasurePickListEntity;
import ru.magnat.sfs.camera.SfsCameraActivity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.controls.InputValueControl;
import ru.magnat.sfs.ui.android.controls.ThreeLevelLogicPadLayout;
import android.content.Intent;

/**
 * @author alex_us
 * 
 */
@SuppressLint("ViewConstructor")
public final class DocMarketingMeasureEntityView extends
		GenericEntityView<DocMarketingMeasureJournal, DocMarketingMeasureEntity>
		implements OnPhotoReceivedListener {
	
	private DocMarketingMeasureEntity mDocMarketingMeasureEntity;
	private ListView _pickup_list;
	private QueryGetMarketingMeasurePickList _pickup_adapter;
	private InputValueControl _pad;
	private int _last_position = -1;

	public DocMarketingMeasureEntityView(Context context,
			DocMarketingMeasureJournal journal, DocMarketingMeasureEntity entity) {
		super(context, journal, entity);

		mDocMarketingMeasureEntity = entity;
	}

	@Override
	protected Boolean onRemove() {
		if (_pickup_adapter!=null) _pickup_adapter.close();
		if (mDocMarketingMeasureEntity!=null) mDocMarketingMeasureEntity.close();
			
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.magnat.sfs.ui.android.SfsContentView#Inflate()
	 */
	@Override
	public SfsContentView inflate() {
		// Resources res = getResources();

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_marketing_measure_entity_view, this);
		{
			TextView view = (TextView) findViewById(R.id.measureCaption);
			view.setText(mDocMarketingMeasureEntity.MarketingMeasure.Descr);
		}
		{
			_pad = null;

			RelativeLayout view = (RelativeLayout) findViewById(R.id.inputControlView);
			// InputValueControl ctrl = new NumericPadLayout(getContext(),
			// null);
			_pad = new ThreeLevelLogicPadLayout(getContext(), null);
			view.addView((View) _pad);
			if (mDocMarketingMeasureEntity.getReadOnly()) {
				Dialogs.createDialog("SFS",
						"Внимание! Отправленный документ исправлять нельзя",
						Command.NO_OP).show();

			} else {
				_pad.setOnValueChangedListener(new OnValueChangedListener() {// перерисуем
					// значение
					public void onValueChanged(Object sender, Object value) {
						View v = _pickup_list.getChildAt(_last_position - _pickup_list.getFirstVisiblePosition());
						if (v == null) return;
						TextView someText = (TextView) v.findViewById(R.id.marketing_measure_result);
						someText.setText(DocMarketingMeasureLineEntity.getStringValue(mDocMarketingMeasureEntity.MarketingMeasure.UnitKind,(Float) value));
					}
				});
				_pad.addOnValueChangedListener(new OnValueChangedListener() {
					// изменим значение в заказе
					public void onValueChanged(Object sender, Object value) {
						if (_pickup_adapter.getSelection() == null)
							return;
						QueryGetMarketingMeasurePickListEntity e = (QueryGetMarketingMeasurePickListEntity) (_pickup_adapter.getSelection());
						mDocMarketingMeasureEntity.changeValue(getContext(),
								e.marketingMeasureObjectId, (Float) value);

					}

				});

				((ImageButton) findViewById(R.id.button_photo))
						.setOnClickListener(new OnClickListener() {

							public void onClick(View arg0) {

								doPhoto();

							}
						});

			}
		}

		_pickup_adapter = new QueryGetMarketingMeasurePickList(getContext(),
				mDocMarketingMeasureEntity, Globals.getEmployee());
		_pickup_adapter.Select();

		_pickup_list = (ListView) findViewById(R.id.tpr_item_list);
		_pickup_list.setAdapter(_pickup_adapter);
		_pickup_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		_pickup_list.setItemsCanFocus(false);
		_pickup_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (_last_position == position)
					return;
				_last_position = position;
				_pickup_adapter.setSelection(position);
				_pickup_list.invalidate();
				_pad.resetValue(0);

			}
		});

		MainActivity.getInstance().setOnPhotoReceived(this);
		return this;
	}

	/*
	 * Ф-ия для работы с фотками Вызывает модуль камеры, передает на вход
	 * текущее значение реквизита Photo (список имен файлов фото), после работы
	 * вызывает метод который присвоит реквизиту Photo новое значение (если
	 * список фото был изменен)
	 */
	protected void doPhoto() {
		String[] photos = null;
		Intent photo_activity = new Intent(MainActivity.getInstance().getApplicationContext(), SfsCameraActivity.class);
		
		photo_activity.putExtra("customer_legal_name", mDocMarketingMeasureEntity.Outlet.Descr);
		photo_activity.putExtra("store_address", mDocMarketingMeasureEntity.Outlet.Address);
		
		if (_pickup_adapter.getSelection() != null) {
			QueryGetMarketingMeasurePickListEntity e = (QueryGetMarketingMeasurePickListEntity) (_pickup_adapter.getSelection());
			
			photo_activity.putExtra("visibility_question", e == null ? "" : e.marketingMeasureObjectDescr);
			
			DocMarketingMeasureLineEntity docline = mDocMarketingMeasureEntity.getLine(getContext(), e.marketingMeasureObjectId);
			if (docline != null) {
				String photo = docline.Photo;
				if (photo != null) {
					photos = Utils.splitString(photo);
					photo_activity.putExtra("OUTPUT_PARAMS", photos);
					
				}
			} else {
				photo_activity.putExtra("OUTPUT_PARAMS", photos);
			}
		}
		MainActivity.getInstance().startActivityForResult(photo_activity, MainActivity.PHOTO_ACTIVITY_CODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.magnat.sfs.ui.android.SfsContentView#refresh()
	 */
	@Override
	public void refresh() {
		_pickup_adapter.notifyDataSetChanged();
	}

	private Context mContext;
	private DocMarketingMeasureEntity mMarketingMeasureEntity;
	
	public void onPhotoReceived(Context context, String[] photos) {
		if (_pickup_adapter.getSelection() == null)
			return;
		QueryGetMarketingMeasurePickListEntity e = (QueryGetMarketingMeasurePickListEntity) (_pickup_adapter.getSelection());
		mDocMarketingMeasureEntity.changePhoto(getContext(), e.marketingMeasureObjectId, photos);
		
		// новый
  		mMarketingMeasureEntity = mDocMarketingMeasureEntity;
		
		DocMarketingPhotoJournal docMarketingPhotoJournal = new DocMarketingPhotoJournal(mContext);
		DocMarketingPhotoEntity owner = null;
		
		docMarketingPhotoJournal.SetMasterDoc(mMarketingMeasureEntity);
		docMarketingPhotoJournal.Select(null, null, true, false);
		while (docMarketingPhotoJournal.Next()) {
			DocMarketingPhotoEntity temp = docMarketingPhotoJournal.Current();
			if (e.marketingMeasureObjectId>0 && (temp.MarketingObject == e.marketingMeasureObjectId) && temp.MasterDocType == MASTER_DOC_MARKETING_MEASURE) {
				owner = temp; 
				break;
			}
		}
		
		if (owner == null) {
			docMarketingPhotoJournal.NewEntity();
			owner = docMarketingPhotoJournal.Current();
			owner.setDefaults(mContext, mMarketingMeasureEntity, e.marketingMeasureObjectId);
			owner.save();
		}
		
		Set<String> photoSet = new HashSet<String>();
		for (String item : photos) {
			photoSet.add(new File(item).getName());
		}
		
		DocMarketingPhotoLine docMarketingPhotoLines = (DocMarketingPhotoLine) owner.getLines(mContext);
		docMarketingPhotoLines.Select();
		DocMarketingPhotoLineEntity temp = null;
		while (docMarketingPhotoLines.Next()) {
			temp = docMarketingPhotoLines.Current();
			if (photoSet.contains(temp.FileName)) {
				photoSet.remove(temp.FileName);
			} else {
				docMarketingPhotoLines.delete(temp);
			}
		}
		
		// Удалить документ DocMarketingPhotoEntity если фотографий нет
		
		for (Iterator<String> iterator = photoSet.iterator(); iterator.hasNext();) {
			docMarketingPhotoLines.NewEntity();
			DocMarketingPhotoLineEntity entity = docMarketingPhotoLines.Current();
			entity.FileName = iterator.next();
			docMarketingPhotoLines.save();
		}
		
		docMarketingPhotoJournal.close();
		docMarketingPhotoLines.close();
		
		refresh();
	}
}
