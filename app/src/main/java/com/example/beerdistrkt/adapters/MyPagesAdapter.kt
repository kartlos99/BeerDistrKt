package com.example.beerdistrkt.adapters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.beerdistrkt.fragPages.amonaweri.AmonaweriSubPageFrag
import com.example.beerdistrkt.utils.LOCATION
import com.example.beerdistrkt.utils.OBJ_ID


class MyPagesAdapter(val fm: FragmentManager, private val objId: Int) :
    androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var titles =
        arrayOf(" დავალიანება ", " კასრები ")

    private val mPagerFragments: MutableList<AmonaweriSubPageFrag> = ArrayList()

    fun getDataFromAdapter(): List<AmonaweriSubPageFrag> {
        return mPagerFragments
    }

    override fun getItem(position: Int): AmonaweriSubPageFrag {
        val bundle = Bundle()
        bundle.putInt(LOCATION, position)
        bundle.putInt(OBJ_ID, objId)
        val amonSubPageFrag = AmonaweriSubPageFrag.newInstance()
        amonSubPageFrag.arguments = bundle
        return amonSubPageFrag
    }

    override fun getCount(): Int {
        return 2
    }

    fun setTitles(titles: Array<String>) {
        this.titles = titles
    }

    val fragmentM: AmonaweriSubPageFrag
        get() = fm.fragments[0] as AmonaweriSubPageFrag

    val fragmentK: AmonaweriSubPageFrag
        get() = fm.fragments[1] as AmonaweriSubPageFrag

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

}
