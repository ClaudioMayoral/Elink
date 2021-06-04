package mx.itesm.ETeam.Elink.PostsRelated

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import mx.itesm.ETeam.Elink.R
import java.util.*

// Autor: Francisco Arenas
// Clase que permite enseñar en un recyclerView los posts hechos por un usuario
class SheepPostAdapter(private val context: Context, private val postList: List<PostData>) :
        RecyclerView.Adapter<SheepPostAdapter.ViewHolder>()
{
        // CurrentUser
        private var currentUserID = FirebaseAuth.getInstance().currentUser

        // Datos de usuario
        private lateinit var uid: String
        private var username: String? = null
        private lateinit var userMail: String
        private lateinit var dirImagen: String
        private lateinit var projectName: String
        private lateinit var projectType: String

        // Datos del post
        //private lateinit var postID: String
        private lateinit var postText: String
        private lateinit var postType: String
        private lateinit var postImage: String
        private lateinit var postTime: String
        private lateinit var postTimeStamp: String
        private lateinit var likes: String

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_sheep_post, parent, false)
            return ViewHolder(vista)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            obtenerDatos(position)

            val postID = postList[position].postID
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = postTimeStamp.toLong()
            postTime = android.text.format.DateFormat
                .format("dd/MM/yyyy hh:mm:aa", calendar) as String

            colocarDatos(holder, postTime)
            configurarBotones(holder, postID)
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
            projectName = postList[position].projectName
            projectType = postList[position].projectType
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
                holder.imagePost.visibility = View.VISIBLE

                try {
                    Picasso.get().load(postImage).fit().into(holder.imagePost)
                } catch (e: Exception){ }
            }
        }

        private fun configurarBotones(holder: ViewHolder, postID: String) {
            holder.moreButton.setOnClickListener{
                mostrarOpciones(holder.moreButton, postID, postImage)
            }
        }

        private fun mostrarOpciones(moreButton: ImageButton, postID: String, postImage: String) {
            val popupMenu = PopupMenu(context, moreButton, Gravity.END)

            popupMenu.menu.add(Menu.NONE, 0, 0, "Borrar")
            popupMenu.menu.add(Menu.NONE, 1, 0, "Editar")

            popupMenu.setOnMenuItemClickListener {
                val id = it.itemId
                if(id == 0){
                    comenzarBorrado(postID, postImage)
                } else if (id == 1){
                    val intent = Intent(context, PostCreation::class.java)
                    intent.putExtra("key", "editPost")
                    intent.putExtra("editPostId", postID)
                    context.startActivity(intent)
                }
                false
            }

            popupMenu.show()
        }

    private fun comenzarBorrado(postID: String, postImage: String) {
        if (postImage == "noImage"){
            borraSinImagen(postID)
        } else {
            borrarConImagen(postID, postImage)
        }
    }

    private fun borrarConImagen(postID: String, postImage: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Borrando...")

        val picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postImage)

        picRef.delete().addOnSuccessListener {
            val query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID")
                .equalTo(postID)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        ds.ref.removeValue()
                    }
                    Toast.makeText(context, "Borrado correctamente", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "" + error.message, Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
            query.addListenerForSingleValueEvent(postListener)
        }
    }

    private fun borraSinImagen(postID: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Borrando...")

        val query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID")
            .equalTo(postID)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    ds.ref.removeValue()
                }
                Toast.makeText(context, "Borrado correctamente", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "" + error.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
        query.addListenerForSingleValueEvent(postListener)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val userPostPicture: CircularImageView
            val imagePost: ImageView
            val moreButton: ImageButton
            val nameUser: TextView
            val postType: TextView
            val time: TextView
            val content: TextView
            val likes: TextView

            init {
                userPostPicture = itemView.findViewById(R.id.userPostPicture)
                imagePost = itemView.findViewById(R.id.imagePost)
                moreButton = itemView.findViewById(R.id.moreButton)
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




    }