package com.petanikode.cobagooglemaps;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

// library untuk penguraian JSON
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Tag untuk debugging
    private String TAG = MapsActivity.class.getSimpleName();

    // URL Endpoint API
    private static String url = "http://codesimpang.co.id/json_get_data_bengkel.php";

    // Array untuk menyimpan data API
    public ArrayList<HashMap<String, String>> daftarLokasi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // jalankan request untuk ngambil lokasi
        new GetLokasi().execute();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        // Add a marker in Sydney and move the camera
//        for(int i=0; i < 10; i++) {
//            LatLng sydney = new LatLng(-34+i, 151+i);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        }




    }


    // Class untuk mengambil data lokasi dari API Endpoin
    private class GetLokasi extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // tampilakn dialog progress

        }

        @Override
        protected Void doInBackground(Void[] arg0){
            HttpHandler sh = new HttpHandler();

            // ambil respon dari URL
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Respon dari URL: " + jsonStr);

            if(jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray lokasi = jsonObject.getJSONArray("Server_Bengkel");

                    // Looping semua lokasi
                    for(int i=0; i < lokasi.length(); i++){
                        JSONObject bengkel = lokasi.getJSONObject(i);

                        // buat nampung sementara datanya
                        String id = bengkel.getString("IdBengkel");
                        String nama = bengkel.getString("NamaBengkel");
                        String alamat = bengkel.getString("AlamatBengkel");
                        String telpBengkel = bengkel.getString("TelpBengkel");
                        String longitude = bengkel.getString("Longitude");
                        String latitude = bengkel.getString("Latitude");

                        // tmp has for single location
                        HashMap<String, String> lokasiBengkel = new HashMap<>();

                        // adding ecaht child node to HashMap key => value
                        lokasiBengkel.put("IdBengkel", id);
                        lokasiBengkel.put("NamaBengkel", nama);
                        lokasiBengkel.put("AlamatBengkel", alamat);
                        lokasiBengkel.put("TelpBengkel", telpBengkel);
                        lokasiBengkel.put("Longitude", longitude);
                        lokasiBengkel.put("Latitude", latitude);

                        daftarLokasi.add(lokasiBengkel);

                    }



                } catch (final JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Tidak bisa mengambil JSON dari Server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tidak Bisa mengambil JSON, cek Log! ", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);


            for(int i = 0; i < daftarLokasi.size(); i++){
                Log.e(TAG, "Benkel:" + daftarLokasi.get(i).get("NamaBengkel") );
                Log.e(TAG, "Latlang:" + daftarLokasi.get(i).get("Latitude") + "," + daftarLokasi.get(i).get("Latitude")  );

                LatLng bengkel = new LatLng(
                        Double.valueOf(daftarLokasi.get(i).get("Latitude")),
                        Double.valueOf(daftarLokasi.get(i).get("Longitude"))
                );

                //LatLng sydney = new LatLng(latlang.longitude, latlang.latitude);
                mMap.addMarker(new MarkerOptions()
                        .position(bengkel)
                        .title(daftarLokasi.get(i).get("NamaBengkel")));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }


        }
    }
}
