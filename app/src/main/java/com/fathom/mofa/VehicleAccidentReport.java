package com.fathom.mofa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fathom.mofa.DataModels.CarPhotosDataModel;
import com.fathom.mofa.DataModels.DamageReportDataModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.fathom.mofa.MainActivity.FRAGMENT;
import static com.fathom.mofa.VehicleRecord.damageReportRecord;
import static com.fathom.mofa.VehicleRecord.vehicleInRecord;
import static com.fathom.mofa.VehicleRecord.vehicleRecord;
import static com.fathom.mofa.VehicleSetUp.vehicle;


public class VehicleAccidentReport extends Fragment {

    private NavController mNavController;
    private ImageView vehicleLeftSide;
    private ImageView vehicleRightSide;
    private ImageView vehicleFrontSide;
    private ImageView vehicleBackSide;
    private ImageView carHasDamage;
    private ImageView carIsUseable;
    private ImageView front;
    private ImageView frontRight;
    private ImageView frontLeft;
    private ImageView windshield;
    private ImageView frontLeftDoor;
    private ImageView frontRightDoor;
    private ImageView backRightDoor;
    private ImageView backLeftDoor;
    private ImageView ceilingFront;
    private ImageView ceilingBack;
    private ImageView backWindShield;
    private ImageView backRight;
    private ImageView backLeft;
    private ImageView back;
    private Button next;
    private Button backButton;
    // Damage Report
    private ViewFlipper mViewFlipper;

    // Photos
    public static CarPhotosDataModel carPhotosRecord = new CarPhotosDataModel();
    private String selector;
    private SimpleDateFormat formatter;
    private Date mDate;

    // Vehicle state
    private boolean carDamageStatus = false;
    private boolean carUseStatus = false;

    private int actionToRecordConfirmation = R.id.action_vehicleAccidentReport_to_handoverConfirmation;


    public VehicleAccidentReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_accident_report, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewFlipper = view.findViewById(R.id.vehicleRecordViewFlipper);
        switch (vehicleInRecord.getCarType()) {
            case "Saloon":
                mViewFlipper.setDisplayedChild(0);
                back = view.findViewById(R.id.backConfirmation);
                backRight = view.findViewById(R.id.backRightConfirmation);
                backLeft = view.findViewById(R.id.backLeftConfirmation);
                backWindShield = view.findViewById(R.id.windShieldBackConfirmation);
                backRightDoor = view.findViewById(R.id.backRightDoorConfirmation);
                backLeftDoor = view.findViewById(R.id.backLeftDoorConfirmation);
                ceilingBack = view.findViewById(R.id.backCeilingConfirmation);
                ceilingFront = view.findViewById(R.id.ceilingConfirmation);
                frontLeftDoor = view.findViewById(R.id.frontLeftDoorConfirmation);
                frontRightDoor = view.findViewById(R.id.frontRightDoorConfirmation);
                windshield = view.findViewById(R.id.windShieldConfirmation);
                frontRight = view.findViewById(R.id.frontRightConfirmation);
                frontLeft = view.findViewById(R.id.frontLeftConfirmation);
                front = view.findViewById(R.id.frontConfirmation);
                break;
            case "Jeep":
                mViewFlipper.setDisplayedChild(1);
                front = view.findViewById(R.id.jeepFront);
                frontLeft = view.findViewById(R.id.jeepFrontLeft);
                frontRight = view.findViewById(R.id.jeepFrontRight);
                frontLeftDoor = view.findViewById(R.id.jeepFrontLeftDoor);
                frontRightDoor = view.findViewById(R.id.jeepFrontRightDoor);
                windshield = view.findViewById(R.id.jeepWindShield);
                ceilingFront = view.findViewById(R.id.jeepCeiling);
//                backCeiling = view.findViewById(R.id.jeepBackCeiling);
                backRightDoor = view.findViewById(R.id.jeepBackRightDoor);
                backLeftDoor = view.findViewById(R.id.jeepBackLeftDoor);
                backWindShield = view.findViewById(R.id.jeepBackWindShield);
                backLeft = view.findViewById(R.id.jeepBackLeft);
                backRight = view.findViewById(R.id.jeepBackRight);
                back = view.findViewById(R.id.jeepBack);
                break;
            case "Family":
                mViewFlipper.setDisplayedChild(2);
                front = view.findViewById(R.id.familyFront);
                frontLeft = view.findViewById(R.id.familyFrontLeft);
                frontRight = view.findViewById(R.id.familyFrontRight);
                frontLeftDoor = view.findViewById(R.id.familyFrontLeftDoor);
                frontRightDoor = view.findViewById(R.id.familyFrontRightDoor);
                windshield = view.findViewById(R.id.familyWindShield);
                ceilingFront = view.findViewById(R.id.familyCeiling);
//                backCeiling = view.findViewById(R.id.jeepBackCeiling);
                backRightDoor = view.findViewById(R.id.familyBackRightDoor);
                backLeftDoor = view.findViewById(R.id.familyBackLeftDoor);
                backWindShield = view.findViewById(R.id.jeepBackWindShield);
                backLeft = view.findViewById(R.id.familyBackLeft);
                backRight = view.findViewById(R.id.familyBackRight);
                back = view.findViewById(R.id.familyBack);
                break;
            case "Van":
                mViewFlipper.setDisplayedChild(3);
                front = view.findViewById(R.id.vanFront);
                frontLeft = view.findViewById(R.id.vanFrontLeft);
                frontRight = view.findViewById(R.id.vanFrontRight);
                frontLeftDoor = view.findViewById(R.id.vanFrontLeftDoor);
                frontRightDoor = view.findViewById(R.id.vanFrontRightDoor);
                windshield = view.findViewById(R.id.vanWindShield);
                ceilingFront = view.findViewById(R.id.vanCeiling);
//                backCeiling = view.findViewById(R.id.jeepBackCeiling);
                backRightDoor = view.findViewById(R.id.vanBackRightDoor);
                backLeftDoor = view.findViewById(R.id.vanBackLeftDoor);
//                backWindShield = view.findViewById(R.id.vanBackWindShield);
                backLeft = view.findViewById(R.id.vanBackLeft);
                backRight = view.findViewById(R.id.vanBackRight);
                back = view.findViewById(R.id.vanBack);
                break;
        }
        vehicleRightSide = view.findViewById(R.id.vehicleRightSideAR);
        vehicleLeftSide = view.findViewById(R.id.vehicleLeftSideAR);
        vehicleFrontSide = view.findViewById(R.id.vehicleFrontSideAR);
        vehicleBackSide = view.findViewById(R.id.vehicleBackSideAR);
        carHasDamage = view.findViewById(R.id.carHasDamageImage);
        carIsUseable = view.findViewById(R.id.carIsUseableImage);
        backButton = view.findViewById(R.id.backAccidentReport);
        next = view.findViewById(R.id.nextAccidentReport);


        mNavController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        carIsUseable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carUseStatus) {
                    carIsUseable.setImageResource(R.drawable.empty_check_box);
                    carUseStatus = false;
                } else {
                    carUseStatus = true;
                    carIsUseable.setImageResource(R.drawable.checked_check_box);
                }
                vehicleRecord.setCarIsUseable(carUseStatus);
            }
        });

        carHasDamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carDamageStatus) {
                    carHasDamage.setImageResource(R.drawable.empty_check_box);
                    carDamageStatus = false;
                } else {
                    carDamageStatus = true;
                    carHasDamage.setImageResource(R.drawable.checked_check_box);
                }
                vehicleRecord.setCarHasDamage(carDamageStatus);

            }
        });

        damageReportRecord.setCarId(vehicleInRecord.getPlateNumber());
        damageReportRecord.setCarTransaction(vehicleRecord.getCarTransaction());
        // To describe the transition of the car which is four Types:
        // 1- Getting the car from the rental company. "RTM"
        // 2- Returning the car to the rental company. "MTR"
        // 3- Hanover to the driver."MTD"
        // 4- Retrieval from the driver. "DTM"
        // D: Driver
        // M: Mofa
        // R: Rental Company
        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isFront()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            front.setImageResource(R.drawable.front_red);
                            break;
                        case "Jeep":
                            front.setImageResource(R.drawable.jeep_front_red);
                            break;
                        case "Family":
                        case "Van":
                            front.setImageResource(R.drawable.family_front_red);
                            break;
                    }
                    damageReportRecord.setFront(true);
                } else if (damageReportRecord.isFront()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            front.setImageResource(R.drawable.front);
                            break;
                        case "Jeep":
                            front.setImageResource(R.drawable.jeep_front);
                            break;
                        case "Family":
                        case "Van":
                            front.setImageResource(R.drawable.family_front);
                            break;
                    }
                    damageReportRecord.setFront(false);
                }
            }
        });

        frontRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isFrontRight()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontRight.setImageResource(R.drawable.front_right_red);
                            break;
                        case "Jeep":
                            frontRight.setImageResource(R.drawable.jeep_front_right_red);
                            break;
                        case "Family":
                        case "Van":
                            frontRight.setImageResource(R.drawable.family_front_right_red);
                            break;
                    }
                    damageReportRecord.setFrontRight(true);
                } else if (damageReportRecord.isFrontRight()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontRight.setImageResource(R.drawable.front_right);
                            break;
                        case "Jeep":
                            frontRight.setImageResource(R.drawable.jeep_front_right);
                            break;
                        case "Family":
                        case "Van":
                            frontRight.setImageResource(R.drawable.family_front_right);
                            break;
                    }
                    damageReportRecord.setFrontRight(false);
                }
            }
        });

        frontLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isFrontLeft()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontLeft.setImageResource(R.drawable.front_left_red);
                            break;
                        case "Jeep":
                            frontLeft.setImageResource(R.drawable.jeep_front_left_red);
                            break;
                        case "Family":
                        case "Van":
                            frontLeft.setImageResource(R.drawable.family_front_left_red);
                            break;
                    }
                    damageReportRecord.setFrontLeft(true);
                } else if (damageReportRecord.isFrontLeft()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontLeft.setImageResource(R.drawable.front_left);
                            break;
                        case "Jeep":
                            frontLeft.setImageResource(R.drawable.jeep_front_left);
                            break;
                        case "Family":
                        case "Van":
                            frontLeft.setImageResource(R.drawable.family_front_left);
                            break;
                    }
                    damageReportRecord.setFrontLeft(false);
                }
            }
        });

        frontRightDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isPassengerDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontRightDoor.setImageResource(R.drawable.front_right_door_red);
                            break;
                        case "Jeep":
                            frontRightDoor.setImageResource(R.drawable.jeep_front_right_door_red);
                            break;
                        case "Family":
                        case "Van":
                            frontRightDoor.setImageResource(R.drawable.family_front_right_door_red);
                            break;
                    }
                    damageReportRecord.setPassengerDoor(true);
                } else if (damageReportRecord.isPassengerDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontRightDoor.setImageResource(R.drawable.front_right_door);
                            break;
                        case "Jeep":
                            frontRightDoor.setImageResource(R.drawable.jeep_front_right_door);
                            break;
                        case "Family":
                        case "Van":
                            frontRightDoor.setImageResource(R.drawable.family_front_right_door);
                            break;
                    }
                    damageReportRecord.setPassengerDoor(false);
                }
            }
        });

        frontLeftDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isDriverDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontLeftDoor.setImageResource(R.drawable.front_left_door_red);
                            break;
                        case "Jeep":
                            frontLeftDoor.setImageResource(R.drawable.jeep_front_left_door_red);
                            break;
                        case "Family":
                        case "Van":
                            frontLeftDoor.setImageResource(R.drawable.family_front_left_door_red);
                            break;
                    }
                    damageReportRecord.setDriverDoor(true);
                } else if (damageReportRecord.isDriverDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            frontLeftDoor.setImageResource(R.drawable.front_left_door);
                            break;
                        case "Jeep":
                            frontLeftDoor.setImageResource(R.drawable.jeep_front_left_door);
                            break;
                        case "Family":
                        case "Van":
                            frontLeftDoor.setImageResource(R.drawable.family_front_left_door);
                            break;
                    }
                    damageReportRecord.setDriverDoor(false);
                }
            }
        });

        backRightDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isBackRightDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backRightDoor.setImageResource(R.drawable.back_door_right_red);
                            break;
                        case "Jeep":
                            backRightDoor.setImageResource(R.drawable.jeep_back_right_door_red);
                            break;
                        case "Family":
                        case "Van":
                            backRightDoor.setImageResource(R.drawable.family_back_right_door_red);
                            break;
                    }
                    damageReportRecord.setBackRightDoor(true);
                } else if (damageReportRecord.isBackRightDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backRightDoor.setImageResource(R.drawable.back_door_right);
                            break;
                        case "Jeep":
                            backRightDoor.setImageResource(R.drawable.jeep_back_right_door);
                            break;
                        case "Family":
                        case "Van":
                            backRightDoor.setImageResource(R.drawable.family_back_right_door);
                            break;
                    }
                    damageReportRecord.setBackRightDoor(false);
                }
            }
        });

        backLeftDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isBackLeftDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backLeftDoor.setImageResource(R.drawable.back_door_left_red);
                            break;
                        case "Jeep":
                            backLeftDoor.setImageResource(R.drawable.jeep_back_left_door_red);
                            break;
                        case "Family":
                        case "Van":
                            backLeftDoor.setImageResource(R.drawable.family_back_left_door_red);
                            break;
                    }
                    damageReportRecord.setBackLeftDoor(true);
                } else if (damageReportRecord.isBackLeftDoor()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backLeftDoor.setImageResource(R.drawable.back_door_left);
                            break;
                        case "Jeep":
                            backLeftDoor.setImageResource(R.drawable.jeep_back_left_door);
                            break;
                        case "Family":
                        case "Van":
                            backLeftDoor.setImageResource(R.drawable.family_back_left_door);
                            break;
                    }
                    damageReportRecord.setBackLeftDoor(false);
                }
            }
        });

        windshield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isFrontWindShield()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            windshield.setImageResource(R.drawable.wind_sheild_red);
                            break;
                        case "Jeep":
                            windshield.setImageResource(R.drawable.jeep_wind_sheild_red);
                            break;
                        case "Family":
                        case "Van":
                            windshield.setImageResource(R.drawable.family_wind_sheild_red);
                            break;
                    }
                    damageReportRecord.setFrontWindShield(true);
                } else if (damageReportRecord.isFrontWindShield()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            windshield.setImageResource(R.drawable.wind_sheild);
                            break;
                        case "Jeep":
                            windshield.setImageResource(R.drawable.jeep_wind_sheild);
                            break;
                        case "Family":
                        case "Van":
                            windshield.setImageResource(R.drawable.family_wind_sheild);
                            break;
                    }

                    damageReportRecord.setFrontWindShield(false);
                }
            }
        });

        ceilingFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isCeiling()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            ceilingFront.setImageResource(R.drawable.ceiling_red);
                            break;
                        case "Jeep":
                            ceilingFront.setImageResource(R.drawable.jeep_ceiling_red);
                            break;
                        case "Family":
                        case "Van":
                            ceilingFront.setImageResource(R.drawable.family_ceiling_red);
                            break;
                    }
                    damageReportRecord.setCeiling(true);
                } else if (damageReportRecord.isCeiling()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            ceilingFront.setImageResource(R.drawable.ceiling);
                            break;
                        case "Jeep":
                            ceilingFront.setImageResource(R.drawable.jeep_ceiling);
                            break;
                        case "Family":
                        case "Van":
                            ceilingFront.setImageResource(R.drawable.family_ceiling);
                            break;
                    }
                    damageReportRecord.setCeiling(false);
                }
            }
        });

        if (vehicleInRecord.getCarType().equals("Saloon")) {
            ceilingBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!damageReportRecord.isBackCeiling()) {
                        ceilingBack.setImageResource(R.drawable.ceiling_back_red);
                        damageReportRecord.setBackCeiling(true);
                    } else if (damageReportRecord.isBackCeiling()) {
                        ceilingBack.setImageResource(R.drawable.ceiling_back);
                        damageReportRecord.setBackCeiling(false);
                    }
                }
            });
        }

        if (vehicleInRecord.getCarType().equals("Saloon") ||
                vehicleInRecord.getCarType().equals("Jeep") ||
                vehicleInRecord.getCarType().equals("Family")) {
            backWindShield.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!damageReportRecord.isBackWindShield()) {
                        switch (vehicleInRecord.getCarType()) {
                            case "Saloon":
                                backWindShield.setImageResource(R.drawable.back_wind_shield_red);
                                break;
                            case "Jeep":
                                backWindShield.setImageResource(R.drawable.jeep_back_red);
                                break;
                            case "Family":
                                backWindShield.setImageResource(R.drawable.family_back_red);
                                break;
                        }
                        damageReportRecord.setBackWindShield(true);
                    } else if (damageReportRecord.isBackWindShield()) {
                        switch (vehicleInRecord.getCarType()) {
                            case "Saloon":
                                backWindShield.setImageResource(R.drawable.back_wind_shield);
                                break;
                            case "Jeep":
                                backWindShield.setImageResource(R.drawable.jeep_back);
                                break;
                            case "Family":
                                backWindShield.setImageResource(R.drawable.family_back);
                                break;
                        }
                        damageReportRecord.setBackWindShield(false);
                    }
                }
            });
        }

        backRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isBackRight()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backRight.setImageResource(R.drawable.back_right_red);
                            break;
                        case "Jeep":
                            backRight.setImageResource(R.drawable.jeep_back_right_red);
                            break;
                        case "Family":
                        case "Van":
                            backRight.setImageResource(R.drawable.family_back_right_red);
                            break;
                    }
                    damageReportRecord.setBackRight(true);
                } else if (damageReportRecord.isBackRight()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backRight.setImageResource(R.drawable.back_right);
                            break;
                        case "Jeep":
                            backRight.setImageResource(R.drawable.jeep_back_right);
                            break;
                        case "Family":
                        case "Van":
                            backRight.setImageResource(R.drawable.family_back_right);
                            break;
                    }
                    damageReportRecord.setBackRight(false);
                }
            }
        });

        backLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isBackLeft()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backLeft.setImageResource(R.drawable.back_left_red);
                            break;
                        case "Jeep":
                            backLeft.setImageResource(R.drawable.jeep_back_left_red);
                            break;
                        case "Family":
                        case "Van":
                            backLeft.setImageResource(R.drawable.family_back_left_red);
                            break;
                    }
                    damageReportRecord.setBackLeft(true);
                } else if (damageReportRecord.isBackLeft()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            backLeft.setImageResource(R.drawable.back_left);
                            break;
                        case "Jeep":
                            backLeft.setImageResource(R.drawable.jeep_back_left);
                            break;
                        case "Family":
                        case "Van":
                            backLeft.setImageResource(R.drawable.family_back_left);
                            break;
                    }
                    damageReportRecord.setBackLeft(false);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!damageReportRecord.isBack()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            back.setImageResource(R.drawable.back_red);
                            break;
                        case "Jeep":
                            back.setImageResource(R.drawable.jeep_back_red);
                            break;
                        case "Family":
                        case "Van":
                            back.setImageResource(R.drawable.family_back_red);
                            break;
                    }
                    damageReportRecord.setBack(true);
                } else if (damageReportRecord.isBack()) {
                    switch (vehicleInRecord.getCarType()) {
                        case "Saloon":
                            back.setImageResource(R.drawable.back);
                            break;
                        case "Jeep":
                            back.setImageResource(R.drawable.jeep_back);
                            break;
                        case "Family":
                        case "Van":
                            back.setImageResource(R.drawable.family_back);
                            break;
                    }
                    damageReportRecord.setBack(false);
                }
            }
        });

        vehicleLeftSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage(getContext());
                selector = "vehicleLeftSide";


            }
        });

        vehicleRightSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage(getContext());
                selector = "vehicleRightSide";


            }
        });

        vehicleFrontSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage(getContext());
                selector = "vehicleFrontSide";


            }
        });

        vehicleBackSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage(getContext());
                selector = "vehicleBackSide";


            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.popBackStack();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVehicleRecordInfo()) {
                    mNavController.navigate(actionToRecordConfirmation);
                }
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        FRAGMENT = "vehicleAccidentReport";
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mDate = new Date();

    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        switch (selector) {
                            case "vehicleRightSide":
                                vehicleRightSide.setImageBitmap(selectedImage);
                                vehicleRecord.setPhotoRightSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "right");
                                carPhotosRecord.setPhotoRightSide(selectedImage);
                                break;
                            case "vehicleLeftSide":
                                vehicleLeftSide.setImageBitmap(selectedImage);
                                vehicleRecord.setPhotoLeftSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "left");
                                carPhotosRecord.setPhotoLeftSide(selectedImage);
                                break;
                            case "vehicleFrontSide":
                                vehicleFrontSide.setImageBitmap(selectedImage);
                                vehicleRecord.setPhotoFrontSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "front");
                                carPhotosRecord.setPhotoFrontSide(selectedImage);
                                break;
                            case "vehicleBackSide":
                                vehicleBackSide.setImageBitmap(selectedImage);
                                vehicleRecord.setPhotoBackSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "back");
                                carPhotosRecord.setPhotoBackSide(selectedImage);
                                break;

                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        Log.d("GET IMAGE", "the Uri is: " + selectedImage.toString());
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Log.d("GET IMAGE", "the path is:" + filePathColumn[0]);

                        if (selectedImage != null) {
                            Context applicationContext = getActivity().getApplicationContext();
                            Cursor cursor = applicationContext.getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            Log.d("GET IMAGE", "the cursor is:" + cursor);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Log.d("GET IMAGE", "the picture path is: " + picturePath);

                                switch (selector) {
                                    case "vehicleRightSide":
                                        vehicleRightSide.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                        vehicleRecord.setPhotoRightSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "right");
                                        carPhotosRecord.setPhotoRightSide(BitmapFactory.decodeFile(picturePath));
                                        break;
                                    case "vehicleLeftSide":
                                        vehicleLeftSide.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                        vehicleRecord.setPhotoLeftSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "left");
                                        carPhotosRecord.setPhotoLeftSide(BitmapFactory.decodeFile(picturePath));
                                        break;
                                    case "vehicleFrontSide":
                                        vehicleFrontSide.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                        vehicleRecord.setPhotoFrontSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "front");
                                        carPhotosRecord.setPhotoFrontSide(BitmapFactory.decodeFile(picturePath));
                                        break;
                                    case "vehicleBackSide":
                                        vehicleBackSide.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                        vehicleRecord.setPhotoBackSide(vehicleInRecord.getPlateNumber() + formatter.format(mDate) + "back");
                                        carPhotosRecord.setPhotoBackSide(BitmapFactory.decodeFile(picturePath));
                                        break;

                                }
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    private boolean getVehicleRecordInfo() {

        if (carDamageStatus) {
            String leftSide = vehicleRecord.getPhotoLeftSide();
            String rightSide = vehicleRecord.getPhotoRightSide();
            String frontSide = vehicleRecord.getPhotoFrontSide();
            String backSide = vehicleRecord.getPhotoBackSide();
            if ((leftSide != null) &&
                    (rightSide != null) && (frontSide != null) &&
                    (backSide != null)) {
                vehicleRecord.setPhotoLeftSide(leftSide);
                vehicleRecord.setPhotoRightSide(rightSide);
                vehicleRecord.setPhotoFrontSide(frontSide);
                vehicleRecord.setPhotoBackSide(backSide);
                // add this in the end
                vehicleRecord.setDamageReport(vehicleInRecord.getPlateNumber());
                damageReportRecord.setCarId(vehicleInRecord.getPlateNumber());
                return true;
            } else {
                Toast.makeText(getContext(), "Please fill the missing fields", Toast.LENGTH_SHORT).show();
                return false;

            }
        }
        if (carUseStatus) {
            vehicleRecord.setDamageReport(vehicleInRecord.getPlateNumber());
            damageReportRecord.setCarId(vehicleInRecord.getPlateNumber());


            return true;
        } else {
            Toast.makeText(getContext(), "Please select if the car is Useable", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
