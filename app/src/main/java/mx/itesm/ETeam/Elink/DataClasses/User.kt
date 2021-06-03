package mx.itesm.ETeam.Elink.DataClasses

/* Clase que representa a un usuario de la aplicación, sus datos se ingresan en la base de datos.
Autor: Alejandro Torices
Modificado por: Francisco Arenas
 */
data class User(val username:String="", val userMail:String="", val usertype:String = "",
                val dirImagen:String="", val userID:String="")
{
    constructor() : this("", "", "", "","")
}
