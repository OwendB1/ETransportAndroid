package com.example.etransportandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.etransportandroid.fragments.CommercialBookingFragment
import com.example.etransportandroid.fragments.HomeFragment
import com.example.etransportandroid.fragments.PrivateBookingFragment
import com.example.etransportandroid.fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var currentFragment: Fragment = HomeFragment()
    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(findViewById<ConstraintLayout>(R.id.container) != null) {
            if(savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .commit()
            }
        }
        mAuth = FirebaseAuth.getInstance()

        // Write a message to the database
        database = FirebaseDatabase.getInstance()

        Log.d("MainActivity", "User id is ${mAuth.currentUser?.uid}")
        
        setupMenuButtons()
    }
    
    private fun setupMenuButtons(){
        booking_button.setOnClickListener {
            if(currentFragment != CommercialBookingFragment()) {
                var isDone = false
                val dbRef = database.reference
                val postListener = object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!isDone) {
                            val groupId = snapshot.child("users").child(mAuth.currentUser?.uid.toString()).child("groupID").value.toString()
                            if(groupId == "Commercial") {
                                addFragmentToActivity(CommercialBookingFragment())
                            } else if (groupId == "Private") {
                                addFragmentToActivity(PrivateBookingFragment())
                            }
                            isDone = true
                        }
                    }
                    override fun onCancelled(error: DatabaseError) { }
                }
                dbRef.addValueEventListener(postListener)
            }
        }

        home_button.setOnClickListener {
            if(currentFragment != HomeFragment()){
                addFragmentToActivity(HomeFragment())
            }
        }

        settings_button.setOnClickListener {
            if(currentFragment != SettingsFragment()) {
                addFragmentToActivity(SettingsFragment())
            }
        }
    }

    fun addFragmentToActivity(fragment: Fragment){
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.replace(R.id.container, fragment)
        tr.commit()
        currentFragment = fragment
    }

    fun loadStartActivity() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }
}