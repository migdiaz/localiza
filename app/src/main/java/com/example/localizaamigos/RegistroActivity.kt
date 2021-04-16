package com.example.localizaamigos

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.localizaamigos.modelo.Usuario
import kotlinx.android.synthetic.main.activity_registro.*
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //Manejamos el evento del botón para pedir los valores de las componentes de nuestro formulario.
        registrar.setOnClickListener {
            var usuario=Usuario()
            //Este usuario lo llenamos con los valores de los campos de texto del formulario:
            usuario.email=txtEmail.text.toString()
            usuario.nickname=txtNickname.text.toString()
            usuario.nombre=txtNombre.text.toString()

            //El siguiente paso, es que este objeto que acabamos de crear(Usuario), lo tenemos que enviar a un servidor externo
            //para que pueda ser guardado y registrado, como en cualquier red social.
            //Para este paso necesitamos enviar esta información a un servidor y el mecanismo de envío es una
            //arquitectura muy particular, que se denomina Arquitectura estilo REST.
            //En android existe una tecnologia muy particular que nos va a yudar a poder enviar nuestro objeto
            //de registro al back end. Esta tecnología se conoce como RETROFIT.

            //Punto3. Objeto JSON
            var usuariojson=JSONObject()
            usuariojson.put("email",usuario.email)
            usuariojson.put("nickname",usuario.nickname)
            usuariojson.put("nombre",usuario.nombre)

            //Punto4. Objeto tipo request
            var url="https://benesuela.herokuapp.com/api/usuario"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, usuariojson,
                    //Lambda ->, función que no tiene nombre. Empiezan y terminan con una llave.
                    Response.Listener { response ->
                        //Ponemos la notificación del back-end en un objeto de tipo Toast
                        //AQUÍ VAMOS A GUARDAR EL OBJETO EN SharedPreferences
                        val preferencias=applicationContext?.getSharedPreferences("AMIGOS", Context.MODE_PRIVATE)?:return@Listener
                        //Con notacion funcional con Lambdas, más moderno y seguro al NullPointerException
                        with(preferencias.edit()){
                          putString("nombre", usuario.nombre).commit()
                          //putFloat("edad", 19.8f).commit()
                        }
                        //El equivalente de arriba pero orientado a objetos
                        //preferencias?.edit()?.putString("nombre", usuario.nombre)?.commit()

                        Toast.makeText(this,     response.get("mensaje").toString(),Toast.LENGTH_LONG).show()
                    },
                    Response.ErrorListener { error ->
                        // TODO: Handle error
                        Toast.makeText(this, "Hubo un error, ${error}",Toast.LENGTH_LONG).show()
                    }
            )

            // Acceso al request por medio de una clase Singleton
            MiSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
        }
    }
}
class MiSingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MiSingleton? = null
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: MiSingleton(context).also {
                        INSTANCE = it
                    }
                }
    }

    //Para el caso d cargar un objeto como una imagen.
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap {
                        return cache.get(url)
                    }
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext es para evitar fuga de mmoria

        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}