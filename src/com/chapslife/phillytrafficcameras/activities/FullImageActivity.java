package com.chapslife.phillytrafficcameras.activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.chapslife.phillytrafficcameras.PenndotApplication;
import com.chapslife.phillytrafficcameras.R;
import com.chapslife.phillytrafficcameras.utils.ImageCacheManager;
import com.chapslife.phillytrafficcameras.utils.ShadowLayout;

/**
 * @author kchapman
 * 
 */
public class FullImageActivity extends Activity implements ImageListener {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final String PACKAGE_NAME = "com.chapslife.phillytrafficcameras";
    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private BitmapDrawable mBitmapDrawable;
    private ColorMatrix colorizerMatrix = new ColorMatrix();
    ColorDrawable mBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private TextView mTextView;
    private ImageView mImageView;
    private FrameLayout mTopLevelLayout;
    private ShadowLayout mShadowLayout;
    private int mOriginalOrientation;
    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    Bundle mSavedInstanceState;
    private String url;
    private Handler handler = new Handler();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTopLevelLayout = (FrameLayout) findViewById(R.id.topLevelLayout);
        mShadowLayout = (ShadowLayout) findViewById(R.id.shadowLayout);
        mSavedInstanceState = savedInstanceState;
        mTextView = (TextView) findViewById(R.id.description);
        // Retrieve the data we need for the picture/description to display and
        // the thumbnail to animate it from
        Bundle bundle = getIntent().getExtras();

        url = bundle.getString(PACKAGE_NAME + ".url");
        thumbnailTop = bundle.getInt(PACKAGE_NAME + ".top");
        thumbnailLeft = bundle.getInt(PACKAGE_NAME + ".left");
        thumbnailWidth = bundle.getInt(PACKAGE_NAME + ".width");
        thumbnailHeight = bundle.getInt(PACKAGE_NAME + ".height");
        mOriginalOrientation = bundle.getInt(PACKAGE_NAME + ".orientation");
        handler.post(camImageRunnable);
        //ImageCacheManager.getInstance().getImageLoader().get(url, this);
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of
     * the activity is fading in. When the pictue is in place, the text
     * description drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * PenndotApplication.sAnimatorScale);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);

        // We'll fade the text in later
        mTextView.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration).scaleX(1).scaleY(1).translationX(0)
                .translationY(0).setInterpolator(sDecelerator).withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        mTextView.setTranslationY(-mTextView.getHeight());
                        mTextView.animate().setDuration(duration / 2).translationY(0).alpha(1)
                                .setInterpolator(sDecelerator);
                    }
                });
        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();

        // Animate a color filter to take the image from grayscale to full
        // color.
        // This happens in parallel with the image scaling and moving into
        // place.
        ObjectAnimator colorizer = ObjectAnimator.ofFloat(FullImageActivity.this, "saturation", 0,
                1);
        colorizer.setDuration(duration);
        colorizer.start();

        // Animate a drop-shadow of the image
        // ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout,
        // "shadowDepth", 0, 1);
        // shadowAnim.setDuration(duration);
        // shadowAnim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except
     * that if the orientation has changed we simply scale the picture back into
     * the center of the screen.
     * 
     * @param endAction
     *            This action gets run after the animation completes (this is
     *            when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * PenndotApplication.sAnimatorScale);

        // No need to set initial values for the reverse animation; the image is
        // at the
        // starting size/location that we want to start from. Just animate to
        // the
        // thumbnail size/location that we retrieved earlier

        // Caveat: configuration change invalidates thumbnail positions; just
        // animate
        // the scale around the center. Also, fade it out since it won't match
        // up with
        // whatever's actually in the center
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation) {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);
            mLeftDelta = 0;
            mTopDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        // First, slide/fade text out of the way
        mTextView.animate().translationY(-mTextView.getHeight()).alpha(0).setDuration(duration / 2)
                .setInterpolator(sAccelerator).withEndAction(new Runnable() {
                    public void run() {
                        // Animate image back to thumbnail size/location
                        mImageView.animate().setDuration(duration).scaleX(mWidthScale)
                                .scaleY(mHeightScale).translationX(mLeftDelta)
                                .translationY(mTopDelta).withEndAction(endAction);
                        if (fadeOut) {
                            mImageView.animate().alpha(0);
                        }
                        // Fade out background
                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
                        bgAnim.setDuration(duration);
                        bgAnim.start();

                        // Animate the shadow of the image
                        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout,
                                "shadowDepth", 1, 0);
                        shadowAnim.setDuration(duration);
                        shadowAnim.start();

                        // Animate a color filter to take the image back to
                        // grayscale,
                        // in parallel with the image scaling and moving into
                        // place.
                        ObjectAnimator colorizer = ObjectAnimator.ofFloat(FullImageActivity.this,
                                "saturation", 1, 0);
                        colorizer.setDuration(duration);
                        colorizer.start();
                    }
                });

    }

    /**
     * Overriding this method allows us to run our exit animation first, then
     * exiting the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(camImageRunnable);
    }
    
    /**
     * This is called by the colorizing animator. It sets a saturation factor
     * that is then passed onto a filter on the picture's drawable.
     * 
     * @param value
     */
    public void setSaturation(float value) {
        colorizerMatrix.setSaturation(value);
        ColorMatrixColorFilter colorizerFilter = new ColorMatrixColorFilter(colorizerMatrix);
        mBitmapDrawable.setColorFilter(colorizerFilter);
    }

    @Override
    public void finish() {
        super.finish();
        
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.android.volley.Response.ErrorListener#onErrorResponse(com.android
     * .volley.VolleyError)
     */
    @Override
    public void onErrorResponse(VolleyError arg0) {
        // do nothing

    }

    Runnable camImageRunnable = new Runnable() {
        public void run() {
            handler.removeCallbacks(camImageRunnable);
            ImageCacheManager.getInstance().getImageLoader().get(url, FullImageActivity.this);
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.android.volley.toolbox.ImageLoader.ImageListener#onResponse(com.android
     * .volley.toolbox.ImageLoader.ImageContainer, boolean)
     */
    @Override
    public void onResponse(ImageContainer ic, boolean arg1) {
        mBitmapDrawable = new BitmapDrawable(getResources(), ic.getBitmap());
        mImageView.setImageDrawable(mBitmapDrawable);

        mBackground = new ColorDrawable(Color.BLACK);
        mTopLevelLayout.setBackground(mBackground);
        handler.postDelayed(camImageRunnable, 10000);
        // Only run the animation if we're coming from the parent activity, not
        // if
        // we're recreated automatically by the window manager (e.g., device
        // rotation)
        if (mSavedInstanceState == null) {
            mSavedInstanceState = new Bundle();
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions
                    // are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as
                    // the thumbnail
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }
}
