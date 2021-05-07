package mx.itesm.ETeam.Elink.PostsRelated

// Autor: Francisco Arenas
// Clase que guarda los datos de un post
data class PostData(val postID: String, val postText: String, val postType: String,
                    val postImage: String, val postTime: String, val uid: String,
                    val userName: String, val userMail: String, val userImage: String)
{
    companion object {
        @Volatile
        @JvmStatic
        private var INSTANCE: PostData? = null

        @JvmStatic
        @JvmOverloads
        fun getInstance(postID: String = "", postText: String = "", postType: String = "",
                        postImage: String = "", postTime: String = "", uid: String = "",
                        userName: String = "", userMail: String = "", userImage: String = "") {
            INSTANCE ?: PostData(postID, postText, postType, postImage, postTime, uid, userName,
                                    userMail, userImage).also { INSTANCE = it }
        }
    }
}