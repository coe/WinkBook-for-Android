package jp.coe.winkbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.coe.winkfragment.WinkFragment;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/*
 *
 * ページを表示する
 * フラグメント差し替えでPDFやepubを表示出来るといいな
 */

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PageActivity extends AppCompatActivity implements WinkFragment.OnFragmentInteractionListener,PageOnFragmentInteractionListener {

    private static final String TAG = "PageActivity";

    public static final String MIMETYPE_EPUB = "application/epub+zip";
    public static final String MIMETYPE_PDF = "application/pdf";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

//    private View mContentView;
    private WIKPageFragment mContentFragment;

    private View mControlsView;
    private boolean mVisible;

    //PDF表示フラグメント
//    private WIKPageFragment mPDFRenderFragment;

    private final Handler mMainThreadHandler = new Handler();

    /**
     * ファイルパスの拡張子から MIME Type を取得する
     *
     * @param filePath
     * @return
     */
    public static String getMimeType(String filePath){
        String mimeType = null;
        String extension = null;
        try{
            extension = MimeTypeMap.getFileExtensionFromUrl(URLEncoder.encode(filePath, "UTF-8"));
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        if(extension != null){
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ストリームを受け取る
        Uri fileUri = null;
        Intent intent = getIntent();
        if (intent.getData() == null) {
            // error
            Log.w(TAG,"file not found");
        } else {
            fileUri = intent.getData();
        }

        //ストリームから開くファイルを判断
        String path = fileUri.getPath();

        Log.d(TAG, "ファイルmimetype " + getMimeType(path));
        setContentView(R.layout.activity_page);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        //レンダーフラグメント生成
        Bundle bundle = new Bundle();
        bundle.putParcelable(Intent.EXTRA_STREAM, fileUri);
        String mime = getMimeType(path);
        if(!TextUtils.isEmpty(mime)) {
            switch (mime){
                case MIMETYPE_EPUB:
                    //EpubRenderFragment追加
                    mContentFragment = new EpubRenderFragment();

                    break;
                case MIMETYPE_PDF:
                    //PDFRenderFragment追加
                    mContentFragment = new PDFRenderFragment();
                    break;

            }
        }

        if(mContentFragment == null){
            //未対応

            return;
        }

        mContentFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            // フラグメントをアクティビティに追加する FragmentTransaction を利用する
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container,mContentFragment);
            transaction.commit();
        }

        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        if(mContentFragment == null) {
            Log.d(TAG,"アラート表示");
            FragmentManager manager = getFragmentManager();
            TestDialogFragment dialog = new TestDialogFragment();
            dialog.show(manager, "dialog");
        } else {

        }
    }

    public static class TestDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            final Activity activity = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(android.R.string.httpErrorUnsupportedScheme)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //戻る
                            activity.finish();
                        }
                    });
            return builder.create();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mContentFragment == null){
            return;
        }

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    public void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentFragment.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };


    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentFragment.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClose() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mContentFragment != null) mContentFragment.nextPage();

            }
        });
    }

    @Override
    public void onLongClose() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mContentFragment != null) mContentFragment.backPage();

            }
        });
    }

    @Override
    public void onLeftClose() {
        this.onLongClose();
    }

    @Override
    public void onRightClose() {
        this.onClose();
    }

}
