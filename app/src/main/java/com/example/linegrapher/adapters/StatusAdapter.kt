package com.example.linegrapher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.linegrapher.databinding.DailyItemBinding
import com.example.linegrapher.models.DailyItemModel

class StatusAdapter(private val dataSet: ArrayList<DailyItemModel>) :
    RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DailyItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        with(viewHolder) {
            binding.apply {
                image.setImageResource(dataSet[position].image)
                aboutDetails.text = dataSet[position].detail
                detail.text = dataSet[position].data
            }

        }
    }

    override fun getItemCount() = dataSet.size

}