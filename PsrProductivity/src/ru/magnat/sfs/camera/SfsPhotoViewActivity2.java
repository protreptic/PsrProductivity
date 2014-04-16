package ru.magnat.sfs.camera;

import ru.magnat.sfs.android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SfsPhotoViewActivity2 extends Activity {
	
	private Intent mResult;
	private ImageView mImageView;
	private String mData;
	private Bitmap mBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera_photo_view_layout);

		mData = getIntent().getExtras().getString("data");
		mImageView = (ImageView) findViewById(R.id.photo);
		mBitmap = BitmapFactory.decodeFile(mData);
		mImageView.setImageBitmap(mBitmap);
		mResult = new Intent();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mBitmap.recycle();
		mBitmap = null;
	}
	
	public void save_click(View view) {
		mResult.putExtra("photo", mData);
		setResult(0, mResult);
		finish();
	}
	
	public void delete_click(View view) {
		mResult.putExtra("photo", mData);
		setResult(1, mResult);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		this.save_click(null);
	}
}
