package br.com.ddmsoftware.churrascoladora;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    public static final String EXTRA_MESSAGE = "br.com.ddmsoftware.churrascoladora.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Load Advertisement Banner
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CalculateChurras");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "CalcChurras");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                calculaChurras(view);

            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

        if (isNetworkAvailable())
            openGooglePlay();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void calculaChurras(View view) {

        /* CALCULO DA CARNE

        450 g de carne para cada homem
        300 g de carne para cada mulher
        150 g de carne para cada criança


        CALCULO DA BEBIDA
        - 600 ml de refrigerante por pessoa
        - 800 ml de cerveja por pessoa
        - 200 ml de água por pessoa
        - Vinho ou espumante se não houver cerveja – calcule ½ garrafa por pessoa
        - Vinho ou espumante se houver cerveja – calcule 1 garrafa para cada 5 pessoas - diminua a cerveja para 600 ml
        - Whisky – 1 garrafa para cada 50 pessoas
        - Coquetel de frutas – 4 litros para 50 pessoas

        */

        int iqtdHomens, iqtdMulheres, iqtdCriancas;

        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat df2 = new DecimalFormat("0,000");


        //Variaveis para calcular Carne
        int iTotalGeral, iTotalCarneH, iTotalCarneM, iTotalCarneC;


        float fMediaCarneBovina = 0, fMediaCarneSuina = 0, fMediaLinguica = 0, fMediaFrango = 0;

        boolean bKilo = false;

        float fPercentualCarne;
        float fSaldo;
        float fMediaRestante = 0;

        EditText edtQtdHomens, edtQtdMulheres, edtQtdCriancas;
        CheckBox chkCarneBovina, chkCarneSuina, chkLinguica, chkFrango, chkCerveja, chkAgua, chkRefri, chkVinho;

        edtQtdHomens = findViewById(R.id.edtQtdHomens);
        edtQtdMulheres = findViewById(R.id.edtQtdMulheres);
        edtQtdCriancas = findViewById(R.id.edtQtdCriancas);

        chkCarneBovina = findViewById(R.id.chkCarneBovina);
        chkCarneSuina = findViewById(R.id.chkCarneSuina);
        chkFrango = findViewById(R.id.chkCarneFrango);
        chkLinguica = findViewById(R.id.chkLinguica);

        chkAgua = findViewById(R.id.chkAgua);
        chkRefri = findViewById(R.id.chkRefrigerante);
        chkVinho = findViewById(R.id.chkVinho);
        chkCerveja = findViewById(R.id.chkCerveja);

        if ((edtQtdHomens.getText().length() == 0) ||
                (edtQtdMulheres.getText().length() == 0) ||
                (edtQtdCriancas.getText().length() == 0)) {

            showMessage(view, getString(R.string.sMessage_Pessoas));

        } else if (!chkCarneBovina.isChecked() &&
                (!chkCarneSuina.isChecked() &&
                        (!chkFrango.isChecked() &&
                                (!chkLinguica.isChecked())))) {

            showMessage(view, getString(R.string.sMessage_Carnes));

        } else if (!chkAgua.isChecked() &&
                (!chkRefri.isChecked() &&
                        (!chkCerveja.isChecked() &&
                                (!chkVinho.isChecked())))) {

            showMessage(view, getString(R.string.sMessage_Bebidas));

        } else {

            iqtdHomens = Integer.parseInt(edtQtdHomens.getText().toString());
            iqtdMulheres = Integer.parseInt(edtQtdMulheres.getText().toString());
            iqtdCriancas = Integer.parseInt(edtQtdCriancas.getText().toString());

            int iTotalPessoas = iqtdCriancas + iqtdHomens + iqtdMulheres;

            iTotalCarneH = iqtdHomens * 450;
            iTotalCarneM = iqtdMulheres * 300;
            iTotalCarneC = iqtdCriancas * 150;

            iTotalGeral = (iTotalCarneH + iTotalCarneM + iTotalCarneC);

            // Valida se todos as carnes foram clicadas
            if (chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (chkFrango.isChecked() && (chkLinguica.isChecked())))) {
                bKilo = false;

                fPercentualCarne = iTotalGeral / 3;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo / 3;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = fMediaRestante;

            } else if (chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = iTotalGeral / 2;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = fMediaRestante;

            } else if (chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = (iTotalGeral * 60) / 100;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;
                fMediaCarneSuina = fMediaRestante;

            } else if (chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = iTotalGeral;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;

            } else if (!chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral / 3;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = fMediaRestante;
            } else if (!chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = 0;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = fMediaRestante;
            } else if (!chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = 0;
                fMediaFrango = 0;
                fMediaLinguica = fMediaRestante;
            } else if (!chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = 0;
                fMediaFrango = 0;
                fMediaLinguica = fMediaRestante;
            } else if (chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = (iTotalGeral * 60) / 100;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;
                fMediaLinguica = fMediaRestante;
            } else if (chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = (iTotalGeral * 60) / 100;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;
                fMediaFrango = fMediaRestante;

            } else if (chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = iTotalGeral / 2;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;
                fMediaCarneSuina = fMediaRestante;
                fMediaLinguica = fMediaRestante;

            } else if (chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fPercentualCarne = iTotalGeral / 2;

                fSaldo = iTotalGeral - fPercentualCarne;
                fMediaRestante = fSaldo / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = fPercentualCarne;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = fMediaRestante;

            } else if (!chkCarneBovina.isChecked() && (chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = 0;
                fMediaLinguica = 0;
            } else if (!chkCarneBovina.isChecked() && (!chkCarneSuina.isChecked() && (chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = 0;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = 0;
            } else if (!chkCarneBovina.isChecked()&& (chkCarneSuina.isChecked() && (chkFrango.isChecked() && (!chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = fMediaRestante;
                fMediaLinguica = 0;
            } else if (!chkCarneBovina.isChecked()&& (chkCarneSuina.isChecked() && (!chkFrango.isChecked() && (chkLinguica.isChecked())))) {

                bKilo = false;

                fMediaRestante = iTotalGeral / 2;

                if ((fMediaRestante >= 1000) || (fMediaCarneBovina >= 1000 && fMediaRestante == 0)) {
                    bKilo = true;
                }
                fMediaCarneBovina = 0;

                fMediaCarneSuina = fMediaRestante;
                fMediaFrango = 0;
                fMediaLinguica = fMediaRestante;
            }


            Intent intent = new Intent(this, ResultActivity.class);
            String message = "Sugestão de quantidade de Carnes para seu churrasco:\n";

            if (bKilo) {

                message = message +
                        "\n>> Carne Bovina.....: " + df2.format(fMediaCarneBovina) + " Kg" +
                        "\n>> Carne Suina.......: " + df2.format(fMediaCarneSuina) + " Kg" +
                        "\n>> Carne Frango.....: " + df2.format(fMediaFrango) + " Kg" +
                        "\n>> Linguica.............: " + df2.format(fMediaLinguica) + " Kg";

                //Toast.makeText(this, message , Toast.LENGTH_LONG).show();

            } else {
                message = message +
                        "\n>> Carne Bovina.....: " + df.format(fMediaCarneBovina) + " Gramas" +
                        "\n>> Carne Suina.......: " + df.format(fMediaCarneSuina) + " Gramas" +
                        "\n>> Carne Frango.....: " + df.format(fMediaFrango) + " Gramas" +
                        "\n>> Linguica.............: " + df.format(fMediaLinguica) + " Gramas";

                //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }


            // **************************************
            // CÁLCULO DA BEBIDA
            //        - 600 ml de refrigerante por pessoa
            //        - 800 ml de cerveja por pessoa
            //        - 200 ml de água por pessoa
            //        - Vinho ou espumante se não houver cerveja – calcule ½ garrafa por pessoa
            //        - Vinho ou espumante se houver cerveja – calcule 1 garrafa para cada 5 pessoas - diminua a cerveja para 600 ml
            //        - Whisky – 1 garrafa para cada 50 pessoas
            //        - Coquetel de frutas – 4 litros para 50 pessoas
            // **************************************

            //Variaveis para calcular Bebidas
            int iTotalCerveja = 0;
            int iTotalRefri = 0;
            int iTotalVinho = 0;
            int iTotalAgua = 0;

            if (chkAgua.isChecked())
                iTotalAgua = (iTotalPessoas * 200) / 1000;

            if (chkCerveja.isChecked())
                iTotalCerveja = (iTotalPessoas * 800) / 1000;

            if (chkRefri.isChecked())
                iTotalRefri = (iTotalPessoas * 600) / 1000;

            if (chkVinho.isChecked() && !chkCerveja.isChecked()) {
                iTotalVinho = (iTotalPessoas) / 2;
            } else if (chkVinho.isChecked() && chkCerveja.isChecked()) {
                iTotalVinho = (iTotalPessoas) / 5;
            }

            //***************************************

            String sBebidas =
                    "\n\n>> Água .................................: " + iTotalAgua + " litro(s)" +
                            "\n>> Cerveja .............................: " + iTotalCerveja + " litro(s)" +
                            "\n>> Refrigerante .....................: " + iTotalRefri + " litro(s)" +
                            "\n>> Vinho (ou espumante) ....: " + iTotalVinho + " litro(s)";


            message = message + sBebidas + "\n\nSugestões de Acompanhamentos: " +
                    "Arroz, Salada Verde, Farofa, Maionese, Pão, Pão de Alho, entre outros ao seu gosto.";

            //Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);

        }
    }

    private void openGooglePlay() {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "OpenGooglePlay");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GooglePlay");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        final String appPackageName = "ddmsoftware"; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=" + appPackageName)));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showMessage(View view, String pMessage) {

        Snackbar.make(view, pMessage, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
