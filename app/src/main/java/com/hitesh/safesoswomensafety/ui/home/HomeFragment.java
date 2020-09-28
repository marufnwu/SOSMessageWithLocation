package com.hitesh.safesoswomensafety.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hitesh.safesoswomensafety.Models.Contact;
import com.hitesh.safesoswomensafety.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class HomeFragment extends Fragment implements LocationListener {

    private HomeViewModel homeViewModel;
    private LocationManager locationManager;
    int PERMISSION_ALL = 1001;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Paper.init(getContext());


        final String msg = Paper.book().read("message", "");
        final List<Contact> contactList = Paper.book().read("contact", new ArrayList<Contact>());

        ImageView imgLocation = root.findViewById(R.id.imgLocation);
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }else {
            locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, (LocationListener) this);

        }

        imgLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!hasPermissions(getContext(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                   if (contactList.size()>0){
                       getLoacation(msg, contactList);
                   }else {
                       Toast.makeText(getContext(), "You didn't add nay contact", Toast.LENGTH_SHORT).show();
                   }
                }

                return false;
            }
        });


        return root;
    }

    private void getLoacation(String msg, List<Contact> contactList) {
       /* Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();*/


        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
         //location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if (bestLocation!=null){
            double lat = bestLocation.getLatitude();
            double lon = bestLocation.getLongitude();



            msg = msg+"\nLocation: "+"http://maps.google.com/?q="+lat+","+lon;
            sendSMS(contactList, msg);
        }else {
            Toast.makeText(getContext(), "Location Null", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //getCompleteAddress(lat, lon);
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getContext(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(List<Contact> contacts, String msg)
    {
       for (Contact contact : contacts){
           try{
               SmsManager smsMgrVar = SmsManager.getDefault();
               smsMgrVar.sendTextMessage(contact.number, null, msg, null, null);
               showCustomDialog();
           }
           catch (Exception ErrVar) {
               Toast.makeText(getContext(),ErrVar.getMessage().toString(),
                       Toast.LENGTH_LONG).show();
               ErrVar.printStackTrace();
           }
           finally {
                //showCustomDialog();
           }
       }
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.success_dialog, viewGroup, false);

        Button btnOk = dialogView.findViewById(R.id.buttonOk);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


}