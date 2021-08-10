package com.example.vitor.uva_analisador.database;

/**
 * Created by vitor on 01/09/2017.
 */

public class ScriptSQL {

    public static String getCreatePessoa(){

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append(" CREATE TABLE  IF NOT EXISTS PESSOA ( ");
        sqlBuilder.append("_id                 INTEGER       NOT NULL ");
        sqlBuilder.append("PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder.append("NOME               VARCHAR (200), ");
        sqlBuilder.append("IDADE           VARCHAR (14), ");
        sqlBuilder.append("FPS           VARCHAR (14), ");
        sqlBuilder.append("TIPOPELE       INT (1) ");
        sqlBuilder.append(");");

        return sqlBuilder.toString();

    }








}
