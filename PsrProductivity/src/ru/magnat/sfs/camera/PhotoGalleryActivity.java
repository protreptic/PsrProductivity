package ru.magnat.sfs.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureJournal;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoJournal;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLine;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoLineEntity;
import ru.magnat.sfs.bom.marketingphotocheck.DocMarketingPhotoCheckEntity;
import ru.magnat.sfs.bom.marketingphotocheck.DocMarketingPhotoCheckJournal;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObject;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.ui.android.doc.DocGenericEntityView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class PhotoGalleryActivity extends ListActivity {
	
	private Context mActivityContext;
	private String mQuestion;
	private Integer mMarketingMeasureDocumentId;
	private DocMarketingMeasureEntity mMarketingMeasureEntity;
	private Integer mMarketingObjectId;
	private RefMarketingMeasureObjectEntity mRefMarketingMeasureObjectEntity;
	private Map<DocMarketingPhotoLineEntity, List<DocMarketingPhotoCheckEntity>> mItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.picture_gallery);
		
		mActivityContext = this;
		
		mImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + getResources().getString(R.string.images_path);
		 
		mQuestion = getIntent().getExtras().getString("question");
		setTitle(mQuestion);
		mMarketingMeasureDocumentId = getIntent().getExtras().getInt("MarketingMeasureDocumentId");
		mMarketingObjectId = getIntent().getExtras().getInt("MarketingObjectId");
		
		DocMarketingMeasureJournal journal = new DocMarketingMeasureJournal(mActivityContext);
		mMarketingMeasureEntity = journal.FindById(mMarketingMeasureDocumentId);
		journal.close();
		
		RefMarketingMeasureObject measureObject = new RefMarketingMeasureObject(mActivityContext);
		mRefMarketingMeasureObjectEntity = measureObject.FindById(mMarketingObjectId);
		measureObject.close();
		
		DocMarketingPhotoJournal docMarketingPhotoJournal = new DocMarketingPhotoJournal(mActivityContext);
		DocMarketingPhotoEntity owner = null;
		docMarketingPhotoJournal.SetMasterDoc(mMarketingMeasureEntity);
		docMarketingPhotoJournal.Select(null, null, true, false);
		while (docMarketingPhotoJournal.Next()) {
			DocMarketingPhotoEntity temp = docMarketingPhotoJournal.Current();
			if (mRefMarketingMeasureObjectEntity != null && (temp.MarketingObject == mRefMarketingMeasureObjectEntity.Id) && temp.MasterDocType == DocGenericEntityView.MASTER_DOC_MARKETING_MEASURE) {
				owner = temp; 
				break;
			}
		}
		
		mItems = new HashMap<DocMarketingPhotoLineEntity, List<DocMarketingPhotoCheckEntity>>(); 
		
		if (owner == null) {
			docMarketingPhotoJournal.close();
			PictureAdapter adapter = new PictureAdapter(mActivityContext, mItems);
			getListView().setAdapter(adapter);
			return;
		}
		
		
		
		DocMarketingPhotoLine docMarketingPhotoLines = (DocMarketingPhotoLine) owner.getLines(mActivityContext);
		docMarketingPhotoLines.Select();
		DocMarketingPhotoLineEntity temp = null;
		while (docMarketingPhotoLines.Next()) {
			temp = docMarketingPhotoLines.Current();
			
			List<DocMarketingPhotoCheckEntity> comments = new ArrayList<DocMarketingPhotoCheckEntity>(); 
			
			DocMarketingPhotoCheckJournal checkJournal = new DocMarketingPhotoCheckJournal(mActivityContext);
			DocMarketingPhotoCheckEntity temp2 = null;
			checkJournal.SetMasterDoc(owner);
			checkJournal.Select(null, null, true, false);
			while (checkJournal.Next()) {
				temp2 = checkJournal.Current();
				comments.add(temp2);
			}
			checkJournal.close();
			
			mItems.put(temp, comments);
		}
		
		docMarketingPhotoJournal.close();
		docMarketingPhotoLines.close();
		
		PictureAdapter adapter = new PictureAdapter(mActivityContext, mItems);
		getListView().setAdapter(adapter);
	}
	
	public class PictureAdapter extends BaseAdapter {

		private Context mActivityContext;
		private LayoutInflater mLayoutInflater;
		private Map<DocMarketingPhotoLineEntity, List<DocMarketingPhotoCheckEntity>> mCollection;
		private List<DocMarketingPhotoLineEntity> mKeys = new ArrayList<DocMarketingPhotoLineEntity>();
		private DocMarketingPhotoLineEntity mTemp;
		
		public PictureAdapter(final Context context, Map<DocMarketingPhotoLineEntity, List<DocMarketingPhotoCheckEntity>> data) {
			mActivityContext = context;
			mLayoutInflater = (LayoutInflater) mActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mCollection = data;
			
			for (DocMarketingPhotoLineEntity item : mCollection.keySet()) {
				mKeys.add(item);
			}
		}
		
		@Override
		public int getCount() {
			return mCollection.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mKeys.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup parent) {
			View view = convertView;
		    if (view == null) {
		    	view = mLayoutInflater.inflate(R.layout.picture_gallery_item, parent, false);
		    }
		    
		    mTemp = mKeys.get(arg0);
		    
		    Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mImagePath + mTemp.FileName), 150, 150);
		    
			ImageView image = (ImageView) view.findViewById(R.id.picture_gallery_image);
			image.setImageBitmap(ThumbImage);
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				    Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setDataAndType(Uri.fromFile(new File(mImagePath + mTemp.FileName)), "image/*");
			        mActivityContext.startActivity(intent);
				}
			});
			TextView comments = (TextView) view.findViewById(R.id.picture_gallery_content);
			comments.setText(getResources().getString(R.string.label_comments)); 
			
			CommentAdapter adapter = new CommentAdapter(mActivityContext, mCollection.get(mTemp));
			ListView list = (ListView) view.findViewById(R.id.picture_gallery_comments);
			list.setAdapter(adapter);

			return view;
		}
		
	}
	
	private String mImagePath;
	
	public class CommentAdapter extends BaseAdapter {

		private Context mActivityContext;
		private LayoutInflater mLayoutInflater;
		private List<DocMarketingPhotoCheckEntity> mComments;
		
		public CommentAdapter(Context context, List<DocMarketingPhotoCheckEntity> comments) {
			mActivityContext = context;
			mLayoutInflater = (LayoutInflater) mActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mComments = comments;
		}
		
		@Override
		public int getCount() {
			return mComments.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mComments.get(arg0);
		} 

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
		    if (view == null) {
		    	view = mLayoutInflater.inflate(R.layout.picture_gallery_comment_item, parent, false);
		    }
		    
		    DocMarketingPhotoCheckEntity temp = mComments.get(position);
		    
			TextView author = (TextView) view.findViewById(R.id.picture_gallery_comment_item_author);
			author.setText(temp.NotesAuthor);
			TextView date = (TextView) view.findViewById(R.id.picture_gallery_comment_item_date);
			date.setText(DateFormat.format(getResources().getString(R.string.date_format), temp.CreateDate)); 
			TextView text = (TextView) view.findViewById(R.id.picture_gallery_comment_item_text);
			text.setText(temp.Notes);
			RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating); 
			ratingBar.setRating(temp.Rating);
			return view;
		}
		
	}
}
