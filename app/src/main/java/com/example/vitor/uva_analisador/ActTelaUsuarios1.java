package com.example.vitor.uva_analisador;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.database.sqlite.*;
import android.database.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vitor.uva_analisador.database.DataBase;
import com.example.vitor.uva_analisador.dominio.RepositorioPessoas;
import com.example.vitor.uva_analisador.dominio.entidades.Pessoa;

public class ActTelaUsuarios1 extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayAdapter<Pessoa> adpPessoas;
    private ListView lstPessoas;

    private DataBase dataBase;
    private SQLiteDatabase conn;
    private RepositorioPessoas repositorioPessoas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_tela_usuarios1);

        lstPessoas = (ListView)findViewById(R.id.lstPessoas);
        lstPessoas.setOnItemClickListener(this);

        try {
            dataBase = new DataBase(this);
            conn = dataBase.getWritableDatabase();

            repositorioPessoas = new RepositorioPessoas(conn);

            adpPessoas = repositorioPessoas.buscaPessoas(this);
            lstPessoas.setAdapter(adpPessoas);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new  AlertDialog.Builder(this);
            dlg.setMessage("Erro ao criar o banco: " + ex.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(R.mipmap.ic_launcher);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adpPessoas = repositorioPessoas.buscaPessoas(this);
        lstPessoas.setAdapter(adpPessoas);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Pessoa pessoa = adpPessoas.getItem(position);

        Intent it = new Intent(this, ActTelaRegistro.class);
        it.putExtra("PESSOA", pessoa);
        startActivityForResult(it, 0);

    }





}
