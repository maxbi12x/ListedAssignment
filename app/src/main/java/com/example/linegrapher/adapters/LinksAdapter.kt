package com.example.linegrapher.adapters

import com.example.linegrapher.models.LinkModel
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.linegrapher.R
import com.example.linegrapher.constants.Constants
import com.example.linegrapher.databinding.LinkItemBinding

class LinksAdapter(
    private val dataSet: List<LinkModel>,
    private val linksClickListener: LinksClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<LinksAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LinkItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = LinkItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        with(viewHolder) {
            binding.apply {
                Glide.with(context).load(dataSet[position].original_image)
                    .placeholder(R.drawable.baseline_broken_image_24).into(binding.itemImage)
                linkText.text = dataSet[position].web_link
                name.text = dataSet[position].app
                date.text = Constants.formatDate(dataSet[position].created_at)
                clicks.text = dataSet[position].total_clicks.toString()

                linkText.setOnClickListener {
                    linksClickListener.linkOpenClicked(dataSet[position].web_link)
                }
                copyLink.setOnClickListener {
                    linksClickListener.linkCopyClicked(dataSet[position].web_link)
                }
            }

        }
    }

    override fun getItemCount() = dataSet.size
    interface LinksClickListener {
        fun linkCopyClicked(link: String)
        fun linkOpenClicked(link: String)
    }
}