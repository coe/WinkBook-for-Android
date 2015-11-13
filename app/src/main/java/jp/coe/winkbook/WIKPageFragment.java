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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 */
abstract public class WIKPageFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "WIKPageFragment";
    private static final String SHOWCASE_ID = "sequence example";

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

    public void start(View btnNext,View btnPrevious){
        //チュートリアル
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(btnNext,
                "軽く目を閉じることでページめくりが可能です。" +
                        "右目を閉じっぱなしで早送りできます", "次へ");

        sequence.addSequenceItem(btnPrevious,
                "左目を閉じっぱなしでページの巻き戻しが可能です", "START");


        sequence.start();
    }

    abstract public void nextPage();
    abstract public void backPage();
    abstract public void close();

}
