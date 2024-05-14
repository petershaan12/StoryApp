package com.petershaan.storyapp.view.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.petershaan.storyapp.R
import com.petershaan.storyapp.data.ResultState
import com.petershaan.storyapp.data.remote.response.StoryItem
import com.petershaan.storyapp.databinding.ActivityMainBinding
import com.petershaan.storyapp.view.ViewModelFactory
import com.petershaan.storyapp.view.adapter.LoadingStateAdapter
import com.petershaan.storyapp.view.adapter.StoryAdapter
import com.petershaan.storyapp.view.detail.DetailActivity
import com.petershaan.storyapp.view.maps.MapsActivity
import com.petershaan.storyapp.view.upload.CameraActivity
import com.petershaan.storyapp.view.upload.CameraActivity.Companion.CAMERAX_RESULT
import com.petershaan.storyapp.view.welcome.WelcomeActivity


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        storyAdapter = StoryAdapter()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        setRefresh()
        getRecycle()
        onClickCallback()
        binding.addStory.setOnClickListener { startCameraX() }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.btn_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logoutButton -> {
                viewModel.logout()
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickCallback() {
        storyAdapter.setOnItemCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryItem) {
                val intent = Intent(    this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun setRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun getRecycle() {
        storyAdapter = StoryAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }
        viewModel.story.observe(this) {
            storyAdapter.submitData(lifecycle, it)

//            result ->
//            when (result) {
//                is ResultState.Error -> {
//                    showLoading(false)
//                    Toast.makeText(this@MainActivity, result.error, Toast.LENGTH_SHORT).show()
//                    binding.swipeRefresh.isRefreshing = false
//                }
//                is ResultState.Loading -> { showLoading(true) }
//                is ResultState.Success -> {
//                    showLoading(false)
//                    binding.swipeRefresh.isRefreshing = false
//                    storyAdapter.submitList(result.data)
//                }
//            }
        }
    }

//    private fun setUpViewModel() {
//        viewModel.getAllStory2().observe(this) { result ->
//            when (result) {
//                is ResultState.Error -> {
//                    showLoading(false)
//                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
//                    binding.swipeRefresh.isRefreshing = false
//                }
//                is ResultState.Loading -> { showLoading(true) }
//                is ResultState.Success -> {
//                    showLoading(false)
//                    binding.swipeRefresh.isRefreshing = false
//                }
//            }
//        }
//    }


    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
        }
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}