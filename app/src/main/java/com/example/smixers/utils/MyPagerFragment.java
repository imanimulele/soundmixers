package com.example.smixers.utils;

import android.view.View;

import androidx.fragment.app.Fragment;

public class MyPagerFragment extends Fragment implements EditableFragmentInterface
{

    protected MyPagerAdapter pagerAdapter;


    @Override
    public void onStart()
    {
        super.onStart();

        try
        {
            View view = getView();

            if(view != null)
            {

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public MyPagerAdapter getPagerAdapter()
    {
        return pagerAdapter;
    }

    public void setPagerAdapter(MyPagerAdapter pagerAdapter)
    {
        this.pagerAdapter = pagerAdapter;
    }

    // switches to edit mode or view mode fragment
    public void switchEditMode(boolean isEditMode)
    {
        if (pagerAdapter != null)
        {
            pagerAdapter.isEditDetails = isEditMode;
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public  void notified(){
        pagerAdapter.notifyDataSetChanged();
    }

    // destroys all pager items
    public void destroyAllPagerItems()
    {
        if (pagerAdapter != null)
        {
            pagerAdapter.destroyAllPagerItems();
        }
    }

    // hides the soft keyboard, if needed
    public void hideSoftKeyboard()
    {
    }

//    // requests focus on the fragment's root view
//    public void requestFocus()
//    {
//        View view = getView();
//
//        if(view != null)
//        {
//            view.requestFocus();
//        }
//    }

    // invoked when the document delete was finished
    public void documentDeleteFinished(String docId)
    {
    }

    // invoked if the user has cancelled the document deletion
    public void documentDeleteCancelled(String docId)
    {
    }

}
