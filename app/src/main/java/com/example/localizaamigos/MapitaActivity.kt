package com.example.localizaamigos

import kotlinx.android.synthetic.main.activity_mapita.*
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import org.json.JSONObject


import java.lang.ref.WeakReference
import java.util.*

/*Las interface OnMapReadyCallback se invica cuando el mapa esta listo, tiene el metodo
                      (https://docs.mapbox.com/android/maps/api/9.6.1/index.html)
       onMapReady().-dentro de el invocamos el metodo enablelocationComponent()
 */

/*La interface PermisionListener (https://docs.mapbox.com/android/core/guides/)
tiene dos metodos:
onExplanationNeeded().- Se implementa con un mensaje donde se informa por que es necesaria
                        la localizacion.
onPermissionResult( ).- Este metodo se implementa donde se informa el resultado de aceptar
                       el permiso. Si se concede se muestra el mapa con la localziacion, sino
                       pues NO!.
 */
class MapitaActivity : AppCompatActivity() , OnMapReadyCallback, PermissionsListener {
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap

    //Los siguientes atributos son para ajustar el rango minio de localizacion
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

    private var locationEngine: LocationEngine? = null



    private val callback: LocationChangeListeningActivityLocationCallback =
            LocationChangeListeningActivityLocationCallback(this)

    //Invovamos los métodos del menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Este método sirve para invocar el menú que acabamos de crear y lo coloca en la toolbar
        menuInflater.inflate(R.menu.menusito, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Aquí se programan todos los eventos de botón
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.accion_todos->{
                //Esta sería la acción a ejecutarse para el caso del primer item
                var i=Intent(applicationContext, MapitaTodos::class.java)
                startActivity(i)
            }
            R.id.accion_uno->{
                //Esta sería la acción a ejecutarse para el caso del segundo item
            }
            else->{
                //Default
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Mapbox.getInstance(this, getString(R.string.miToken))


        setContentView(R.layout.activity_mapita)

        mapita.onCreate(savedInstanceState)
        mapita.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.OUTDOORS) {

            // Habilitamos la localizacion
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Checams si los permisos de localizacion son otorgados
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Personalizacion de opciones de localizacion
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyColor(ContextCompat.getColor(this, R.color.design_default_color_on_secondary))
                    .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(
                    this,
                    loadedMapStyle
            )
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Obtenemos una instancia del a componente de localizacion
            mapboxMap.locationComponent.apply {

                // Activamos la localizacin con opciones
                activateLocationComponent(locationComponentActivationOptions)

                // Habilitamos si la localizacionesta en true
                isLocationComponentEnabled = true

                // ponemos el tracking activado
                cameraMode = CameraMode.TRACKING

                // ajustamos la brujula como visible
                renderMode = RenderMode.COMPASS

                //El nuevo
                initLocationEngine();
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine?.requestLocationUpdates(request, callback, mainLooper)
        locationEngine?.getLastLocation(callback)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Necesitas tu localizacion", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "No conediste el permiso de localozacion", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mapita.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapita.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapita.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapita.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapita.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapita.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapita.onLowMemory()
    }


    inner  class LocationChangeListeningActivityLocationCallback internal constructor(activity: MapitaActivity?):
            LocationEngineCallback<LocationEngineResult> {
        private val activityWeakReference: WeakReference<MapitaActivity>


        override fun onSuccess(result: LocationEngineResult){
            val activity:MapitaActivity=activityWeakReference.get()!!
            if(activity!=null){
                val location=result.lastLocation?:return


                Toast.makeText(
                        activity,
                        "${result.lastLocation!!.latitude.toString()},${result.lastLocation!!.longitude.toString()},${result.lastLocation!!.altitude.toString()}",
                        Toast.LENGTH_SHORT
                ).show()

                //  Globales.lat=result.lastLocation!!.latitude
                //  Globales.lng=result.lastLocation!!.longitude
                //En la siguiente clase vamos a poner en eta seccion una corutina con la cual
                //estaremos enviando cada segundo o cuando tenga conexion
                //la ultima localizacion actualziada.
                //Generar el objeto de tipo localización que se va a enviar al back-end
                var loca=JSONObject()
                //Le ajustamos los valores del GPS
                loca.put("lat",result.lastLocation!!.latitude)
                loca.put("lon",result.lastLocation!!.longitude)
                loca.put("fecha",Date().toString())
                //El usuario debe de estar YA REGISTRADO Y GUARDADO LOCALMENTE EN
                //SharedPreferences
                var usuario=JSONObject()
                usuario.put("email","mangel.darzamendi2@gmail.com")
                usuario.put("localizacion",loca)
                //El siguiente paso es invocar aquí tu servicio REST en la url pero usando
                //el método PUT

                var url="https://benesuela.herokuapp.com/api/usuario"
                var jsonRequest=JsonObjectRequest(Request.Method.PUT, url, usuario,
                Response.Listener {
                     //Este Toast lo quitas una vez que verifiques que funciona"
                     //Toast.makeText(applicationContext, it.get("mensaje").toString(), Toast.LENGTH_LONG).show()
                }, Response.ErrorListener {
                     //Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                })

                //Se invoca el request con el Singleton
                MiSingleton.getInstance(applicationContext).addToRequestQueue(jsonRequest)




                if(activity.mapboxMap!=null&&result.lastLocation!=null){
                    activity.mapboxMap.getLocationComponent()
                            .forceLocationUpdate(result.lastLocation)
                }
            }
        }

        /**
         *
         *
         *@paramexceptiontheexceptionmessage
         */
        override fun onFailure(exception: Exception){
            var activity :MapitaActivity=activityWeakReference.get()!!
            if(activity!=null){
                Toast.makeText(
                        activity, exception.localizedMessage,
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        init{
            activityWeakReference=WeakReference<MapitaActivity>(activity)
        }
    }


}