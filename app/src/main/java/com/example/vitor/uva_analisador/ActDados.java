package com.example.vitor.uva_analisador;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitor.uva_analisador.database.DataBase;
import com.example.vitor.uva_analisador.dominio.RepositorioPessoas;
import com.example.vitor.uva_analisador.dominio.entidades.Pessoa;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Objects;
import java.util.regex.Pattern;

import static android.R.attr.data;
import static android.R.attr.inAnimation;

public class ActDados extends AppCompatActivity {

     /* Definição dos objetos que serão usados na Activity Principal
        statusMessage mostrará mensagens de status sobre a conexão
        counterMessage mostrará o valor do contador como recebido do Arduino
        connect é a thread de gerenciamento da conexão Bluetooth
     */

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    static Switch aSwitch;
    static TextView statusMessage;
    static TextView energy;
    static TextView uvIndex;
    static TextView elapsedTime;
    static String funciona = null;
    static int funciona2 = 0, cont=0;

    //private static SQLiteDatabase conn;

    ConnectionThread connect;

    //handler variables
    static int skinTypes[] = new int[50];
    static int ProtectorValue[] = new int[50];
    static int limitTime[] = new int[50];
    static int eachTime[] = new int[50];
    static String names[] = new String[50];
    static int flags[] = new int[50];
    static int numberArray = 0;
    static int numberArrayInfo[] = new int[50];

    private static Context contx;
    private static Object hhhh;
    private static Resources resources;

    private static ImageView imagemtroca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_dados);
        contx = this;
        hhhh = getSystemService(NOTIFICATION_SERVICE);
        resources = getResources();

        for (int x = 0; x < flags.length; x++){
            flags[x] = 0;
        }

        //NotificationManager nnnnnnnnm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /* Link entre os elementos da interface gráfica e suas
            representações em Java.
         */

        statusMessage = (TextView) findViewById(R.id.statusMessage);
        //energy = (TextView) findViewById(R.id.energy);
        uvIndex = (TextView) findViewById(R.id.uvIndex);
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);

        imagemtroca = (ImageView) findViewById(R.id.iamgemtroca);

        /* Teste rápido. O hardware Bluetooth do dispositivo Android
            está funcionando ou está bugado de forma misteriosa?
            Será que existe, pelo menos? Provavelmente existe.
         */
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Hardware Bluetooth não encontrado.");
        } else {
            statusMessage.setText("Bluetooth OK.");
        }

        /* A chamada do seguinte método liga o Bluetooth no dispositivo Android
            sem pedido de autorização do usuário. É altamente não recomendado no
            Android Developers, mas, para simplificar este app, que é um demo,
            faremos isso. Na prática, em um app que vai ser usado por outras
            pessoas, não faça isso.
         */
        if(!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
            statusMessage.setText("Solicitando ativação do Bluetooth...");
        } else {
            statusMessage.setText("Bluetooth already enabled.");//("Bluetooth já ativo.");//
        }
        /* Definição da thread de conexão como cliente.
            Aqui, você deve incluir o endereço MAC do seu módulo Bluetooth.
            O app iniciará e vai automaticamente buscar por esse endereço.
            Caso não encontre, dirá que houve um erro de conexão.
         */

        connect = new ConnectionThread("20:14:12:03:21:03");
        connect.start();

        /* Um descanso rápido, para evitar bugs esquisitos.
         */
        try {
            Thread.sleep(1000);
        } catch (Exception E) {
            E.printStackTrace();
        }


    }

    public void searchPairedDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Bluetooth turned on");//("Bluetooth ativado");//
            }
            else {
                statusMessage.setText("Bluetooth turned off");//("Bluetooth desativado");//
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            }
            else {
                statusMessage.setText("Nenhum dispositivo selecionado");
            }
        }
    }

    public void start_stop (View view){

        if (cont%2 == 0) {
            //start
            char[] vai = {'N'};
            String StartBit = new String(vai);
            byte[] data = StartBit.getBytes();
            connect.write(data);
        }
        else {
            //stop
            char[] vai = {'M'};
            String StartBit = new String(vai);
            byte[] data = StartBit.getBytes();
            connect.write(data);
        }
        cont++;
    }

    public void StartPic(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle("Warning");//("Atenção");
        builder.setMessage( "Remember to update the users' sunscreen factor" );//("Não esqueça de atualizar o fator de protetor solar dos usuários");
        AlertDialog dialog = builder.create();
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                //ação do botão "sim"
            }
        });
        dialog = builder.create();
        dialog = builder.show();

        char[] vai = {'N'};
        String StartBit = new String(vai);
        byte[] data =  StartBit.getBytes();
        connect.write(data);

    }

    public void StopPic(View view) {

        char[] vai = {'M'};
        String StartBit = new String(vai);
        byte[] data =  StartBit.getBytes();
        connect.write(data);
    }

    public void notifyy(View view){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setTicker("Ticker Texto")
                .setContentTitle("Warning")//("Atenção")
                .setSmallIcon(R.mipmap.logo_riscos)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_sem_fundo));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static int num1=0, num2=0, num3=0, num4=0;

    public static Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            // Esse método é invocado sempre que a thread de conexão Bluetooth recebe uma mensagem.

            /******Acessa o banco de dados para ler os nomes das pessoas******************/
            DataBase dataBase;
            SQLiteDatabase conn;
            dataBase = new DataBase(contx);
            conn = dataBase.getReadableDatabase();
            /****************************************************************************/

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);
            if(dataString.isEmpty())dataString="E48E48E48";
            String sep[] = dataString.split(Pattern.quote("E"));

            if (dataString.equals("---S")) {
                statusMessage.setText("Connected.");//("Conectado.");
            }
            else if (dataString.equals("---N")){
                statusMessage.setText("Connection Error.");//("Erro de conexão.");
            }
            else {

                int energyScore = Integer.parseInt(sep[1]);
                int energyCount = Integer.parseInt(sep[2]);
                int finalEnergy = 255*energyCount + energyScore;

                funciona2 = finalEnergy;
                if (sep[3].equals("A")){
                    uvIndex.setText("10");
                }
                else if (sep[3].equals("B")){
                    uvIndex.setText("11");
                }
                else {
                    uvIndex.setText(sep[3]);
                }

                String strEnergyFinal = Integer.toString(finalEnergy);
//                energy.setText(strEnergyFinal);

                if (sep[3].equals("0")){
                    imagemtroca.setImageResource(R.drawable.nivelzero);
                } else if (sep[3].equals("1")){
                    imagemtroca.setImageResource(R.drawable.nivelum);
                }else if (sep[3].equals("2")){
                    imagemtroca.setImageResource(R.drawable.niveldois);
                }else if (sep[3].equals("3")){
                    imagemtroca.setImageResource(R.drawable.niveltres);
                }else if (sep[3].equals("4")){
                    imagemtroca.setImageResource(R.drawable.nivelquatro);
                }else if (sep[3].equals("5")){
                    imagemtroca.setImageResource(R.drawable.nivelcinco);
                }else if (sep[3].equals("6")){
                    imagemtroca.setImageResource(R.drawable.nivelseis);
                }else if (sep[3].equals("7")){
                    imagemtroca.setImageResource(R.drawable.nivelsete);
                }else if (sep[3].equals("8")){
                    imagemtroca.setImageResource(R.drawable.niveloito);
                }else if (sep[3].equals("9")){
                    imagemtroca.setImageResource(R.drawable.nivelnove);
                }else if (sep[3].equals("A")){
                    imagemtroca.setImageResource(R.drawable.niveldez);
                }else if (sep[3].equals("B")){
                    imagemtroca.setImageResource(R.drawable.nivelonze);
                }

                elapsedTime.setText(sep[4]);


                String nome_usuarios_1=new String(), nome_usuarios_2=new String(), nome_usuarios_3=new String();
                String nome_usuarios_4=new String(), nome_usuarios_5=new String(), nome_usuarios_6=new String();
                int num_pele_1 = 0, num_pele_2 = 0, num_pele_3 = 0, num_pele_4 = 0, num_pele_5 = 0, num_pele_6 = 0;


                Cursor cursor = conn.query("PESSOA", null, null, null, null, null, null);

                if (cursor.getCount() > 0){
                    cursor.moveToFirst();
                    int cnt = 0;
                    do {
                        names[cnt] = cursor.getString(1).toString();
                        skinTypes[cnt] = cursor.getInt(4);
                        if (cursor.getInt(3) == 0){
                            ProtectorValue[cnt] = 1;
                        }
                        else {
                            ProtectorValue[cnt] = cursor.getInt(3);
                        }
                        cnt++;
                    }while (cursor.moveToNext());
                }

                for (int j = 0; j < skinTypes.length; j++){
                    if (skinTypes[j] == 1) limitTime[j] = 60;
                    else if (skinTypes[j] == 2)limitTime[j] = 60;
                    else if (skinTypes[j] == 3)limitTime[j] = 120;
                    else if (skinTypes[j] == 4)limitTime[j] = 180;
                    else if (skinTypes[j] == 5)limitTime[j] = 240;
                    else if (skinTypes[j] == 6)limitTime[j] = 240;
                }

                for (int k = 0; k < limitTime.length; k++){
                    eachTime[k] = limitTime[k]*ProtectorValue[k];
                }

                //Notificacao no app
                NotificationManager nm = (NotificationManager)hhhh;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(contx);
                PendingIntent p = PendingIntent.getActivity(contx, 0, new Intent(contx, ActTelaUsuarios1.class), 0);
                builder.setTicker("Ticker Texto")
                        .setContentTitle("Warning")//("Atenção")
                        .setSmallIcon(R.mipmap.logo_riscos)
                        .setContentIntent(p)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.logo_sem_fundo));

                numberArray=0;
                for (int count = 0, n=0; count < eachTime.length; count++){
                    if (funciona2 > eachTime[count]){
                        if (names[count] != null && flags[count] == 0) {
                            numberArray++;
                            numberArrayInfo[n] = count;
                            n++;
                            flags[count]++;
                        }
                    }
                }

                String frase = null;
                if (numberArray > 1){
                    frase = "Users ";//"Usuários ";
                    for (int aux = 0; aux < numberArray; aux++){
                        if (aux == numberArray-1) {
                            frase += names[numberArrayInfo[aux]] + " in danger";//" em risco";
                        }
                        else if (aux == numberArray-2){
                            frase += names[numberArrayInfo[aux]] + " and ";//" e ";
                        }
                        else {
                            frase += names[numberArrayInfo[aux]] + ", ";
                        }
                    }
                }
                else{
                    frase = "User "/*"Usuário "*/ + names[numberArrayInfo[0]] + " in danger";//" em risco";
                }

                for (int aux = 0; aux < eachTime.length; aux++) {
                    if (funciona2 > eachTime[aux]) {
                        if (names[aux] != null) {
                            if (flags[aux] == 1) {
                                builder.setContentText(frase);
                                Notification n = builder.build();
                                n.vibrate = new long[]{150, 300, 150, 300};
                                nm.notify(R.mipmap.ic_launcher, n);
                                som();
                                flags[aux]++;
                            }
                        }
                    }
                }
            }
        }
    };

    public static void som(){
        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(contx, som);
            toque.play();
        }catch (Exception e){}
    }

    public static void GerarNotificacao(){

        ActDados actDados = new ActDados();
        Context context = actDados.getBaseContext();

        NotificationManager nm = (NotificationManager) actDados.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, new Intent(context, ActTelaUsuarios1.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setTicker("Ticker Texto")
                .setContentTitle("Warning")//("Atenção")
                .setSmallIcon(R.mipmap.logo_riscos);
        //    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_sem_fundo))


//        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
//        String [] descs = new String[]{"Usuário deve sair da exposição ao sol"};
//        for (int i=0; i<descs.length; i++){
//            style.addLine(descs[i]);
//        }
//        builder.setStyle(style);

        builder.setContentIntent(p);

        Notification n = builder.build();
        n.vibrate = new long[]{150, 300, 150, 600};
        nm.notify(R.mipmap.ic_launcher, n);

        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        }catch (Exception e){

        }

    }


    /* Esse método é invocado sempre que o usuário clicar na TextView
        que contem o contador. O app Android transmite a string "restart",
        seguido de uma quebra de linha, que é o indicador de fim de mensagem.
     */

    public void restartCounter(View view) {
        connect.    write("restart\n".getBytes());
    }


}

