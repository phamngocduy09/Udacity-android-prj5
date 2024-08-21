package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    // TODO: Declare ViewModel
    private val navController by lazy { findNavController() }
    private lateinit var binding: FragmentElectionBinding
    private val viewModel: ElectionsViewModel by lazy {
        ElectionsViewModel(requireActivity().application)
    }
    private val aptUpcoming by lazy { ElectionListAdapter(navController) }
    private val aptSaved by lazy { ElectionListAdapter(navController) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View {
        binding = FragmentElectionBinding.inflate(inflater)

        binding.data = viewModel
        binding.rcvUpcoming.apply {
            adapter = aptUpcoming
        }
        binding.rcvSaveElection.apply {
            adapter = aptSaved
        }
        viewModel.upcomingData.observe(viewLifecycleOwner) {
            aptUpcoming.submitList(it)
        }
        viewModel.savedData.observe(viewLifecycleOwner) {
            aptSaved.submitList(it)
        }
        return binding.root
    }
}