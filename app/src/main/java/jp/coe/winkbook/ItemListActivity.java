package jp.coe.winkbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.File;


public class ItemListActivity extends AppCompatActivity
        implements ItemListFragment.Callbacks {
    private static final String TAG = "ItemListActivity";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

//        //ストリームを受け取る
//        Uri fileUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
//
//        //ストリームから開くファイルを判断
//        String path = fileUri.getPath();
//
//        //ファイルをフラグメントに渡す



        // TODO: If exposing deep links into your app, handle intents here.
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
            Intent detailIntent = new Intent(this, ItemListActivity.class);
            detailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(detailIntent);

        } else {

            //ファイルを開く
            Intent detailIntent = new Intent(this, PageActivity.class);
            detailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(detailIntent);
        }


//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
//            ItemDetailFragment fragment = new ItemDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.item_detail_container, fragment)
//                    .commit();
//
//        } else {
//            // In single-pane mode, simply start the detail activity
//            // for the selected item ID.
//            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
//            detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
//            startActivity(detailIntent);
//        }
    }

}
