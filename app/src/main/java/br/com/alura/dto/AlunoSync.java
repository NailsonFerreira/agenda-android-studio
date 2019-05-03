package br.com.alura.dto;

import java.util.List;

import br.com.alura.agenda.modelo.Aluno;

public class AlunoSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public String getMomentoDaUltimaModificacao(){
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos(){
        return alunos;
    }
}
