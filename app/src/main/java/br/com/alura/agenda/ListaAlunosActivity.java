package br.com.alura.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import br.com.alura.agenda.adapter.AlunosAdapter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.event.AtualizaListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import br.com.alura.agenda.dto.AlunoSync;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        swipe = findViewById(R.id.swipe_lista_alunos);
        
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                buscaAlunos();
            }
        });

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno)  listaAlunos.getItemAtPosition(position);
            Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
            intentVaiProFormulario.putExtra("aluno", aluno);
            startActivity(intentVaiProFormulario);
            }
        });

        Button novoAluno = (Button) findViewById (R.id.novo_aluno);

        //Metodo para chamar FormularioActivity ao clicar em um item da lista
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)  {
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
        buscaAlunos();
    }

    @Override// Menu webservice
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_enviar_notas:
                new EnviarAlunosTask(this).execute();
            break;
            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;
            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaMapa);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        for(Aluno aluno: alunos){
            Log.i("id do aluno", String.valueOf(aluno.getId()));
        }

        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void atualizaListaAlunoEvent(AtualizaListaAlunoEvent event){
        carregaLista();
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregaLista();

    }

    private void buscaAlunos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();

        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.sincroniza(alunoSync.getAlunos());
                dao.close();
                carregaLista();
                swipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("on Failure chamado", t.getMessage());
                swipe.setRefreshing(false);

            }
        });
    }

    //menus de clique longo
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        //abre menu de opcoes no item selecionado
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        //objeto aluno recebe um final pois sendo uma variavel causaria conflito no mesmo objeto aluno em dao.deletar(aluno)
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        //Intent para configurar opção ligar para contato
        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){//Checa se a permissão de foi aceita pelo usuario
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        //Intent para configurar envio de SMS
        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);

        //Intent para configurar pesquisa de localização pelo google maps
        MenuItem itemMapa = menu.add("Ver no Mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        //Intent para configurar acesso ao site pelo noavegador padrão do aparelho
        final MenuItem itemSite = menu.add("Visitar Site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        String site = aluno.getSite();
        if (!site.startsWith("http://")){
            site = "http://" + site;
        }
        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);


        //Intent para configurar opção de deletar aluno da agenda
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                        dao.deleta(aluno);
                        dao.close();
                        carregaLista();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ListaAlunosActivity.this, "Não foi possivel deletar",Toast.LENGTH_SHORT);
                    }
                });




                return false;
            }
        });
    }


}
