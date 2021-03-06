package mx.itesm.ETeam.Elink.PostsRelated

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import mx.itesm.ETeam.Elink.R
import mx.itesm.ETeam.Elink.ShowSheepInfo
import java.io.File
import java.io.FileOutputStream
import java.util.*

// Autor: Francisco Arenas
// Clase que permite enseñar en un recyclerView los posts hechos
class SharkPostAdapter(private val context: Context, private val postList: List<PostData>) :
                    RecyclerView.Adapter<SharkPostAdapter.ViewHolder>()
{
    // Datos de usuario
    private lateinit var uid: String
    private var username: String? = null
    private lateinit var userMail: String
    private lateinit var dirImagen: String

    //TEST
    private lateinit var projectName: String
    private lateinit var projectType: String

    // Datos del post
    private lateinit var postText: String
    private lateinit var postType: String
    private lateinit var postImage: String
    private lateinit var postTime: String
    private lateinit var postTimeStamp: String

    // Datos de likes
    private val myUID = FirebaseAuth.getInstance().currentUser
    private lateinit var likes: String
    private lateinit var likesReference: DatabaseReference      //FirebaseDatabase.getInstance().reference.child("Likes")
    private lateinit var postsRef: DatabaseReference            //FirebaseDatabase.getInstance().reference.child("Posts")
    private var processLike = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
                                  .inflate(R.layout.single_shark_post, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        likesReference = FirebaseDatabase.getInstance().reference.child("Likes")
        postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        obtenerDatos(position)

        val postID = postList[position].postID
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = postTimeStamp.toLong()
        postTime = android.text.format.DateFormat
                                      .format("dd/MM/yyyy hh:mm:aa", calendar) as String

        colocarDatos(holder, postTime, postID)
        configurarBotones(holder, position, postID)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun obtenerDatos(position: Int) {
        uid = postList[position].uid
        username = postList[position].username
        userMail = postList[position].userMail
        dirImagen = postList[position].dirImagen
        postText = postList[position].postText
        postType = postList[position].postType
        postImage = postList[position].postImage
        postTimeStamp = postList[position].postTime
        likes = postList[position].likes                // numero total de likes
        projectName = postList[position].projectName
        projectType = postList[position].projectType
    }

    private fun colocarDatos(holder: ViewHolder, postTime: String, postID: String) {
        holder.nameUser.text = username.toString()
        holder.time.text = postTime
        holder.postType.text = postType
        holder.content.text = postText

        val likeFormatter = "$likes Likes"
        holder.likes.text = likeFormatter
        colocarLikes(holder, postID)

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
                Picasso.get().load(postImage).fit().into(holder.imagePost)
            } catch (e: Exception){ }
        }
    }

    private fun colocarLikes(holder: ViewHolder, postKey: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(postKey).hasChild(myUID?.uid!!)){
                    holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_liked, 0, 0, 0)
                    holder.likeButton.text = "Te gusta"
                } else {
                    holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like, 0, 0, 0)
                    holder.likeButton.text = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "" + error.message, Toast.LENGTH_SHORT).show()
            }

        }
        likesReference.addValueEventListener(postListener)

    }

    private fun configurarBotones(holder: ViewHolder, position: Int, postId: String) {
        holder.likeButton.setOnClickListener{
            val likes: Int = Integer.parseInt(postList[position].likes)
            processLike = true

            val postListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(processLike) {
                        processLike = if(snapshot.child(postId).hasChild(myUID?.uid!!)) {
                            postsRef.child(postId).child("likes").setValue(""+(likes-1))
                            likesReference.child(postId).child(myUID.uid).removeValue()
                            false
                        } else {
                            postsRef.child(postId).child("likes").setValue(""+(likes+1))
                            likesReference.child(postId).child(myUID.uid).setValue("Liked")
                            false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            likesReference.addValueEventListener(postListener)

        }

        holder.donarButton.setOnClickListener{
            Toast.makeText(context, "SE IMPLEMENTARÁ DESPUÉS",
                Toast.LENGTH_SHORT).show()
        }

        holder.compartirButton.setOnClickListener{
            val bitmapDrawable = holder.imagePost.drawable as BitmapDrawable
            if (bitmapDrawable == null){
                // post sin imagen
                compartirSoloTexto(postText, postType)
            } else {
                val bitmap = bitmapDrawable.bitmap
                compartirImagenTexto(bitmap, postType, postText)
            }
        }
    }

    private fun compartirImagenTexto(bitmap: Bitmap?, postType: String, postText: String) {
        val shareBody = "Post de: ${username}\nTipo de post: $postType\nContenido: $postText"
        val uri = guardarImagen(bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Asunto aquí")
        intent.type = "image/png"
        context.startActivity(Intent.createChooser(intent, "Compartir vía"))

    }

    private fun guardarImagen(bitmap: Bitmap?): Uri? {
        val imageFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, "mx.itesm.ETeam.Elink.fileprovider"
                , file)
        } catch (e: java.lang.Exception){
            Toast.makeText(context, ""+e.message, Toast.LENGTH_SHORT).show()
        }
        return uri
    }

    private fun compartirSoloTexto(postText: String, postType: String) {
        val shareBody = "Post de: ${username}\nTipo de post: $postType\nContenido: $postText"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Asunto aquí")
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(intent, "Compartir vía"))

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
         val userPostPicture: CircularImageView
         val imagePost: ImageView
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