package com.birdisolutions.birdikegel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import static android.os.AsyncTask.Status.FINISHED;
import static com.birdisolutions.birdikegel.MainActivity.PRESION_MERCURIO;
import static com.birdisolutions.birdikegel.MainActivity.SONIDO_MENUS;
import static com.birdisolutions.birdikegel.MainActivity.SONIDO_TEXTO;


public class Entorno_Juego extends AppCompatActivity {

    ProgressBar progressBarHorizontal;
    Button btnProgress;
    ImageButton btnsalir;
    ImageView texto_imagen;
    TextView indicador_presion, contador_serie;

    int numero_serie;
    private Sesion la_sesion;
    boolean acabar_sesion, en_progreso;
    long tiempo_Relajacion, tiempo_Barra;
    long tiempo_Contraccion;
    long tiempo_cero;
    int progreso;

    boolean ocupado = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entorno__juego);

        añadirVistas();

        //     Datos Condiguración:

        boolean presion_mercurio = getIntent().getBooleanExtra(PRESION_MERCURIO, false);
        boolean sonido_texto = getIntent().getBooleanExtra(SONIDO_TEXTO, false);
        boolean sonido_menus = getIntent().getBooleanExtra(SONIDO_MENUS, false);

// Recibimos la sesión a realizar. Se realiza a través de la clase Comunicador y sus métodos estáticos. Queda almacenada en la variable la_esión

        la_sesion = (Sesion) Comunicador.getObjeto();


//Definimos escuchadores en botones

        btnProgress.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                ejecuta_sesion();
                btnProgress.setClickable(false);
            }
        });

        btnsalir.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        contador_serie.setText(null);
        if (presion_mercurio) indicador_presion.setText("mmHg");
        else indicador_presion.setText("cmH2O");


    }


    private void añadirVistas() {

        btnProgress = (Button) findViewById(R.id.btn1);
        btnsalir = (ImageButton) findViewById(R.id.salir);
        texto_imagen = (ImageView) findViewById(R.id.mensaje);
        texto_imagen.setBackground(null);

        progressBarHorizontal = (ProgressBar) findViewById(R.id.progressbar_Horizontal);
        progressBarHorizontal.setProgress(0);

        indicador_presion = (TextView) findViewById(R.id.indicador_presion);
        contador_serie = (TextView) findViewById(R.id.contador_series);
    }

    void ejecuta_sesion() {

        int numero_series = la_sesion.dime_tamano();
        int i = 1;
        while (i < numero_series) {
            contador_serie.setText((i + 1) + "/" + numero_series);
            ejecuta_serie(i);
            i++;
        }

    }

    void ejecuta_serie(int n_serie) {

        Serie mi_serie = la_sesion.coje_serie(n_serie);

        new ejecuta_la_series().execute(mi_serie);

    }



        public class ejecuta_la_series extends AsyncTask<Serie, Integer, Integer> {




            @Override
            protected Integer doInBackground(Serie... series) {
                int count = series.length;
                int repeticiones, progreso;
                long tiempo_relax, tiempo_contrae;

                for (int i = 0; i < count; i++) {

                    tiempo_relax = series[i].getTiempo_relajacion() * 1000;
                    tiempo_contrae = series[i].getTiempo_contraccion() * 1000;
                    repeticiones = series[i].getRepeticiones();
                  for (int j = 0; j < repeticiones; j++) {

                      //Relaja
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              texto_imagen.setImageResource(R.drawable.relaja_relax_reducido);
                          }
                      });
                      tiempo_cero = System.currentTimeMillis();
                      SystemClock.sleep(2);

                      while ((System.currentTimeMillis() - tiempo_cero) < tiempo_relax) {
                          SystemClock.sleep(20);
                          progreso = (int) (100. * (System.currentTimeMillis() - tiempo_cero) / tiempo_relax);
                          publishProgress(progreso);

                      }

                      //Contrae

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              texto_imagen.setImageResource(R.drawable.contrae_squeeze_reducido);
                          }
                      });
                      tiempo_cero = System.currentTimeMillis();
                      SystemClock.sleep(2);

                      while ((System.currentTimeMillis() - tiempo_cero) < tiempo_contrae) {
                          SystemClock.sleep(20);
                          progreso = (int) (100. * (System.currentTimeMillis() - tiempo_cero) / tiempo_contrae);
                          publishProgress(progreso);


                      }
                      if (isCancelled()) break;
                  }
            }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(Entorno_Juego.this, "onPreExecute", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                Toast.makeText(Entorno_Juego.this, "onPostExecute", Toast.LENGTH_SHORT).show();
                btnProgress.setClickable(true);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBarHorizontal.setProgress(values[0]);
            }

            @Override
            protected void onCancelled(Integer integer) {
                super.onCancelled(integer);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        }
}


/*






                int count = series.length;
                int repeticiones, progreso;
                long tiempo_relax, tiempo_contrae;

                for (int i = 0; i < count; i++) {

                    tiempo_relax = series[i].getTiempo_relajacion() * 1000;
                    tiempo_contrae = series[i].getTiempo_contraccion() * 1000;
                    repeticiones = series[i].getRepeticiones();
                    for (int j = 0; j < repeticiones; j++) {

                        //Relaja
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                texto_imagen.setImageResource(R.drawable.relaja_relax_reducido);
                            }
                        });
                        tiempo_cero = System.currentTimeMillis();
                        SystemClock.sleep(2);

                        while ((System.currentTimeMillis() - tiempo_cero) < tiempo_relax) {
                            SystemClock.sleep(20);
                            progreso = (int) (100. * (System.currentTimeMillis() - tiempo_cero) / tiempo_relax);
                            publishProgress(progreso);

                        }

                        //Contrae

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                texto_imagen.setImageResource(R.drawable.contrae_squeeze_reducido);
                            }
                        });
                        tiempo_cero = System.currentTimeMillis();
                        SystemClock.sleep(2);

                        while ((System.currentTimeMillis() - tiempo_cero) < tiempo_contrae) {
                            SystemClock.sleep(20);
                            progreso = (int) (100. * (System.currentTimeMillis() - tiempo_cero) / tiempo_contrae);
                            publishProgress(progreso);

                        }

                    }

*/