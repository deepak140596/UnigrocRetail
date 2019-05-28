package com.avvnapps.unigrocretail.dashboard.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.avvnapps.unigrocretail.dashboard.CurrentOrdersFragment
import com.avvnapps.unigrocretail.dashboard.NewOrdersFragment
import com.avvnapps.unigrocretail.dashboard.ReadyOrdersFragment

/**
 * Created by Deepak Prasad on 29-12-2018.
 */

class DashboardPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return CurrentOrdersFragment()
            1 -> return NewOrdersFragment()
            2 -> return ReadyOrdersFragment()
        }

        return Fragment()
    }


    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Current Orders"
            1 -> "New Orders"
            2 -> "Ready"
            else -> "New Orders"
        }
    }
}
