package com.lixinxin.tippickerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixinxin on 16/4/22.
 */
public class TipPickerView extends View {

    private static final int STATE_EVENT_NONE = 0;
    private static final int STATE_EVENT_MOVE = 1;

    private int mState = STATE_EVENT_NONE;

    private int mItemHeight; //每一个Item的高度
    private int mSelectRadius; //选中的圆的半径
    private int mItemWidth; //每一个Item的宽度
    private int mTextSize; //字号
    private int mTipRadius; //旁边提示圆的半径
    private int mTipMargin; //提示圆和List之间的间距
    private int mViewHeight; //整个View的高度
    private int mPadding; //List的上边距
    private int mColorText = 0x666666; //字体颜色
    private int mColorShader = 0x999999; //List背景色
    private int mColorCirclr = 0xff8170; //选中圆以及提示圆背景色
    private Context mContext;

    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private float mLastDownY;
    /**
     * 自动回滚到中间的速度
     */
    private static final float SPEED = 2;

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint colorPaint = new Paint();
    private final Paint circlePaint = new Paint();

    private int currentSelect = 0;
    private boolean isTounch = false;

    private List<String> mData = new ArrayList<String>();
    private onSelectListener mSelectListener;

    private Point selectPoint = new Point();


    public TipPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TipPickerView(Context context) {
        this(context, null);
    }

    private void init() {
        mItemHeight = DimenUtils.dp2px(40, mContext.getResources().getDisplayMetrics());
        mSelectRadius = DimenUtils.dp2px(27, mContext.getResources().getDisplayMetrics());
        mItemWidth = DimenUtils.dp2px(50, mContext.getResources().getDisplayMetrics());
        mTipRadius = DimenUtils.dp2px(20, mContext.getResources().getDisplayMetrics());
        mTipMargin = DimenUtils.dp2px(15, mContext.getResources().getDisplayMetrics());
        mTextSize = DimenUtils.sp2px(13, mContext.getResources().getDisplayMetrics());
        mPadding = DimenUtils.dp2px(7, mContext.getResources().getDisplayMetrics());
        mViewHeight = mItemHeight * 4 + mPadding;
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(mColorText);
        textPaint.setTextSize(mTextSize);
        textPaint.setAlpha(255);
        colorPaint.setColor(mColorShader);
        colorPaint.setStyle(Paint.Style.FILL);
        colorPaint.setStrokeWidth(2);
        colorPaint.setAntiAlias(true);
        colorPaint.setAlpha(255);
        circlePaint.setColor(mColorCirclr);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true);
        circlePaint.setAlpha(255);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        RectF barRectF = new RectF(mTipRadius * 2 + mTipMargin, mPadding,
                mTipRadius * 2 + mTipMargin + mItemWidth, mItemHeight * 4 + mPadding);
        canvas.drawRect(barRectF, colorPaint);
        float selectX = (barRectF.left + barRectF.right) / 2;
        float tipX = mTipRadius;
        float selectY = getSelectY(barRectF);
        selectPoint.set(mTipRadius, (int) selectY);
        canvas.drawCircle(selectX, selectY, mSelectRadius, circlePaint);
        Paint.FontMetricsInt fmi = textPaint.getFontMetricsInt();
        float baseline = (float) (selectY - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mData.get(currentSelect), selectX, baseline, textPaint);
        if (isTounch) {
            canvas.drawCircle(tipX, selectY, mTipRadius, circlePaint);
            canvas.drawText(mData.get(currentSelect), tipX, baseline, textPaint);
        }
        drawOtherText(canvas, barRectF, selectY);
    }

    private float getSelectY(RectF rectF) {
        float selectY = rectF.top + mItemHeight + mItemHeight / 2;
        if (currentSelect == 0) {
            selectY = rectF.top + mItemHeight / 2;
        }
        if (currentSelect == mData.size() - 2) {
            selectY = rectF.top + mItemHeight / 2 + mItemHeight * 2;
        }
        if (currentSelect == mData.size() - 1) {
            selectY = rectF.bottom - mItemHeight / 2;
        }
        return selectY;
    }

    private void drawOtherText(Canvas canvas, RectF rectF, float selectY) {
        float otherX = rectF.centerX();
        float topY = selectY - mItemHeight;
        float bottomY = selectY + mItemHeight;
        int topPosition = currentSelect - 1;
        int bottomPosition = currentSelect + 1;
        while (topY > mPadding && topPosition >= 0) {
            Paint.FontMetricsInt fmi = textPaint.getFontMetricsInt();
            float baseline = (float) (topY - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mData.get(topPosition--), otherX, baseline, textPaint);
            topY -= mItemHeight;
        }
        while (bottomY < mViewHeight && bottomPosition < mData.size()) {
            Paint.FontMetricsInt fmi = textPaint.getFontMetricsInt();
            float baseline = (float) (bottomY - (fmi.bottom / 2.0 + fmi.top / 2.0));
            canvas.drawText(mData.get(bottomPosition++), otherX, baseline, textPaint);
            bottomY += mItemHeight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isTounch = true;
                mLastDownY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                isTounch = false;
                if (Math.abs(mMoveLen) < 0.0001) {
                    mMoveLen = 0;
                }
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = Math.abs(mMoveLen) < SPEED ? 0 : mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
                invalidate();
                performSelect();
                break;
        }
        return true;
    }

    private void doMove(MotionEvent event) {
        mMoveLen += (event.getY() - mLastDownY);
        if (mMoveLen > mItemHeight / 2 && currentSelect < mData.size() - 1) {
            // 往下滑超过离开距离
            currentSelect++;
            mMoveLen = mMoveLen - mItemHeight;
        } else if (mMoveLen < -mItemHeight / 2 && currentSelect > 0) {
            // 往上滑超过离开距离
            currentSelect--;
            mMoveLen = mMoveLen + mItemHeight;
        }
        mLastDownY = event.getY();
        invalidate();
    }

    private void performSelect() {
        if (mSelectListener != null)
            mSelectListener.onSelect(mData.get(currentSelect));
    }

    public void setmSelectListener(onSelectListener mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    public void setmData(List<String> mData) {
        this.mData = mData;
        this.currentSelect = 0;
        invalidate();
    }

    public void setCurrentSelect(int currentSelect) {
        this.currentSelect = currentSelect;
        invalidate();
    }

    public interface onSelectListener {
        void onSelect(String text);
    }

}
