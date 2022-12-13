package com.example.smixers.utils;

import static com.example.smixers.utils.ImageManager.rotateImage;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.example.smixers.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.smixers.R;

import com.example.smixers.models.*;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager
{
    private static final String TAG = "FirestoreManager";

    // firestore references
    private static FirebaseFirestore mFirestore = null;
    private static FirebaseUser mFirebaseUser = null;

    // storage references
    private static FirebaseStorage mStorage = null;
    private static StorageReference mStorageRef = null;
    private static StorageReference mImageStorageRef = null;


    // list of files to upload
    private static List<String> alFilesToUpload = new ArrayList<String>();


    private static Bitmap theBitmap;
    // Initializes the Firestore references, if needed
    public static void initIfNeeded(Context context)
    {
        // init firebase app
        if(mFirestore == null)
        {
            FirebaseApp.initializeApp(context);
        }

        getFirestoreInstance();  // init firestore instance
        getImageStorageRef();  // init storage references
    }


    // returns the current firestore instance
    public static FirebaseFirestore getFirestoreInstance()
    {
        if(mFirestore == null)
        {
            // Enable Firestore logging
            FirebaseFirestore.setLoggingEnabled(true);

            // Firestore
            mFirestore = FirebaseFirestore.getInstance();
//
//            // setup fs-timestamps (com.google.firebase.Timestamp) instead of java.util.Date
//            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                    .setTimestampsInSnapshotsEnabled(true)
//                    .build();
//            mFirestore.setFirestoreSettings(settings);
        }

        return mFirestore;
    }


    // returns the current firebase user
    public static FirebaseUser getFirebaseUser()
    {
        if(mFirebaseUser == null)
        {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        return mFirebaseUser;
    }


    // sets the firebase user
    public static void setFirebaseUser(FirebaseUser firebaseUser)
    {
        mFirebaseUser = firebaseUser;
    }


    // returns the  reference to the 'images' storage-folder
    public static StorageReference getImageStorageRef()
    {
        if(mImageStorageRef == null)
        {
            mStorage = FirebaseStorage.getInstance();
            mStorageRef = mStorage.getReference();
            mImageStorageRef = mStorageRef.child("images");
        }

        return mImageStorageRef;
    }


    // initializes spinner with collection values
    public static void initSpinner(final View rootView, final int spinnerResId, final Context context,
                                   final String collectionName, final String dataField, final String selectedItem,
                                   final List<String> alDataValues, final List<String> alIdValues, boolean insertEmptyItem)
    {
        Query query = getFirestoreInstance().collection(collectionName).whereEqualTo("is_disabled", false);
        if(!dataField.endsWith("_id"))
            query = query.orderBy(dataField);

        initSpinner(rootView, spinnerResId, context, query, dataField, selectedItem, alDataValues, alIdValues, insertEmptyItem);
    }


    // initializes spinner with collection values
    public static void initSpinner(final View rootView, final int spinnerResId, final Context context,
                                   final Query query, final String dataField, final String selectedItem,
                                   final List<String> alDataValues, final List<String> alIdValues, final boolean insertEmptyItem)
    {
        alDataValues.clear();
        if(alIdValues != null)
            alIdValues.clear();

        Source source = Utils.isNetworkConnected(context) ? Source.DEFAULT : Source.CACHE;

        query.get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    QuerySnapshot docs = task.getResult();

//                    int i = 0, selIndex = -1;

                    if(insertEmptyItem)
                    {
                        alDataValues.add("");

                        if(alIdValues != null)
                            alIdValues.add("");

//                        if(selectedItem == null || selectedItem.length() == 0)
//                            selIndex = i;
//
//                        i++;
                    }

                    for(QueryDocumentSnapshot doc : docs)
                    {
                        String dataValue = (String)doc.get(dataField);
                        if(dataValue == null)
                            dataValue = doc.getId();
                        alDataValues.add(dataValue);

                        if(alIdValues != null)
                            alIdValues.add(doc.getId());

//                        if(selectedItem != null && dataValue.equalsIgnoreCase(selectedItem))
//                            selIndex = i;
//
//                        i++;
                    }

                    Log.i(TAG, "InitSpinner - field: " + dataField + ", sizeArray: " + alDataValues.size());

                    // set spinner values and optionally select an item
                    initSpinner(rootView, spinnerResId, context, alDataValues, selectedItem);
                }
                else
                {
                    Log.w(TAG, "Loading spinner values failed.", task.getException());
                    Utils.showErrorDialog(context, "Loading spinner values failed.", task.getException());
                }
            }
        });

//        final ListenerRegistration listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>()
//        {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot docs, @Nullable FirebaseFirestoreException e)
//            {
//                if(e != null)
//                {
//                    try
//                    {
//                        Log.w(TAG, "Loading spinner values failed.", e);
//                        Utils.showErrorDialog(context, "Loading spinner values failed.", e);
//                    }
//                    catch(Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//
//                    return;
//                }
//
//                int i = 0, selIndex = -1;
//
//                if(insertEmptyItem)
//                {
//                    alDataValues.add("");
//
//                    if(alIdValues != null)
//                        alIdValues.add("");
//
//                    i++;
//                }
//
//                for(QueryDocumentSnapshot doc : docs)
//                {
//                    String dataValue = (String)doc.get(dataField);
//                    alDataValues.add(dataValue);
//
//                    if(alIdValues != null)
//                        alIdValues.add(doc.getId());
//
//                    if(selectedItem != null && dataValue.equals(selectedItem))
//                        selIndex = i;
//
//                    i++;
//                }
//
//                // villages drop-down adapter
//                String[] arrayVillages = alDataValues.toArray(new String[0]);
//                ArrayAdapter<String> villagesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayVillages);
//                villagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                // set spinner adapter
//                Spinner spinner = (Spinner) rootView.findViewById(spinnerResId);
//                spinner.setAdapter(villagesAdapter);
//
//                // set selected item
//                if(selIndex >= 0)
//                {
//                    spinner.setSelection(selIndex);
//                }
//
//                if(listenerRegistration != null)
//                {
//                    listenerRegistration.remove();
//                }
//            }
//        });

    }


    // initializes spinner with collection values
    public static void initSpinner(final View rootView, final int spinnerResId, final Context context, final List<String> alDataValues, String selectedItem)
    {
        // villages drop-down adapter
        String[] arrayVillages = alDataValues.toArray(new String[0]);
        ArrayAdapter<String> villagesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayVillages);
        villagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // set spinner adapter
      Spinner spinner = (Spinner)rootView.findViewById(spinnerResId);
      spinner.setAdapter(villagesAdapter);

        int selIndex = -1;
        if(selectedItem == null)
            selectedItem = "";

        if(selectedItem.trim().length() > 0)
        {
            int i = 0;
            for(String dataValue : alDataValues)
            {
                if(dataValue.equalsIgnoreCase(selectedItem))
                    selIndex = i;
                i++;
            }
        }

        // set selected item
        if(selIndex >= 0)
            spinner.setSelection(selIndex);
    }


    private static final String[] OPTIONS = new String[]{
            "CASH", "MOBILE", "PLEDGE",
    };

//
//   public static void autoComplete(final View rootView,final int spinnerResId,final Context context){
//
//
//       ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//               R.layout.drop_downitem, OPTIONS);
//
//       AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)rootView.
//               findViewById(spinnerResId);
//
//       autoCompleteTextView.setAdapter(adapter);
//
//   }


    // sets last-modified fields of a data-values set
    public static void setLastModified(Map<String, Object> fieldValues)
    {
        setLastModified(fieldValues, false);
    }


    // sets last-modified & disabled-fields of a data-values set
    public static void setLastModified(Map<String, Object> fieldValues, boolean isDisabled)
    {
        Timestamp modifiedAt = new Timestamp(new Date());
        String modifiedBy = getFirebaseUser() != null ? getFirebaseUser().getUid() : "";

        fieldValues.put("modified_at", modifiedAt);
        fieldValues.put("modified_by", modifiedBy);
        fieldValues.put("is_disabled", isDisabled);
    }

    public static void setLastMeterReading(Map<String, Object> fieldValues)
    {
        Timestamp modifiedAt = new Timestamp(new Date());
        String modifiedBy = getFirebaseUser() != null ? getFirebaseUser().getUid() : "";

        fieldValues.put("last_readingDate", modifiedAt);
        fieldValues.put("read_by", modifiedBy);

    }


    // sets last-modified fields of not-disabled data object
    public static void setLastModified(Object obj)
    {
        setLastModified(obj, false);
    }

    // sets last-modified & disabled-fields of a data object
    public static void setLastModified(Object obj, boolean isDisabled)
    {
        Timestamp modifiedAt = new Timestamp(new Date());
        String modifiedBy = getFirebaseUser() != null ? getFirebaseUser().getUid() : "";

        Class objClass = obj.getClass();
        if(objClass != null)
        {
            try
            {
                Field field = objClass.getDeclaredField("modified_at");
                field.setAccessible(true);
                field.set(obj, modifiedAt);

                field = objClass.getDeclaredField("modified_by");
                field.setAccessible(true);
                field.set(obj, modifiedBy);

                field = objClass.getDeclaredField("is_disabled");
                field.setAccessible(true);
                field.set(obj, isDisabled);
            }
            catch(NoSuchFieldException ex)
            {
                Log.w(TAG, "No such field.", ex);
            }
            catch(IllegalAccessException ex)
            {
                Log.w(TAG, "Illegal access.", ex);
            }
        }
    }


    // creates document in the collection with the specified object's data
    public static void createRecord(final Context context, final String collectionName, final String documentId, final Object docObject,
                                    final FirestoreRecordCreatedInterface callback, final MyPagerAdapter pagerAdapter, final FloatingActionButton addItemButton)
    {
        if(addItemButton != null)
        {
            // hide the add-item button
            addItemButton.setEnabled(false);
            addItemButton.hide();  //.setVisibility(View.GONE);
        }

        final DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);

        // check for existing document first
        final Source firestoreSource = Utils.getFirestoreSource(context);
        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.toObject(docObject.getClass()) == null || doc.getBoolean("is_disabled"))
                    {
                        // documentId is new
                        WriteBatch batch = getFirestoreInstance().batch();
                        batch.set(docRef, docObject);

                        // Commit to Firestore
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "Creating " + collectionName + ":" + documentId + " succeeded.");
                                    recordWasCreated(context, collectionName, documentId, docObject, callback, pagerAdapter, addItemButton);

//                                    if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                                    {
//                                        // delete the person in cache
//                                        if(SearchCacheManager.isModifyCacheThreadRunning())
//                                            SearchCacheManager.stopModifyCacheThread();
//
//                                        SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//                                    }
//
//                                    try
//                                    {
//                                        if(pagerAdapter != null)
//                                        {
//                                            // go to edit mode
//                                            pagerAdapter.isEditDetails = true;
//                                            pagerAdapter.notifyDataSetChanged();
//                                        }
//                                        else if(callback != null)
//                                        {
//                                            callback.onFirestoreRecordCreated(collectionName, documentId, docObject);
//                                        }
//
//                                        if(addItemButton != null)
//                                        {
//                                            addItemButton.setEnabled(true);
//                                            addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                    catch (Exception ex)
//                                    {
//                                        ex.printStackTrace();
//                                    }
                                }
                                else
                                {
                                    Log.w(TAG, "Creating '" + collectionName + ":" + documentId + "' failed.", task.getException());
                                    Utils.showErrorDialog(context, "Creating new '" + collectionName + "' failed.", task.getException());
                                }
                            }
                        });


                        if(!Utils.isNetworkConnected(context))
                        {
                            // batch will not resolve offline
                            recordWasCreated(context, collectionName, documentId, docObject, callback, pagerAdapter, addItemButton);

//                            if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                            {
//                                // delete the person in cache
//                                if(SearchCacheManager.isModifyCacheThreadRunning())
//                                    SearchCacheManager.stopModifyCacheThread();
//
//                                SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//                            }
//
//                            try
//                            {
//                                if(pagerAdapter != null)
//                                {
//                                    // go to edit mode
//                                    pagerAdapter.isEditDetails = true;
//                                    pagerAdapter.notifyDataSetChanged();
//                                }
//                                else if(callback != null)
//                                {
//                                    callback.onFirestoreRecordCreated(collectionName, documentId, docObject);
//                                }
//
//                                if(addItemButton != null)
//                                {
//                                    addItemButton.setEnabled(true);
//                                    addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                    else
                    {
                        // document found
                        Log.w(TAG, "'" + collectionName + "' with ID:" + documentId + " already exists.");
                        Utils.showErrorDialog(context, collectionName + "  " + documentId + " already exists.", task.getException());
                    }
                }
                else
                {
                    Log.w(TAG, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                    //Utils.showErrorDialog(context, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());

                    if((task.getException() instanceof FirebaseFirestoreException) && ((FirebaseFirestoreException)task.getException()).getCode() == FirebaseFirestoreException.Code.UNAVAILABLE)
                    {
                        // probably offline - create the new statement
                        WriteBatch batch = getFirestoreInstance().batch();
                        batch.set(docRef, docObject);

                        // Commit to Firestore
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "Creating " + collectionName + ":" + documentId + " succeeded.");
                                    recordWasCreated(context, collectionName, documentId, docObject, callback, pagerAdapter, addItemButton);

//                                    if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                                    {
//                                        // delete the person in cache
//                                        if(SearchCacheManager.isModifyCacheThreadRunning())
//                                            SearchCacheManager.stopModifyCacheThread();
//
//                                        SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//                                    }
//
//                                    try
//                                    {
//                                        if(pagerAdapter != null)
//                                        {
//                                            // go to edit mode
//                                            pagerAdapter.isEditDetails = true;
//                                            pagerAdapter.notifyDataSetChanged();
//                                        }
//                                        else if(callback != null)
//                                        {
//                                            callback.onFirestoreRecordCreated(collectionName, documentId, docObject);
//                                        }
//
//                                        if(addItemButton != null)
//                                        {
//                                            addItemButton.setEnabled(true);
//                                            addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                    catch (Exception ex)
//                                    {
//                                        ex.printStackTrace();
//                                    }
                                }
                                else
                                {
                                    Log.w(TAG, "Creating '" + collectionName + ":" + documentId + "' failed.", task.getException());
                                    Utils.showErrorDialog(context, "Creating new '" + collectionName + "' failed.", task.getException());
                                }
                            }
                        });

                        if(!Utils.isNetworkConnected(context))
                        {
                            // batch will not resolve offline
                            recordWasCreated(context, collectionName, documentId, docObject, callback, pagerAdapter, addItemButton);

//                            if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                            {
//                                // delete the person in cache
//                                if(SearchCacheManager.isModifyCacheThreadRunning())
//                                    SearchCacheManager.stopModifyCacheThread();
//
//                                SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//                            }
//
//                            try
//                            {
//                                if(pagerAdapter != null)
//                                {
//                                    // go to edit mode
//                                    pagerAdapter.isEditDetails = true;
//                                    pagerAdapter.notifyDataSetChanged();
//                                }
//                                else if(callback != null)
//                                {
//                                    callback.onFirestoreRecordCreated(collectionName, documentId, docObject);
//                                }
//
//                                if(addItemButton != null)
//                                {
//                                    addItemButton.setEnabled(true);
//                                    addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                    else
                    {
                        Utils.showErrorDialog(context, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                    }
                }
            }
        });
    }


    // invoked when the record is created successfully
    private static void recordWasCreated(Context context, String collectionName, String documentId, Object docObject,
                                         FirestoreRecordCreatedInterface callback, MyPagerAdapter pagerAdapter, FloatingActionButton addItemButton)
    {
//        if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//        {
//            // modify the person in cache
//            if(SearchCacheManager.isModifyCacheThreadRunning())
//                SearchCacheManager.stopModifyCacheThread();
//
//            SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//        }

        try
        {
            if(pagerAdapter != null)
            {
                // go to edit mode
                pagerAdapter.isEditDetails = true;
                pagerAdapter.notifyDataSetChanged();
            }

            if(callback != null)
            {
                callback.onFirestoreRecordCreated(collectionName, documentId, docObject);
            }

            if(addItemButton != null)
            {
                addItemButton.setEnabled(true);
                addItemButton.show();  // .setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    // invoked when the record is created successfully
    private static void recordWasCreated(Context context, String collectionName, String documentId, Map<String, Object> fieldValues,
                                         FirestoreRecordCreatedInterface callback, MyPagerAdapter pagerAdapter, FloatingActionButton addItemButton)
    {
//        if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//        {
//            // modify the person in cache
//            if(SearchCacheManager.isModifyCacheThreadRunning())
//                SearchCacheManager.stopModifyCacheThread();
//
//            SearchCacheManager.startModifyCacheThread((Activity)context, "", documentId, fieldValues);
//        }

        try
        {
            if(pagerAdapter != null)
            {
                // go to edit mode
                pagerAdapter.isEditDetails = true;
                pagerAdapter.notifyDataSetChanged();
            }

            if(callback != null)
            {
                callback.onFirestoreRecordCreated(collectionName, documentId, fieldValues);
            }

            if(addItemButton != null)
            {
                addItemButton.setEnabled(true);
                addItemButton.show();  // .setVisibility(View.VISIBLE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    // creates document in the collection with the specified object's data
    public static void createRecord(final Context context, final String collectionName, final String documentId, final Map<String, Object> fieldValues,
                                    final FirestoreRecordCreatedInterface callback, final MyPagerAdapter pagerAdapter, final FloatingActionButton addItemButton)
    {
        if(addItemButton != null)
        {
            // hide the add-item button
            addItemButton.setEnabled(false);
            addItemButton.hide();  // .setVisibility(View.GONE);
        }

        final DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);

        // check for existing document first
        final Source firestoreSource = Utils.getFirestoreSource(context);
        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.get("is_disabled") == null || doc.getBoolean("is_disabled"))
                    {
                        // documentId is new
                        WriteBatch batch = getFirestoreInstance().batch();
                        batch.set(docRef, fieldValues);

                        // Commit to Firestore
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "Creating " + collectionName + ":" + documentId + " succeeded.");
                                    recordWasCreated(context, collectionName, documentId, fieldValues, callback, pagerAdapter, addItemButton);

//                                    if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                                    {
//                                        // delete the person in cache
//                                        if(SearchCacheManager.isModifyCacheThreadRunning())
//                                            SearchCacheManager.stopModifyCacheThread();
//
//                                        SearchCacheManager.startModifyCacheThread((Activity)context, "", fieldValues);
//                                    }
//
//                                    try
//                                    {
//                                        if(pagerAdapter != null)
//                                        {
//                                            // go to edit mode
//                                            pagerAdapter.isEditDetails = true;
//                                            pagerAdapter.notifyDataSetChanged();
//                                        }
//                                        else if(callback != null)
//                                        {
//                                            callback.onFirestoreRecordCreated(collectionName, documentId, fieldValues);
//                                        }
//
//                                        if(addItemButton != null)
//                                        {
//                                            addItemButton.setEnabled(true);
//                                            addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                    catch (Exception ex)
//                                    {
//                                        ex.printStackTrace();
//                                    }
                                }
                                else
                                {
                                    Log.w(TAG, "Creating '" + collectionName + ":" + documentId + "' failed.", task.getException());
                                    Utils.showErrorDialog(context, "Creating new '" + collectionName + "' failed.", task.getException());
                                }
                            }
                        });


                        if(!Utils.isNetworkConnected(context))
                        {
                            // batch will not resolve offline
                            recordWasCreated(context, collectionName, documentId, fieldValues, callback, pagerAdapter, addItemButton);

//                            try
//                            {
//                                if(pagerAdapter != null)
//                                {
//                                    // go to edit mode
//                                    pagerAdapter.isEditDetails = true;
//                                    pagerAdapter.notifyDataSetChanged();
//                                }
//                                else if(callback != null)
//                                {
//                                    callback.onFirestoreRecordCreated(collectionName, documentId, fieldValues);
//                                }
//
//                                if(addItemButton != null)
//                                {
//                                    addItemButton.setEnabled(true);
//                                    addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                    else
                    {
                        // document found
                        Log.w(TAG, "'" + collectionName + "' with ID:" + documentId + " already exists.");
                        Utils.showErrorDialog(context, collectionName + "  " + documentId + " already exists.", task.getException());
                    }
                }
                else
                {
                    Log.w(TAG, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                    //Utils.showErrorDialog(context, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());

                    if((task.getException() instanceof FirebaseFirestoreException) && ((FirebaseFirestoreException)task.getException()).getCode() == FirebaseFirestoreException.Code.UNAVAILABLE)
                    {
                        // probably offline - create the new statement
                        WriteBatch batch = getFirestoreInstance().batch();
                        batch.set(docRef, fieldValues);

                        // Commit to Firestore
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "Creating " + collectionName + ":" + documentId + " succeeded.");
                                    recordWasCreated(context, collectionName, documentId, fieldValues, callback, pagerAdapter, addItemButton);

//                                    try
//                                    {
//                                        if(pagerAdapter != null)
//                                        {
//                                            // go to edit mode
//                                            pagerAdapter.isEditDetails = true;
//                                            pagerAdapter.notifyDataSetChanged();
//                                        }
//                                        else if(callback != null)
//                                        {
//                                            callback.onFirestoreRecordCreated(collectionName, documentId, fieldValues);
//                                        }
//
//                                        if(addItemButton != null)
//                                        {
//                                            addItemButton.setEnabled(true);
//                                            addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                    catch (Exception ex)
//                                    {
//                                        ex.printStackTrace();
//                                    }
                                }
                                else
                                {
                                    Log.w(TAG, "Creating '" + collectionName + ":" + documentId + "' failed.", task.getException());
                                    Utils.showErrorDialog(context, "Creating new '" + collectionName + "' failed.", task.getException());
                                }
                            }
                        });

                        if(!Utils.isNetworkConnected(context))
                        {
                            // batch will not resolve offline
                            recordWasCreated(context, collectionName, documentId, fieldValues, callback, pagerAdapter, addItemButton);

//                            try
//                            {
//                                if(pagerAdapter != null)
//                                {
//                                    // go to edit mode
//                                    pagerAdapter.isEditDetails = true;
//                                    pagerAdapter.notifyDataSetChanged();
//                                }
//                                else if(callback != null)
//                                {
//                                    callback.onFirestoreRecordCreated(collectionName, documentId, fieldValues);
//                                }
//
//                                if(addItemButton != null)
//                                {
//                                    addItemButton.setEnabled(true);
//                                    addItemButton.show();  // .setVisibility(View.VISIBLE);
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                    else
                    {
                        Utils.showErrorDialog(context, "Getting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                    }
                }
            }
        });
    }


    // replaces document in the collection with the specified object's data
    public static void updateRecord(final Context context, final String collectionName, final String documentId, final Object docObject)
    {
        DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);

        WriteBatch batch = getFirestoreInstance().batch();
        batch.set(docRef, docObject);

        // Commit to Firestore
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "Saving data " + collectionName + ":" + documentId + " succeeded.");
                    recordWasUpdated(context, collectionName, documentId, docObject);
                }
                else
                {
                    try
                    {
                        Log.w(TAG, "Saving data to '" + collectionName + ":" + documentId + "' failed.", task.getException());
                        Utils.showErrorDialog(context, "Saving data to '" + collectionName + "' failed.", task.getException());
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });

        if(!Utils.isNetworkConnected(context))
        {
            // batch will not resolve offline
            Log.d(TAG, "Offline - saving data " + collectionName + ":" + documentId + " succeeded.");
            recordWasUpdated(context, collectionName, documentId, docObject);
        }
    }


    // invoked when the record is updated successfully
    private static void recordWasUpdated(Context context, String collectionName, String documentId, Object docObject)
    {
//        if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//        {
//            // modify the person in cache
//            if(SearchCacheManager.isModifyCacheThreadRunning())
//                SearchCacheManager.stopModifyCacheThread();
//
//            SearchCacheManager.startModifyCacheThread((Activity)context, "", (Household)docObject);
//        }
    }


    // invoked when the record is updated successfully
    private static void recordWasUpdated(Context context, String collectionName, String documentId, Map<String, Object> fieldValues)
    {
//        if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//        {
//            // modify the person in cache
//            if(SearchCacheManager.isModifyCacheThreadRunning())
//                SearchCacheManager.stopModifyCacheThread();
//
//            SearchCacheManager.startModifyCacheThread((Activity)context, "", documentId, fieldValues);
//        }
    }


    // updates document fields in the collection with the specified values
    public static void updateRecord(final Context context, final String collectionName, final String documentId, final Map<String, Object> fieldValues)
    {
        DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);

        WriteBatch batch = getFirestoreInstance().batch();
        batch.update(docRef, fieldValues);

        // Commit to Firestore
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "Updating data '" + collectionName + ":" + documentId + "' succeeded.");
                    recordWasUpdated(context, collectionName, documentId, fieldValues);
                }
                else
                {
                    try
                    {
                        Log.w(TAG, "Updating data '" + collectionName + ":" + documentId + "' failed.", task.getException());
                        Utils.showErrorDialog(context, "Updating data '" + collectionName + "' failed.", task.getException());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });

        if(!Utils.isNetworkConnected(context))
        {
            // batch will not resolve offline
            Log.d(TAG, "Offline - updating data " + collectionName + ":" + documentId + " succeeded.");
            recordWasUpdated(context, collectionName, documentId, fieldValues);
        }
    }


    // invoked when the record is successfully deleted
    public static void recordWasDeleted(Context context, String collectionName, String documentId,
                                         EditableFragmentInterface editableFragment, NavController navController)
    {
//        if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//        {
//            // delete the person in cache
//            if(SearchCacheManager.isDeleteCacheThreadRunning())
//                SearchCacheManager.stopDeleteCacheThread();
//
//            SearchCacheManager.startDeleteCacheThread((Activity)context, "", documentId);
//        }

        try
        {
            if(editableFragment != null)
            {
                // delete has finished
                editableFragment.documentDeleteFinished(documentId);

                // close edit-mode fragment
                //editableFragment.hideSoftKeyboard();
                editableFragment.switchEditMode(false);
                //editableFragment.requestFocus();
            }
            else if(navController != null)
            {
                // go to previous fragment
                navController.popBackStack();
            }
        }
        catch (Exception ex)
        {
            Log.w(TAG, "PagerAdapter or NavController error.", ex);
        }
    }

    // aks for confirmation and then deletes the given record
    public static void confirmAndDeleteDocument(final Context context, final String collectionName, final String documentId,
                                                final EditableFragmentInterface editableFragment, final NavController navController)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //View focused = ((Activity)context).getCurrentFocus();
                //Log.i(TAG, "Focused view: " + focused);

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked - delete the record
                        Map<String, Object> saveData = new HashMap<String, Object>();
                        setLastModified(saveData, true);

                        // save the disabled record
                        DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);
                        WriteBatch batch = getFirestoreInstance().batch();
                        batch.update(docRef, saveData);

                        // commit to Firestore
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "Deleting '" + collectionName + ":" + documentId + "' succeeded.");
                                    recordWasDeleted(context, collectionName, documentId, editableFragment, navController);

//                                    if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                                    {
//                                        // delete the person in cache
//                                        if(SearchCacheManager.isDeleteCacheThreadRunning())
//                                            SearchCacheManager.stopDeleteCacheThread();
//
//                                        SearchCacheManager.startDeleteCacheThread((Activity)context, "", documentId);
//                                    }
//
//                                    try
//                                    {
//                                        if(editableFragment != null)
//                                        {
//                                            // delete has finished
//                                            editableFragment.documentDeleteFinished(documentId);
//
//                                            // close edit-mode fragment
//                                            //editableFragment.hideSoftKeyboard();
//                                            editableFragment.switchEditMode(false);
//                                            //editableFragment.requestFocus();
//                                        }
//                                        else if(navController != null)
//                                        {
//                                            // go to previous fragment
//                                            navController.popBackStack();
//                                        }
//                                    }
//                                    catch (Exception ex)
//                                    {
//                                        Log.w(TAG, "PagerAdapter or NavController error.", ex);
//                                    }
                                }
                                else
                                {
                                    try
                                    {
                                        Log.w(TAG, "Deleting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                                        Utils.showErrorDialog(context, "Deleting '" + collectionName + "' failed.", task.getException());
                                    }
                                    catch(Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });

                        if(!Utils.isNetworkConnected(context))
                        {
                            // batch will not resolve offline
                            recordWasDeleted(context, collectionName, documentId, editableFragment, navController);

//                            if(collectionName.equals("household") && SearchCacheManager.isSearchCacheAvailable((Activity)context))
//                            {
//                                // delete the person in cache
//                                if(SearchCacheManager.isDeleteCacheThreadRunning())
//                                    SearchCacheManager.stopDeleteCacheThread();
//
//                                SearchCacheManager.startDeleteCacheThread((Activity)context, "", documentId);
//                            }
//
//                            try
//                            {
//                                if(editableFragment != null)
//                                {
//                                    // delete has finished
//                                    editableFragment.documentDeleteFinished(documentId);
//
//                                    // close edit-mode fragment
//                                    //editableFragment.hideSoftKeyboard();
//                                    editableFragment.switchEditMode(false);
//                                    //editableFragment.requestFocus();
//                                }
//                                else if(navController != null)
//                                {
//                                    // go to previous fragment
//                                    navController.popBackStack();
//                                }
//                            }
//                            catch (Exception ex)
//                            {
//                                Log.w(TAG, "PagerAdapter or NavController error.", ex);
//                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked - just exit
                        if(editableFragment != null)
                        {
                            // delete has finished
                            editableFragment.documentDeleteCancelled(documentId);

                            //editableFragment.requestFocus();
                        }
                        break;
                }
            }
        };

//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//        dialogBuilder.setMessage(context.getString(R.string.confirm_delete_record))
//                .setPositiveButton(context.getString(R.string.option_yes), dialogClickListener)
//                .setNegativeButton(context.getString(R.string.option_no), dialogClickListener)
//                .show();
    }


    // aks for confirmation and then deletes the given record
    public static void deleteDocument(final Context context, final String collectionName, final String documentId,
                                      final EditableFragmentInterface editableFragment, final NavController navController)
    {
        Map<String, Object> saveData = new HashMap<String, Object>();
        setLastModified(saveData, true);

        // save the disabled record
        DocumentReference docRef = getFirestoreInstance().collection(collectionName).document(documentId);
        WriteBatch batch = getFirestoreInstance().batch();
        batch.update(docRef, saveData);

        // commit to Firestore
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.d(TAG, "Deleting '" + collectionName + ":" + documentId + "' succeeded.");
                    recordWasDeleted(context, collectionName, documentId, editableFragment, navController);
                }
                else
                {
                    try
                    {
                        Log.w(TAG, "Deleting '" + collectionName + ":" + documentId + "' failed.", task.getException());
                        Utils.showErrorDialog(context, "Deleting '" + collectionName + "' failed.", task.getException());
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });

        if(!Utils.isNetworkConnected(context))
        {
            // batch will not resolve offline
            recordWasDeleted(context, collectionName, documentId, editableFragment, navController);
        }
    }


//    // sets the title text for the given household
//    public static void setTitleTextHousehold(final Context context, final String householdId, final TextView textView)
//    {
//        final DocumentReference docRef = getFirestoreInstance().collection("household").document(householdId);
//        final Source firestoreSource = Utils.getFirestoreSource(context);
//
//        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot doc = task.getResult();
//                    Household household = doc.toObject(Household.class);
//
//                    try
//                    {
//                        if(household != null && textView != null)
//                        {
//                            String titleText = household.getHousehold_id();
//                            if(titleText != null && titleText.trim().length() > 8)
//                                titleText = titleText.substring(0, 8) + "...";
//
//                            if(household.getHead_of_household() != null && household.getHead_of_household().length() > 0)
//                                titleText += " - " + household.getHead_of_household();
//
//                            textView.setText(titleText);
//                        }
//                        else
//                        {
//                            // document not found
//                            if(textView != null)
//                            {
//                                textView.setText("");
//                            }
//
//                            Log.w(TAG, "Household " + householdId + " not found!");
//                            Utils.showErrorDialog(context, "Household " + householdId + " not found!");
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//                else
//                {
//                    try
//                    {
//                        Log.w(TAG, "Getting household " + householdId + " failed.", task.getException());
//                        Utils.showErrorDialog(context, "Getting household " + householdId + " failed.", task.getException());
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//
//    }


    // sets the title text for the given wata card
//    public static void setTitleTextWataCard(final Context context, final String cardId, final TextView textView)
//    {
//        final DocumentReference docRef = getFirestoreInstance().collection("wata_card").document(cardId);
//        final Source firestoreSource = Utils.getFirestoreSource(context);
//
//        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot doc = task.getResult();
//                    WataCard watacard = doc.toObject(WataCard.class);
//
//                    try
//                    {
//                        if(watacard != null && textView != null)
//                        {
//                            String titleText = watacard.getCard_id();
//
//                            String sHouseholdId = watacard.getHousehold_id();
//                            if(sHouseholdId != null && sHouseholdId.trim().length() > 8)
//                                sHouseholdId = sHouseholdId.substring(0, 8) + "...";
//
//                            if(sHouseholdId != null && sHouseholdId.trim().length() > 0)
//                                titleText += " - " + sHouseholdId;
//                            if(watacard.getHead_of_household() != null && watacard.getHead_of_household().trim().length() > 0)
//                                titleText += " - " + watacard.getHead_of_household();
//
//                            textView.setText(titleText);
//                        }
//                        else
//                        {
//                            // document not found
//                            if(textView != null)
//                            {
//                                textView.setText("");
//                            }
//
//                            Log.w(TAG, "Card " + cardId + " not found!");
//                            Utils.showErrorDialog(context, "Card " + cardId + " not found!");
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//                else
//                {
//                    try
//                    {
//                        Log.w(TAG, "Getting card " + cardId + " failed.", task.getException());
//                        Utils.showErrorDialog(context, "Getting card " + cardId + " failed.", task.getException());
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//
//    }


//    public static void setTitleTextWataMeter(final Context context, final String meter_sn, final TextView textView)
//    {
//        final DocumentReference docRef = getFirestoreInstance().collection("household_meter").document(meter_sn);
//        final Source firestoreSource = Utils.getFirestoreSource(context);
//
//        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot doc = task.getResult();
//                    HouseHoldMeter watameter = doc.toObject(HouseHoldMeter.class);
//
//                    try
//                    {
//                        if(watameter != null && textView != null)
//                        {
//                            String titleText = watameter.getMeter_sn();
//
//                            String sHouseholdId = watameter.getHousehold_id();
//                            if(sHouseholdId != null && sHouseholdId.trim().length() > 8)
//                                sHouseholdId = sHouseholdId.substring(0, 13) + "...";
//
//                            if(sHouseholdId != null && sHouseholdId.trim().length() > 0)
//                                titleText += "  -  " + sHouseholdId;
//
//
//                            textView.setText(titleText);
//                        }
//                        else
//                        {
//                            // document not found
//                            if(textView != null)
//                            {
//                                textView.setText("");
//                            }
//
//                            Log.w(TAG, "Meter " + meter_sn + " not found!");
//                            Utils.showErrorDialog(context, "Meter " + meter_sn + " not found!");
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//                else
//                {
//                    try
//                    {
//                        Log.w(TAG, "Getting card " + meter_sn + " failed.", task.getException());
//                        Utils.showErrorDialog(context, "Getting card " + meter_sn + " failed.", task.getException());
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//
//    }


//    // sets the title text for the given wata card
//    public static void setWataCardTypeText(final Context context, final int cardTypeId, final TextView textView)
//    {
//        final DocumentReference docRef = getFirestoreInstance().collection("wata_card_type").document(Integer.toString(cardTypeId));
//        final Source firestoreSource = Utils.getFirestoreSource(context);
//
//        docRef.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task)
//            {
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot doc = task.getResult();
//                    WataCardType cardType = doc.toObject(WataCardType.class);
//
//                    try
//                    {
//                        if(cardType != null && textView != null)
//                        {
//                            textView.setText(cardType.getCard_type_name());
//                        }
//                        else
//                        {
//                            // document not found
//                            if(textView != null)
//                            {
//                                textView.setText("");
//                            }
//
//                            Log.w(TAG, "Card type " + cardTypeId + " not found!");
//                            Utils.showErrorDialog(context, "Card type " + cardTypeId + " not found!");
//                        }
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//                else
//                {
//                    try
//                    {
//                        Log.w(TAG, "Getting card type " + cardTypeId + " failed.", task.getException());
//                        Utils.showErrorDialog(context, "Getting card type " + cardTypeId + " failed.", task.getException());
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//
//    }


    // returns storage reference to the given image file
    public static StorageReference getImageStorageRef(String imageName)
    {
        if(imageName == null || imageName.length() == 0)
            return null;

        if(!imageName.endsWith(".jpg") && !imageName.endsWith(".png"))
            imageName += ".jpg";

        // get reference to the image file
        StorageReference imageRef = getImageStorageRef().child(imageName);

        return imageRef;
    }

    // uploads file content
    public static UploadTask uploadBytes(final StorageReference fileRef, byte[] data)
    {
        final UploadTask uploadTask = fileRef.putBytes(data);
        //uploadTasks.add(uploadTask);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads
                Log.w(TAG, "Upload file bytes failed: " + fileRef.getName(), exception);
                //uploadTasks.remove(uploadTask);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.i(TAG, "Upload file bytes succeeded: " + fileRef.getName());
                //uploadTasks.remove(uploadTask);

            }
        });

        return uploadTask;
    }


    // uploads image file to the storage
    public static UploadTask uploadImageFile(Context context, final File imageFile)
    {
        if(!Utils.isNetworkConnected(context))
            return null;

        final Uri file = Uri.fromFile(imageFile);
        final String fileName = file.getLastPathSegment();

        StorageReference fileRef = getImageStorageRef().child(fileName);
        final UploadTask uploadTask = fileRef.putFile(file);
        //uploadTasks.add(uploadTask);

        alFilesToUpload.add(fileName);
        Log.i(TAG, "Uploading image file to storage: " + fileName);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads
                Log.w(TAG, "Upload image file failed: " + fileName, exception);
                //uploadTasks.remove(uploadTask);

                // delete from list, but don't delete the file
                alFilesToUpload.remove(fileName);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.i(TAG, "Upload image file succeeded: " + fileName);
                //uploadTasks.remove(uploadTask);

                // delete both the file and file-name from the list
                alFilesToUpload.remove(fileName);
                Utils.deleteFile(imageFile);
            }
        });

        return uploadTask;
    }


    // looks for files to upload and starts upload them to the server, if network is available
    public static boolean checkForImageFilesToUpload(Context context)
    {
        if(!Utils.isNetworkConnected(context))
            return false;

        Log.i(TAG, "Looking for image files to upload...");

        // look for files in the upload-folder
        File uploadDir = ImageManager.getUploadDirectory(context);
        File[] uploadFiles = uploadDir.listFiles();

        int numFiles = uploadFiles.length;
        int sentFiles = 0;

        for (File file : uploadFiles)
        {
            if (!file.isDirectory())
            {
                final Uri fileUri = Uri.fromFile(file);
                final String fileName = fileUri.getLastPathSegment();

                if(!alFilesToUpload.contains(fileName))
                {
                    uploadImageFile(context, file);
                    sentFiles++;
                }
            }
        }

        if(numFiles > 0)
        {
            Log.i(TAG, "Found " + numFiles + " image files to upload. Sent" + sentFiles + " files.");
        }

        return true;
    }


    // downloads image file from the storage into the given image view
    public static void downloadImageFile(final Context context, final Image image, final ImageView imageView)
    {
        if(image == null || image.getFile_name() == null || image.getFile_name().length() == 0)
            return;
        if(imageView == null || !Utils.isNetworkConnected(context))
            return;

        StorageReference fileRef = getImageStorageRef().child(image.getFile_name());

        fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if(task.isSuccessful())
                {
                    try
                    {
                        Uri fileUri = task.getResult();
                        if(imageView != null)
                            Glide.with(imageView.getContext())
                                    .load(fileUri)
                                    .into(imageView);





                    }
                    catch(Exception ex)
                    {
                        Log.w(TAG, "Glide error.", ex);
                    }
                }
                else
                {
                    Log.w(TAG, "Downloading image file failed: " + image.getFile_name(), task.getException());

                    try
                    {
                        // try to use the thumbnail instead
                        if(image.getThumbnailData() != null)
                        {
                            byte[] thumbBytes = Base64.decode(image.getThumbnailData(), Base64.NO_WRAP);

                            if(imageView != null)
                                Glide.with(imageView.getContext())
                                        .load(thumbBytes)
                                        .into(imageView);
                        }
                        else
                        {
                            if(imageView != null)
                                Glide.with(imageView.getContext())
                                        .load(R.drawable.no_photo)
                                        .into(imageView);
                        }
                    }
                    catch(Exception ex)
                    {
                        Log.w(TAG, "Glide error.", ex);
                    }
                }
            }

            public Bitmap RotateBitmap(Bitmap source, float angle)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);
                return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            }

            private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
                int width = image.getWidth();
                int height = image.getHeight();

                float bitmapRatio = (float) width / (float) height;
                if (bitmapRatio > 0) {
                    width = maxSize;
                    height = (int) (width / bitmapRatio);
                } else {
                    height = maxSize;
                    width = (int) (height * bitmapRatio);
                }
                return Bitmap.createScaledBitmap(image, width, height, true);
            }

            private Bitmap rotateImageIfRequired(Bitmap img, Uri fileUri) throws IOException {

                ExifInterface ei = new ExifInterface(fileUri.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Log.w("Orintenatin", String.valueOf(orientation));
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Log.w("Orintenatin", String.valueOf(orientation));
                        return rotateImage(img, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Log.w("Orintenatin", String.valueOf(orientation));
                        return rotateImage(img, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Log.w("Orintenatin", String.valueOf(orientation));
                        return rotateImage(img, 270);
                    default:
                        return img;
                }
            }
        });
    }


//    public static void deleteWataCard(final Context context, final String cardId, final EditableFragmentInterface editableFragment, final NavController navController)
//    {
//        // delete card logs
//        Query qCardLog = mFirestore.collection("wata_card_log").whereEqualTo("card_id", cardId);  // .whereEqualTo("is_disabled", false);
//        final Source firestoreSource = Utils.getFirestoreSource(context);
//        qCardLog.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//                    QuerySnapshot docs = task.getResult();
//
//                    for (QueryDocumentSnapshot doc : docs)
//                    {
//                        WataCardLog cardLog = doc.toObject(WataCardLog.class);
//                        deleteDocument(context, "wata_card_log", cardLog.getCard_log_id(), editableFragment, navController);
//                    }
//                }
//                else
//                {
//                    Log.w(TAG, "Loading watacard logs failed.", task.getException());
//                }
//            }
//        });
//
//        // delete card op-logs
//        Query qCardLogOp = mFirestore.collection("wata_card_log_op").whereEqualTo("card_id", cardId);  // .whereEqualTo("is_disabled", false);
//        qCardLogOp.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//                    QuerySnapshot docs = task.getResult();
//
//                    for (QueryDocumentSnapshot doc : docs)
//                    {
//                        WataCardLogOp cardLog = doc.toObject(WataCardLogOp.class);
//                        deleteDocument(context, "wata_card_log_op", cardLog.getOp_log_id(), editableFragment, navController);
//                    }
//                }
//                else
//                {
//                    Log.w(TAG, "Loading watacard op-logs failed.", task.getException());
//                }
//            }
//        });
//
//        // delete card ws-logs
//        Query qCardLogWs = mFirestore.collection("wata_card_log_ws").whereEqualTo("card_id", cardId);  // .whereEqualTo("is_disabled", false);
//        qCardLogWs.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//                    QuerySnapshot docs = task.getResult();
//
//                    for (QueryDocumentSnapshot doc : docs)
//                    {
//                        WataCardLogWs cardLog = doc.toObject(WataCardLogWs.class);
//                        deleteDocument(context, "wata_card_log_ws", cardLog.getWs_log_id(), editableFragment, navController);
//                    }
//                }
//                else
//                {
//                    Log.w(TAG, "Loading watacard ws-logs failed.", task.getException());
//                }
//            }
//        });
//
//        // delete card remarks
//        Query qCardRemark = mFirestore.collection("remark").whereEqualTo("parent_id", cardId);  // .whereEqualTo("is_disabled", false);
//        qCardRemark.get(firestoreSource).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//                    QuerySnapshot docs = task.getResult();
//
//                    for (QueryDocumentSnapshot doc : docs)
//                    {
//                        Remark remark = doc.toObject(Remark.class);
//                        deleteDocument(context, "remark", remark.getRemark_id(), editableFragment, navController);
//                    }
//                }
//                else
//                {
//                    Log.w(TAG, "Loading watacard remarks failed.", task.getException());
//                }
//            }
//        });
//
//        // delete wata card
//        deleteDocument(context, "wata_card", cardId, editableFragment, navController);
//    }


}
