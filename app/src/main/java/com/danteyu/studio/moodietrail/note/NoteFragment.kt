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
                "9527", 20200202, 1, listOf(), listOf(), "content",  null, "https://res.klook.com/images/fl_lossy.progressive,q_65/c_fill,w_1295,h_720,f_auto/w_80,x_15,y_15,g_south_west,l_klook_water/activities/qcnercaton9p23l4muav/%E8%B2%93%E9%A0%AD%E9%B7%B9%E5%92%96%E5%95%A1%E5%BB%B3-%E7%A7%8B%E8%91%89%E5%8E%9F.jpg",
                listOf()
            ), Note(
                "9526", 20200202, 1, listOf(), listOf(), "content", null, "https://storage.googleapis.com/petsmao-images/images/2019/10/5a56af32-1571795279-a7d32647e0a334efbedeb39fdbad225a.jpg",
                listOf()
            )
            , Note(
                "9528", 20200202, 1, listOf(), listOf(), "content",  null, "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AAE4gJC.img?h=0&w=720&m=6&q=60&u=t&o=f&l=f&x=812&y=306",
                listOf()
            )
            , Note(
                "9529", 20200202, 1, listOf(), listOf(), "content",  null, "https://lh4.googleusercontent.com/proxy/OrAlPhVCUS-Y_6_w7jMqmCm4S__B5t4T7CEQS2k3DPsDyV8nD9SGpWIkshtAdmesP1AKBTnTOWB4HMGpN3eNJDcV_R0NzqoSS7nSp02vZQ",
                listOf()
            )
            , Note(
                "9525", 20200202, 1, listOf(), listOf(), "content",  null, "https://jpninfo.com/wp-content/uploads/sites/2/2018/03/Fotolia_98500332_Subscription_Monthly_M.jpg",
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