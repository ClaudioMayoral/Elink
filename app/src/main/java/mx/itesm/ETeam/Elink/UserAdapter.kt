package mx.itesm.ETeam.Elink

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import mx.itesm.ETeam.Elink.DataClasses.User

// Autor: Francisco Arenas
// Clase que permite enseñar en un recyclerView los usuarios que se siguen
class UserAdapter(private val context: Context, private val userList: List<User>) :
                    RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    // Firebase
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    // To be displayed, user data
    private var projectName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(context)
                                  .inflate(R.layout.single_user, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        dbReference = FirebaseDatabase.getInstance().reference
        val user = userList[position]

        configureView(holder, user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    private fun configureView(holder: ViewHolder, userInList: User) {
        holder.follow_button.visibility = View.VISIBLE
        holder.nameUser.text = userInList.username
        val typeText = "Usuario tipo: ${userInList.usertype}"
        holder.usertype.text = typeText

        if (userInList.usertype == "shark"){
            reviewFollowingStatus(holder)
        } else {
            dbReference.child("Users").child(userInList.userID).get().addOnSuccessListener {
                projectName = it.child("Project").child("nombreDelProyecto").value.toString()
                println("project name: $projectName")
                val projectFormat = "Proyecto: $projectName"
                holder.status_project.text = projectFormat
            }.addOnFailureListener{
                Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
            }

            val followerText: String
            if (holder.follow_button.text.toString() == "Siguiendo") {
                followerText = "Lo sigues"
                holder.status_project.text = followerText
            } else {
                followerText = "No lo sigues"
                holder.status_project.text = followerText
            }
        }
            /*
            val dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userInList.userID).child("Project")
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val projectName = ds.child("nombreDelProyecto").toString()
                        val projectFormat = "Proyecto: $projectName"
                        holder.status_project.text = projectFormat
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "" + databaseError.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dbReference.addValueEventListener(postListener)


            dbReference.child("Users").child(userInList.userID).get().addOnSuccessListener {
                projectName = it.child("Project/nombreDelProyecto").toString()
                println("project name: $projectName")
                val projectFormat = "Proyecto: $projectName"
                holder.status_project.text = projectFormat
            }.addOnFailureListener{
                Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
            }


            dbReference.child("Users").child(firebaseUser.uid).get().addOnSuccessListener {
                projectName = it.child("Project/nombreDelProyecto").value.toString()
                currentUserType = "shark"
                val projectFormat = "Proyecto: $projectName"
                holder.status_project.text = projectFormat
            }.addOnFailureListener{
                Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
            }
            */
        Glide.with(context).load(userInList.dirImagen).into(holder.userPicture)
        println("image url: ${userInList.dirImagen}")
        showButton(holder, userInList)
        isFollowing(userInList.userID, holder.follow_button)

        holder.itemView.setOnClickListener {
            val editor: SharedPreferences.Editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("profileiD", userInList.userID)
            editor.apply()

            val supportFragmentManager = (context as FragmentActivity?)!!.supportFragmentManager
            val fragmentToChange = SharkProfileFrag()

            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentToChange).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        }

        holder.follow_button.setOnClickListener{
            if (holder.follow_button.text.toString() == "Seguir"){
                // Add a following user to our current user
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(firebaseUser.uid).child("following").child(userInList.userID)
                    .setValue(true)

                // Add our current user to followers list in userInList
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(userInList.userID).child("followers").child(firebaseUser.uid)
                    .setValue(true)
            } else {
                // Remove a following user to our current user
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(firebaseUser.uid).child("following").child(userInList.userID)
                    .removeValue()

                // Remove our current user to followers list in userInList
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(userInList.userID).child("followers").child(firebaseUser.uid).removeValue()
            }
        }
    }

    private fun reviewFollowingStatus(holder: ViewHolder) {
        dbReference.child("Users").child(firebaseUser.uid).get().addOnSuccessListener {
            val type = it.child("usertype").value.toString()
            if (type == "shark"){
                val followerText: String
                if (holder.follow_button.text.toString() == "Siguiendo") {
                    followerText = "Lo sigues"
                    holder.status_project.text = followerText
                } else {
                    followerText = "No lo sigues"
                    holder.status_project.text = followerText
                }
            } else {
                val followerText: String
                if (holder.follow_button.text.toString() == "Siguiendo") {
                    followerText = "Te sigue"
                    holder.status_project.text = followerText
                } else {
                    followerText = "No te sigue"
                    holder.status_project.text = followerText
                }
            }
        }.addOnFailureListener{
            Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showButton(holder: ViewHolder, userInList: User) {
        dbReference.child("Users").child(firebaseUser.uid).get().addOnSuccessListener {
            val type = it.child("usertype").value.toString()
            if (type == "shark"){
                if (userInList.userID == firebaseUser.uid){
                    holder.follow_button.visibility = View.GONE
                } else {
                    holder.follow_button.visibility = View.VISIBLE
                }
            } else {
                holder.follow_button.visibility = View.GONE
            }
        }.addOnFailureListener{
            Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun isFollowing(userID: String, button: Button){
        val reference = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(firebaseUser.uid).child("following")

        val followersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val followerText: String
                if (snapshot.child(userID).exists()) {
                    followerText = "Siguiendo"
                    button.text = followerText
                } else {
                    followerText = "Seguir"
                    button.text = followerText
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        reference.addValueEventListener(followersListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val userPicture: CircularImageView
        val nameUser: TextView
        val usertype: TextView
        val status_project: TextView
        val follow_button: Button

        init {
            userPicture = itemView.findViewById(R.id.userPicture)
            nameUser = itemView.findViewById(R.id.nameUser)
            usertype = itemView.findViewById(R.id.usertype)
            status_project = itemView.findViewById(R.id.status_project_name)
            follow_button = itemView.findViewById(R.id.followButton)
        }
    }

}