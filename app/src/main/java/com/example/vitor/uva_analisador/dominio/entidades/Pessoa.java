package com.example.vitor.uva_analisador.dominio.entidades;

import java.io.Serializable;

/**
 * Created by vitor on 01/09/2017.
 */

public class Pessoa implements Serializable{
    private long id;
    private String nome;
    private String idade;
    private String fps;
    private int tipoPele;


    public Pessoa(){
        id=0;
    }


    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipoPele() {
        return tipoPele;
    }

    public void setTipoPele(int tipoPele) {
        this.tipoPele = tipoPele;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    @Override
    public String toString(){

        return nome + " - Skin Type - "/*" - Tipo de Pele - "*/ + tipoPele;

    }



}
