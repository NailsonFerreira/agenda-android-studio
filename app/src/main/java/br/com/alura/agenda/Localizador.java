package br.com.alura.agenda;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
/*Classe responsavel por instanciar a localização atual do usuario*/
public class Localizador implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private final GoogleApiClient client;
    private final GoogleMap mapa;

    public Localizador(Context context, GoogleMap mapa){

        client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this) //Depende da interface LocationListener
                .build();

        client.connect();
        this.mapa = mapa;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setSmallestDisplacement(50); //Atualiza localização a cada 50 metros
        request.setInterval(1000); //atualiza a cada 1000 milisegundos (1 segundo)
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Alta precisão

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this); //flag. Precisa de permissão, mas ainda compila. porem é preciso autorizar manualmente no android
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override //Faz atualização da tela e centraliza o mapa mediante a localização do GPS
    public void onLocationChanged(Location location) {
        LatLng coordenada = new LatLng(location.getLatitude(), location.getLongitude()); //converte a coordenada entre as APIs
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(coordenada); //recebe uma coordenada atual metiante os requisitos pedidos no metodo onConnected e move a camera
        mapa.moveCamera(cameraUpdate);
    }
}
