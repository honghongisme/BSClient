package com.example.picturerecognize.module.social.user.post

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentAdapter(fm : FragmentManager, fragmentArray : Array<Fragment>, titleArray : Array<String>) : FragmentPagerAdapter(fm) {

    var mFragments = fragmentArray
    var mTitles = titleArray

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }
}