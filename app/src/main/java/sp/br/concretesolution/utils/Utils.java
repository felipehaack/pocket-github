package sp.br.concretesolution.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

public class Utils {

    public float getConvertDpToPixel(Activity activity, int dp) {

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return (float) ((dp * displayMetrics.density) + 0.5);
    }

    public int getScreenWidth(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
}
