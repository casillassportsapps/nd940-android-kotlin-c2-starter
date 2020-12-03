package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapter.AsteroidAdapter
import com.udacity.asteroidradar.api.AsteroidApiStatus
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.PodDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val asteroidDoa = AsteroidDatabase.getDatabase(application).asteroidDao
        val podDoa = PodDatabase.getDatabase(application).podDao

        val factory = MainViewModelFactory(asteroidDoa, podDoa)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.imageOfDayStatus.observe(viewLifecycleOwner, { status ->
            if (status == AsteroidApiStatus.LOADING) {
                binding.activityMainImageOfTheDay.setImageResource(R.drawable.loading_animation)
                binding.activityMainImageOfTheDay.contentDescription =
                    getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
            } else if (status == AsteroidApiStatus.DONE) {
                val image = viewModel.imageOfDay()
                if (image?.isImage == true) {
                    Picasso.with(activity)
                        .load(image.url)
                        .error(R.drawable.ic_broken_image)
                        .into(binding.activityMainImageOfTheDay)
                    binding.activityMainImageOfTheDay.contentDescription = getString(
                        R.string.nasa_picture_of_day_content_description_format,
                        image.title
                    )
                } else {
                    binding.activityMainImageOfTheDay.setImageResource(R.drawable.placeholder_picture_of_day)
                    binding.activityMainImageOfTheDay.contentDescription =
                        getString(R.string.nasa_picture_of_day_unavailable)
                }
            }
        })

        binding.asteroidRecycler.adapter =
            AsteroidAdapter(AsteroidAdapter.OnClickListener { asteroid ->
                viewModel.displayAsteroidData(asteroid)
            })

        viewModel.asteroidStatus.observe(viewLifecycleOwner, { show ->
            binding.statusLoadingWheel.visibility =
                if (show == AsteroidApiStatus.LOADING) View.VISIBLE else View.GONE
        })

        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, { asteroid ->
            if (asteroid != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.displayAsteroidDataComplete()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.week -> viewModel.getAsteroids(AsteroidFilter.WEEK)
            R.id.today -> viewModel.getAsteroids(AsteroidFilter.TODAY)
            R.id.saved -> viewModel.getAsteroids(AsteroidFilter.SAVED)
        }
        return true
    }
}
