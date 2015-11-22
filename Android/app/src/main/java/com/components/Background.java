package com.components;

/**
 * Created by Ravish on 10/29/2014.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.renderscript.*;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.node.App;


public class Background {

    private static Bitmap bitmap;
    private static Bitmap bitmapOut;
    private static ImageView imageView;
 //   public static native String getProcessorType();
    private static RenderScript script;

    public static void loadBlurredImage(ImageView image, Bitmap resource) {

        imageView = image;

        bitmap = scaleToScreenSize(resource);
        bitmapOut = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        imageView.setImageBitmap(bitmap);

        final boolean hasRenderSupport = hasRenderSupport();

        imageView.setVisibility(hasRenderSupport ? View.VISIBLE : View.GONE);

        if(hasRenderSupport) {
            script = RenderScript.create(App.get());
            loadBlurScript();
        }

    }

    private static void loadBlurScript() {

        final Allocation input = Allocation.createFromBitmap(script, bitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());


        final ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(script, Element.U8_4(script));
        blurScript.setRadius(25f);
        blurScript.setInput(input);
        blurScript.forEach(output);
        output.copyTo(bitmapOut);

        imageView.setImageBitmap(bitmapOut);
    }

    private static Bitmap scaleToScreenSize(Bitmap bitmap) {
        WindowManager winMan = (WindowManager)App.get().getSystemService(Context.WINDOW_SERVICE);
        float scale = App.get().getResources().getDisplayMetrics().density;

        Display display = winMan.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int)(size.x * scale + 0.5f);
        int height = (int)(size.y * scale + 0.5f);

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return scaled;
    }


    private static boolean hasRenderSupport() {
        return true;

        //final String processorType = getProcessorType();

        //return (processorType.equals("unknown") || processorType.equals("armeabi1"));
    }

}
