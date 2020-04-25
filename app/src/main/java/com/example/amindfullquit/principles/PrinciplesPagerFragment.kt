package com.example.amindfullquit.principles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.amindfullquit.R

class PrinciplesPagerFragment: Fragment() {

    //TODO: view model
    private var pos = 0

    //VIEW
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_slide_principles, container, false)
        textView = rootView.findViewById(R.id.tv_principle)

        //Get position from arg
        arguments?.getInt(POSITION_KEY)?.let { pos = it }

        //viewModel !
        when (pos) {
            0 -> textView.text = "Smoke"
            1 -> textView.text = "Rain"
            2 -> textView.text = "Sit"
            else -> textView.text = "EveryDay"
        }

        return rootView
    }

    companion object {
        private const val POSITION_KEY = "pager_position"

        fun newInstance(position: Int): PrinciplesPagerFragment {
            val frag = PrinciplesPagerFragment()

            val arg = Bundle()
            arg.putInt(POSITION_KEY, position)
            frag.arguments = arg

            return frag
        }
    }
}