package com.meditrust.findadoctor.bookings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.ListItemBookedDoctorBinding
import com.meditrust.findadoctor.databinding.ListItemFindBookDoctorBinding
import com.meditrust.findadoctor.home.DoctorDiffCallback
import com.meditrust.findadoctor.profile.data.model.Doctor
import kotlin.collections.filter
import kotlin.text.contains
import kotlin.text.isNullOrEmpty

class BookedDoctorsAdapter(

) : ListAdapter<Doctor, BookedDoctorsAdapter.ProductViewHolder>(DoctorDiffCallback()) {

    private var originalList = listOf<Doctor>()

    inner class ProductViewHolder(private val binding: ListItemBookedDoctorBinding) :
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

                    } else {
                        // Item is not selected, so select it
                        selectedItems.add(product)

                    }

                }
            }
        }

        fun bind(doctor: Doctor) {
            binding.tvDoctorName.text = doctor.name
            binding.tvDoctorSpecialization.text = doctor.specialization
            binding.stockLabel.text = "${doctor.clinic_name}"
            // Assuming imgAddNew is the ImageView inside your layout
            // Glide.with(binding.imgAddNew.context).load(product.imageUrl).into(binding.imgAddNew)
            Glide.with(itemView.context)
                .load(doctor.profile_image)
                .placeholder(R.drawable.ic_profile_avatar)
                .into(binding.imgAddNew)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemBookedDoctorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnDoctorClickListener {
        fun onDoctorClick(product: Doctor)
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

