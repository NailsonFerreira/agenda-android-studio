package br.com.alura.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import br.com.alura.agenda.modelo.Prova;

public class ListaProvasFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_provas, container,false);

        List<String> topicosPort = Arrays.asList("Adverbio", "Sujeito", "Figuras de Linguagem");
        Prova provaPortugues = new Prova("Portugues", "20/04/19", topicosPort);

        List<String> topicosMat = Arrays.asList("Equação 1", "Algebra booleana", "Algebra linear");
        Prova provaMatematica = new Prova("Matematica", "22/04/19", topicosMat);

        List<Prova> provas = Arrays.asList(provaMatematica, provaPortugues);

        ArrayAdapter<Prova> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, provas);

        ListView lista = (ListView) view.findViewById(R.id.provas_lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = (Prova) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Clicou na prova de "+ prova, Toast.LENGTH_SHORT).show();
                Intent vaiParaDetalhes = new Intent(getContext(), DetalhesProvaActivity.class); //Instancia a activity DetalhesProvaActivity
                vaiParaDetalhes.putExtra("prova", prova);
                startActivity(vaiParaDetalhes); // Inicia a activity e vai para tela DetalhesProvaActivity

            }
        });

        return view;
    }
}
