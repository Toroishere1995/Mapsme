package com.example.mapsme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.search.SearchAuthApi.GoogleNowAuthResult;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		OnConnectionFailedListener, ConnectionCallbacks, LocationListener {
	GoogleMap gmp;
	double latSe = 28.364702;
	double logSe = 79.411985;
	final static float zoom = 15;
	EditText eLoc;
	Button b;
	GoogleApiClient mGoogleApiClient;
	Location mLastLocation;
	Marker marker;
	Circle shap;
	Marker marker1, marker2;
	ArrayList<Marker> markme = new ArrayList<Marker>();
	int length, flag = 0, k = 0;
	long time, stop, diff = 0;
	Polyline line;
	Polygon shape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API).build();
		}
		if (serviceOk()) {
			setContentView(R.layout.activity_map);
			if (init()) {
				Toast.makeText(this, "Yup it's Working", Toast.LENGTH_SHORT);
				// gotoLocation(latSe, logSe, zoom);
				// gmp.setMyLocationEnabled(true);

				eLoc = (EditText) findViewById(R.id.editLoc);
				b = (Button) findViewById(R.id.go);
			} else {
				Toast.makeText(this, "Map aint loding", Toast.LENGTH_SHORT);
			}

		} else {
			setContentView(R.layout.activity_main);
		}
	}

	private void gotoLocation(double lat, double log, float z) {
		// TODO Auto-generated method stub
		LatLng ll = new LatLng(lat, log);
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ll, z);
		gmp.moveCamera(cu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.none) {
			gmp.setMapType(GoogleMap.MAP_TYPE_NONE);
		} else if (id == R.id.satellite) {
			gmp.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else if (id == R.id.hybrid) {
			gmp.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		} else if (id == R.id.terrain) {
			gmp.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		} else if (id == R.id.norm) {
			gmp.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		} else if (id == R.id.conloc) {
			mLastLocation = LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
			if (mLastLocation != null) {
				double latti = mLastLocation.getLatitude();
				double longi = mLastLocation.getLongitude();
				LatLng ll = new LatLng(latti, longi);
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ll, 15);
				gmp.animateCamera(cu);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean serviceOk() {
		int isAvail = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvail == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvail)) {
			Dialog d = GooglePlayServicesUtil.getErrorDialog(isAvail, this,
					9001);
			d.show();
		} else {
			Toast.makeText(this, "Sorry", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean init() {
		if (gmp == null) {
			SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mapV);
			gmp = smf.getMap();
			gmp.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng arg0) {
					// TODO Auto-generated method stub
					Geocoder gc = new Geocoder(MainActivity.this);
					List<Address> la = null;
					try {
						la = gc.getFromLocation(arg0.latitude, arg0.longitude,
								1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Address add = la.get(0);
					MainActivity.this.setMarker(arg0.latitude, arg0.longitude,
							add.getLocality(), add.getCountryName());
				}
			});
			gmp.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker mark) {
					// TODO Auto-generated method stub
					String str = mark.getPosition().latitude + " , "
							+ mark.getPosition().longitude;
					Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT)
							.show();
					return false;
				}
			});
			gmp.setOnMarkerDragListener(new OnMarkerDragListener() {

				@Override
				public void onMarkerDragStart(Marker arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkerDragEnd(Marker arg0) {
					// TODO Auto-generated method stub
					Geocoder gc = new Geocoder(MainActivity.this);
					List<Address> la = null;
					try {
						la = gc.getFromLocation(arg0.getPosition().latitude,
								arg0.getPosition().longitude, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Address add = la.get(0);
					arg0.setTitle(add.getLocality());
					arg0.setSnippet(add.getCountryName());
					arg0.showInfoWindow();

				}

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub

				}
			});
			if (gmp != null) {
				gmp.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public View getInfoContents(Marker am) {
						// TODO Auto-generated method stub
						View v = getLayoutInflater().inflate(R.layout.icon_go,
								null);

						TextView local = (TextView) v.findViewById(R.id.local);
						TextView latde = (TextView) v.findViewById(R.id.latt);

						TextView londe = (TextView) v.findViewById(R.id.longi);
						TextView coun = (TextView) v.findViewById(R.id.coun);
						String ctr = am.getTitle();
						LatLng llo = am.getPosition();
						local.setText(ctr);
						latde.setText("Lattitude" + llo.latitude);
						londe.setText("Longitude" + llo.longitude);
						coun.setText(am.getSnippet());
						return v;
					}
				});
			}
		}
		return (gmp != null);
	}

	public void goLocation(View v) {
		String str = eLoc.getText().toString();
		if (str == null) {
			Toast.makeText(this, "Enter Location to be accesed",
					Toast.LENGTH_SHORT).show();
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		Geocoder gc = new Geocoder(this);

		try {
			List<Address> list = gc.getFromLocationName(str, 1);
			Address ad = list.get(0);
			String mtr = ad.getLocality();
			String country = ad.getCountryName();
			double latSu = ad.getLatitude();
			double longSu = ad.getLongitude();
			Toast.makeText(this, mtr, Toast.LENGTH_SHORT).show();
			gotoLocation(latSu, longSu, zoom);
			setMarker(latSu, longSu, mtr, country);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setMarker(double latSu, double longSu, String mtr,
			String country) {
		// TODO Auto-generated method stub
		/*
		 * 
		 * 
		 * if (markme.size() == 3 && flag == 0) { removeEach();
		 * 
		 * } if (markme.size() == 2 && flag == 1) { removeEverything(); }
		 */
		
		if (marker != null) {
			removeCircle();
		}
		MarkerOptions mo = new MarkerOptions();
		mo.title(mtr);
		LatLng llo = new LatLng(latSu, longSu);
		mo.position(llo);
		mo.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		mo.snippet(country);
		mo.draggable(true);
		marker = gmp.addMarker(mo);// Remove this For Line and PolyGon
		shap = draCircle(llo);
		/*
		 * if(marker1==null){ marker1=gmp.addMarker(mo); } else
		 * if(marker2==null){ marker2=gmp.addMarker(mo); drawLine(); } else {
		 * removeEverything(); marker1=gmp.addMarker(mo); }
		 * 
		 * markme.add(gmp.addMarker(mo)); if(k==0){ time=
		 * System.currentTimeMillis();
		 * 
		 * }else{ stop = System.currentTimeMillis(); diff=stop-time; }
		 * 
		 * k++;
		 * 
		 * if (markme.size() == 2 && diff < 2000) { drawLine();
		 * Toast.makeText(this, "" + diff, Toast.LENGTH_SHORT).show(); flag = 1;
		 * } if (markme.size() == 3) { drawSome(); Toast.makeText(this, "Sorry"
		 * + diff, Toast.LENGTH_SHORT).show(); }
		 */
	}

	private Circle draCircle(LatLng llo){
		CircleOptions co=new CircleOptions().center(llo).radius(1000).fillColor(Color.MAGENTA).strokeColor(Color.LTGRAY).strokeWidth(3);
		return gmp.addCircle(co);
	}

	private void removeCircle() {
		// TODO Auto-generated method stub
		marker.remove();
		marker = null;
		shap.remove();
		shap = null;
	}

	/*
	 * private void drawSome() { PolygonOptions pop = new PolygonOptions(); //
	 * length = markme.size(); pop.fillColor(Color.MAGENTA); pop.strokeWidth(5);
	 * pop.strokeColor(Color.BLUE); for (int i = 0; i < 3; i++) {
	 * pop.add(markme.get(i).getPosition()); } shape = gmp.addPolygon(pop); }
	 * 
	 * private void removeEach() { for (Marker mar : markme) { mar.remove(); }
	 * markme.clear(); shape.remove(); shape = null; }
	 * 
	 * private void drawLine() { // TODO Auto-generated method stub
	 * PolylineOptions po = new PolylineOptions(); //
	 * po.add(marker1.getPosition()); // po.add(marker2.getPosition());
	 * po.add(markme.get(0).getPosition()); po.add(markme.get(1).getPosition());
	 * po.color(Color.RED); line = gmp.addPolyline(po); k=0; }
	 * 
	 * private void removeEverything() { // TODO Auto-generated method stub
	 * 
	 * marker1.remove(); marker1=null; marker2.remove(); marker2=null;
	 * 
	 * for (Marker mar : markme) { mar.remove(); } markme.clear();
	 * line.remove();
	 * 
	 * 
	 * }
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mGoogleApiClient.disconnect();
		super.onStop();
		MapStateManager msm = new MapStateManager(this);
		msm.stateHandle(gmp);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MapStateManager msm = new MapStateManager(this);
		CameraPosition smp = msm.retrieve(gmp);
		if (smp != null) {
			CameraUpdate cup = CameraUpdateFactory.newCameraPosition(smp);
			gmp.moveCamera(cup);
		}
	}

	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Connection Set", Toast.LENGTH_SHORT).show();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.setFastestInterval(1000);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, request, this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		String str = "Location:" + arg0.getLatitude() + ","
				+ arg0.getLongitude();
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}
