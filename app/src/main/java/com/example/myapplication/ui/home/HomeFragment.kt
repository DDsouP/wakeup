package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.MemoryAdapter

import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var adapter: MemoryAdapter

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
            val layoutManager = GridLayoutManager(activity,2)
            recyclerview_home.layoutManager = layoutManager
            adapter = MemoryAdapter(this, imagesUri)
            recyclerview_home.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val layoutManager = GridLayoutManager(activity,2)
        recyclerview_home.layoutManager = layoutManager
        adapter = MemoryAdapter(this, imagesUri)
        recyclerview_home.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}