package com.davinciapp.amindfullquit.principles

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.davinciapp.amindfullquit.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class PrinciplesPagerFragment : Fragment() {

    //TODO: view model
    private var pos = 0

    //VIEW
    private lateinit var collapsingToolBar: CollapsingToolbarLayout
    private lateinit var toolbarImageView: ImageView
    private lateinit var descriptionView: TextView
    private lateinit var instructionsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_slide_principles, container, false)
        collapsingToolBar = rootView.findViewById(R.id.collapsing_toolbar_principles)
        toolbarImageView = rootView.findViewById(R.id.iv_toolbar_principles)
        descriptionView = rootView.findViewById(R.id.tv_description_principles)
        instructionsTextView = rootView.findViewById(R.id.tv_instructions_principles)

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.eb_garamond)
        rootView.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_principles).apply {
            setExpandedTitleTypeface(typeface)
            setCollapsedTitleTypeface(typeface)
        }

        //Get position from arg
        arguments?.getInt(POSITION_KEY)?.let { pos = it }

        //viewModel !
        val imageId: Int
        val toolBarTitle: String
        val description: String

        when (pos) {
            0 -> {
                toolBarTitle = getString(R.string.mindful_smoking)
                imageId = R.drawable.man_smoking
                description = getString(R.string.mindful_smoking_description)
                instructionsTextView.text = Html.fromHtml(getString(R.string.mindful_instructions))
            }
            1 -> {
                toolBarTitle = getString(R.string.rain)
                imageId = R.drawable.man_in_rain
                description = getString(R.string.rain_description)
                instructionsTextView.text = Html.fromHtml(getString(R.string.rain_instructions))
            }
            2 -> {
                toolBarTitle = getString(R.string.sitting_meditation)
                imageId = R.drawable.man_sitting
                description = getString(R.string.sitting_meditation_description)
                instructionsTextView.text = Html.fromHtml(getString(R.string.sitting_instructions))

            }
            else -> {
                toolBarTitle = getString(R.string.every_day_activities)
                imageId = R.drawable.man_walking
                description = getString(R.string.daily_activities_description)
                instructionsTextView.text = Html.fromHtml(getString(R.string.everyday_instructions))
            }
        }

        toolbarImageView.setImageResource(imageId)
        collapsingToolBar.title = toolBarTitle
        descriptionView.text = description
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