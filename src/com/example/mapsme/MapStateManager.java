package com.example.mapsme;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;

public class MapStateManager {
SharedPreferences sp;
public MapStateManager(Context con){
	sp=con.getSharedPreferences("Sharing", con.MODE_PRIVATE);
}
public void stateHandle(GoogleMap GM){
	SharedPreferences.Editor ed=sp.edit();
	CameraPosition cp=GM.getCameraPosition();
	ed.putFloat("Lattitude", (float)cp.target.latitude);
	ed.putFloat("Longitude", (float)cp.target.longitude);
	ed.putFloat("ZOOM", cp.zoom);
	ed.putFloat("Bearing", cp.bearing);
	ed.putFloat("Titl", cp.tilt);
	ed.putInt("MapType", GM.getMapType());
	ed.commit();
	}
public CameraPosition retrieve(GoogleMap gf){
	CameraPosition cpo;
	
	double latt=sp.getFloat("Lattitude", 0);
	if(latt==0){
		return null;
	}
	double longi=sp.getFloat("Longitude", 0);
	float zoomg=sp.getFloat("ZOOM",0);
	float bear=sp.getFloat("Bearing",0);
	float tiltg=sp.getFloat("Titl",0);
	int type=sp.getInt("MapType", 0);
	gf.setMapType(type);
	LatLng ll = new LatLng(latt, longi);
	cpo=new CameraPosition(ll, zoomg, tiltg, bear);
	
	return cpo;
}

}
