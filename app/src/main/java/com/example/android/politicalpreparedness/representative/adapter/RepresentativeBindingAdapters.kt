package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.VoterInfoFragmentArgs
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_profile))
            .circleCrop()
            .into(view)
    }
}

@BindingAdapter("electionInfoTitle")
fun bindElectionInfoTitleText(view: TextView, voterInfo: VoterInfo?) {
    voterInfo?.run {
        view.text = view.resources.getString(R.string.election_info_text, stateName)
    }
}


@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T>{
    return adapter as ArrayAdapter<T>
}
