package com.fathom.mofa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fathom.mofa.DataModels.NotificationDataModel;
import com.fathom.mofa.DataModels.VehicleDataModel;
import com.fathom.mofa.ServicesAndRepos.VehicleRepository;
import com.fathom.mofa.ViewModels.NotificationViewModel;
import com.fathom.mofa.ViewModels.VehicleViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.fathom.mofa.LoginActivity.USER;
import static com.fathom.mofa.MainActivity.FRAGMENT;
import static com.fathom.mofa.VehicleRegistration.carPhotos;
import static com.fathom.mofa.VehicleSetUp.vehicle;
import static com.fathom.mofa.VehicleSetUpDamageReport.damageReport;
import static com.fathom.mofa.VehicleSetUpRentalInfo.rentalInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleSetUpSignature extends Fragment {

//    declare class variables
    private NavController mNavController;
    private Button rentalSignature;
    private Button mofaSignature;
    private int signatureSelector;
    private VehicleViewModel model;
    private NotificationViewModel mNotificationViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef;
    private StorageReference frontImageRef;
    private StorageReference backImageRef;
    private final String TAG = "VEHICLE SET UP";
    private ProgressDialog progressDialog;
    private VehicleRepository mVehicleRepository;
    public static boolean doneUploading = false;

    public VehicleSetUpSignature() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_set_up_signature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//       link UI and set values
        rentalSignature = view.findViewById(R.id.rentalSignature);
        mofaSignature = view.findViewById(R.id.mofaSignature);
        Button done = view.findViewById(R.id.doneVehicleSetUp);
        // Dialog components
        final Dialog dialog = new Dialog(getContext());
        final CaptureSignatureView mSig = new CaptureSignatureView(getContext(), null);
        dialog.setContentView(R.layout.signature);
        dialog.setTitle("Signature");
        Button saveSignature = dialog.findViewById(R.id.save);
        Button cancelSignature = dialog.findViewById(R.id.cancel);
        Button clearSignature = dialog.findViewById(R.id.clear);
        LinearLayout signatureLayout = dialog.findViewById(R.id.signature2);

        // setting up Progress Dialog and Storage
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mNavController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        // adding the signature functionality to the signatureLayout
        signatureLayout.addView(mSig, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        model = new ViewModelProvider(requireActivity()).get(VehicleViewModel.class);
        model.initVehicles();
        model.getVehicles().observe(getViewLifecycleOwner(), new Observer<List<VehicleDataModel>>() {
            @Override
            public void onChanged(List<VehicleDataModel> vehicleDataModels) {

            }
        });

        mNotificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        mNotificationViewModel.initNotifications();
        mNotificationViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<NotificationDataModel>>() {
            @Override
            public void onChanged(List<NotificationDataModel> notificationDataModels) {

            }
        });
        // filling up some data for Vehicle Object
        vehicle.setRentalInfo(vehicle.getPlateNumber());
        vehicle.setCarName(vehicle.getManufacturer()+ " "+vehicle.getModel()+ " "+vehicle.getPlateNumber());
        vehicle.setPhotoLeftSide(vehicle.getPlateNumber()+"left");
        vehicle.setPhotoRightSide(vehicle.getPlateNumber()+"right");
        vehicle.setPhotoFrontSide(vehicle.getPlateNumber()+"front");
        vehicle.setPhotoBackSide(vehicle.getPlateNumber()+"back");
        vehicle.setStatus("Returned");

        rentalSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureSelector = 0;
                dialog.show();

            }
        });

        mofaSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureSelector = 1;
                dialog.show();

            }
        });

//       done click implementation
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadVehicleInfo();
                uploadDamageReportOfVehicle();
                uploadRentalInfoOfVehicle();
                uploadNotification();
                uploadVehicleLeftSide();
                uploadVehicleRightSide();
                uploadVehicleFrontSide();
                uploadVehicleBackSide();
                uploadVehicleFrontInterior();
                uploadVehicleBackInterior();
                uploadVehicleTrunk();
                updateVehicleToViewModel();



                mNavController.navigate(R.id.home);

            }
        });

        cancelSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        clearSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSig.ClearCanvas();
            }
        });

        saveSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureSelector == 0) {
                    Bitmap signature = mSig.getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), signature);
                    rentalSignature.setBackground(d);
                    mSig.ClearCanvas();
                    dialog.dismiss();
                }
                if (signatureSelector == 1) {

                    Bitmap signature = mSig.getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), signature);
                    mofaSignature.setBackground(d);
                    mSig.ClearCanvas();
                    dialog.dismiss();
                }

            }
        });
    }

//    upload notification info
    private void uploadNotification() {
         SharedPreferences pref = getActivity().getSharedPreferences(USER, 0); // 0 - for private mode
        String name = pref.getString("userName", "");
        String added = getResources().getString(R.string.been_added);
        NotificationDataModel notification = new NotificationDataModel();
        notification.setNotificationContent(vehicle.getManufacturer()+" "+ vehicle.getModel()+" "+vehicle.getMake()+" "+ added+" "+name);
        Date date = new Date();
        notification.setNotificationDate(date);
        notification.setNotificationType("Set Up");
        db.collection("Notifications")
                .document().set(notification);
        addNotificationToDataModel(notification);

    }


    private void uploadVehicleInfo() {
        progressDialog.show();
        db.collection("Vehicles")
                .document(vehicle.getPlateNumber()).set(vehicle);
    }

    private void uploadRentalInfoOfVehicle() {
        db.collection("Rental Information")
                .document(rentalInfo.getCarId()).set(rentalInfo);

    }

    private void uploadDamageReportOfVehicle() {
        damageReport.setCarType(vehicle.getCarType());
        // Should set damageReportName
        db.collection("Damage Reports")
                .document().set(damageReport);

    }

    private void uploadVehicleRightSide() {
        StorageReference rightImageRef = storageRef.child(vehicle.getPhotoRightSide());

        Bitmap bitmap = carPhotos.getPhotoRightSide();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = rightImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User right image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User right image uploaded.");

                carPhotos.setPhotoRightSide(null);

            }
        });

    }

//    upload images
    private void uploadVehicleLeftSide() {
        StorageReference leftImageRef = storageRef.child(vehicle.getPhotoLeftSide());

        Bitmap bitmap = carPhotos.getPhotoLeftSide();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = leftImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User left image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User left image uploaded.");
                carPhotos.setPhotoLeftSide(null);

            }
        });

    }

    private void uploadVehicleFrontSide() {
        frontImageRef = storageRef.child(vehicle.getPhotoFrontSide());

        Bitmap bitmap = carPhotos.getPhotoFrontSide();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = frontImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User front image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User front image uploaded.");
                carPhotos.setPhotoFrontSide(null);

            }
        });

    }

    private void uploadVehicleBackSide() {
        backImageRef = storageRef.child(vehicle.getPhotoBackSide());

        Bitmap bitmap = carPhotos.getPhotoBackSide();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = backImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User back image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User back image uploaded.");
                carPhotos.setPhotoBackSide(null);
            }
        });

    }

    private void uploadVehicleFrontInterior() {
        backImageRef = storageRef.child(vehicle.getVehicleFrontInterior());

        Bitmap bitmap = carPhotos.getVehicleFrontInterior();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = backImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User back image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User back image uploaded.");
                carPhotos.setVehicleFrontInterior(null);
            }
        });

    }
    private void uploadVehicleBackInterior() {
        backImageRef = storageRef.child(vehicle.getVehicleBackInterior());

        Bitmap bitmap = carPhotos.getVehicleBackInterior();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = backImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User back image failed to upload.");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User back image uploaded.");

                carPhotos.setVehicleBackInterior(null);
            }
        });

    }

    private void uploadVehicleTrunk() {
        backImageRef = storageRef.child(vehicle.getVehicleTrunk());

        Bitmap bitmap = carPhotos.getVehicleTrunk();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = backImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "User back image failed to upload.");
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "User back image uploaded.");
                progressDialog.dismiss();
                carPhotos.setVehicleTrunk(null);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        FRAGMENT = "vehicleSetUpSignature";
    }

//    update the model
    private void updateVehicleToViewModel() {

        model.addVehicle(vehicle);
    }

//    update the model
    private void addNotificationToDataModel(NotificationDataModel notification) {
        mNotificationViewModel.addNotification(notification);

    }

    @Override
    public void onStop() {
        super.onStop();
        rentalSignature = null;
        mofaSignature = null;
        model = null;
        mNotificationViewModel = null;
        storageRef = null;
        frontImageRef = null;
        backImageRef = null;
        mVehicleRepository = null;

    }


    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("LOG_TAG", "Error checking internet connection", e);
            }
        } else {
            Log.d("LOG_TAG", "No network available!");
            Toast.makeText(context, "the internet is not available", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }



}
