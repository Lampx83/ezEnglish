package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.jquiz.project2.R;

import org.json.JSONException;

import java.util.List;

import database.DataBaseHandler;
import database.TranslateHandler;
import entity.Translate;
import others.MyPoint;

public class MapsActivity extends ParentActivity implements ClusterManager.OnClusterClickListener<MyPoint>, ClusterManager.OnClusterInfoWindowClickListener<MyPoint>, ClusterManager.OnClusterItemClickListener<MyPoint>, ClusterManager.OnClusterItemInfoWindowClickListener<MyPoint>, OnMapReadyCallback {
    private ClusterManager<MyPoint> mClusterManager;
    private Translate translate;
    private TranslateHandler mTranslateHandler;
    private long translateid = -1;
    private boolean shouldCluster = false;
    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private boolean isFirst = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

        translateid = getIntent().getLongExtra("translateid", -1);
        mTranslateHandler = new TranslateHandler(this);
        if (translateid != -1) {
            //View one word
            translate = mTranslateHandler.getByID(translateid);

        }
        getSupportActionBar().setCustomView(R.layout.sw);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        SwitchCompat switchForActionBar = (SwitchCompat) getSupportActionBar().getCustomView().findViewById(R.id.switchForActionBar);
        switchForActionBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    shouldCluster = true;
                else
                    shouldCluster = false;
                isFirst = false;
                mClusterManager.setRenderer(new MyItemRenderer());

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMapIfNeeded();
    }

    private class MyItemRenderer extends DefaultClusterRenderer<MyPoint> {
        public MyItemRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyPoint item, MarkerOptions markerOptions) {
            markerOptions.title(item.result);
            markerOptions.snippet(item.text);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.alpha(0.7f);
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MyPoint> cluster) {
            if (isFirst)
                return super.shouldRenderAsCluster(cluster);
            else
                return shouldCluster;
        }
    }


    private void readItems() throws JSONException {
        List<MyPoint> list = mTranslateHandler.getAllByforMap("where " + DataBaseHandler.TRANSLATEID + "!=" + translateid, "");
        mClusterManager.addItems(list);
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyPoint> cluster) {

    }

    @Override
    public boolean onClusterClick(Cluster<MyPoint> cluster) {
        String s = getResources().getString(R.string.items) + ":";
        int i = 0;
        for (MyPoint myPoint : cluster.getItems()) {
            if (i++ > 6) {
                s = s + "\r\n ...";
                break;
            }
            s = s + "\r\n - " + myPoint.text;
        }

        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onClusterItemClick(MyPoint myPoint) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyPoint myPoint) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            fragment.getMapAsync(this);
        }else {

            mMap.getUiSettings().setMapToolbarEnabled(false);
            if (mMap != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mClusterManager = new ClusterManager<MyPoint>(this, getMap());
                mClusterManager.setOnClusterClickListener(this);

                mClusterManager.setRenderer(new MyItemRenderer());
                getMap().setOnCameraChangeListener(mClusterManager);
                mMap.setOnMarkerClickListener(mClusterManager);


                try {
                    readItems();
                } catch (JSONException e) {
                    Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
                }

                if (translate != null) {
                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(translate.latitude, translate.longitude), 16));
                    LatLng mark = new LatLng(translate.latitude, translate.longitude);
                    Marker word = getMap().addMarker(new MarkerOptions().position(mark).title(translate.text).snippet(translate.result));
                    word.showInfoWindow();
                } else {
                    if (MainActivity.latitude == 0) {
                        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.626679, 121.028153), 7.7f));
                    } else
                        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.latitude, MainActivity.longitude), 16));
                }
            }
        }

    }

    protected GoogleMap getMap() {

        return mMap;
    }
}