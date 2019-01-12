package com.hexadecimal.mapandlocationdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    // istenilen iznin cevabını almak icin bu metodu kullaniyoruz
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){      // istedigimiz iznin requestCode'unu girdik

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){   // eger bir cevap geldiyse ve gelen cevap olumluysa yapilacak

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10, locationListener);
                    // kullanicinin konumunu belirli saniye araliklariyla veya belirli miktarda hareket ettiyse hesapla gibi secenekler
                    // icin GPS_PROVIDER'dan sonra saniye ve uzaklik icin degerler girdik, su an 0 dakika ve 0 metre
                    // sorgulari bu sekilde optimize etmek pil kullanimini azaltabilir
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);                                       // arkaplanda haritayi olusturan kisim
        mapFragment.getMapAsync(this);

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

        // kullanicinin lokasyon bilgisine erismek icin kullandik
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // kullanicinin lokasyonu degistiginde neler yapilacagini belirtmek icin kullandik
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // her konum degistiginde yeni marker konulacagi icin eskilerini siliyoruz
                mMap.clear();

                // Verilen noktaya marker yerlestirir
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // harita uzerindeki marker'ın rengini degistirmek icin asadaki kodu yazdik,
                // istenirse custom icon da yerlestirilebilir, uber > araba icon gibi...
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // uygulama acildiginda kameranın zoom yapma miktarini verdik, 1 en az 20 en cok
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 5));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        // eger kullanici daha dusuk bir versiyon kullaniyorsa
        if(Build.VERSION.SDK_INT < 24){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0 ,locationListener);
        } else  {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // eger izin vermediyse yapilacaklar

                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION } , 1);
                // burada uygulamanin konuma erisim izni olmadigi icin tekrar konuma ulasma izni istedik,
                // string[]{} icinde birden fazla gerekli izin varsa onlari yaziyoruz,
                // sona yazdigimiz degerle iznimizi takip edebiliriz

            } else  {
                // tum izinler alinmissa konumun alinacagi yer burası
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,10 ,locationListener);

                // kullanicinin son bulundugu konumu alip oraya marker yerlestiriyoruz
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // her konum degistiginde yeni marker konulacagi icin eskilerini siliyoruz
                mMap.clear();

                // Verilen noktaya marker yerlestirir
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                // harita uzerindeki marker'ın rengini degistirmek icin asadaki kodu yazdik,
                // istenirse custom icon da yerlestirilebilir, uber > araba icon gibi...
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // uygulama acildiginda kameranın zoom yapma miktarini verdik, 1 en az 20 en cok
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
            }
        }
    }
}
