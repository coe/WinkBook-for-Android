package jp.coe.winkbook;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.File;
import java.util.LinkedList;


public class ItemListActivity extends AppCompatActivity
        implements ItemListFragment.Callbacks {
    private static final String TAG = "ItemListActivity";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private ItemListFragment mFragment;

    private LinkedList mTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitleList = new LinkedList();
        mTitleList.addLast(getTitle());
        toolbar.setTitle((String) mTitleList.getLast());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FragmentManager dialogmanager = getFragmentManager();
        final android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //license
                LicenseDialogFragment dialog = new LicenseDialogFragment();
                dialog.show(dialogmanager, "dialog");

            }
        });

        if (savedInstanceState == null) {
            //フラグメント追加
            ItemListFragment fragment = new ItemListFragment();
            // フラグメントをアクティビティに追加する FragmentTransaction を利用する
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.frameLayout, fragment);
            transaction.commit();

        }

    }

    public static class LicenseDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            final Activity activity = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            WebView webview = new WebView(getActivity());
            webview.loadUrl("file:///android_asset/lisences.html");

            builder.setView(webview)
//                    .setMessage(android.R.string.httpErrorUnsupportedScheme)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //戻る
                        }
                    });
            return builder.create();
        }
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(File file) {
        Log.d(TAG, "onItemSelected " + file.getName());
        Log.d(TAG, "onItemSelected " + file.getPath());


        if (file.isDirectory()) {
            //さらに階層開く
            Log.d(TAG, "isDirectory");
            Bundle arguments = new Bundle();
            arguments.putParcelable(Intent.EXTRA_STREAM, Uri.fromFile(file));
            ItemListFragment fragment = new ItemListFragment();
            fragment.setArguments(arguments);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            mTitleList.addLast(file.getName());
            toolbar.setTitle((String)mTitleList.getLast());

            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.frameLayout, fragment)
                    .addToBackStack(null)
                    .commit();

        } else {

            //ファイルを開く
            Intent detailIntent = new Intent(this, PageActivity.class);
            detailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(detailIntent);
        }
    }

    @Override
    public void onBackPressed() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String pop = (String) mTitleList.pollLast();
        if(mTitleList.size() > 0) {
            toolbar.setTitle((String)mTitleList.getLast());
        }
        super.onBackPressed();
    }
}