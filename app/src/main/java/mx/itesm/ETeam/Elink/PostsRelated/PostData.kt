package mx.itesm.ETeam.Elink.PostsRelated

// Autor: Francisco Arenas
// Clase que guarda los datos de un post
data class PostData(val postID: String, val postText: String, val postType: String,
                    val postImage: String, val postTime: String, val uid: String,
                    val userName: String, val userMail: String, val userImage: String)
{
    constructor() : this("", "", "",
    "", "", "",
    "", "", "")

}