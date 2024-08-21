package com.example.android.politicalpreparedness.utils

import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R

@BindingAdapter("buttonFollowText")
fun showButtonText(button: AppCompatButton, isElectionSaved: Boolean = false) {
    if (isElectionSaved) {
        button.text = button.resources.getString(R.string.unfollow_election_text)
    } else {
        button.text = button.resources.getString(R.string.follow_election_text)
    }
}