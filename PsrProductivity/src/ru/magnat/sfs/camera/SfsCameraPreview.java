package ru.magnat.sfs.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import ru.magnat.sfs.android.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SfsCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private static String LOG_TAG = "SfsCameraPreview";

	private SurfaceHolder mHolder;
	private Camera mCamera;

	public SfsCameraPreview(Context context) {
		super(context);
	}

	public SfsCameraPreview(Context context, Camera camera) {
		super(context);

		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			if (mCamera == null) {
				mCamera = SfsCameraActivity.sInstance.getCamera();
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}

		} catch (IOException e) {
			Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null) {
			return;
		}

		try {
			mCamera.stopPreview();
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}