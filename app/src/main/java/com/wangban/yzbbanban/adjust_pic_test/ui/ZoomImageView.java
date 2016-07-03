package com.wangban.yzbbanban.adjust_pic_test.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnTouchListener,
		OnGlobalLayoutListener, OnScaleGestureListener{
	private boolean Once;
	private Matrix mScaleMatrix;
	private float mInitScale;
	private float mMidScale;
	private float mMaxScale;

	private ScaleGestureDetector mScaleGestureDetector;

	private int mLastPointerCount;
	private float mLastX;
	private float mLastY;

	private int mTouchSlop;
	private boolean isCanDrag;
	private RectF matrixRectF;
	private boolean isCheckLeftAndRight;
	private boolean isCheckTopAndBottom;

	private GestureDetector mGestureDetector;
	private boolean isAutoScale;
	/**
	 * 自定义
	 */
	private String url;
	private Thread thread;
	//touch event
	private long mLastTime, mCurTime;
	private int count;
	private float mLastMotionX, mLastMotionY;
	private boolean isMoved;
	private long time, time2;
	private int moveStyle;

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScaleMatrix = new Matrix();
		super.setScaleType(ScaleType.MATRIX);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale) {
							return true;
						}
						float x = e.getX();
						float y = e.getY();

						if (getScale() < mMidScale) {
//							mScaleMatrix.postScale(mMidScale / getScale(),
//									mMidScale / getScale(), x, y);
//							setImageMatrix(mScaleMatrix);
							postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
							isAutoScale = true;
						} else {
//							mScaleMatrix.postScale(mInitScale / getScale(),
//									mInitScale / getScale(), x, y);
//							setImageMatrix(mScaleMatrix);
							postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
							isAutoScale = true;
						}
						return true;
					}
				});
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	private class AutoScaleRunnable implements Runnable {
		private float mTargetScale;
		// ���ŵ����ĵ�
		private float x;
		private float y;

		private final float BIGGER = 1.07f;
		private final float SMALL = 0.93f;

		private float tmpScale;

		public AutoScaleRunnable(float mTargetScale, float x, float y) {
			super();
			this.mTargetScale = mTargetScale;
			this.x = x;
			this.y = y;

			if (getScale() < mTargetScale) {
				tmpScale=BIGGER;
			}
			if (getScale() > mTargetScale) {
				tmpScale = SMALL;
			}
		}

		@Override
		public void run() {
			mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);
			
			float currentScale = getScale();
			if ((tmpScale>1.0f && currentScale<mTargetScale) 
					|| (tmpScale<1.0f && currentScale<mTargetScale)) {
				postDelayed(this, 16);
			}else {
				float scale = mTargetScale/currentScale;
				mScaleMatrix.postScale(scale, scale, x, y);
				checkBorderAndCenterWhenScale();
				setImageMatrix(mScaleMatrix);
				
				isAutoScale = false;
			}
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		if (!Once) { // �ǵ�һ��
			// �ؼ��Ŀ�͸�
			int width = getWidth();
			int height = getHeight();
			// �õ�ͼƬ���Լ���͸�
			Drawable d = getDrawable();
			if (d == null)
				return;

			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			float scale = 1.0f;
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			if (dw < width && dh > height) {
				scale = height * 1.0f / dh;
			}
			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}
			// �õ��˳�ʼ������
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;
			// ��ͼƬ�ƶ����ؼ�����
			int dx = getWidth() / 2 - dw / 2;
			int dy = getHeight() / 2 - dh / 2;

			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, getWidth() / 2,
					getHeight() / 2);
			setImageMatrix(mScaleMatrix);

			Once = true;
		}
	}

	public float getScale() {
		float[] values = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;
		if ((scale < mMaxScale && scaleFactor > 1.0f)
				|| (scale > mInitScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mInitScale)
				scaleFactor = mInitScale / scale;
			if (scale * scaleFactor > mMaxScale)
				scale = mMaxScale / scale;
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());

			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);
		}
		return true;
	}

	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();
		Drawable d = getDrawable();
		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF);
		}
		return rectF;
	}
	private void checkBorderAndCenterWhenScale() {
		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();
		if (rect.width() >= width) {
			if (rect.left > 0) {
				deltaX = -rect.left;
			}
			if (rect.right < width) {
				deltaX = width - rect.right;
			}
		}
		if (rect.height() >= height) {
			if (rect.top > 0) {
				deltaY = -rect.top;
			}
			if (rect.bottom < height) {
				deltaY = height - rect.bottom;
			}
		}
		if (rect.width() < width) {
			deltaX = width / 2f - rect.right + rect.width() / 2f;
		}
		if (rect.height() < height) {
			deltaY = height / 2f - rect.bottom + rect.height() / 2f;
		}

		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}

		mScaleGestureDetector.onTouchEvent(event);

		float x = 0;
		float y = 0;
		int pointerCount = event.getPointerCount();
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX();
			y += event.getY();
		}

		x /= pointerCount;
		y /= pointerCount;

		if (mLastPointerCount != pointerCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}
		mLastPointerCount = pointerCount;
		RectF rectF = getMatrixRectF();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (rectF.width()>getWidth()+0.01
					|| rectF.height()>getHeight()+0.01) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}



			break;
			
		case MotionEvent.ACTION_MOVE:
			if (rectF.width()>getWidth()+0.01
					|| rectF.height()>getHeight()+0.01) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}

			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isMoveAction(dx, dy);
			}
			if (isCanDrag) {
				if (getDrawable() != null) {
					isCheckLeftAndRight = isCheckTopAndBottom = true;
					if (rectF.width() < getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}
					if (rectF.height() < getHeight()) {
						isCheckTopAndBottom = false;
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);

					checkBorderWhenTranslate();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastPointerCount = 0;
			break;
		}
		return true;
	}

	private void checkBorderWhenTranslate() {
		RectF rectF = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		if (rectF.top > 0 && isCheckTopAndBottom) {
			deltaY = -rectF.top;
		}

		if (rectF.bottom < height && isCheckTopAndBottom) {
			deltaY = height - rectF.bottom;
		}
		if (rectF.left > 0 && isCheckLeftAndRight) {
			deltaX = -rectF.left;
		}
		if (rectF.right < width && isCheckLeftAndRight) {
			deltaX = width - rectF.right;
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	private boolean isMoveAction(float dx, float dy) {
		return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
	}



}