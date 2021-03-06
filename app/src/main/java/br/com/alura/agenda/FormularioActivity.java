package br.com.alura.agenda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        if(aluno != null){
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Metodo para tirar foto do contato
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg"; // Passa uma String com o caminho que a foto deve ser armazenada |  concatena o caminho com o nome do arquivo que é gerado pelo metodo System.currentTimeMillis()
                File arquivoFoto = new File(caminhoFoto); // Revisar estre trecho, pois apartir da API 24 o codigo mostrado na aula causa FATAL ERROR ao abrir a camera.
                Uri fotoURI = FileProvider.getUriForFile(FormularioActivity.this, BuildConfig.APPLICATION_ID + ".provider", arquivoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                if (intentCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intentCamera, CODIGO_CAMERA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CODIGO_CAMERA) {
                helper.carregaImagem(caminhoFoto);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
            Aluno aluno = helper.pegaAluno();
            AlunoDAO dao = new AlunoDAO(this);


                if (aluno.getId() != null)  {
                    dao.altera(aluno);
                } else {
                    dao.insere(aluno);
                }

                dao.close();

//                new InsereAlunoTask(aluno).execute();
                Call call = new RetrofitInicializador().getAlunoService().insere(aluno);

                // .enqueue faz uma requsição assincrona, evitando que o app trave na tela caso haja problemas na requisição
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.i("onResponse", "Requisição com sucesso");
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.i("onFailure", "Requisição falhou");

                    }
                });
                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() +  " salvo", Toast.LENGTH_SHORT).show();

            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
