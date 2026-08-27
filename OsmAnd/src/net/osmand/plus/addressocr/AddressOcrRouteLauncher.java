package net.osmand.plus.addressocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/** Adds a small OCR entry point to the map screen without changing OsmAnd XML layouts. */
public final class AddressOcrRouteLauncher {
    private static final int BUTTON_ID = 0x7f0a5a01;

    private AddressOcrRouteLauncher() {}

    public static void install(Activity activity) {
        activity.getWindow().getDecorView().postDelayed(() -> {
            ViewGroup content = activity.findViewById(android.R.id.content);
            if (content == null || content.findViewById(BUTTON_ID) != null) return;
            Button button = new Button(activity);
            button.setId(BUTTON_ID);
            button.setText("OCR");
            button.setTextSize(12);
            button.setAllCaps(false);
            button.setTextColor(Color.WHITE);
            button.setContentDescription("Skanna adresser med OCR");
            button.setOnClickListener(v -> activity.startActivity(new Intent(activity, AddressOcrRouteActivity.class)));
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.END);
            lp.topMargin = dp(activity, 96);
            lp.rightMargin = dp(activity, 12);
            content.addView(button, lp);
        }, 500);
    }

    private static int dp(Activity activity, int value) {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}
