package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class AgendaInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token Firebase", "Refreshed token: " + refreshedToken);

        enviaTokenParaServidor(refreshedToken);
    }

    private void enviaTokenParaServidor(String refreshedToken) {
    }
}
