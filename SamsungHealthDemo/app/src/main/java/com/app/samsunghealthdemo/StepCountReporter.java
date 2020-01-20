/**
 * Copyright (C) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * Digital Media & Communications Business, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.app.samsunghealthdemo;

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import android.util.Log;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

public class StepCountReporter {
    private final HealthDataStore mStore;
    private StepCountObserver mStepCountObserver;
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
    private String dataIdentifier = "";
    private LinkedHashMap<String, HealthData> dataHashmap;

    public StepCountReporter(HealthDataStore store) {
        mStore = store;
    }

    public void start(StepCountObserver listener) {
        mStepCountObserver = listener;
        // Register an observer to listen changes of step count and get today step count
        dataIdentifier = "";
        dataHashmap = new LinkedHashMap<>();
        HealthDataObserver.addObserver(mStore, HealthConstants.StepCount.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.Weight.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.BodyTemperature.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.BloodGlucose.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.BloodPressure.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.HeartRate.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.HbA1c.HEALTH_DATA_TYPE, mObserver);
        HealthDataObserver.addObserver(mStore, HealthConstants.Height.HEALTH_DATA_TYPE, mObserver);

        readTodayStepCount();
        readWeight();
        readBodyTemperature();
        readBloodGlucose();
        readBloodPressure();
        readECG();
        readHeartRate();
        readOxygenSaturation();
        readHbA1c();
        readHeight();
    }

    // Read the data on demand
    private void readTodayStepCount() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        ReadRequest request = new ReadRequest.Builder()
                    .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                    .setProperties(new String[] {HealthConstants.StepCount.COUNT})
                    .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                            startTime, endTime)
                    .build();

        try {
            dataIdentifier = HealthConstants.StepCount.COUNT;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readWeight() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.Weight.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.Weight.WEIGHT})
                .build();
        try {
            dataIdentifier = HealthConstants.Weight.WEIGHT;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readBodyTemperature(){
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.BodyTemperature.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.BodyTemperature.TEMPERATURE})
                .build();
        try {
            dataIdentifier = HealthConstants.BodyTemperature.TEMPERATURE;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readBloodGlucose() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.BloodGlucose.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.BloodGlucose.GLUCOSE})
                .build();
        try {
            dataIdentifier = HealthConstants.BloodGlucose.GLUCOSE;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readBloodPressure() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.BloodPressure.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.BloodPressure.DIASTOLIC, HealthConstants.BloodPressure.SYSTOLIC})
                .build();
        try {
            dataIdentifier = HealthConstants.BloodPressure.DIASTOLIC +" "+ HealthConstants.BloodPressure.SYSTOLIC;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readECG() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.Electrocardiogram.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.Electrocardiogram.DATA})
                .build();
        try {
            dataIdentifier = HealthConstants.Electrocardiogram.DATA;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readHeartRate() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.HeartRate.HEART_RATE})
                .build();
        try {
            dataIdentifier = HealthConstants.HeartRate.HEART_RATE;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readOxygenSaturation() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.OxygenSaturation.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.OxygenSaturation.SPO2})
                .build();
        try {
            dataIdentifier = HealthConstants.OxygenSaturation.SPO2;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readHbA1c() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.HbA1c.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.HbA1c.HBA1C})
                .build();
        try {
            dataIdentifier = HealthConstants.HbA1c.HBA1C;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    private void readHeight() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.Height.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.Height.HEIGHT})
                .build();
        try {
            dataIdentifier = HealthConstants.Height.HEIGHT;
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainActivity.APP_TAG, "Getting step count fails.", e);
        }
    }

    //

    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    private final HealthResultHolder.ResultListener<ReadResult> mListener = new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            int count = 0;

            try {
                for (HealthData data : result) {
                    dataHashmap.put(dataIdentifier, data);
                    count += data.getInt(dataIdentifier);
                }
            } finally {
                result.close();
            }

            if (mStepCountObserver != null) {
                mStepCountObserver.onChanged(dataHashmap);
            }
        }
    };

    private final HealthDataObserver mObserver = new HealthDataObserver(null) {

        // Update the step count when a change event is received
        @Override
        public void onChange(String dataTypeName) {
            dataIdentifier = "";
            dataHashmap = new LinkedHashMap<>();
            Log.d(MainActivity.APP_TAG, "Observer receives a data changed event");
            readTodayStepCount();
            readWeight();
            readBodyTemperature();
            readBloodGlucose();
            readBloodPressure();
            readECG();
            readHeartRate();
            readOxygenSaturation();
            readHbA1c();
            readHeight();
        }
    };

    public interface StepCountObserver {
        void onChanged(LinkedHashMap<String, HealthData> dataHash);
    }
}
