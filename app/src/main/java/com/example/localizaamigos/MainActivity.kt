package com.example.localizaamigos

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        //Si el usuario ya se metio una vez y se registro debemos buscar o invocar el archivo
        //de preferencias compartidas y buscar al nombre de usuario y si existe
        //despues ocultamos el boton de registro porque ya no hay necesidad de registrar
        //Aqui vamos a la inversa, leemos el archivo de preferencias compartidas
        val preferencias=applicationContext?.getSharedPreferences("AMIGOS", Context.MODE_PRIVATE)?:return
        //Para leer la informacion se hace directamente con el objeto preferencias y no con el edit
        with(preferencias){
            val valorNombre=getString("nombre", null)
            if(!valorNombre.isNullOrEmpty()){
                Toast.makeText(applicationContext, "Bienvenido de nuevo ${valorNombre}", Toast.LENGTH_LONG).show()
                //Ocultamos el boton de REGISTRO
                empezar.visibility=View.INVISIBLE
            }
        }
        //Vamos a poner un código gracioso, vamos a activar vibrador!
        var vibrador = getSystemService(VIBRATOR_SERVICE) as Vibrator
        //Invocamos el vibrador
        vibrador.vibrate(3000)
        //Empezamos a programar para ver la forma de implementar los eventos de boton
        //en android studios
        var botonEmpezar=findViewById<Button>(R.id.empezar)
        //Invocamos el botón Localizar por su id, ahora con el plugin de extensiones.
        localizar.setOnClickListener {
            startActivity(Intent(this,MapitaActivity::class.java))
        }
        //Manejamos el evento
        botonEmpezar.setOnClickListener {
            //Antes de la navegación hacia la RegistroActivity vamos a invocar a un componente
            //que se llama Toast: son mensajes de corta duración que aparecen en la pantalla
            Toast.makeText(applicationContext,"¡Vamos a registrarnos!",Toast.LENGTH_LONG).show()
            //El siguiente renglón nos lleva de este Activity al de Registro Y La redireccionamos:
            startActivity(Intent(this,RegistroActivity::class.java))
        }
        //declaracion de una variable en kotlin
        var x=4
        var y="Hola Mundo"
        //Lo siguiente también es correcto:
        var z:Int
        var w:String

        //Vamos a imprimir en el logcat para ello usamos la clase log
        Log.i("MALO","Este es mi primer mensaje con etiqueta en info")

        //Vamos a concatenar una variable em KOTLIN
        var mensajito=" vamos a concatenar"
        Log.i("MALO","En KOTLIN"+mensajito+" mas facil"+" que Java")
        //La versión de concatenado de Kotlin es mucho mejor
        Log.i("MALO","En KOTLIN $mensajito mas facil que Java")
        //Además la interpolación reloaded
        Log.i("MALO","Vamos a interpolar una expresion ${5+3} que puede ser una operacion")

        //Invocamos o mandamos llamar nuestra funcioncita
        saludar()
        //Aquí estamos dentro del cuerpo del método onCreate
        //Kotlin permite aquí dentro implementar una función, cosa que Java no
        fun mensajito(){
            Log.i("MALO","Implementando una funcion dentro de otra!")
        }
        //Aquí invocamos la función anterior
        mensajito()

        //Funciones con tipo de retorno
        fun sumar():Int{
            var x=5+4
            return x
        }
        //La invocamos
        Log.i("MALO","INVOCAMOS LA FUNCION ${sumar()} con el interpolador de expresiones")

        //Otra modalidad es la siguiente, que recibe argumentos
        fun saludar2(mensaje:String){
            Log.i("MALO",mensaje)
        }
        //La invocamos
        saludar2("Este mensajito es el argumento de la funcion")
        fun saludar2(nombre:String,edad:Int){
            Log.i("MALO","Tu nombre es $nombre y tu edad es $edad")
        }
        //Invocamos la función con sus argumentos
        saludar2("Miguel",22)




    }

    //AQUÍ DECLARAMOS UNA FUNCIONCITA
    fun saludar(){
        Log.i("MALO","Implementando mi primer funcion en Kotlin")
    }
}