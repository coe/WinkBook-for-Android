package jp.coe.winkbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2015/10/23.
 */
public class FileArrayAdapter extends ArrayAdapter<File> {

    private List<File> mFiles;
    private LayoutInflater mInflater;
    private int mResource = 0;

    public FileArrayAdapter(Context context, int resource, List<File> objects) {
        super(context, resource, objects);
        mResource = resource;
        mFiles = objects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    public FileArrayAdapter(Context context, int resource, int textViewResourceId, List<File> objects) {
//        super(context, resource, textViewResourceId, objects);
//        mResource = resource;
//        mFiles = objects;
//        mTextViewResourceId = textViewResourceId;
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(mResource, null);
        }

        File file = mFiles.get(position);
        ((TextView) view.findViewById(R.id.text)).setText(file.getName());

        if(file.isDirectory()) {
            ((ImageView) view.findViewById(R.id.image)).setImageResource(android.R.drawable.ic_menu_more);
        } else {
            ((ImageView) view.findViewById(R.id.image)).setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return view;//super.getView(position,convertView,parent);
    }


}
