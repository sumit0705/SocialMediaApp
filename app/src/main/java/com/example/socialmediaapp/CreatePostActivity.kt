package com.example.socialmediaapp

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.socialmediaapp.daos.PostDao

class CreatePostActivity : AppCompatActivity() {

    private lateinit var postButton: Button
    private lateinit var postInput: EditText
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postButton=findViewById(R.id.postButton)

        postInput=findViewById(R.id.postInput)

        postDao = PostDao()

        postButton.setOnClickListener {
            val input=postInput.text.toString().trim()
            if(input.isNotEmpty()) {
                postDao.addPost(input)
                val toast=Toast.makeText(this, "Post has been created",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM,30,50)
                toast.show()
                finish()

            }

            
        }
    }
}