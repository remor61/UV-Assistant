package com.example.vitor.uva_analisador.dominio;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.widget.*;

import com.example.vitor.uva_analisador.ActDados;
import com.example.vitor.uva_analisador.dominio.entidades.Pessoa;

/**
 * Created by vitor on 01/09/2017.
 */

public class RepositorioPessoas {

    private SQLiteDatabase conn;

    public RepositorioPessoas(SQLiteDatabase conn){
        this.conn = conn;
    }

    private ContentValues preencheContentValues(Pessoa pessoa){
        ContentValues values = new ContentValues();

        values.put("NOME", pessoa.getNome());
        values.put("IDADE", pessoa.getIdade());
        values.put("FPS", pessoa.getFps());
        values.put("TIPOPELE", pessoa.getTipoPele());

        return values;
    }


    public void inserir(Pessoa pessoa){

        ContentValues values = preencheContentValues(pessoa);
        conn.insertOrThrow("PESSOA", null, values);

    }

    public void alterar(Pessoa pessoa){

        ContentValues values = preencheContentValues(pessoa);
        conn.update("PESSOA", values, " _id = ? ", new String[]{ String.valueOf(pessoa.getId()) });

    }

    public void excluir(long id){
        conn.delete("PESSOA", " _id = ? ", new String[]{ String.valueOf(id) });
    }


    public ArrayAdapter<Pessoa> buscaPessoas(Context context){

        ArrayAdapter<Pessoa> adpPessoas = new ArrayAdapter<Pessoa>(context, android.R.layout.simple_list_item_1);

        Cursor cursor = conn.query("PESSOA", null, null, null, null, null, null);

        if (cursor.getCount()>0){

            cursor.moveToFirst();
            do {

                Pessoa pessoa = new Pessoa();
                pessoa.setId(cursor.getLong(0));
                pessoa.setNome(cursor.getString(1));
                pessoa.setIdade(cursor.getString(2));
                pessoa.setFps(cursor.getString(3));
                pessoa.setTipoPele(cursor.getInt(4));
                adpPessoas.add(pessoa);

            }while (cursor.moveToNext());

        }

        return adpPessoas;
    }


}
