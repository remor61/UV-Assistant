package com.example.vitor.uva_analisador;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class ActAjuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_ajuda);

       // WebView view = (WebView) findViewById(R.id.textContent);
        String text;
        text = "<html><body><p align=\"justify\">";
        //text = "In development.";
//        text+= "    Bem vindo ao UV - Assistant: Analisador de Risco à" +
//                "Radiaçao Ultravioleta. Esse aplicativo tem como objetivo " +
//                "monitorar vários usuários cadastrados em nosso " +
//                "sistema com o intuito de alertar os mesmos quando" +
//                "esse tipo de radiação está sendo prejudicial à saúde do mesmo. " +
//                "Para isso, levamos em conta alguns fatores, os quais você pode " +
//                "encontrar abaixo na explicação do aplicativo.";
//
//        text+= " O aplicativo apresenta quatro botões na tela inicial:\n" +
//                "\n\n Registrar novo usuário \n\n\n" +
//
//                "           Essa opção é responsável pelo cadastramento de usuários no sistema. Ao clicar, surgirá uma nova tela. Nesta tela, terá alguns campos solicitando dados deste usuário que será registrado. Um dos campos é o tipo de pele do mesmo. O tipo de pele é um fator de extrema importância em nosso sistema. A pele influencia no tempo de exposição segura as pessoas, ou seja, pessoas diferentes suportam tempos diferentes expostas ao sol. Para selecionar o tipo de pele do usuario, clique no ícone que representa uma camera. A câmera do dispositivo irá se abrir. A seguir tira uma foto do seu braço junto ao cartão com as cores vermelho, verde, azul e branco e, a seguir, recorte as mesmas. Caso a cor de pele identificado não seja parecida com a sua, escolha você mesmo a cor da pele, através da lista ao lado do icone da camera. No canto superior direito, há um ícone com tres pontos desenhado, clique no mesmo para salvar o usuário.\n";
//        text+= "            O segundo botão refere-se aos usuários já cadastrados no sistema. Ao clicá-lo, abrir-se-á uma tela com uma lista de todos os usuários registrados. Você pode editar e excluir esses usuários clicando em cima dos referidos nomes, pois a tela de cadastro contendo os dados do usuário em questão irá se abrir e, clicando nos três pontos, agora terá as opções de salvar a edição ou de realizar a exclusão desse usuário.";
//        text+= "            O terceiro botão é o botão de dados. Quando clicado, abre-se uma tela que irá se comunicar com o dispositivo externo do sistema. Quando o bluetooth estiver conectado, você deve clicar no botao de start, para começar a transferencia de dados e stop para pará-la. Assim, nessa tela aparecerá o tempo decorrente e a radiação ultravioleta do momento.";
        text+= "</p></body></html>";
        //view.loadData(text, "text/html", "utf-8");
        //view.setBackgroundColor(Color.TRANSPARENT);
    }
}
