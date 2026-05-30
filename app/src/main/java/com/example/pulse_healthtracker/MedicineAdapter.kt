package com.example.pulse_healthtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

class MedicineAdapter(
    private var medicines: List<Medicine>,
    private val onToggleClick: (Medicine) -> Unit,
    private val onItemClick: (Medicine) -> Unit,
    private val onDeleteClick: (Medicine) -> Unit,
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMedicineName)
        val tvDose: TextView = view.findViewById(R.id.tvMedicineDose)
        val tvTime: TextView = view.findViewById(R.id.tvMedicineTime)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val ivToggle: ImageView = view.findViewById(R.id.ivToggle)
        val indicator: View = view.findViewById(R.id.indicator)
        val medicineCard: View = view.findViewById(R.id.medicineCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.tvName.text = medicine.pillName
        
        val doseText = if ((medicine.dose % 1.0) == 0.0) {
            medicine.dose.toInt().toString()
        } else {
            medicine.dose.toString()
        }
        holder.tvDose.text = String.format("%s pill(s) · %s", doseText, medicine.foodRelation)

        holder.tvTime.text = medicine.time

        val context = holder.itemView.context
        if (medicine.isTaken) {
            holder.tvStatus.text = context.getString(R.string.taken)
            holder.tvStatus.setTextColor(0xFF4ECDC4.toInt())
            holder.ivToggle.setImageResource(R.drawable.toggle_green)
            holder.indicator.setBackgroundResource(R.drawable.bg_indicator_green)
        } else {
            holder.tvStatus.text = context.getString(R.string.pending)
            holder.tvStatus.setTextColor(0xFFFFB347.toInt())
            holder.ivToggle.setImageResource(R.drawable.toggle_red)
            holder.indicator.setBackgroundResource(R.drawable.bg_indicator_orange)
        }

        holder.medicineCard.setOnClickListener {
            onItemClick(medicine)
        }

        holder.ivToggle.setOnClickListener {
            onToggleClick(medicine)
        }

        holder.medicineCard.setOnLongClickListener { view ->
            showPopupMenu(view, medicine)
            true
        }
    }

    private fun showPopupMenu(view: View, medicine: Medicine) {
        val popup = PopupMenu(view.context, view)
        popup.menu.add("Edit")
        popup.menu.add("Delete")
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Edit" -> onItemClick(medicine)
                "Delete" -> onDeleteClick(medicine)
            }
            true
        }
        popup.show()
    }

    override fun getItemCount() = medicines.size

    fun updateData(newMedicines: List<Medicine>) {
        medicines = newMedicines
        notifyDataSetChanged()
    }
}
