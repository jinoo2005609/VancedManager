package com.vanced.manager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.vanced.manager.R
import com.vanced.manager.databinding.ViewSocialLinkBinding
import com.vanced.manager.model.LinkModel
import com.vanced.manager.ui.viewmodels.HomeViewModel

class LinkAdapter(
    private val context: Context,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    private val github = LinkModel(
        AppCompatResources.getDrawable(context, R.drawable.ic_github),
        GITHUB
    )

    private val website = LinkModel(
        AppCompatResources.getDrawable(context, R.drawable.ic_website),
        WEBSITE
    )

    val links = arrayOf(github, website)

    inner class LinkViewHolder(private val binding: ViewSocialLinkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val logo = binding.linkImage

        fun bind(position: Int) {
            binding.linkBg.setOnClickListener {
                viewModel.openUrl(context, links[position].linkUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = ViewSocialLinkBinding.inflate(LayoutInflater.from(context), parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(position)
        holder.logo.setImageDrawable(links[position].linkIcon)
    }

    override fun getItemCount(): Int = links.size

    companion object {
        const val GITHUB = "https://github.com/jinoo2005609/VancedManager"
        const val WEBSITE = "https://vanced.esherloon.com/"
    }

}