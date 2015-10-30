package jp.coe.winkbook;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
abstract public class WIKPageFragment extends android.support.v4.app.Fragment {

    public WIKPageFragment() {
        // Required empty public constructor
    }

    abstract public void nextPage();
    abstract public void backPage();
    abstract public void close();

}
