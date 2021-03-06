package btu.treasurehunt;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import btu.treasurehunt.Sensors.Accelerometer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import btu.treasurehunt.Sensors.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import static btu.treasurehunt.R.id.container;

/**
 *
 * Created by Dez on 12/5/2017.
 */

public class Collectingcoins extends  Fragment implements SensorEventListener {
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    ArrayList<String> checked = new ArrayList<>();
    private Button send;
    List<String> sNames;
    List<Sensor> sList;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    SessionManager session;
    String value;
    ListView list;
    Account myaccount;
    SensorManager sManager;
    int Count=0;
    float valueLight,valueThermometer,valueStepCounter , valueProximity, valueBarometer,
            valueXaccelerometer , valueYaccelerometer , valueZaccelerometer,
            valueXmagnetometer, valueYMagnetometer , valueZmagnetometer,
            valueXgyroscope , valueYgyroscope, valueZgyroscope,
            valueXgravity , valueYgravity, valueZgravity,
            valueXrotation , valueYrotation , valueZrotation;

    Sensor mlight , mTemperature , mPedometer , mAccelerometer , mMagnetometer , mGyroscope,mProximity ,mGravity , mRotation ,mBarometer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ProgressDialog progressDialog = new ProgressDialog(Collectingcoins.this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        View rootView = inflater.inflate(R.layout.collecting_coins, container, false);
        list = (ListView) rootView.findViewById(R.id.list);
        sManager = (SensorManager)this.getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        sList =  sManager.getSensorList(Sensor.TYPE_ALL);
        send =(Button)rootView.findViewById(R.id.sendInfo);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        session = new SessionManager(this.getActivity().getApplicationContext());
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        ((MainActivity)getActivity()).fabtext.setVisibility(View.GONE);
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://databaserest.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Accountservice service = retrofit.create(Accountservice.class);
        String id="0";
        if(session.isLoggedIn())
        {
            id=session.getUserDetails().get("id");
        }

        mlight= sManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mTemperature = sManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mPedometer = sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mAccelerometer =sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer= sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope= sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mProximity =sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mGravity =sManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mRotation =sManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mBarometer=sManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sNames = new ArrayList();
        for (int i = 0 ; i< sList.size() ; i++)
        {
            if(sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_LIGHT)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_PRESSURE)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)||sList.get(i)==sManager.getDefaultSensor(Sensor.TYPE_GRAVITY))
                sNames.add(((Sensor) sList.get(i)).getType()==Sensor.TYPE_ACCELEROMETER?"Accelerometer":((Sensor) sList.get(i)).getType()==Sensor.TYPE_MAGNETIC_FIELD?"Magnetometer":((Sensor) sList.get(i)).getType()==Sensor.TYPE_GYROSCOPE?"Gyroscope":((Sensor) sList.get(i)).getType()==Sensor.TYPE_LIGHT?"Light Sensor":((Sensor) sList.get(i)).getType()==Sensor.TYPE_PRESSURE?"Barometer":((Sensor) sList.get(i)).getType()==Sensor.TYPE_PROXIMITY?"Proximity Sensor":((Sensor) sList.get(i)).getType()==Sensor.TYPE_AMBIENT_TEMPERATURE?"Thermometer":((Sensor) sList.get(i)).getType()==Sensor.TYPE_ROTATION_VECTOR?"Rotation Sensor":((Sensor) sList.get(i)).getType()==Sensor.TYPE_STEP_COUNTER?"Pedometer":((Sensor) sList.get(i)).getType()==Sensor.TYPE_GRAVITY?"Gravity Sensor":"");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice, sNames );
        // setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice, sNames ));
        list.setAdapter(adapter);
        // getListView().setTextFilterEnabled(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> parent , View view , int position , long id)
            {
                String selectedItem = ((TextView) view).getText().toString();

                if(checked.contains(selectedItem))
                {
                    checked.remove(selectedItem);
                }
                else
                    checked.add(selectedItem);
            }

        } );
        listener();
        Call<Account> call = service.get(Long.parseLong(id));
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> account) {
                myaccount=account.body();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {}
        });
        return rootView;


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor , int accuracy)
    {

    }
    @Override
    public final void onSensorChanged(SensorEvent event)
    {
       /* valueLight = event.values[0];
        valueThermometer = event.values[0];
        valueStepCounter=event.values[0];
*/

        int sensorType = event.sensor.getType();


        if (sensorType == Sensor.TYPE_LIGHT)
        {
            valueLight= event.values[0];
        }

        if (sensorType == Sensor.TYPE_STEP_COUNTER)
        {
            valueStepCounter= event.values[0];
        }
        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            valueThermometer= event.values[0];
        }
        if(sensorType == Sensor.TYPE_ACCELEROMETER)
        {
            valueXaccelerometer = event.values[0];
            valueYaccelerometer = event.values[1];
            valueZaccelerometer = event.values[2];
        }
        if(sensorType == Sensor.TYPE_MAGNETIC_FIELD)
        {
            valueXmagnetometer = event.values[0];
            valueYMagnetometer = event.values[1];
            valueZmagnetometer = event.values[2];
        }
        if(sensorType == Sensor.TYPE_GYROSCOPE)
        {
            valueXgyroscope = event.values[0];
            valueYgyroscope = event.values[1];
            valueZgyroscope = event.values[2];
        }
        if (sensorType == Sensor.TYPE_PROXIMITY)
        {
            valueProximity= event.values[0];
        }
        if(sensorType == Sensor.TYPE_GRAVITY)
        {
            valueXgravity = event.values[0];
            valueYgravity = event.values[1];
            valueZgravity = event.values[2];
        }

        if(sensorType == Sensor.TYPE_ROTATION_VECTOR)
        {
            valueXrotation = event.values[0];
            valueYrotation = event.values[1];
            valueZgravity = event.values[2];
        }

        if (sensorType == Sensor.TYPE_PRESSURE)
        {
            valueBarometer= event.values[0];
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        sManager.registerListener(this,mlight, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mTemperature,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mPedometer,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mProximity,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mGravity,SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mRotation, SensorManager.SENSOR_DELAY_NORMAL );
        sManager.registerListener(this,mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener(this,mBarometer , SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        sManager.unregisterListener(this);
    }

    public void listener ()
    {


        send.setOnClickListener(new  View.OnClickListener() {
                                    @Override

                                    public void onClick (View v)
                                    {
                                        if(checked.size()!=0) {

                                            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                                    != PackageManager.PERMISSION_GRANTED) {

                                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

                                            } else {
                                                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                                if (location != null){
                                                    double latti = location.getLatitude();
                                                    double longi = location.getLongitude();
                                                    Retrofit retrofit = new Retrofit.Builder()
                                                            .baseUrl("https://databaserest.herokuapp.com/")
                                                            .addConverterFactory(GsonConverterFactory.create())
                                                            .build();
                                                    sensorBatch sensorBatch = new sensorBatch(myaccount,latti,longi);
                                                    final sensorBatchservice batchservice = retrofit.create(sensorBatchservice.class);
                                                    Call<sensorBatch> createbatchCall = batchservice.create(sensorBatch);

                                                    createbatchCall.enqueue(new Callback<sensorBatch>() {
                                                        @Override
                                                        public void onResponse(Call<sensorBatch> _, Response<sensorBatch> response) {

                                                            sensorbatchsucces(response.body());
                                                        }

                                                        @Override
                                                        public void onFailure(Call<sensorBatch> _, Throwable t) {

                                                            t.printStackTrace();
                                                        }
                                                    });
                                                    //  ((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
                                                } else {

                                                }
                                            }



                                        }

               /* send.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                Toast.makeText(getContext() , " Sending Data" , Toast.LENGTH_SHORT).show();


                new Thread(new Runnable() {
                    public void run() {
                        while (progressStatus < 100) {
                            progressStatus += 1;
                            // Update the progress bar and display the
                            //current value in the text view
                            handler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressStatus);
                                   // textView.setText(progressStatus+"/"+progressBar.getMax());
                                }
                            });
                            try {
                                // Sleep for 200 milliseconds.
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

*/
                                    }
                                }

        );
    }
    public void getcoins(int size, sensorBatch batch)
    {
        if(Count==size)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://databaserest.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final sensorBatchservice Batchservice = retrofit.create(sensorBatchservice.class);
            Call<sensorBatch> createCall = Batchservice.getcoins(batch.id);

            createCall.enqueue(new Callback<sensorBatch>() {
                @Override
                public void onResponse(Call<sensorBatch> _, Response<sensorBatch> response) {

                    Toast.makeText(getContext(),"You received "+response.body().coins, Toast.LENGTH_LONG ).show();
                }

                @Override
                public void onFailure(Call<sensorBatch> _, Throwable t) {

                    t.printStackTrace();
                }
            });
            Count=0;
        }
    }
    public void sensorbatchsucces(final sensorBatch batch)
    {String single = "single";
        String environment = "environment";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://databaserest.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String itemcheck ;

        for(int i = 0 ; i<checked.size() ; i++)
        {
            if(checked.get(i) == "Accelerometer")
            {
                value="x: "+valueXaccelerometer+", y: "+valueYaccelerometer+", z: "+valueZaccelerometer;
                Accelerometer accelerometer = new Accelerometer(value,batch,single);
                final Accelerometerservice Aservice = retrofit.create(Accelerometerservice.class);
                Call<Accelerometer> createCall = Aservice.create(accelerometer);

                createCall.enqueue(new Callback<Accelerometer>() {
                    @Override
                    public void onResponse(Call<Accelerometer> _, Response<Accelerometer> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Accelerometer> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }

            if(checked.get(i) == "Magnetometer")
            {
                float valuex = valueXmagnetometer;
                float valuey =valueYMagnetometer;
                float valuez = valueZmagnetometer;
                value="x: "+valueXmagnetometer+", y: "+valueYMagnetometer+", z: "+valueZmagnetometer;
                Magnetometer magnetometer = new Magnetometer(value,batch,environment);
                final Magnetometerservice Mservice = retrofit.create(Magnetometerservice.class);
                Call<Magnetometer> createCall = Mservice.create(magnetometer);

                createCall.enqueue(new Callback<Magnetometer>() {
                    @Override
                    public void onResponse(Call<Magnetometer> _, Response<Magnetometer> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Magnetometer> _, Throwable t) {

                        t.printStackTrace();
                    }
                });

            }
            if(checked.get(i) == "Gyroscope")
            {
                value="x: "+valueXgyroscope+", y: "+valueYgyroscope+", z: "+valueZgyroscope;
                Gyroscope gyroscope = new Gyroscope(value,batch,single);
                final Gyroscopeservice Aservice = retrofit.create(Gyroscopeservice.class);
                Call<Gyroscope> createCall = Aservice.create(gyroscope);

                createCall.enqueue(new Callback<Gyroscope>() {
                    @Override
                    public void onResponse(Call<Gyroscope> _, Response<Gyroscope> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Gyroscope> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }

            if(checked.get(i) == "Proximity Sensor")
            {
                value=String.valueOf(valueProximity);
                Proximity proximity = new Proximity(value,batch,single);
                final Proximityservice Aservice = retrofit.create(Proximityservice.class);
                Call<Proximity> createCall = Aservice.create(proximity);

                createCall.enqueue(new Callback<Proximity>() {
                    @Override
                    public void onResponse(Call<Proximity> _, Response<Proximity> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Proximity> _, Throwable t) {

                        t.printStackTrace();
                    }
                });

            }
            if(checked.get(i) == "Light Sensor")
            {
                value=String.valueOf(valueLight);
                Light light = new Light(value,batch,environment);
                final Lightservice Aservice = retrofit.create(Lightservice.class);
                Call<Light> createCall = Aservice.create(light);

                createCall.enqueue(new Callback<Light>() {
                    @Override
                    public void onResponse(Call<Light> _, Response<Light> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Light> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }

            if(checked.get(i) == "Gravity Sensor")
            {
                value="x: "+valueXgravity+", y: "+valueYgravity+", z: "+valueZgravity;
                Gravity gravity = new Gravity(value,batch,single);
                final Gravityservice Aservice = retrofit.create(Gravityservice.class);
                Call<Gravity> createCall = Aservice.create(gravity);

                createCall.enqueue(new Callback<Gravity>() {
                    @Override
                    public void onResponse(Call<Gravity> _, Response<Gravity> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Gravity> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }

            if(checked.get(i) == "Rotation Sensor")
            {
                value="x: "+valueXrotation+", y: "+valueYrotation+", z: "+valueZrotation;
                Rotation rotation = new Rotation(value,batch,single);
                final Rotationservice Aservice = retrofit.create(Rotationservice.class);
                Call<Rotation> createCall = Aservice.create(rotation);

                createCall.enqueue(new Callback<Rotation>() {
                    @Override
                    public void onResponse(Call<Rotation> _, Response<Rotation> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Rotation> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }
            if(checked.get(i) == "Pedometer")
            {
                value=String.valueOf(valueStepCounter);
                Pedometer pedometer = new Pedometer(value,batch,single);
                final Pedometerservice Aservice = retrofit.create(Pedometerservice.class);
                Call<Pedometer> createCall = Aservice.create(pedometer);

                createCall.enqueue(new Callback<Pedometer>() {
                    @Override
                    public void onResponse(Call<Pedometer> _, Response<Pedometer> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Pedometer> _, Throwable t) {

                        t.printStackTrace();
                    }
                });

            }
            if(checked.get(i) == "Barometer")
            {
                value=String.valueOf(valueBarometer);
                Barometer barometer = new Barometer(value,batch,environment);
                final Barometerservice Aservice = retrofit.create(Barometerservice.class);
                Call<Barometer> createCall = Aservice.create(barometer);

                createCall.enqueue(new Callback<Barometer>() {
                    @Override
                    public void onResponse(Call<Barometer> _, Response<Barometer> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Barometer> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }

            if(checked.get(i) == "Thermometer")
            {
                value=String.valueOf(valueThermometer);
                Thermometer thermometer = new Thermometer(value,batch,environment);
                final Thermometerservice Aservice = retrofit.create(Thermometerservice.class);
                Call<Thermometer> createCall = Aservice.create(thermometer);

                createCall.enqueue(new Callback<Thermometer>() {
                    @Override
                    public void onResponse(Call<Thermometer> _, Response<Thermometer> response) {
                        Count++;
                        getcoins(checked.size(), batch);
                    }

                    @Override
                    public void onFailure(Call<Thermometer> _, Throwable t) {

                        t.printStackTrace();
                    }
                });
            }
        }
    }

}

