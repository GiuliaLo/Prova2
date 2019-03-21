package com.example.prova2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
*/

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int RC_OCR_CAPTURE = 9003;


    private CameraSource mCameraSource;

    private String sectionNumber;

    private OnFragmentInteractionListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * param param1 Parameter 1.
     * param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    //public static CameraFragment newInstance(String param1, String param2) {
    public static CameraFragment newInstance(int sectionNumber) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionNumber = getArguments().getString(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        final SurfaceView cameraView = view.findViewById(R.id.camera_view);


        TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();

        mCameraSource = new CameraSource
                .Builder(getActivity(), textRecognizer)
                .setAutoFocusEnabled(true)
                .build();

        Button btnCapture = (Button) view.findViewById(R.id.button);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePicture();
                Intent intent = new Intent(getActivity(), OcrCaptureActivity.class);
                startActivityForResult(intent, RC_OCR_CAPTURE);
                Snackbar.make(v, "Capture text", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA}, 0);
                    }
                    mCameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mCameraSource != null)
                    mCameraSource.stop();
            }
        });


        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                SparseArray<TextBlock> items = detections.getDetectedItems();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    if (item != null && item.getValue() != null) {
                        Log.i(TAG, "Text detected! " + item.getValue());

                    }
                }
            }


        });



        return view;
    }

    /*
    private void takePicture() {
        if (mCameraDevice == null)
            return;
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] rawSizes = null;
            if (characteristics != null)
                rawSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.RAW_SENSOR);

            //Capture image with custom size
            int width = 640;
            int height = 480;
            if(rawSizes != null && rawSizes.length > 0) {
                width = rawSizes[0].getWidth();
                height = rawSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width,height, RAW_SENSOR,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            String directoryPath = Environment.getExternalStorageDirectory()+"/DCIM/WelcomeApp";
            File directory = new File(directoryPath);
            if (! directory.exists()){
                directory.mkdir();
            }

            file = new File(directoryPath+"/"+ UUID.randomUUID().toString()+".jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);

                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        {
                            if(image != null)
                                image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try{
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                    }finally {
                        if(outputStream != null)
                            outputStream.close();
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            };

            mCameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try{
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    */

    private Context mContext;

    private final Object mCameraLock = new Object();

    // Guarded by mCameraLock
    //private CameraDevice mCamera;

/*
    public void takePicture(CameraSource.ShutterCallback shutter, CameraSource.PictureCallback jpeg) {
        synchronized (mCameraLock) {
            if (mCameraDevice != null) {

                PictureDoneCallback doneCallback = new PictureDoneCallback();
                doneCallback.mDelegate = jpeg;
                mCameraSource.takePicture(startCallback, null, null, doneCallback);
            }
        }
    }

    private class PictureDoneCallback implements CameraSource.PictureCallback {
        private CameraSource.PictureCallback mDelegate;

        @Override
        public void onPictureTaken(byte[] data) {
            if (mDelegate != null) {
                mDelegate.onPictureTaken(data);
            }
            synchronized (mCameraLock) {
                if (mCameraDevice != null) {
                    mCameraDevice.startPreview();
                }
            }
        }
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Snackbar.make(getView(), "Text read successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //statusMessage.setText(R.string.ocr_success);
                    Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    Snackbar.make(getView(), "No text captured", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                Snackbar.make(getView(), "Error reading text", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //statusMessage.setText(String.format(getString(R.string.ocr_error),
                        //CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
