package com.example.smixers.utils;


// common methods for editable fragments
public interface EditableFragmentInterface
{

    // switches to edit mode or view mode fragment
    public void switchEditMode(boolean isEditMode);

    // hides the soft keyboard
    public void hideSoftKeyboard();

//    // requests focus on the fragment's root view
//    public void requestFocus();

    // invoked when the document delete was finished
    public void documentDeleteFinished(String docId);

    // invoked if the user has cancelled the document deletion
    public void documentDeleteCancelled(String docId);

}
