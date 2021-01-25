package com.example.todoapp.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.User
import com.example.todoapp.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.custom_row.view.rowLayout
import kotlinx.android.synthetic.main.custom_row.view.tv_date
import kotlinx.android.synthetic.main.custom_row.view.tv_description
import kotlinx.android.synthetic.main.custom_row.view.tv_subject
import java.util.*


open class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var userList = emptyList<User>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}



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
        return userList.size
    }

    var showItems: Boolean = false

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        if(showItems){
            holder.itemView.tv_subject.maxLines = 30
            holder.itemView.tv_description.maxLines = 30
        }

        else{
            holder.itemView.tv_subject.maxLines = 1
            holder.itemView.tv_description.maxLines = 1
        }

        holder.itemView.tv_subject.text = currentItem.subject
        holder.itemView.tv_description.text = currentItem.description
        holder.itemView.tv_date.text = currentItem.date

        holder.itemView.rowLayout.setOnClickListener{

            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(user: List<User>){
        this.userList = user
        notifyDataSetChanged()
    }


    fun setRecyclerViewItemTouchListener(recyclerView: RecyclerView, mUserViewModel: UserViewModel) {

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition

                swapItems(fromPos, toPos)
                return true// true if moved, false otherwise
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                val position = viewHolder.adapterPosition
                mUserViewModel.deleteUser(userList[position])

                notifyDataSetChanged()
            }


            fun swapItems(fromPosition: Int, toPosition: Int) {
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(userList, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(userList, i, i - 1)
                    }
                }
                notifyItemMoved(fromPosition, toPosition)
            }



        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


}


