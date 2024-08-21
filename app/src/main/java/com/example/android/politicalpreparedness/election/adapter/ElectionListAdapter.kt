package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.election.ElectionsFragmentDirections
import com.example.android.politicalpreparedness.network.models.ElectionDB

class ElectionListAdapter(private val navController: NavController) :
    ListAdapter<ElectionDB, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.election_item,
                parent,
                false
            ),
            navController
        )
    }

    class ElectionViewHolder(
        private val binding: ElectionItemBinding,
        private val navController: NavController
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ElectionDB) {
            binding.data = data
            binding.layoutItemElection.setOnClickListener {
                navController.navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        data.id,
                        data.division
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // TODO: Add companion object to inflate ViewHolder (from)

    // TODO: Create ElectionDiffCallback
    companion object ElectionDiffCallback : DiffUtil.ItemCallback<ElectionDB>() {
        override fun areItemsTheSame(oldItem: ElectionDB, newItem: ElectionDB): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ElectionDB, newItem: ElectionDB): Boolean {
            return oldItem.id == newItem.id
        }
    }
}