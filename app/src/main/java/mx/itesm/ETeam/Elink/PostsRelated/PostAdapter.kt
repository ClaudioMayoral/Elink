package mx.itesm.ETeam.Elink.PostsRelated

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_creation.view.*
import kotlinx.android.synthetic.main.single_post.view.*
import mx.itesm.ETeam.Elink.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// Autor: Francisco Arenas
// Clase que permite enseñar en un recyclerView los posts hechos
class PostAdapter(private val context: Context, private val postList: List<PostData>) :
                    RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    // Datos de usuario
    private lateinit var uid: String
    private lateinit var username: String
    private lateinit var userMail: String
    private lateinit var dirImagen: String

    // Datos del post
    private lateinit var postID: String
    private lateinit var postText: String
    private lateinit var postType: String
    private lateinit var postImage: String
    private lateinit var postTime: String
    private lateinit var postTimeStamp: String
    private lateinit var likes: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
                                  .inflate(R.layout.single_post, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        obtenerDatos(position)
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = postTimeStamp.toLong()
        postTime = android.text.format.DateFormat
                                      .format("dd/MM/yyyy hh:mm:aa", calendar) as String

        colocarDatos(holder, postTime)
        configurarBotones(holder)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun obtenerDatos(position: Int) {
        uid = postList[position].uid
        username = postList[position].userName
        userMail = postList[position].userMail
        dirImagen = postList[position].dirImage
        postID = postList[position].postID
        postText = postList[position].postText
        postType = postList[position].postType
        postImage = postList[position].postImage
        postTimeStamp = postList[position].postTime
    }

    private fun colocarDatos(holder: ViewHolder, postTime: String) {
        holder.nameUser.text = username
        holder.time.text = postTime
        holder.postType.text = postType
        holder.content.text = postText

        // foto de usuario
        try {
            Picasso.get().load(dirImagen).placeholder(R.drawable.icon_profile)
                                         .into(holder.userPostPicture)
        } catch (e: Exception){ }

        //
        if(postImage == "noImage"){
            holder.imagePost.visibility = View.GONE
        } else {
            try {
                Picasso.get().load(postImage).into(holder.imagePost)
            } catch (e: Exception){ }
        }
    }

    private fun configurarBotones(holder: ViewHolder) {
        holder.moreButton.setOnClickListener{
            Toast.makeText(context, "MORE BUTTON",
                Toast.LENGTH_SHORT).show()
        }

        holder.likeButton.setOnClickListener{
            Toast.makeText(context, "LIKE BUTTON",
                Toast.LENGTH_SHORT).show()
        }

        holder.donarButton.setOnClickListener{
            Toast.makeText(context, "DONATE BUTTON",
                Toast.LENGTH_SHORT).show()
        }

        holder.compartirButton.setOnClickListener{
            Toast.makeText(context, "SHARE BUTTON",
                Toast.LENGTH_SHORT).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         val userPostPicture: ImageView
         val imagePost: ImageView
         val moreButton: ImageButton
         val likeButton: Button
         val donarButton: Button
         val compartirButton: Button
         val nameUser: TextView
         val postType: TextView
         val time: TextView
         val content: TextView
         val likes: TextView

         init {
             userPostPicture = itemView.findViewById(R.id.userPostPicture)
             imagePost = itemView.findViewById(R.id.imagePost)
             moreButton = itemView.findViewById(R.id.moreButton)
             likeButton = itemView.findViewById(R.id.likeButton)
             donarButton = itemView.findViewById(R.id.donarButton)
             compartirButton = itemView.findViewById(R.id.compartirButton)
             nameUser = itemView.findViewById(R.id.nameUser)
             postType = itemView.findViewById(R.id.postType)
             time = itemView.findViewById(R.id.time)
             content = itemView.findViewById(R.id.content)
             likes =  itemView.findViewById(R.id.likes)
         }

        /*
        val userPostPicture: ImageView = itemView.findViewById(R.id.userPostPicture)
         val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
         val moreButton: ImageButton = itemView.findViewById(R.id.moreButton)
         val likeButton: Button = itemView.findViewById(R.id.likeButton)
         val donarButton: Button = itemView.findViewById(R.id.donarButton)
         val compartirButton: Button = itemView.findViewById(R.id.compartirButton)
         val nameUser: TextView = itemView.findViewById(R.id.nameUser)
         val postType: TextView = itemView.findViewById(R.id.postType)
         val time: TextView = itemView.findViewById(R.id.time)
         val content: TextView = itemView.findViewById(R.id.content)
         val likes: TextView = itemView.findViewById(R.id.likes)
         */


        /*
        fun set(post: PostData){
            imagenUsuario = itemView.postPicture
            Picasso.get().load(userImage).placeholder(R.drawable.icon_profile).into()
            itemView.nameUser.text = post.userName
            itemView.postType.text = post.postType
            itemView.time.text = post.postTime
            itemView.content.text = post.postText
        }
        */

    }


    /*

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.single_post, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getDbData(position)
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = postList[position].postTime.toLong()
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:nn:aa", Locale.getDefault())
        val postDate = formatter.format(calendar).toString()

        setData(holder, postDate)
        configurarBotones(holder)
    }

    private fun configurarBotones(holder: ViewHolder) {
        holder.moreButton.setOnClickListener{
            Toast.makeText(context, "No seas tímido. Comparte algo con tus seguidores",
                Toast.LENGTH_SHORT).show()
        }

        holder.likeButton.setOnClickListener{
            Toast.makeText(context, "No seas tímido. Comparte algo con tus seguidores",
                Toast.LENGTH_SHORT).show()
        }

        holder.donarButton.setOnClickListener{
            Toast.makeText(context, "No seas tímido. Comparte algo con tus seguidores",
                Toast.LENGTH_SHORT).show()
        }

        holder.compartirButton.setOnClickListener{
            Toast.makeText(context, "No seas tímido. Comparte algo con tus seguidores",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(holder: ViewHolder, postDate: String) {
        holder.nameUser.text = userName
        holder.time.text = postDate
        holder.postType.text = postType
        holder.content.text = postText

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.icon_profile).into(holder.postPicture)
        } catch (e: Exception){
        }

        if(postImage == "noImage"){
            holder.imagePost.visibility = View.GONE
        } else {
            try {
                Picasso.get().load(postImage).into(holder.imagePost)
            } catch (e: Exception){
            }
        }
    }

    private fun getDbData(position: Int) {
        uid = postList[position].uid
        userName = postList[position].userName
        userMail = postList[position].userMail
        userImage = postList[position].userImage
        postID = postList[position].postID
        postType = postList[position].postType
        postText = postList[position].postText
        postImage = postList[position].postImage
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    */
}