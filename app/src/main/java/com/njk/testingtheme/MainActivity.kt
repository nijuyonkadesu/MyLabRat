package com.njk.testingtheme

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.njk.testingtheme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Request camera permissions
        if (allPermissionsGranted()) {
            // startCamera()
            // TODO replace with navigation
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        binding.apply {
            bottomNavigationView.selectedItemId = R.id.home
            bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.qr -> {
//                        FirstFragmentDirections.actionFirstFragmentToBarcodeScanningActivity()
                        val barcodeIntent = Intent(this@MainActivity, BarcodeScanningActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(barcodeIntent)
                        Toast.makeText(this@MainActivity, "rfid click detected", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.home -> {
                        Toast.makeText(this@MainActivity, "home click detected", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.ticket -> {
                        val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment()
                        navController.navigate(action)

                        Toast.makeText(this@MainActivity, "ticket click detected", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            bottomNavigationView.setOnItemReselectedListener{  }
        }
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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    // [START get permission]
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // startCamera()
                // TODO: replace with navigation
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val TAG = "BARCODE"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).toTypedArray()
    }
    // [END get permission]
}
// TODO: Navigation is too broken, fix it properly