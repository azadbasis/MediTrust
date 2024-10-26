package com.meditrust.findadoctor.home
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.meditrust.findadoctor.profile.data.model.Doctor

class DoctorDiffCallback : ItemCallback<Doctor>() {
    override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
        return oldItem.doctor_id == newItem.doctor_id
    }


    override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
        return oldItem == newItem
    }


}