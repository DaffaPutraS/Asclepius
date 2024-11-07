package com.dicoding.asclepius.view.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ListItemNewsBinding

class NewsAdapter(private val onItemClick: (String) -> Unit) : ListAdapter<ArticlesItem, NewsAdapter.NewsViewHolder>(NEWS_DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = ListItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindNewsData(getItem(position))
    }

    class NewsViewHolder(
        private val binding : ListItemNewsBinding,
        private val onItemClick: (String) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bindNewsData(news: ArticlesItem) {
            binding.textViewNewsTitle.text = news.title
            binding.textViewNewsDescription.text = news.description
            Glide.with(binding.imageViewNews.context)
                .load(news.urlToImage)
                .placeholder(R.drawable.not_found)
                .into(binding.imageViewNews)

            binding.root.setOnClickListener {
                onItemClick(news.url)
            }
        }
    }

    companion object {
        val NEWS_DIFF_CALLBACK: DiffUtil.ItemCallback<ArticlesItem> =
            object : DiffUtil.ItemCallback<ArticlesItem>() {
                override fun areItemsTheSame(
                    oldItem: ArticlesItem,
                    newItem: ArticlesItem
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: ArticlesItem,
                    newItem: ArticlesItem
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}