package com.dicoding.asclepius.view.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.databinding.ListItemHistoryBinding

class HistoryAdapter : ListAdapter<CancerEntity, HistoryAdapter.HistoryViewHolder>(HISTORY_DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val binding = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindHistoryData(getItem(position))
    }

    class HistoryViewHolder(
        private val binding: ListItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindHistoryData(cancer: CancerEntity) {
            binding.textViewResultHistory.text = cancer.result

            val imageUri = Uri.parse(cancer.image)
            Glide.with(binding.imageViewHistory.context)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imageViewHistory)
        }
    }

    companion object {
        val HISTORY_DIFF_CALLBACK: DiffUtil.ItemCallback<CancerEntity> =
            object : DiffUtil.ItemCallback<CancerEntity>() {
                override fun areItemsTheSame(
                    oldItem: CancerEntity,
                    newItem: CancerEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CancerEntity,
                    newItem: CancerEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}