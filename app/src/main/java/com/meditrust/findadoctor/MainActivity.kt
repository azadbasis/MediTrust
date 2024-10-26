package com.meditrust.findadoctor

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.core.util.DatastoreUtil
import com.meditrust.findadoctor.core.util.PersistenceUtil
import com.meditrust.findadoctor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // Declare the binding variable
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var datastoreUtil: DatastoreUtil
    private lateinit var persistenceUtil: PersistenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        // Initialize the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.visibility = View.GONE
        setupNavigation()
        setupObject()
        setupAuthStateListener()

        // Handle navigation based on onboarding completion
        handleInitialNavigation()
    }

    private fun handleInitialNavigation() {
        if (!persistenceUtil.isOnboardingCompleted()) {
            // Navigate to Onboarding Fragment if not completed
            navController.navigate(R.id.action_homeFragment_to_onBoardingFragment)
        } else {
            // Navigate to Home Fragment if onboarding is completed
            navController.navigate(R.id.homeFragment)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment ,R.id.findBookFragment ,R.id.bookingsFragment ,R.id.messageFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                 //   fab.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
//                    fab.visibility = View.GONE
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupObject() {
        auth = FirebaseAuth.getInstance()
        datastoreUtil = DatastoreUtil.Companion.getInstance(this) // Initialize DatastoreUtil
        persistenceUtil= PersistenceUtil(this)
    }

    private fun setupAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun checkFirebaseAuthState() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.d(TAG, "onAuthStateChanged:signed_out")
            val destination = if (persistenceUtil.isOnboardingCompleted()) {
                R.id.loginFragment
            } else {
                R.id.onBoardingFragment
            }
            navController.navigate(destination)
        } else {
            Log.d(TAG, "onAuthStateChanged:${if (currentUser.isAnonymous) "guest_user" else "signed_in"}:${currentUser.uid}")
        }
    }

    override fun onResume() {
        super.onResume()
       checkFirebaseAuthState()
    }



}