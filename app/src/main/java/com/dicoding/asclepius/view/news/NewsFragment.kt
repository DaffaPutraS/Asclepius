package com.dicoding.asclepius.view.news

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {

    private var _binding : FragmentNewsBinding? = null
    private lateinit var adapter: NewsAdapter
    private lateinit var viewModel: NewsViewModel
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, NewsFactory.getFactoryInstance(requireContext()))[NewsViewModel::class.java]

        adapter = NewsAdapter { newsUrl ->
            val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(newsUrl))
            startActivity(browserIntent)
        }

        binding.rvNews.adapter = adapter
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext())

        observeNewsData()
    }

    private fun observeNewsData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoadingIndicator(isLoading)
        }
        viewModel.listNews.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
        }
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}