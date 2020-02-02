package com.danteyu.studio.moodietrail.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.databinding.FragmentNoteBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory

/**
 * Created by George Yu in Jan. 2020.
 */
class NoteFragment : Fragment() {

    /**
     * Lazily initialize [NoteViewModel]
     */
    private val viewModel by viewModels<NoteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = NoteAdapter(NoteAdapter.OnClickListener {})
        val noteList = listOf(
            Note(
                "9527", 20200202, 1, listOf(), listOf(), "title", "", null, "",
                listOf()
            ), Note(
                "9526", 20200202, 1, listOf(), listOf(), "title", "", null, "",
                listOf()
            )
            , Note(
                "9528", 20200202, 1, listOf(), listOf(), "title", "", null, "",
                listOf()
            )
            , Note(
                "9529", 20200202, 1, listOf(), listOf(), "title", "", null, "",
                listOf()
            )
            , Note(
                "9525", 20200202, 1, listOf(), listOf(), "title", "", null, "",
                listOf()
            )
        )


//        binding.recyclerNote.adapter = NoteAdapter(NoteAdapter.OnClickListener {})
        binding.recyclerNote.adapter = adapter
        adapter.submitList(noteList)

        binding.layoutSwipeRefreshNote.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(this, Observer {
            it?.let {
                binding.layoutSwipeRefreshNote.isRefreshing = it
            }
        })

        return binding.root
    }
}