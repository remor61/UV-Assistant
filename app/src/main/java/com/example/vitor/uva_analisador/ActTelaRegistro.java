package com.example.vitor.uva_analisador;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.example.vitor.uva_analisador.database.DataBase;
import com.example.vitor.uva_analisador.dominio.RepositorioPessoas;
import com.example.vitor.uva_analisador.dominio.entidades.Pessoa;



public class ActTelaRegistro extends AppCompatActivity {

    private EditText ednome, edidade, edfps;
    private ImageView ImageBt, mostraFoto;
    private TextView tipo_da_pele, nRGB;
    private Button bt_salvar, bt_voltar;
    private Spinner spnTipoPele;
    private CheckBox check_pele;

    static int tipoPele;
    static int conta_vezes=0;

    static boolean NullControl = false;
    static boolean SkinControl = false;
//    static int conta_vezes=0;

  //  static float valor_branco=0, deltafloat=0;

    private ArrayAdapter<String> adpTipoPele;

    private DataBase dataBase;
    private SQLiteDatabase conn;
    private RepositorioPessoas repositorioPessoas;
    private Pessoa pessoa;
    public static int cvn=0;
   // public static float RED=0,GREEN=0,BLUE=0;
   // static final int REQUEST_IMAGE_CAPTURE = 1;
    private static float mediaWhiteR = 0, mediaWhiteG = 0, mediaWhiteB = 0, mediaWhiteGray = 0, mediaRed = 0, mediaGreen = 0, mediaBlue = 0, mediaGray = 0;

    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //captured picture uri
    private Uri picUri;
    //keep track of cropping intent
    final int PIC_CROP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tela_registro);

        ednome = (EditText)findViewById(R.id.ednome);
        edidade = (EditText)findViewById(R.id.edidade);
        edfps = (EditText) findViewById(R.id.edfps);
        ImageBt = (ImageView) findViewById(R.id.imagemmmmm);
      //  mostraFoto = (ImageView)findViewById(R.id.foto_dps_tirar);
        tipo_da_pele = (TextView)findViewById(R.id.tipo_da_pele);
        check_pele = (CheckBox) findViewById(R.id.checkboxpele);

        spnTipoPele = (Spinner)findViewById(R.id.spnTipoPele);

        adpTipoPele = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpTipoPele.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnTipoPele.setAdapter(adpTipoPele);

        adpTipoPele.add("Pale White");//("Branco I");
        adpTipoPele.add("White");//("Branco II");
        adpTipoPele.add("Cream White");//("Morena Clara");
        adpTipoPele.add("Moderate Brown");//("Morena");
        adpTipoPele.add("Dark Brown");//("Morena Escura");
        adpTipoPele.add("Darkest Brown");//("Negra");

        Bundle bundle = getIntent().getExtras();

        if ((bundle != null) && (bundle.containsKey("PESSOA"))){
            pessoa = (Pessoa) bundle.getSerializable("PESSOA");
            preencheDados();
        }
        else {
            pessoa = new Pessoa();
        }


//        pessoa = new Pessoa();

        try {
            dataBase = new DataBase(this);
            conn = dataBase.getWritableDatabase();

            repositorioPessoas = new RepositorioPessoas(conn);

        }catch (SQLException ex){
            AlertDialog.Builder dlg = new  AlertDialog.Builder(this);
            dlg.setMessage("Erro ao criar o banco: " + ex.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }

        //Disable the button if user has no camera
        if (!hasCamera()){
            ImageBt.setEnabled(false);
        }

        //chama a função de abrir a camera se o botao for pressionado
        ImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_tela_registro, menu);

        if (pessoa.getId() != 0)
            menu.getItem(1).setVisible(true);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.mni_acao1:
                salvar();
                if(NullControl && SkinControl) {
                    NullControl = false;
                    SkinControl = false;
                    finish();
                }
            break;

            case R.id.mni_acao2:
                excluir();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void preencheDados(){

        ednome.setText(pessoa.getNome());
        edidade.setText(pessoa.getIdade());
        edfps.setText(pessoa.getFps());

        //spnTipoPele.setSelection(pessoa.getTipoPele()-1);

    }

    private void excluir(){
        try {
            repositorioPessoas.excluir(pessoa.getId());

        }catch (Exception ex){
            AlertDialog.Builder dlg = new  AlertDialog.Builder(this);
            dlg.setMessage("Erro ao excluir os dados: " + ex.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }
    }

    private void salvar(){

        if(ednome.getText().toString().length()==0) {
            ednome.setError("Insert a Name!");
            NullControl = false;
        }
        else{
            if(edidade.getText().toString().length()==0) {
                edidade.setError("Insert an Age!");
                NullControl = false;
            }
            else {
                if (edfps.getText().toString().length() == 0) {
                    edfps.setError("Insert a Sunscreen Factor! (0 if not using)");
                    NullControl = false;
                }
                else{
                    try {
                        if (check_pele.isChecked()) {
                            pessoa.setTipoPele((spnTipoPele.getSelectedItemPosition())+1);
                            SkinControl = true;
                        }else {
                            pessoa.setTipoPele(tipoPele);
                        }
                        if(SkinControl){
                            pessoa.setNome(ednome.getText().toString());
                            pessoa.setIdade(edidade.getText().toString());
                            pessoa.setFps(edfps.getText().toString());

                            if(pessoa.getId() == 0)
                                repositorioPessoas.inserir(pessoa);
                            else
                                repositorioPessoas.alterar(pessoa);

                            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_LONG).show();
                        }else
                            Toast.makeText(this,"Please take the picture of your skin or enable the manual list!",Toast.LENGTH_LONG).show();

                    }catch (Exception ex){
                        AlertDialog.Builder dlg = new  AlertDialog.Builder(this);
                        dlg.setMessage("Erro ao inserir os dados: " + ex.getMessage());
                        dlg.setNeutralButton("OK", null);
                        dlg.show();
                    }finally {
                        NullControl = true;
                    }
                }
            }
        }
    }

    public static Bitmap grayScale(Bitmap original, int cv){

        Bitmap finalImage = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        int A, R=0, G=0, B=0;
        int pixelColor;
        int height = original.getHeight();
        int widht = original.getWidth();
        float tempRed=0, tempGreen=0, tempBlue=0, tempGray=0;
        int somaRed=0, somaGreen=0, somaBlue=0, somaGray=0, n=0;

        for (int y = 0; y < height; y++){
            for (int x = 0; x < widht; x++){
                pixelColor = original.getPixel(x, y);
                A = Color.alpha(pixelColor);
                R = Color.red(pixelColor);
                G = Color.green(pixelColor);
                B = Color.blue(pixelColor);

                somaRed+=R;
                somaGreen+=G;
                somaBlue+=B;
                n++;

//                int i = (int) (0.3 * R + 0.59 * G + 0.11 * B);
//                somaGray+=i;
//
//                finalImage.setPixel(x, y, Color.argb(A, i, i, i));
            }
        }

        tempRed = somaRed/n;
        tempGreen = somaGreen/n;
        tempBlue = somaBlue/n;
       // tempGray = somaGray/n;

        //Log.i("myTag","temp red: "+String.valueOf(tempRed));
        //Log.i("myTag","temp green: "+String.valueOf(tempGreen));
        //Log.i("myTag","temp Blue: "+String.valueOf(tempBlue));

         if (cv == 1){
             mediaRed = tempRed;
        } else if (cv == 2){
             mediaGreen = tempGreen;
        } else if (cv == 3){
             mediaBlue = tempBlue;
        } else if (cv == 4){
             mediaWhiteR = tempRed;
             mediaWhiteG = tempGreen;
             mediaWhiteB = tempBlue;
             //mediaWhiteGray = tempGray;
        }
//        else if (cv == 5){
//            mediaGray = tempGray;
//        }

        if (cv >= 5) {

          //  Log.i("myTag","Media White: "+String.valueOf(mediaWhiteR));
            //corrige o vermelho
          //  double vezes=0;
//            if (mediaWhiteR >= 240) vezes = 1;
//            else if (mediaWhiteR >= 220) vezes = 1.01;
//            else if (mediaWhiteR >= 200) vezes = 1.02;
//            else if (mediaWhiteR >= 180) vezes = 1.03;
//            else if (mediaWhiteR >= 160) vezes = 1.04;
//            else if (mediaWhiteR >= 140) vezes = 1.05;
//            else if (mediaWhiteR >= 120) vezes = 1.06;
//            else if (mediaWhiteR >= 100) vezes = 1.07;

            float delta = mediaWhiteR - mediaRed;
            float red1, red2, red3, red4, red5, red6;
            double newMediaRed;

            red6 = (float) (((30.0 / 255.0) * delta) + mediaRed);
            red5 = (float) (((140.0 / 255.0) * delta) + mediaRed);
            red4 = (float) (((179.0 / 255.0) * delta) + mediaRed);
            red3 = (float) (((220.0 / 255.0) * delta) + mediaRed);
            red2 = (float) (((237.0 / 255.0) * delta) + mediaRed);
            //red1 = (float) (((249.0 / 255.0) * delta) + mediaRed);
            newMediaRed = (((tempRed / 255) * delta) + mediaRed);
            //Log.i("myTag","Media Red: "+String.valueOf(newMediaRed));
            //newMediaRed = newMediaRed * vezes;

            //corrige verde
            float green1, green2, green3, green4, green5, green6, newMediaGreen;

            delta = mediaWhiteG - mediaGreen;

            green6 = (float) (((22.0 / 255.0) * delta) + mediaGreen);
            green5 = (float) (((92.0 / 255.0) * delta) + mediaGreen);
            green4 = (float) (((116.0 / 255.0) * delta) + mediaGreen);
            green3 = (float) (((165.0 / 255.0) * delta) + mediaGreen);
            green2 = (float) (((176.0 / 255.0) * delta) + mediaGreen);
            green1 = (float) (((202.0 / 255.0) * delta) + mediaGreen);
            newMediaGreen = (((tempGreen / 255) * delta) + mediaGreen);
            //Log.i("myTag","Media Green: "+String.valueOf(newMediaGreen));
            //corrige azul
            float blue1, blue2, blue3, blue4, blue5, blue6, newMediaBlue;

            delta = mediaWhiteB - mediaBlue;

            blue6 = (float) (((18.0 / 255.0) * delta) + mediaBlue);
            blue5 = (float) (((50.0 / 255.0) * delta) + mediaBlue);
            blue4 = (float) (((90.0 / 255.0) * delta) + mediaBlue);
            blue3 = (float) (((134.0 / 255.0) * delta) + mediaBlue);
            blue2 = (float) (((158.0 / 255.0) * delta) + mediaBlue);
            //blue1 = (float) (((190.0 / 255.0) * delta) + mediaBlue);
            newMediaBlue = (((tempBlue / 255) * delta) + mediaBlue);
            //Log.i("myTag","Media Blue: "+String.valueOf(newMediaBlue));

            //corrige cinza
//            float gray1, gray2, gray3, gray4, gray5, gray6, newMediaGrey;
//
//            delta = mediaWhiteGray - mediaGray;
//
//            gray6 = (float) (((220.0/255.0)*delta) +mediaGray);
//            gray5 = (float) (((199.0/255.0)*delta) + mediaGray); //199/255
//            gray4 = (float) (((160.0/255.0)*delta) + mediaGray); //174/255
//            gray3 = (float) (((121.0/255.0)*delta) + mediaGray); //135/255
//            gray2 = (float) (((106.0/255.0)*delta) + mediaGray); //119/255
//            gray1 = (float) (((68.0/255.0)*delta) + mediaGray); //68/255
//            newMediaGrey = (((tempGray/255)*delta) + mediaGray);


//            Log.i("myTag","Red6: "+String.valueOf(red6)+" Green6: "+String.valueOf(green6)+" Blue6: "+String.valueOf(blue6));
//            Log.i("myTag","Red5: "+String.valueOf(red5)+" Green5: "+String.valueOf(green5)+" Blue5: "+String.valueOf(blue5));
//            Log.i("myTag","Red4: "+String.valueOf(red4)+" Green4: "+String.valueOf(green4)+" Blue4: "+String.valueOf(blue4));
//            Log.i("myTag","Red3: "+String.valueOf(red3)+" Green3: "+String.valueOf(green3)+" Blue3: "+String.valueOf(blue3));
//            Log.i("myTag","Red2: "+String.valueOf(red2)+" Green2: "+String.valueOf(green2)+" Blue2: "+String.valueOf(blue2));

            int peleRed = 0, peleGreen = 0, peleBlue = 0, peleGray = 0;

            //determina faixa do vermelho
            if (0 < newMediaRed && newMediaRed <= red6){
                if(newMediaRed - red6 < 3.0)
                    peleRed = 5;
                else peleRed = 5;
            }

            else if (red6 < newMediaRed && newMediaRed <= red5)
                peleRed = 5;
            else if (red5 < newMediaRed && newMediaRed <= red4)
                peleRed = 4;
            else if (red4 < newMediaRed && newMediaRed <= red3)
                peleRed = 3;
            else if (red3 < newMediaRed && newMediaRed <= red2)
                peleRed = 2;
            else if (red2 < newMediaRed && newMediaRed <= 255)
                peleRed = 1;

            //determina faixa do verde
            if (0 < newMediaGreen && newMediaGreen <= green6)
                peleGreen = 6;
            else if (green6 < newMediaGreen && newMediaGreen <= green5)
                peleGreen = 5;
            else if (green5 < newMediaGreen && newMediaGreen <= green4)
                peleGreen = 4;
            else if (green4 < newMediaGreen && newMediaGreen <= green3)
                peleGreen = 3;
            else if (green3 < newMediaGreen && newMediaGreen <= green2)
                peleGreen = 2;
            else if (green2 < newMediaGreen && newMediaGreen <= 255)
                peleGreen = 1;

            //determina faixa do azul
            if (0 < newMediaBlue && newMediaBlue <= blue6)
                peleBlue = 6;
            else if (blue6 < newMediaBlue && newMediaBlue <= blue5)
                peleBlue = 5;
            else if (blue5 < newMediaBlue && newMediaBlue <= blue4)
                peleBlue = 4;
            else if (blue4 < newMediaBlue && newMediaBlue <= blue3)
                peleBlue = 3;
            else if (blue3 < newMediaBlue && newMediaBlue <= blue2)
                peleBlue = 2;
            else if (blue2 < newMediaBlue && newMediaBlue <= 255)
                peleBlue = 1;

//                RED = peleRed;
//                GREEN = peleGreen;
//                BLUE = peleBlue;
            //determina faixa do cinza
//            if (mediaGray <= newMediaGrey && newMediaGrey < gray6) peleGray=6;
//            else if (gray6 <= newMediaGrey && newMediaGrey < gray5) peleGray=5;
//            else if (gray5 <= newMediaGrey && newMediaGrey < gray4) peleGray=4;
//            else if (gray4 <= newMediaGrey && newMediaGrey < gray3) peleGray=3;
//            else if (gray3 <= newMediaGrey && newMediaGrey < gray2) peleGray=2;
//            else if (gray2 <= newMediaGrey && newMediaGrey < 255) peleGray=1;
//            else peleGray=0;

            tipoPele = (peleRed + peleGreen + peleBlue)/3; //+ peleGray)/4;
            if(tipoPele > 0) SkinControl = true;

//            Log.i("myTag","pele red: "+String.valueOf(peleRed));
//            Log.i("myTag","pele green: "+String.valueOf(peleGreen));
//            Log.i("myTag","pele blue: "+String.valueOf(peleBlue));
//            //Log.i("myTag","pele gray: "+String.valueOf(peleGray));
//            Log.i("myTag","pele: "+String.valueOf(tipoPele));
        }

        return finalImage;
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //Launching the camera
    public void launchCamera(){
        try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Take picture and pass result along to onActivityResult
                conta_vezes=0;
                cvn=0;
                startActivityForResult(intent, CAMERA_CAPTURE);
        }catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //If you want to return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {

                //get the Uri for the captured image
                picUri = data.getData();
                //carry out the crop operation
                do {
                    performCrop();
                    conta_vezes++;
                } while (conta_vezes < 5);
                //performCrop();
            } else if (requestCode == PIC_CROP) {
                cvn++;
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                //retrieve a reference to the ImageView
                Bitmap newPhoto = grayScale(thePic, cvn);

                //display the returned cropped image
                ImageBt.setImageBitmap(newPhoto);

                //nRGB.setText("R: "+rS+" G: "+rG+" B: "+rB);
                //if (tipoPele == 1) tipo_da_pele.setText("Type: " + tipoPele);
                if (tipoPele == 1) tipo_da_pele.setText("Type: Pale White");//("Tipo: Branca I");
                else if (tipoPele == 2) tipo_da_pele.setText("Type: Cream White");//("Tipo: Branca II");
                else if (tipoPele == 3) tipo_da_pele.setText("Type: Moderate Brown");//("Tipo: Morena Clara");
                else if (tipoPele == 4) tipo_da_pele.setText("Type: Dark Brown");//("Tipo: Morena");
                else if (tipoPele == 5) tipo_da_pele.setText("Type: Darkest Brown");//("Tipo: Morena Escura");
                else if (tipoPele == 6) tipo_da_pele.setText("Type: White");//("Tipo: Negra");

            }
        }
    }

    private void performCrop() {


        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



}
