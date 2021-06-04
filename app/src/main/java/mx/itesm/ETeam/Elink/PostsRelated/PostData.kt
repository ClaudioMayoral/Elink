package mx.itesm.ETeam.Elink.PostsRelated

// Autor: Francisco Arenas
// Clase que guarda los datos de un post
data class PostData(val uid: String, val username: String, val userMail: String,
                    val dirImagen: String, val postID: String, val postText: String,
                    val postType: String, val postImage: String, val postTime: String,
                    val projectName: String, val projectType: String, val likes: String)
{
    constructor() : this("", "", "", "", "", "",
                            "", "", "", "", "", "")
}

