package com.example.localizaamigos

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.example.localizaamigos.modelo.ServicioUsuario
import com.example.localizaamigos.modelo.Usuario
import kotlinx.android.synthetic.main.activity_mapita_todos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


class MapitaTodos : AppCompatActivity() {


    var    mapboxMap:MapboxMap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Siempre antes
        Mapbox.getInstance(this, getString(R.string.miToken))

        setContentView(R.layout.activity_mapita_todos)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            this.mapboxMap=mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(8.0))


                //Checmos el tamanio
                todos()

            }
        }

    }


    fun todos() {
        var url = "https://benesuela.herokuapp.com/api/usuario/localizacion/"
        var usuarios=ArrayList<Usuario>()

        GlobalScope.launch(Dispatchers.IO){


            var retrofit = Retrofit.Builder()
                .baseUrl("https://benesuela.herokuapp.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()

            //PASO 2: GENERAR UN OBJETO PARAHABILITAR TU SERVICIO DE RETROFIT USANDO EL OBJETO
            //DEL PUNTO ANTERIOR
            var servicioUsuario = retrofit.create(ServicioUsuario::class.java)


            //PASO 2
            var enviarUsuarios = servicioUsuario.buscarTodos()




            //SE ENVIA  AL BACK- END Y  EN ESTE MOMENTO SE OBTIENE LA RESPUESTA
            usuarios = enviarUsuarios.execute().body()!!

            //Aqui viene la magia para compartir con el activity BusquedasActvity
            //nuestra variable usuarios de arriba



            //El siguiente se ejecuta en el Thread de la VIEW
            launch(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Tama" +usuarios.size, Toast.LENGTH_LONG).show()
                //Todo lo del mapita



                usuarios.forEach {
                    mapboxMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.localizacion?.lat!!, it.localizacion?.lon!!))
                            .title(it.email)
                            .snippet(" fecha:"+it.localizacion?.fecha!!)
                    )
                    Log.i("X","usuario:"+it.email)
                }






            }
            //


        }


    }


    // Add the mapView lifecycle to the activity's lifecycle methods
    public override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}