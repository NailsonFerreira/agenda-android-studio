package br.com.alura.agenda;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;


//Classe responsavel por gerencia funçoes no mapa
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Metodo responsavel por responder se o mapa para ser iniciado. recebe o contexto da interface OnMapReadyCallback
        getMapAsync(this);
    }

    @Override //Prepara o mapa para ser instanciado
    public void onMapReady(GoogleMap googleMap) {

        //Recebe um geocoder e passa as coordenadas para que a camera seja iniciada na localização
        LatLng posicaoEscola = pegaCoordenadaDoEndereco("Praça Constantino Gomes");
        if(posicaoEscola != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicaoEscola, 15);
            googleMap.moveCamera(update);
        }

        //Cria um marcador que recebe os dados e localização do aluno
        AlunoDAO alunoDAO = new AlunoDAO(getContext());
        for(Aluno aluno : alunoDAO.buscaAlunos()){
            LatLng coordenada = pegaCoordenadaDoEndereco(aluno.getEndereco());
            if(coordenada != null){
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(aluno.getNome());
                marcador.snippet(String.valueOf(aluno.getNota()));
                googleMap.addMarker(marcador);
            }
        }
        alunoDAO.close();
        new Localizador(getContext(), googleMap);

    }

    //Recebe um endereço e retorna um geocoder
    private LatLng pegaCoordenadaDoEndereco(String endereco){
        try {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);
            if(!resultados.isEmpty()){
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


}
