package com.example.smixers.utils;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


public abstract class MyPagerAdapter extends FragmentStatePagerAdapter
{

    public boolean isEditDetails = false;

    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    private ViewPager viewPager = null;


    public MyPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        Object obj = super.instantiateItem(container, position);
        registeredFragments.put(position, (Fragment)obj);

        if(obj instanceof MyPagerFragment)
        {
            // setup the pager adapter
            ((MyPagerFragment)obj).setPagerAdapter(this);
        }

        return obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // get current item at the given position
    public Fragment getRegisteredFragment(int position)
    {
        return registeredFragments.get(position);
    }

    // remove all items
    public void destroyAllPagerItems()
    {
        int itemCount = getCount();
        for(int i = 0; i < itemCount; i++)
        {
            Fragment fragment = getRegisteredFragment(i);
            if(fragment != null)
            {
                destroyItem(viewPager, i, fragment);
            }
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
