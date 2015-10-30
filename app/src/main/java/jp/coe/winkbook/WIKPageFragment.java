package jp.coe.winkbook;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
abstract public class WIKPageFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "WIKPageFragment";

    public WIKPageFragment() {
        // Required empty public constructor
    }

    public File getFile(){
        Log.d(TAG,"getFile");
        //ファイル受け取り
        //ストリームを受け取る
        Uri fileUri = getArguments().getParcelable(Intent.EXTRA_STREAM);

        //ストリームから開くファイルを判断
        String path = fileUri.getPath();
        Log.d(TAG,"getPath " + path);
        return new File(path);
    }

    abstract public void nextPage();
    abstract public void backPage();
    abstract public void close();

}
