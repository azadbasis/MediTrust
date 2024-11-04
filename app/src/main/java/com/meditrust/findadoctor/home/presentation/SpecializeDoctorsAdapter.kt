package com.meditrust.findadoctor.home.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.ListItemSpecializationDoctorBinding
import com.meditrust.findadoctor.home.DoctorDiffCallback
import com.meditrust.findadoctor.profile.data.model.Doctor
import kotlin.collections.filter
import kotlin.text.contains
import kotlin.text.isNullOrEmpty

class SpecializeDoctorsAdapter(
    private val onProductClickListener: OnProductClickListener
) : ListAdapter<Doctor, SpecializeDoctorsAdapter.ProductViewHolder>(DoctorDiffCallback()) {

    private var originalList = listOf<Doctor>()

    inner class ProductViewHolder(private val binding: ListItemSpecializationDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val selectedItems = mutableListOf<Doctor>()

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = getItem(position)
                    if (selectedItems.contains(product)) {
                        // Item is already selected, so deselect it
                        selectedItems.remove(product)
                        binding.imgBookmarkDoctor.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.ic_check
                            )
                        )

                    } else {
                        // Item is not selected, so select it
                        selectedItems.add(product)
                        binding.imgBookmarkDoctor.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.ic_bookmark
                            )
                        )
                    }
                    onProductClickListener.onProductClick(product)
                }
            }
        }

        fun bind(doctor: Doctor) {
            //4.5(120 reviews)
            val specializationExperience = "${doctor.specialization}. Experiance ${doctor.experience} years"
            binding.tvDoctorRating.text = "${doctor.average_rating} (${doctor.ratings_count} reviews)"
            binding.tvDoctorName.text = doctor.name
            binding.tvCategoryName.text = specializationExperience
         //   binding.stockLabel.text = "${doctor.clinic_name}"
            Glide.with(itemView.context)
                .load(doctor.profile_image)
                .placeholder(R.drawable.ic_profile_avatar)
                .into(binding.imgDoctor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemSpecializationDoctorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnProductClickListener {
        fun onProductClick(product: Doctor)
    }

    override fun submitList(list: List<Doctor>?) {
        originalList = list ?: listOf()
        super.submitList(originalList)
    }

    fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(query, ignoreCase = true) }
        }
        super.submitList(filteredList)
    }
}

