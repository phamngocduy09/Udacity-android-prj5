package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private val viewModel by lazy { VoterInfoViewModel(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        // TODO: Add ViewModel values and create ViewModel
        val arguments = VoterInfoFragmentArgs.fromBundle(requireArguments())
        // TODO: Add binding values
        viewModel.findElection(arguments.argElectionId)
        binding.data = viewModel
        // TODO: Populate voter info -- hide views without provided data.

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        // TODO: Handle loading of URLs

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }

    // TODO: Create method to load URL intents
}