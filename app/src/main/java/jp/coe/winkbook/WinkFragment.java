package jp.coe.winkbook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.Date;

import jp.coe.winkbook.ui.camera.CameraSourcePreview;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WinkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WinkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WinkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WinkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WinkFragment newInstance(String param1, String param2) {
        WinkFragment fragment = new WinkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WinkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wink, container, false);
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);

        return view;
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");

        super.onStart();
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onClose();
        public void onLongClose();

    }


    /* ここから */

    private static final String TAG = "MainActivity";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
//    private GraphicOverlay mGraphicOverlay;


    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static final float THRESHOLD = 0.35f;

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class WinkFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            Log.d(TAG, "WinkFaceTrackerFactory instance");
            return new WinkFaceTracker(face);
        }
    }

    private boolean closeFlg = false;

    private Date firstRightEyeCloseDate = new Date(0);
    private Date firstLeftEyeCloseDate = new Date(0);

    private final int LONG_CLOSE_MILL = 2000;
    private final int SHORT_CLOSE_MILL = 300;


    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class WinkFaceTracker extends Tracker<Face> {

        WinkFaceTracker(Face face) {
            Log.d(TAG, "WinkFaceTracker instance");
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face face) {
            Log.d(TAG, "onNewItem");
//            if(THRESHOLD > face.getIsLeftEyeOpenProbability() && THRESHOLD > face.getIsRightEyeOpenProbability()) {
//                Log.e(TAG, "目閉じてる！");
//                mListener.onClose();
//                closeFlg = true;
//            }

            Log.d(TAG, "getIsLeftEyeOpenProbability "+face.getIsLeftEyeOpenProbability());
            Log.d(TAG, "getIsRightEyeOpenProbability " + face.getIsRightEyeOpenProbability());
        }

        private boolean isLeftClose(Face face){
            return THRESHOLD > face.getIsLeftEyeOpenProbability() && face.getIsLeftEyeOpenProbability() > 0;
        }

        private boolean isRightClose(Face face){
            return THRESHOLD > face.getIsRightEyeOpenProbability() && face.getIsRightEyeOpenProbability() > 0;
        }

        private void reset(){
            Log.d(TAG,"reset");

            firstLeftEyeCloseDate = new Date(Long.MAX_VALUE);
            firstRightEyeCloseDate = new Date(Long.MAX_VALUE);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            Log.d(TAG, "onUpdate");

            Log.d(TAG, "getIsLeftEyeOpenProbability " + face.getIsLeftEyeOpenProbability());
            Log.d(TAG, "getIsRightEyeOpenProbability "+face.getIsRightEyeOpenProbability());

            //どちらも正の値でなければリターン
            if(face.getIsLeftEyeOpenProbability() < 0 || face.getIsRightEyeOpenProbability() < 0) return;

            //初めて目を閉じた時間
            if(firstRightEyeCloseDate.compareTo(new Date(Long.MAX_VALUE)) == 0 && isLeftClose(face)){
                firstLeftEyeCloseDate = new Date(System.currentTimeMillis());
            }

            if(firstRightEyeCloseDate.compareTo(new Date(Long.MAX_VALUE)) == 0 && isRightClose(face)){
                firstRightEyeCloseDate = new Date(System.currentTimeMillis());
            }

            //今両目を閉じているか
            if(isLeftClose(face) && isRightClose(face)) {
                Log.d(TAG,"長く目を閉じるチェック");

                //長めに閉じているか
                final Date now = new Date(System.currentTimeMillis() - LONG_CLOSE_MILL);
                Log.d(TAG,"firstLeftEyeCloseDate " + firstLeftEyeCloseDate.getTime());
                Log.d(TAG,"now " + now.getTime());

                int diff = now.compareTo(firstLeftEyeCloseDate);
                int diff2 = now.compareTo(firstRightEyeCloseDate);

                if (diff > 0 && diff2 > 0) {
                    //今の時刻より引いてある日時が、firstLeftEyeCloseDateよりも
                    Log.d(TAG,"長く目を閉じる");
                    reset();
                    mListener.onLongClose();
                    return;

                }

            }

            //短く目を閉じているか
            final Date now = new Date(System.currentTimeMillis() - SHORT_CLOSE_MILL);
            int diff = now.compareTo(firstLeftEyeCloseDate);
            int diff2 = now.compareTo(firstRightEyeCloseDate);

            Log.d(TAG,"軽く目を閉じるチェック");
            Log.d(TAG,"firstLeftEyeCloseDate " + firstLeftEyeCloseDate.getTime());
            Log.d(TAG,"now " + now.getTime());

            if (diff > 0 && diff2 > 0) {
                Log.d(TAG,"軽く目を閉じるチェック");
                if(!isRightClose(face) && !isLeftClose(face) ) {
                    Log.d(TAG,"軽く目を閉じる");
                    mListener.onClose();
                    reset();
                    return;
                }
            }

            if(!isLeftClose(face) && !isRightClose(face)) {
                reset();
            }



        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            Log.d(TAG, "onMissing");
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            Log.d(TAG, "onDone");
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final Activity thisActivity = getActivity();

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        //アラート
        ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM);

//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(thisActivity, permissions,
//                        RC_HANDLE_CAMERA_PERM);
//            }
//        };
//
//        Snackbar.make(getActivity().findViewById(R.id.preview), android.R.string.dialog_alert_title,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(android.R.string.ok, listener)
//                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {
        Log.d(TAG, "createCameraSource");

        Context context = getActivity().getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new WinkFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w("TAG", "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Face Tracker sample")
                .setMessage(android.R.string.unknownName)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        Log.d(TAG, "startCameraSource");

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        //オーバーレイしないとWinkFaceTrackerが反応しない

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

    }

}
