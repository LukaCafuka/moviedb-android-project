package hr.algebra.moviedb.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.moviedb.adapter.ItemAdapter
import hr.algebra.moviedb.databinding.FragmentItemsBinding
import hr.algebra.moviedb.framework.fetchItems
import hr.algebra.moviedb.model.Item

class ItemsFragment : Fragment() {

    private lateinit var binding: FragmentItemsBinding
    private lateinit var items: MutableList<Item>

    // inicijalizACIJSKA
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        items = requireContext().fetchItems()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.apply {
            adapter = ItemAdapter(requireContext(), items)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}