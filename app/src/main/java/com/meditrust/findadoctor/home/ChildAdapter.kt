package com.meditrust.findadoctor.home

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.meditrust.findadoctor.R
import com.meditrust.findadoctor.databinding.ItemTransactionStatusBinding
import com.meditrust.findadoctor.databinding.ListItemRecentDoctorBinding
import com.meditrust.findadoctor.databinding.ListItemSpecializationBinding
import com.meditrust.findadoctor.databinding.ListItemTopRatedDoctorBinding
import com.meditrust.findadoctor.home.data.model.Specialization
import com.meditrust.findadoctor.home.data.model.TopRatedDoctor
import com.meditrust.findadoctor.profile.data.model.Doctor
import java.util.Random
import kotlin.text.first
import kotlin.text.format

class ChildAdapter(
    private val items: List<Any>,
    private val screenWidth: Int,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//Specializations
    companion object {
        private const val TYPE_SPECIALIZATION = 2
    private const val TYPE_TOP_RATED_DOCTOR = 1
    private const val TYPE_DOCTOR = 0
    private const val TYPE_FEATURE = 3
    }

    // Define an interface for handling item clicks
    interface OnItemClickListener {
        fun onDoctorClick(doctor: Doctor)
        fun onTopDoctorClick(doctor: TopRatedDoctor)
        fun onSpecializationClick(tradeContact: Specialization, colorHexCode: String)
        fun onViewAllDoctorsClick()
        fun onViewAllTopRatedDoctorsClick()
        fun onViewAllSpecializationsClick()
        fun onAddToCartClick(doctor: Doctor)
        fun onAddToCartClick(doctor: TopRatedDoctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SPECIALIZATION -> {
                val binding = ListItemSpecializationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SpecializationViewHolder(binding, listener, items)
            }
            TYPE_DOCTOR -> {
                val binding =
                    ListItemRecentDoctorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                DoctorsViewHolder(binding, listener, items)
            }
            TYPE_TOP_RATED_DOCTOR -> {
                val binding =
                    ListItemTopRatedDoctorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                TopRatedDoctorsViewHolder(binding, listener, items)
            }

            TYPE_FEATURE -> {
                //  val binding = ItemDailyCashFlowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val binding =
                    ItemTransactionStatusBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                TextViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Doctor -> {
                // Correct casting for DoctorsViewHolder
                (holder as DoctorsViewHolder).bind(item)
            }
            is TopRatedDoctor -> {
                // Correct casting for TopRatedDoctorsViewHolder
                (holder as TopRatedDoctorsViewHolder).apply {
                    val layoutParams = itemView.layoutParams
                    layoutParams.width = screenWidth / 2 - 40
                    itemView.layoutParams = layoutParams
                    bind(item)
                }
            }
            is Specialization -> {
                // Correct casting for SpecializationViewHolder
                (holder as SpecializationViewHolder).bind(item)
            }
            is TextItem -> {
                // Correct casting for TextViewHolder
                (holder as TextViewHolder).bind(item)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Doctor -> TYPE_DOCTOR
            is TopRatedDoctor -> TYPE_TOP_RATED_DOCTOR
            is Specialization -> TYPE_SPECIALIZATION
            is TextItem -> TYPE_FEATURE
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }


    override fun getItemCount() = items.size
}

class TextViewHolder(
    private val binding: ItemTransactionStatusBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(textItem: TextItem) {
    }
}

class DoctorsViewHolder(
    private val binding: ListItemRecentDoctorBinding,
    private val listener: ChildAdapter.OnItemClickListener,
    private val items: List<Any>
) : RecyclerView.ViewHolder(binding.root) {

    // List to keep track of selected items
    private val selectedItems = mutableListOf<Doctor>()

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onDoctorClick(items[position] as Doctor)
            }
        }
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (selectedItems.contains(items[position])) {
                    // Item is already selected, so deselect it
                    selectedItems.remove(items[position])
                    binding.imgBookmarkDoctor.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_add
                        )
                    )
                } else {
                    // Item is not selected, so select it
                    selectedItems.add(items[position] as Doctor)
                    binding.imgBookmarkDoctor.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_check
                        )
                    )
                    listener.onAddToCartClick(items[position] as Doctor)
                }
            }
        }
    }

    fun bind(doctor: Doctor) {
        val specializationExperience = "${doctor.specialization}. Experiance ${doctor.experience} years"
        binding.tvDoctorRating.text = "${doctor.average_rating} (${doctor.ratings_count} reviews)"
        binding.tvDoctorName.text = doctor.name
        binding.tvCategoryName.text = specializationExperience
        Glide.with(itemView.context)
            .load(doctor.profile_image)
            .placeholder(R.drawable.ic_profile_avatar)
            .into(binding.imgDoctor)
    }
}
class TopRatedDoctorsViewHolder(
    private val binding: ListItemTopRatedDoctorBinding,
    private val listener: ChildAdapter.OnItemClickListener,
    private val items: List<Any>
) : RecyclerView.ViewHolder(binding.root) {
    // List to keep track of selected items
    private val selectedItems = mutableListOf<TopRatedDoctor>()

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onTopDoctorClick(items[position] as TopRatedDoctor)
            }
        }
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (selectedItems.contains(items[position])) {
                    // Item is already selected, so deselect it
                    selectedItems.remove(items[position])
                    binding.imgAddItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_check
                        )
                    )
                } else {
                    // Item is not selected, so select it
                    selectedItems.add(items[position] as TopRatedDoctor)
                    binding.imgAddItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_bookmark
                        )
                    )
                    listener.onAddToCartClick(items[position] as TopRatedDoctor)
                }
            }
        }
    }

    fun bind(doctor: TopRatedDoctor) {
        val topRatedDoctor = doctor.doctor
        binding.tvDoctorName.text = topRatedDoctor.name
        binding.tvDoctorSpecialization.text = topRatedDoctor.specialization
        binding.stockLabel.text = topRatedDoctor.clinic_name
        Glide.with(itemView.context)
            .load(topRatedDoctor.profile_image)
            .placeholder(R.drawable.ic_profile_avatar)
            .into(binding.imgAddNew)
    }
}

class SpecializationViewHolder(
    private val binding: ListItemSpecializationBinding,
    private val listener: ChildAdapter.OnItemClickListener,
    private val items: List<Any>
) : RecyclerView.ViewHolder(
    binding.root
) {
    private var colorBackground: Int? = null

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val hexColor = colorBackground?.let { bgColor ->
                    String.format("#%06X", (0xFFFFFF and bgColor))
                } ?: "#FFFFFF"
                Log.d("TAG", "colorBackground:$hexColor ")
                listener.onSpecializationClick(items[position] as Specialization, hexColor)
            /*    val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_check)
                binding.tvProductName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, drawable)
                binding.tvProductName.compoundDrawablePadding = -15*/
            }
        }
    }

    fun bind(tradeContact: Specialization) {
        binding.tvProductName.text = tradeContact.name
        val position = adapterPosition
        if (position == 0) {
            binding.imgAddNew.visibility = View.VISIBLE
            binding.customerTile.visibility = View.INVISIBLE
        } else {
            binding.imgAddNew.visibility = View.VISIBLE
            binding.customerTile.visibility = View.INVISIBLE
            val firstLetter = tradeContact.name.first().toString()
            binding.customerTile.text = firstLetter
        }
         
         
        binding.imgAddNew.setImageDrawable(
            ContextCompat.getDrawable(
                itemView.context,
                tradeContact.imageResId
            )
        )

        val colorSets = listOf(
            Pair(
                R.color.md_theme_primary_mediumContrast,
                R.color.md_theme_onPrimary_mediumContrast
            ),
            Pair(
                R.color.md_theme_errorContainer_highContrast,
                R.color.md_theme_onPrimary_mediumContrast
            ),
            Pair(
                R.color.md_theme_tertiary_mediumContrast,
                R.color.md_theme_onPrimary_mediumContrast
            ),
            Pair(R.color.md_theme_tertiaryContainer, R.color.md_theme_onPrimary_mediumContrast),
            Pair(R.color.md_theme_onErrorContainer, R.color.md_theme_onPrimary_mediumContrast)
        )

        // Select a random color set
        val rnd = Random()
        val colorSet = colorSets[rnd.nextInt(colorSets.size)]

        // Get the colors from the resources
        val colorBackground = ContextCompat.getColor(itemView.context, colorSet.first)
        val colorText = ContextCompat.getColor(itemView.context, colorSet.second)

        // Set the colors to the TextViews
        binding.customerTile.setTextColor(colorText)

        // Get the shape drawable and set the color
        val shapeDrawable = binding.customerTile.background as GradientDrawable
        shapeDrawable.setColor(colorBackground)
        binding.imgAddNew.setBackgroundColor(colorBackground)

        // Store colorBackground somewhere accessible later
        this.colorBackground = colorBackground
    }
}
