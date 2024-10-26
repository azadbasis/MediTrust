package com.meditrust.findadoctor.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.firstOrNull
import  com.meditrust.findadoctor.R
import com.meditrust.findadoctor.home.data.model.Specialization
import com.meditrust.findadoctor.home.data.model.TopRatedDoctor
import com.meditrust.findadoctor.profile.data.model.Doctor

class ParentAdapter (private  var data: MutableList<List<Any>>, private val screenWidth: Int, private val listener: ChildAdapter.OnItemClickListener) : RecyclerView.Adapter<ParentAdapter.VerticalViewHolder>() {

    inner class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val tvViewAll: TextView = itemView.findViewById(R.id.tv_view_all)
        private val horizontalRecyclerView: RecyclerView =
            itemView.findViewById(R.id.horizontal_recycler_view)

        fun bind(items: List<Any>) {
            val layoutManager = when (items.firstOrNull()) {
                is TextItem -> LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                is Doctor -> GridLayoutManager(
                    itemView.context,
                    2 // Span count of 2 for two items per row
                )
                else -> LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }

            horizontalRecyclerView.layoutManager = layoutManager

            val horizontalAdapter = ChildAdapter(items, screenWidth, listener)
            horizontalRecyclerView.adapter = horizontalAdapter

            // Set the title and "View all" click listener based on the type of items
            when (items.firstOrNull()) {
               /* is Category -> {
                    title.text = "Categories"
                    tvViewAll.setOnClickListener {
                        // Handle "View all" click for categories
                        // listener.onViewAllCategoriesClick()
                    }
                }*/
                is Specialization -> {
                    title.text = "Specializations"
                    tvViewAll.visibility = View.GONE
                    tvViewAll.setOnClickListener {
                        // listener.onViewAllContactsClick() should be called on the listener instance
                        listener.onViewAllSpecializationsClick()

                        // If you want to handle "View all" click for recent customers, you can do it here
                    }
                }
                is Doctor -> {
                    title.text = "Recently listed"
                    tvViewAll.visibility = View.GONE
                    tvViewAll.setOnClickListener {
                        // Handle "View all" click for recently listed products
                            listener.onViewAllDoctorsClick()
                    }
                }

                is TopRatedDoctor -> { // New case for Top Rated Doctors
                    title.text = "Top Rated Doctors"
                    tvViewAll.visibility = View.GONE
                    tvViewAll.setOnClickListener {
                        // Handle "View all" click for top-rated doctors
                        listener.onViewAllTopRatedDoctorsClick()
                    }
                }

                is TextItem -> {
                    // title.text = ""//"Daily Cashflow"
                    title.visibility = View.GONE
                    tvViewAll.visibility = View.GONE
                    // No need to set a click listener for "View all" as it's not applicable for TextItem
                }

                else -> title.text = ""
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        // Inflate your layout and create view holder
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.home_list, parent, false)
        return VerticalViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size


    fun updateData(newData: List<List<Any>>) {
        data.clear()
        data.addAll(newData)

        notifyDataSetChanged()
    }

}

