package ru.magnat.sfs.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.location.SfsLocationManager;
import ru.magnat.sfs.util.Files;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SfsCameraActivity extends Activity {
	
	private static final String LOG_TAG = "SfsCameraActivity";
    private static final short JPEG_QUALITY = 90;
    private static final short JPEG_PICTURE_WIDTH = 2048;
    private static final short JPEG_PICTURE_HEIGHT = 1536;
    private static final float SCALE = 0.7f;
    
    public static SfsCameraActivity sInstance; 
    
    private static final short PREVIEW_1 = 12;
    private static final short PREVIEW_2 = 13;
    
    private static final short DELETE_PICTURE = 1;
    private static final short SAVE_PICTURE = 0;
    
    private static final short PORTRAIT_ORIENTATION = 1;
    
    private Context mContext;
    private Camera mCamera;
    private SfsCameraPreview mPreview;
    private byte[] mData;
    private List<File> mPicturesList = new ArrayList<File>();

    private int mCurrentOrientation = 0;

    public void setCamera(final Camera camera) {
    	this.mCamera = camera;
    }
    
    public void flash_button_click(View view) {

    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == SfsCameraActivity.PREVIEW_1) {
    		if (resultCode == SfsCameraActivity.SAVE_PICTURE) {
    			ru.magnat.sfs.util.Files.makeDirectories(mImagePath, mOutboxPath);
    			 
    			String fileName = data.getExtras().getString("photo");
    			File pictureFile = new File(fileName);
                mPicturesList.add(pictureFile);
                
                Files.copyFile(pictureFile, new File(mOutboxPath + pictureFile.getName()));
                
                ImageView imageView = new ImageView(mContext);
                imageView.setId(Math.abs(pictureFile.hashCode()));
                imageView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Iterator<File> iterator = mPicturesList.iterator();
						while (iterator.hasNext()) {
							File file = (File) iterator.next();
							if (Math.abs(file.hashCode()) == v.getId()) {	
								Intent intent = new Intent(getApplicationContext(), SfsPhotoViewActivity.class);
								intent.putExtra("photo", file.getAbsolutePath());
								startActivityForResult(intent, 13);
								return;
							}
						}
					}
				});
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileName), 100, 100);
                imageView.setImageBitmap(thumbImage);
                
                LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
                layout.addView(imageView);
                layout = null;
                
                imageView = null;
                
                mData = null;
				TextView photoCount = (TextView) findViewById(R.id.photoCount);
				photoCount.setText(String.valueOf(mPicturesList.size()));
    		}
    		if (resultCode == SfsCameraActivity.DELETE_PICTURE) {
    			ru.magnat.sfs.util.Files.makeDirectories(mImagePath, mOutboxPath);
    			
    			String photo = data.getExtras().getString("photo");
    			Iterator<File> iterator = mPicturesList.iterator();
    			while (iterator.hasNext()) {
    				File file = iterator.next();
					if (file.getAbsolutePath().equals(photo)) {
	    				if (file.exists()) {
	    					iterator.remove();
							if (file.delete()) {
								//Toast.makeText(mContext, "Снимок удален!", Toast.LENGTH_LONG).show();
								LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
								layout.removeView((ImageView) layout.findViewById(Math.abs(file.hashCode())));
								layout = null;

								TextView photoCount = (TextView) findViewById(R.id.photoCount);
								photoCount.setText(String.valueOf(mPicturesList.size()));
								photoCount = null;

								File file2 = new File(mOutboxPath + file.getName());
								file2.delete();
							}
	    				} else {
	    					iterator.remove();
							
							LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
							layout.removeView((ImageView) layout.findViewById(Math.abs(file.hashCode())));
	    					
							TextView photoCount = (TextView) findViewById(R.id.photoCount);
							photoCount.setText(String.valueOf(mPicturesList.size()));
	    				}
					}
        		}
    		}
    	}
    	if (requestCode == SfsCameraActivity.PREVIEW_2) {
    		ru.magnat.sfs.util.Files.makeDirectories(mImagePath, mOutboxPath);
    		
    		// Если пользователь решил удалить снимок
    		if (resultCode == SfsCameraActivity.DELETE_PICTURE) {
    			String photo = data.getExtras().getString("photo");
    			Iterator<File> iterator = this.mPicturesList.iterator();    			
    			while (iterator.hasNext()) {
    				File file = iterator.next();    				
					if (file.getAbsolutePath().equals(photo)) {
	    				if (file.exists()) {
	    					iterator.remove();
							if (file.delete()) {
								LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
								layout.removeView((ImageView) layout.findViewById(Math.abs(file.hashCode())));
								layout = null;

								TextView photoCount = (TextView) findViewById(R.id.photoCount);
								photoCount.setText(String.valueOf(this.mPicturesList.size()));
	
								File file2 = new File(mOutboxPath + file.getName());
								file2.delete();
							}
	    				} else {
	    					iterator.remove();
							
							LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
							layout.removeView((ImageView) layout.findViewById(Math.abs(file.hashCode())));
	    					
							TextView photoCount = (TextView) findViewById(R.id.photoCount);
							photoCount.setText(String.valueOf(this.mPicturesList.size()));
	    				}
					}
    			}
    		}
    	}
    }
    
    private PictureCallback mPictureCallback = new PictureCallback() {

		@Override
        public void onPictureTaken(byte[] data, Camera camera) {
			
			ru.magnat.sfs.util.Files.makeDirectories(mImagePath, mOutboxPath);
			
			mData = data;
			String fileName = mImagePath + new SimpleDateFormat(getResources().getString(R.string.simple_date_format), new Locale("ru", "RU")).format(new Date()) + ".jpg";
			File pictureFile = new File(fileName); 
			Matrix matrix = new Matrix();

            try {	 
    			Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);

            	FileOutputStream fos = new FileOutputStream(pictureFile);
                if (mCurrentOrientation == SfsCameraActivity.PORTRAIT_ORIENTATION) { 
                	matrix.preRotate(90);
                }

                matrix.postScale(SfsCameraActivity.SCALE, SfsCameraActivity.SCALE);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.compress(CompressFormat.JPEG, SfsCameraActivity.JPEG_QUALITY, fos);
            	bitmap.recycle();
            	bitmap = null;
                fos.close();
                fos = null;
                mData = null;
                
                SfsLocationManager locationManager = SfsLocationManager.getInstance(mContext);

                String latitude = locationManager.getLatitude();
                String longitude = locationManager.getLongitude();
                
                JSONObject object = new JSONObject();
                try {
                  object.put("customer_legal_name", mCustomerLegalName);
                  object.put("store_address", mCustomerAddress);
                  object.put("visibility_question", mVisibilityQuestion);
                  object.put("latitude", latitude);
                  object.put("longitude", longitude);
                } catch (JSONException e) {
                  e.printStackTrace();
                }

                String userComment = object.toString();
                
				ExifInterface exifi = new ExifInterface(pictureFile.getAbsolutePath());
				exifi.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitude);
				exifi.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitude);
				exifi.setAttribute("UserComment", userComment);  
				exifi.saveAttributes();

				Intent intent = new Intent(getApplicationContext(), SfsPhotoViewActivity2.class);
				intent.putExtra("data", pictureFile.getAbsolutePath());
				startActivityForResult(intent, 12);
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "Файл не найден: " + e);
            } catch (IOException e) {
                Log.d(LOG_TAG, "Ошибка доступа к файлу: " + e);
            } catch (OutOfMemoryError e) {
				Log.d("", "", e);
			}
        }
    };
    
	private Size getPreferredPictureSize() {
    	Parameters parameters = mCamera.getParameters();
    	List<Size> pictureSizesList = parameters.getSupportedPictureSizes();
    	
    	for (Size size : pictureSizesList) {
    		float megaPixels = ((float) size.width * (float) size.height) / 1000000;    		
			if ((megaPixels > 1.9f) && (megaPixels < 2.1f)) {
				return size;
			}
		}
    	
    	return parameters.getPictureSize();
    }
    
	private String mOutboxPath;
	private String mImagePath;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview_layout);
        
        mContext = this;
        mOutboxPath = Environment.getExternalStorageDirectory().getAbsolutePath() + getResources().getString(R.string.outbox_path); 
        mImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + getResources().getString(R.string.images_path);
        ru.magnat.sfs.util.Files.makeDirectories(mImagePath, mOutboxPath);
        
        mCamera = getCameraInstance();
        
        Size size = this.getPreferredPictureSize();
        
        Log.d("", "Chosen picture size is " + size.width + "/" + size.height);
        
        Parameters parameters = mCamera.getParameters();       
        parameters.setJpegQuality(SfsCameraActivity.JPEG_QUALITY);
        parameters.setPictureSize(size.width, size.height);
        
        mCamera.setParameters(parameters);
        mPreview = new SfsCameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.setLayoutParams(new LayoutParams(640, 480));
        preview.addView(mPreview);
        
        //mModelName = Device.getDeviceName();
        
        ImageView captureButton = (ImageView) findViewById(R.id.tpr_measure_list_item_photo);
        captureButton.setOnClickListener (
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	try {
		                if (SfsCameraActivity.this.mPicturesList.size() > 4) {
		                	SfsCameraActivity.this.mCamera.stopPreview();
							AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							builder.setCancelable(false);
							builder.setIcon(R.drawable.ic_launcher);
							builder.setMessage(getResources().getString(R.string.dialog_camera_restriction)); 
							builder.setNegativeButton(getResources().getString(R.string.btn_continue), new OnClickListener() {
								@Override 
								public void onClick(DialogInterface dialog, int which) {
									SfsCameraActivity.this.mCamera.startPreview();
									dialog.dismiss();
								}
							});
							builder.create().show();
		                	return;
		                }
                		
                		mCamera.takePicture(null, null, SfsCameraActivity.this.mPictureCallback);
                	} catch (RuntimeException e) {
                		Log.d("", "", e);
                	}
                }
            }
        );
        
        Intent data = this.getIntent();
        if (data == null) {
        	Log.d("", "data = " + data);
        } else {
        	Bundle bundle = this.getIntent().getExtras();
        	if (bundle == null) {
        		Log.d("", "bundle = " + bundle);
        	} else {
        		String[] photos = getIntent().getExtras().getStringArray("OUTPUT_PARAMS");
        		mCustomerLegalName = getIntent().getExtras().getString("customer_legal_name");
        		mCustomerAddress = getIntent().getExtras().getString("store_address");
        		mVisibilityQuestion = getIntent().getExtras().getString("visibility_question");
        		if (photos == null) {
            		Log.d("", "photos = " + photos);
        		} else {
                	for (String string : photos) {
                		File file;
                		if ((getIntent().getExtras()).getInt("ProductId") > 0) {
                			file = new File(mImagePath + string);
                		} else {
                			file = new File(string);
                		}
                		
                		if (file.exists()) {
	                		this.mPicturesList.add(file);
	                        ImageView imageView = new ImageView(mContext);
	                        imageView.setId(Math.abs(file.hashCode()));
	                        imageView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	                        imageView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
	                        imageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Log.d("", "click: " + v.getId());
									
									for (File file : mPicturesList) {
										if (Math.abs(file.hashCode()) == v.getId()) {	
											Intent intent = new Intent(getApplicationContext(), SfsPhotoViewActivity.class);
											intent.putExtra("photo", file.getAbsolutePath());
											startActivityForResult(intent, 13);
											return;
										}
									}
								}
							});
	                        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 100, 100);
	                        imageView.setImageBitmap(thumbImage);
	                        
	                        LinearLayout layout = (LinearLayout) findViewById(R.id.photos);
	                        layout.addView(imageView);
	                        
	                		TextView photoCount = (TextView) findViewById(R.id.photoCount);
							photoCount.setText(String.valueOf(mPicturesList.size()));
							
							layout = null;
							photoCount = null;
	                        imageView = null;
                		} else {

                		}
        			}
        		}
        	}
        } 
        
        SfsCameraActivity.sInstance = this;
    }
    
    private String mCustomerLegalName;
    private String mCustomerAddress;
    private String mVisibilityQuestion;
    
    public Camera getCamera() {
    	return this.mCamera;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if (this.mCamera == null) {
        	this.mCamera = SfsCameraActivity.getCameraInstance();
        	
            Parameters parameters = this.mCamera.getParameters();
            parameters.setJpegQuality(SfsCameraActivity.JPEG_QUALITY);
            parameters.setPictureSize(SfsCameraActivity.JPEG_PICTURE_WIDTH, SfsCameraActivity.JPEG_PICTURE_HEIGHT);
                       
            mCamera.setParameters(parameters);
        	mPreview = new SfsCameraPreview(mContext, mCamera);
        	flash_button_click(null);
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	this.releaseCamera();
    }
    
    private void releaseCamera(){
        if (mCamera != null) {
        	mCamera.stopPreview();
        	mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview = null;
        }
    }
    
    @Override
    public void onBackPressed() {
		Intent result = new Intent();
		String[] photoList = new String[mPicturesList.size()];
		for (int i = 0; i < mPicturesList.size(); i++) {
			File file = (File) mPicturesList.get(i);
			photoList[i] = file.getAbsolutePath();
		}
		result.putExtra("INPUT_PARAMS", photoList);
		SfsCameraActivity.this.setResult(RESULT_OK, result);
		finish();
    }
    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {}
        return c;
    }
	
	public boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
}
