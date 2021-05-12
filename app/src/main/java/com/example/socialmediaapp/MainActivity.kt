package com.example.socialmediaapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.daos.PostDao
import com.example.socialmediaapp.daos.UserDao
import com.example.socialmediaapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var addButton: FloatingActionButton
    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton=findViewById(R.id.addButton)
        recyclerView=findViewById(R.id.recyclerView)

        addButton.setOnClickListener {
            val intent = Intent(applicationContext, CreatePostActivity::class.java)
            startActivity(intent)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollection
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signout -> {
                signOutCurrentUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOutCurrentUser() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)

        builder.setMessage("Do you want to Sign out?")
        builder.setTitle("Warning !")
        builder.setCancelable(false)

        builder.setPositiveButton("No") { dialog, which ->
            dialog.cancel()

        }

        builder.setNegativeButton("Yes") { dialog, which ->
            signOut()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun signOut() {

        FirebaseAuth.getInstance().signOut()

        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()

        val toast = Toast.makeText(this, "Successfully Signed Out!!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 30, 50)
        toast.show()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}