package br.com.alura.agenda.task;

import android.os.AsyncTask;

import br.com.alura.agenda.WebClient;
import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.modelo.Aluno;

public class InsereAlunoTask extends AsyncTask{

    private final Aluno aluno;
    public InsereAlunoTask (Aluno aluno){
        this.aluno = aluno;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String json = new AlunoConverter().converteParaJSONCompleto(aluno);
        new WebClient().insere(json);
        return null;
    }
}
