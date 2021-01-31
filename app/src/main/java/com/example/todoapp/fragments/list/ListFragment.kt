package com.example.todoapp.fragments.list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.viewmodel.UserViewModel


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding
        get() = _binding!!


    private val mUserViewModel: UserViewModel by viewModels()
    private val adapter = ListAdapter()
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentListBinding.inflate(layoutInflater, container, false)
        val settings: SharedPreferences? = activity?.getSharedPreferences("settings", 0)

        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter

        val layoutBoolean = settings?.getBoolean("gridCheckbox", false)
        val showBoolean = settings?.getBoolean("showCheckbox", false)


        if(layoutBoolean == true){
            recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        }
        else{
            recyclerView.layoutManager = StaggeredGridLayoutManager(1,LinearLayoutManager.VERTICAL)
        }

        adapter.showItems = showBoolean == true


        mUserViewModel.readAllData.observe(viewLifecycleOwner, { user ->
            adapter.setData(user)
        })

        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setHasOptionsMenu(true)

        adapter.setRecyclerViewItemTouchListener(recyclerView, mUserViewModel)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onDestroy() {
        mUserViewModel.update(ListAdapter().userList)
        super.onDestroy()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val settings: SharedPreferences? = activity?.getSharedPreferences("settings", 0)
        val editor = settings?.edit()

        if(item.itemId==R.id.menu_delete_all_notes){
            deleteAllNotes()
        }

        if (item.itemId == R.id.menu_show){
            if(item.isChecked){
                adapter.showItems= false
                item.isChecked = false
            }
            else{
                adapter.showItems = true
                item.isChecked = true
            }
            editor?.putBoolean("showCheckbox", item.isChecked)
            editor?.apply()
            adapter.notifyDataSetChanged()
            return true
        }


        if(item.itemId == R.id.menu_change_layout){
            if(item.isChecked){
                recyclerView.layoutManager = StaggeredGridLayoutManager(1,LinearLayoutManager.VERTICAL)
                item.isChecked = false
            }
            else{
                recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
                item.isChecked = true
            }
            editor?.putBoolean("gridCheckbox", item.isChecked)
            editor?.apply()
            adapter.notifyDataSetChanged()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    private fun deleteAllNotes(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _->
            mUserViewModel.deleteAllUsers()
            Toast.makeText(requireContext(), "Successfully removed", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){ _, _-> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure?")
        builder.create().show()

    }


    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        //Checkbox
        val settings = activity?.getSharedPreferences("settings", 0)

        val isShowChecked = settings?.getBoolean("showCheckbox", false)
        val isChangeLayoutChecked = settings?.getBoolean("gridCheckbox", false)

        val itemMenuShow = menu.findItem(R.id.menu_show)
        val itemChangeLayout = menu.findItem(R.id.menu_change_layout)

        if (isShowChecked != null) {
            itemMenuShow.isChecked = isShowChecked
        }

        if(isChangeLayoutChecked != null) {
            itemChangeLayout.isChecked = isChangeLayoutChecked
        }
        //Search
        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as SearchView

        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onQueryTextSubmit(query: String): Boolean {
        searchDatabase(query)
        return true
    }

    override fun onQueryTextChange(query: String): Boolean {
        searchDatabase(query)
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        mUserViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, { list ->
            list.let {
                Log.d("SEARCH", it.toString())
                adapter.setData(it)
            }
        })
    }

}

