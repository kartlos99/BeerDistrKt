package com.example.beerdistrkt.adapters

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.statement.StatementSubPageFragment
import com.example.beerdistrkt.utils.LOCATION
import com.example.beerdistrkt.utils.OBJ_ID


class MyPagesAdapter(val fm: FragmentManager, private val objId: Int) :
    androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var titles = arrayOf(" ფინანსები ", " კასრები ")

    private val mPagerFragments: MutableList<StatementSubPageFragment> = ArrayList()

    fun getDataFromAdapter(): List<StatementSubPageFragment> {
        return mPagerFragments
    }

    override fun getItem(position: Int): StatementSubPageFragment {
        val bundle = bundleOf(
            LOCATION to position,
            OBJ_ID to objId,
        )
        return StatementSubPageFragment.newInstance().apply {
            arguments = bundle
        }
    }

    override fun getCount(): Int {
        return 2
    }

    fun setTitles(titles: Array<String>) {
        this.titles = titles
    }

    val fragmentM: StatementSubPageFragment
        get() = fm.findFragmentByTag(makeFragmentTag(0))
                as StatementSubPageFragment

    val fragmentK: StatementSubPageFragment
        get() = fm.findFragmentByTag(makeFragmentTag(1))
                as StatementSubPageFragment

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    private fun makeFragmentTag(id: Long): String? {
        return "android:switcher:${R.id.statement_viewpager}:$id"
    }
}
