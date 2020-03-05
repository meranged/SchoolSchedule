package com.meranged.schoolschedule.ui.callsschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.convertIntTo00
import com.meranged.schoolschedule.database.TimeSlot
import com.meranged.schoolschedule.databinding.ListItemTimeSlotBinding

class  CallsScheduleAdapter(val clickListener: CallsScheduleListener): ListAdapter<TimeSlot, CallsScheduleAdapter.ViewHolder>(CallsScheduleDiffCallback()) {

    class ViewHolder private constructor (val binding: ListItemTimeSlotBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemTimeSlotBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: TimeSlot, clickListener: CallsScheduleListener) {

            binding.tslot = item

            val res = binding.root.context.resources

            binding.lessonTitle.text = res.getString(R.string.title_lesson, item.number)
            binding.lessonStart.text = "${convertIntTo00(item.startTimeHours)}:${convertIntTo00(item.startTimeMinutes)}"
            binding.lessonFinish.text = "${convertIntTo00(item.finishTimeHours)}:${convertIntTo00(item.finishTimeMinutes)}"

            binding.clickListener = clickListener
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)

    }

}

class CallsScheduleDiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
    override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return oldItem.timeslotId == newItem.timeslotId
    }

    override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return ((oldItem.startTimeHours != newItem.startTimeHours)
        or (oldItem.finishTimeHours != newItem.finishTimeHours)
        or (oldItem.finishTimeMinutes != newItem.finishTimeMinutes)
        or (oldItem.startTimeMinutes != newItem.startTimeMinutes)
        or (oldItem.number != newItem.number)
        or (oldItem.comment != newItem.comment)
        or (oldItem.subject_id != newItem.subject_id))

    }
}

class CallsScheduleListener(val clickListener: (ts: TimeSlot, ftype: Int) -> Unit) {

    fun onClick(slot: TimeSlot, ftype: Int) = clickListener(slot, ftype)

}

