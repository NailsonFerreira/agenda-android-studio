package br.com.alura.agenda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import br.com.alura.agenda.modelo.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        List<String> topPortugues = Arrays.asList("Adverbio", "Sujeito", "Figuras de Linguagem");
        Prova provaPortugues = new Prova("Portugues", "20/04/19", topPortugues);

        List<String> topMatematica = Arrays.asList("Equação 1", "Algebra booleana", "Algebra linear");
        Prova provaMatematica = new Prova("Matematica", "22/04/19", topMatematica);

        List<Prova> provas = Arrays.asList(provaMatematica, provaPortugues);

        ArrayAdapter<Prova> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, provas);

        ListView lista = (ListView) findViewById(R.id.provas_lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = (Prova) parent.getItemAtPosition(position);
                Toast.makeText(ProvasActivity.this, "Clicou na prova de "+ prova, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
