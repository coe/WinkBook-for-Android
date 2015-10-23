package jp.coe.winkbook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EpubRenderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EpubRenderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpubRenderFragment extends Fragment implements WIKPageInterface {

    private static final String TAG = "EpubRenderFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PageOnFragmentInteractionListener mListener;

    //UI
    private ImageView image;
    private Button btnPrevious;
    private Button btnNext;

    private Bitmap currentBitmap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EpubRenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EpubRenderFragment newInstance(String param1, String param2) {
        Log.d(TAG,"newInstance");
        EpubRenderFragment fragment = new EpubRenderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EpubRenderFragment() {
        Log.d(TAG,"EpubRenderFragment");
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG,"onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        image = (ImageView) view.findViewById(R.id.image);
        btnPrevious = (Button) view.findViewById(R.id.btn_previous);
        btnNext = (Button) view.findViewById(R.id.btn_next);

        //set buttons event
//        btnPrevious.setOnClickListener(onActionListener(-1)); //previous button clicked
//        btnNext.setOnClickListener(onActionListener(1)); //next button clicked

        int index = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            index = savedInstanceState.getInt("current_page", 0);
        }
        if(currentBitmap != null) {
            Log.d(TAG,"currentBitmap "+currentBitmap.getWidth() + " : " + currentBitmap.getHeight());
            image.setImageBitmap(currentBitmap);

        }
//        showPage(index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_epub_render, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Log.d(TAG, "onButtonPressed");

    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG,"onAttach");

        super.onAttach(activity);
        try {
            mListener = (PageOnFragmentInteractionListener) activity;
            mListener.renderFragment(this);

            //TODO:この辺でEpub初期化
            //inputsteream生成

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "211829.epub");

            InputStream epubInputStream = new FileInputStream(file);

            Book book = (new EpubReader()).readEpub(epubInputStream);

            // Log the book's coverimage property

            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
                    .getInputStream());

            Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by "
                    + coverImage.getHeight() + " pixels");

            // Log the tale of contents

            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

//            book.getContents().get(0).getData();

            currentBitmap = coverImage;


        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively Log the Table of Contents
     *
     * @param tocReferences
     * @param depth
     */

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        Log.d(TAG,"logTableOfContents");

        if (tocReferences == null) {

            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());
            logTableOfContents(tocReference.getChildren(), depth + 1);

        }

    }


    @Override
    public void onDetach() {
        Log.d(TAG,"onDetach");

        super.onDetach();
        mListener = null;
    }

    @Override
    public void nextPage() {
        Log.d(TAG, "nextPage");
    }

    @Override
    public void backPage() {
        Log.d(TAG, "backPage");

    }

    @Override
    public void close() {
        Log.d(TAG, "close");

    }
}
