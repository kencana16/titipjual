package com.kencana.titipjual

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.kencana.titipjual.data.UserPreferences
import com.kencana.titipjual.databinding.ActivityMainBinding
import com.kencana.titipjual.ui.auth.AuthActivity
import com.kencana.titipjual.utils.BASE_IMG_URL
import com.kencana.titipjual.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    @Inject
    lateinit var userPreferences: UserPreferences
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.penjualanFragment, R.id.pesananFragment, R.id.productFragment, R.id.penjualFragment
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log out?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { _, _ -> performLogout() }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
            true
        }

        updateUI()

        val header: View = binding.navView.getHeaderView(0)
        val photo = header.findViewById<ImageView>(R.id.iv_user_photo)
        val name = header.findViewById<TextView>(R.id.tv_user_name)
        val contact = header.findViewById<TextView>(R.id.tv_user_contact)
        viewModel.user.observe(
            this,
            {
                Glide.with(photo)
                    .load(BASE_IMG_URL + it.foto)
                    .circleCrop()
                    .error(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_baseline_broken_image_24,
                            null
                        )
                    )
                    .into(photo)
                name.text = it.username
                contact.text = it.noHp
            }
        )
    }

    private fun updateUI() {
        viewModel.getUser()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchevent")
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun performLogout() = lifecycleScope.launch {
        viewModel.logout()
        userPreferences.clear()
        startNewActivity(AuthActivity::class.java)
    }

    override fun onBackPressed() = when {
        R.id.penjualanFragment == navController.currentDestination?.id -> {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { _, _ -> exitProcess(0) }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }

        else -> super.onBackPressed()
    }
}
