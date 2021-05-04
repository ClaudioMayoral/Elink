package mx.itesm.ETeam.Elink.DataClasses

/*
Representa las preferencias del usuario shark, de forma que pueda seguir los proyectos de su interés.
Autor: Alejandro Torices
 */
data class SharkPreferences(val ambiental:Boolean, val tecnologia:Boolean,val social:Boolean,
                            val entretenimiento:Boolean,val lifeStyle:Boolean)
