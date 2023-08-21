package com.example.practicalappdev.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.practicalappdev.R
import com.example.practicalappdev.databinding.LocationItemBinding
import com.example.practicalappdev.models.Address
import com.example.practicalappdev.utils.CommonUtils


class LocationAdapter(
    private val context: Context, private val interactionListener: OnItemInteractionListener
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {
    private var response: ArrayList<Address> = ArrayList()

    class LocationViewHolder(val binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(LocationItemBinding.bind(view))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        with(holder.binding) {
            with(response[position]) {
                tvCity.text = city
                tvAddress.text = address

                if (isPrimary) {
                    tvPrimary.isVisible = true
                    ivDelete.isInvisible = true
                    tvDistance.isInvisible = true

                } else {
                    ivDelete.isVisible = true
                    tvPrimary.isGone = true
                    tvDistance.isVisible = true

                }

                tvDistance.text = "Distance: ${String.format("%.2f", distance)} KM"


            }

            ivEdit.setOnClickListener {
                interactionListener.onEditItem(position, response[position])
            }
            ivDelete.setOnClickListener {

                deleteDialog(position)



            }
        }
    }

    private fun deleteDialog(position: Int) {
        val dialog = Dialog(context)

        dialog.setContentView(R.layout.custom_delete_dailog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        val btnOK = dialog.findViewById<AppCompatButton>(R.id.buttonYes)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.buttonNo)

        btnOK.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            interactionListener.onDeleteItem(position, response[position])
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

        })

        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(address: List<Address>) {
        response.clear()
        response.addAll(address)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDistance(address: List<Address>) {
        response.clear()
        response.addAll(address)
        response.forEach {
            if (!it.isPrimary) {
                it.distance = CommonUtils.distance(
                    response[0].latitude, response[0].longitude, it.latitude, it.longitude
                )
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return response.size
    }

    interface OnItemInteractionListener {
        fun onEditItem(position: Int, response: Address)
        fun onDeleteItem(position: Int, response: Address)
    }
}