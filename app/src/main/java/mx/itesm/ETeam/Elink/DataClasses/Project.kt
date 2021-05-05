package mx.itesm.ETeam.Elink.DataClasses

/* Clase que representa un proyecto para ser ingresado en la base de datos.
Un conjunto de proyectos se desplegará en la pantalla principal.
Autor: Alejandro Torices
 */
data class Project(val nombreDelProyecto:String="", val descripcionProyecto:String="",
                   val moneyGoal:Int=0, val categoria:String="")
