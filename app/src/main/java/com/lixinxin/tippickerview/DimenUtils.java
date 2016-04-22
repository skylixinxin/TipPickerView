package com.lixinxin.tippickerview;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by lixinxin on 16/4/19.
 */
public class DimenUtils {
    private static final int DP_TO_PX = TypedValue.COMPLEX_UNIT_DIP;
    private static final int SP_TO_PX = TypedValue.COMPLEX_UNIT_SP;
    private static final int PX_TO_DP = TypedValue.COMPLEX_UNIT_MM + 1;
    private static final int PX_TO_SP = TypedValue.COMPLEX_UNIT_MM + 2;

    private static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case DP_TO_PX:
            case SP_TO_PX:
                return TypedValue.applyDimension(unit, value, metrics);
            case PX_TO_DP:
                return value / metrics.density;
            case PX_TO_SP:
                return value / metrics.scaledDensity;
        }
        return 0;
    }

    public static int dp2px(float value, DisplayMetrics metrics) {
        return (int) applyDimension(DP_TO_PX, value, metrics);
    }

    public static int sp2px(float value, DisplayMetrics metrics) {
        return (int) applyDimension(SP_TO_PX, value, metrics);
    }

    public static int px2dp(float value, DisplayMetrics metrics) {
        return (int) applyDimension(PX_TO_DP, value, metrics);
    }

    public static int px2sp(float value, DisplayMetrics metrics) {
        return (int) applyDimension(PX_TO_SP, value, metrics);
    }
}
