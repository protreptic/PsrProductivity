package ru.magnat.sfs.camera;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.marketingphotocheck.DocMarketingPhotoCheckEntity;
import ru.magnat.sfs.bom.marketingphotocheck.DocMarketingPhotoCheckJournal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SfsPhotoViewActivity extends Activity {
	
	private Intent mResult;
	private String mPhotoPath;
	private ImageView mImageView;
	
	private ListView mComments;

	private String[] mPhotoTitles;

	private Float mRating;
	
	private DocMarketingPhotoEntity mDocMarketingPhotoEntity;
	private List<DocMarketingPhotoCheckEntity> mDocMarketingPhotoCheckEntityList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera_photo_view_layout);
		mDocMarketingPhotoCheckEntityList = new ArrayList<DocMarketingPhotoCheckEntity>();
		mDocMarketingPhotoEntity = MainActivity.getInstance().mCurrentPhotoMeasure;
		
		DocMarketingPhotoCheckJournal docMarketingPhotoCheckJournal = new DocMarketingPhotoCheckJournal(this);
		docMarketingPhotoCheckJournal.SetMasterDoc(mDocMarketingPhotoEntity);
		docMarketingPhotoCheckJournal.Select(null, null, true, false);
		while (docMarketingPhotoCheckJournal.Next()) {
			mDocMarketingPhotoCheckEntityList.add(docMarketingPhotoCheckJournal.Current());
		}
		docMarketingPhotoCheckJournal.close();
		
		mPhotoPath = getIntent().getExtras().getString("photo");
		mPhotoTitles = getIntent().getExtras().getStringArray("titles");
		mRating = (Float) getIntent().getExtras().getFloat("rating");
		RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
		ratingBar.setRating(((mRating == null) ? 3f : mRating));
		ratingBar.setVisibility(View.VISIBLE);
		
		if (mPhotoTitles == null) {
			mPhotoTitles = new String[0];
		}

		mImageView = (ImageView) findViewById(R.id.photo);
		mImageView.setImageBitmap(BitmapFactory.decodeFile(mPhotoPath));
		
		mResult = new Intent();
		mResult.putExtra("photo", mPhotoPath);
		
		TextView view = (TextView) findViewById(R.id.label);
		view.setVisibility(View.VISIBLE);
		
		mComments = (ListView) findViewById(R.id.list);
		mComments.setVisibility(View.INVISIBLE);
	} 
	
	public void save_click(View view) {
		setResult(0, mResult);
		finish();
	}
	
	public void delete_click(View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(SfsPhotoViewActivity.this);
		builder.setCancelable(false);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage(getResources().getString(R.string.dialog_camera_delete)); 
		builder.setPositiveButton(getResources().getString(R.string.btn_yes), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(1, mResult);
				finish();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.btn_no), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(0, mResult);
				finish();
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onBackPressed() {
		this.save_click(null);
	}
}
