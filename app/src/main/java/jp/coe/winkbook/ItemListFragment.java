package jp.coe.winkbook;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemListFragment extends ListFragment {

    private static final String TAG = "ItemListFragment";

    private static final File DEFAULT_DIR = Environment.getExternalStorageDirectory();

    private File mBaseDir;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ArrayList<File> mFiles = null;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    private FileArrayAdapter mAdapter;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(File file);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(File file) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        //一回Viewを取り外す
//        container.removeAllViews();
//        View rootView = inflater.inflate(R.layout.activity_item_list, container,false);
//        return rootView;
//
//    }

    private void checkFilePermission(){
        //TODO:ファイルをモデルとして扱う
        Log.d(TAG, "getExternalStorageDirectory " + mBaseDir.getPath());

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE
            );

        } else {
            mFiles = new ArrayList<File>(Arrays.asList(mBaseDir.listFiles()));
            Log.d(TAG,"mFiles " + mFiles.size());

            // TODO: replace with a real list adapter.

            mAdapter = new FileArrayAdapter(getActivity(),
                    R.layout.list_item,
                    mFiles
                    );

            setListAdapter(mAdapter);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG,"onAttach");
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        //最初のディレクトリ取得


        if(getArguments() != null && getArguments().containsKey(Intent.EXTRA_STREAM)){
            Log.d(TAG,"file");

            Uri fileUri = getArguments().getParcelable(Intent.EXTRA_STREAM);
            //ストリームから開くファイルを判断
            String path = fileUri.getPath();
            Log.d(TAG,"getPath " + path);
            mBaseDir = new File(path);
        } else {
            Log.d(TAG,"noFile");
            mBaseDir = Environment.getExternalStorageDirectory();
        }

        checkFilePermission();

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        File file = mFiles.get(position);
        if(file.isDirectory()){
//            //リスト更新
//            mBaseDir = file;
//            mFiles = new ArrayList<File>(Arrays.asList(mBaseDir.listFiles()));
//            //
//            mAdapter.clear();
//            //一番上に、戻る列追加
//
//            mAdapter.addAll(mFiles);
//            mAdapter.notifyDataSetChanged();
//            getListView().invalidateViews();
            mCallbacks.onItemSelected(file);


        } else {

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mCallbacks.onItemSelected(file);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        checkFilePermission();
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
