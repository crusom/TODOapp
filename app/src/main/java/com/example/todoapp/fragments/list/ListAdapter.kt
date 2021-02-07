package com.example.todoapp.fragments.list


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.Note
import kotlinx.android.synthetic.main.custom_row.view.*
import java.util.*


open class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var noteList = emptyList<Note>()
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.custom_row,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    var showItems: Boolean = false

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = noteList[position]
        if(showItems){
            holder.itemView.tv_subject.maxLines = 20
            holder.itemView.tv_description.maxLines = 20
        }
        else{
            holder.itemView.tv_subject.maxLines = 3
            holder.itemView.tv_description.maxLines = 3
        }

        holder.itemView.tv_subject.text = currentItem.subject
        holder.itemView.tv_description.text = currentItem.description
        holder.itemView.tv_date.text = currentItem.date

        Log.d("List adapter reminder:", currentItem.reminder.toString())

        if(currentItem.reminder!=null){
            holder.itemView.iv_reminder.setImageResource(R.drawable.ic_notification)
            holder.itemView.iv_reminder.setColorFilter(Color.RED)
            holder.itemView.tv_reminder_date.text = currentItem.reminderDate
        }
        else{
            holder.itemView.iv_reminder.setImageResource(android.R.color.transparent)
            holder.itemView.tv_reminder_date.text = ""
        }

        holder.itemView.rowLayout.setOnClickListener{
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(note: List<Note>){
        this.noteList = note
        notifyDataSetChanged()
    }


    fun setRecyclerViewItemTouchListener(recyclerView: RecyclerView) {

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition

                swapItems(fromPos, toPos)
                return true
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {}


            fun swapItems(fromPosition: Int, toPosition: Int) {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(noteList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(noteList, i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
            }



        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


}

