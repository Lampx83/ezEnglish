package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
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

import java.util.Date;
import java.util.List;

import controlvariable.MyGlobal;
import database.DataBaseHandler;
import database.TranslateHandler;
import entity.Translate;
import others.MyPoint;

public class MoreInfoActivity extends ParentActivity implements ClusterManager.OnClusterClickListener<MyPoint>, ClusterManager.OnClusterInfoWindowClickListener<MyPoint>, ClusterManager.OnClusterItemClickListener<MyPoint>, ClusterManager.OnClusterItemInfoWindowClickListener<MyPoint>, OnMapReadyCallback {
    private ClusterManager<MyPoint> mClusterManager;
    private Translate translate;
    private TranslateHandler mTranslateHandler;
    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private long translateid = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moreinfo);
        translateid = getIntent().getLongExtra("translateid", -1);
        setTitle(getResources().getString(R.string.more_info));
        mTranslateHandler = new TranslateHandler(this);
        fragment = new SupportMapFragment();
        translate = mTranslateHandler.getByID(translateid);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        ((TextView) findViewById(R.id.tvTopic)).setText(Html.fromHtml("<b>" + getString(R.string.topic) + "</b>: " + translate.learn_context));
        ((TextView) findViewById(R.id.tvListen)).setText(Html.fromHtml("<b>" + getString(R.string.listened) + "</b>: " + translate.tts + " " + getString(R.string.times)));

        String practice = translate.practice_times + " " + getString(R.string.times);
        if (translate.score > 0)
            practice = practice + " (" + getString(R.string.highest_score) + ": " + translate.score + ")";


        ((TextView) findViewById(R.id.tvPractice)).setText(Html.fromHtml("<b>" + getString(R.string.practiced) + " </b>: " + practice));
        Date d1 = new Date();
        d1.setTime(translate.time * 1000);
        String time_ago = (String) DateUtils.getRelativeTimeSpanString(d1.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
        String create_by = getString(R.string.you);
        if (!translate.user_id.equals(MyGlobal.user_id))
            create_by = translate.user_name;
        ((TextView) findViewById(R.id.tvCreateBy)).setText(Html.fromHtml("<b>" + getString(R.string.created_by) + "</b>: " + create_by));
        ((TextView) findViewById(R.id.tvTime)).setText(Html.fromHtml("<b>" + getString(R.string.created_time) + "</b>: " + time_ago));
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
        if (translate.latitude != 0) {
            setUpMapIfNeeded();
        } else {
            findViewById(R.id.tvError).setVisibility(View.VISIBLE);
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        fragment.getMapAsync(this);
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
            getMap().setMyLocationEnabled(true);
            mClusterManager = new ClusterManager<MyPoint>(this, getMap());
            mClusterManager.setRenderer(new MyItemRenderer());
            getMap().setOnCameraChangeListener(mClusterManager);

            try {
                readItems();
            } catch (JSONException e) {
                Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
            }


            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(translate.latitude, translate.longitude), 16));
            LatLng mark = new LatLng(translate.latitude, translate.longitude);
            Marker word = getMap().addMarker(new MarkerOptions().position(mark).title(translate.text).snippet(translate.result));
            word.showInfoWindow();
        }
    }

    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }


}