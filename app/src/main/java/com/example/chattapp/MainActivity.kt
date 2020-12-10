package com.example.chattapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.chattapp.fragments.FriendsListFragment
import com.example.chattapp.fragments.SearchFragment
import com.example.chattapp.fragments.SettingsFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_settings.*


//test


class MainActivity : AppCompatActivity() {
    private lateinit var userRef: CollectionReference
    private lateinit var firebaseUserID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUserDataBase()
//Get currentUserProfile's Uri and show it with Glide by the help of showImage function
        val currentUserDocument= userRef.document(firebaseUserID)
        lateinit var currentUserProfile:String
        currentUserDocument.get()
            .addOnSuccessListener{
                if (it != null) {
                    //ger value of field "profile"
                    currentUserProfile=it.data?.get("profile") as String
                    Log.d("TAG", "profilesUri is $currentUserProfile")
                    //must be here, if the data is back from server in USA,can show, otherwise has the problem of initialization.
                    showImage(currentUserProfile.toUri() ,profile_image)
                }else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        tool_bar_title.text = "${FirebaseAuth.getInstance().currentUser?.email}"
        setSupportActionBar(toolbar_main)
        supportActionBar!!.title = ""



        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(FriendsListFragment(), "Chats")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
        view_pager.adapter = viewPagerAdapter
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_signOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }

        }
        return false
    }
    private fun initUserDataBase() {
        val db = Firebase.firestore
        userRef = db.collection("users")
        firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
    }
    //"showImage" function : To load the uri in the position - imageView.
    private fun showImage(uri: Uri, imageView: ImageView) {
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
    //Adapter to show these three Fragments, similar as RecyclerView.(Have changed the deprecated one from the tutorial, suggestions from "stack overflow")
    class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }
    }
}

