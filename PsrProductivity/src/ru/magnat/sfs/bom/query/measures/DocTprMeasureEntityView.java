package ru.magnat.sfs.bom.query.measures;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnPhotoReceivedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoJournal;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLine;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLineEntity;
import ru.magnat.sfs.bom.ref.csku.RefCsku;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.camera.SfsCameraActivity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ListView;

public class DocTprMeasureEntityView extends GenericEntityView<DocTprMeasureJournal, DocTprMeasureEntity> implements TprValueChangeListener, OnPhotoReceivedListener {
		
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private DocTprMeasureEntity mDocTprMeasureEntity;
	
	private ListView mListView;
	private QueryGetTprMeasurePickList mAdapter;
	
	private Long mProductId;
	
	public DocTprMeasureEntityView(Context context, DocTprMeasureJournal journal, DocTprMeasureEntity entity) {
		super(context, journal, entity);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDocTprMeasureEntity = entity;
	}

	@Override
	public Boolean onBackPressed() {
		onRemove();
		
		return true;
	}
	
	@Override
	protected Boolean onRemove() {
		DocTprMeasureLine docTprMeasureLine = (DocTprMeasureLine) mDocTprMeasureEntity.getLines(mContext);
		if (docTprMeasureLine != null && docTprMeasureLine.getCount() == 0) {
			mDocTprMeasureEntity.IsMark = true;
			mDocTprMeasureEntity.save();
			
			return true;
		}
		
		Dialogs.createDialog("", "Загрузить документ в 1С?", new Command() {
			
			@Override
			public void execute() {
				mDocTprMeasureEntity.IsMark = false;
				mDocTprMeasureEntity.save();
				closeView();
			}
		}, new Command() {
			
			@Override
			public void execute() {
				mDocTprMeasureEntity.IsMark = true;
				mDocTprMeasureEntity.save();
				closeView();
			}
		}, Command.NO_OP, 0).show();

		return false;
	}
	
	@Override
	public void closeView() {
		if (mAdapter != null) {
			mAdapter.close();
		}
		if (mDocTprMeasureEntity != null) {
			mDocTprMeasureEntity.close();
		}
		
		super.closeView();
	}
	
	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.doc_tpr_measure_entity_view, this);

		mAdapter = new QueryGetTprMeasurePickList(getContext(), mDocTprMeasureEntity, this);
		mAdapter.Select();
		
		mListView = (ListView) findViewById(R.id.tpr_item_list);
		mListView.setAdapter(mAdapter);
		
		if (mDocTprMeasureEntity.getReadOnly()) {
			Dialogs.createDialog("SFS", "Отправленный документ исправлять нельзя", Command.NO_OP).show();
		}

		MainActivity.sInstance.setOnPhotoReceived(this);
		
		return this;
	}

	@Override
	public void refresh() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onTprValueChange(Context context, TprType type, Integer productId, Boolean value) {
		RefCsku refCsku = new RefCsku(mContext);
		RefCskuEntity cskuEntity = refCsku.FindById(productId);
		
		DocTprMeasureLine docTprMeasureLine = (DocTprMeasureLine) mDocTprMeasureEntity.getLines(mContext);
		DocTprMeasureLineEntity docTprMeasureLineEntity = docTprMeasureLine.getLine(mContext, cskuEntity);
		
		if (docTprMeasureLineEntity == null) {
			docTprMeasureLine.NewEntity();
			docTprMeasureLineEntity = docTprMeasureLine.Current();
			docTprMeasureLineEntity.TprSubject = cskuEntity;
			docTprMeasureLineEntity.PriceReduced = false;
			docTprMeasureLineEntity.PriceCardPromoted = false;
		}
		
		switch (type) {
			case PRICE_REDUCED:
				docTprMeasureLineEntity.PriceReduced = value;
			break;
			case PRICE_LABEL_PRESENTED:
				docTprMeasureLineEntity.PriceCardPromoted = value;
			break;
		}
		
		docTprMeasureLineEntity.save();
		docTprMeasureLine.save();
		docTprMeasureLine.close();
		refCsku.close();
	}
	public static final int MASTER_DOC_TPR_MEASURE = 64;
	@Override
	public void onTprPhoto(Context context, Integer productId) {
		RefCsku refCsku = new RefCsku(mContext);
		RefCskuEntity cskuEntity = refCsku.FindById(productId);
		refCsku.close();
		
		mProductId = cskuEntity.Id;
		
		// найти уже имеющиеся фотографии
		DocMarketingPhotoJournal docMarketingPhotoJournal = new DocMarketingPhotoJournal(mContext);
		DocMarketingPhotoEntity docMarketingPhotoEntity = null;
		
		docMarketingPhotoJournal.SetMasterDoc(mDocTprMeasureEntity);
		docMarketingPhotoJournal.Select(null, null, true, false);
		while (docMarketingPhotoJournal.Next()) {
			DocMarketingPhotoEntity temp = docMarketingPhotoJournal.Current();
			if (temp.MarketingObject == cskuEntity.Id && temp.MasterDocType == MASTER_DOC_TPR_MEASURE) {
				docMarketingPhotoEntity = temp; 
				break;
			}
		}
		String[] photos = null;
		if (docMarketingPhotoEntity != null) {
			// выбрать их в массив и передать на вход фото модуля для отображения и редактирования
			DocMarketingPhotoLine docMarketingPhotoLines = (DocMarketingPhotoLine) docMarketingPhotoEntity.getLines(mContext);
			docMarketingPhotoLines.Select();
			
			int photoCount = (int) docMarketingPhotoLines.Count();
			int count = 0;
			if (photoCount > 0) {
				photos = new String[photoCount];
				DocMarketingPhotoLineEntity temp = null;
				while (docMarketingPhotoLines.Next()) {
					temp = docMarketingPhotoLines.Current();
					photos[count] = temp.FileName;
					count++;
				}
			}
			docMarketingPhotoLines.close();
		}
	
		docMarketingPhotoJournal.close();
		
		Intent intent = new Intent(mContext, SfsCameraActivity.class);
		intent.putExtra("ProductId", productId);
		intent.putExtra("OUTPUT_PARAMS", photos);
		
		MainActivity.sInstance.startActivityForResult(intent, MainActivity.PHOTO_ACTIVITY_CODE);
	}
	
	@Override
	public void onPhotoReceived(Context context, String[] photos) {	
		RefCsku refCsku = new RefCsku(mContext);
		RefCskuEntity cskuEntity = refCsku.FindById(mProductId);
		refCsku.close();
		
		DocMarketingPhotoJournal docMarketingPhotoJournal = new DocMarketingPhotoJournal(mContext);
		DocMarketingPhotoEntity docMarketingPhotoEntity = null;
		
		docMarketingPhotoJournal.SetMasterDoc(mDocTprMeasureEntity);
		docMarketingPhotoJournal.Select(null, null, true, false);
		while (docMarketingPhotoJournal.Next()) {
			DocMarketingPhotoEntity temp = docMarketingPhotoJournal.Current();
			if (temp.MarketingObject == cskuEntity.Id && temp.MasterDocType == MASTER_DOC_TPR_MEASURE) {
				docMarketingPhotoEntity = temp; 
				break;
			}
		}
		
		if (docMarketingPhotoEntity == null) {
			docMarketingPhotoJournal.NewEntity();
			docMarketingPhotoEntity = docMarketingPhotoJournal.Current();
			docMarketingPhotoEntity.setDefaults(mDocTprMeasureEntity, cskuEntity);
			docMarketingPhotoEntity.save();
		}
		
		Set<String> photoSet = new HashSet<String>();
		for (String item : photos) {
			photoSet.add(new File(item).getName());
		}
		
		// удаляем из коллекции фотографий, те которые уже есть в документе
		DocMarketingPhotoLine docMarketingPhotoLines = (DocMarketingPhotoLine) docMarketingPhotoEntity.getLines(mContext);
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
		// добавляем новые фотографии к документу
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
