package com.example.smixers.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;

import com.example.smixers.R;
import com.example.smixers.models.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageManager
{

    private static final String TAG = "ImageManager";

    // default thumbnail dimenstions
    //private static final int DEF_THUMBNAIL_WIDTH = 200;
    //private static final int DEF_THUMBNAIL_HEIGHT = 200;
    private static final int DEF_THUMBNAIL_SIZE = 200;
private static  Bitmap theBitmap;
    // default image dimenstions for
    //private static final int DEF_IMAGE_WIDTH = 2000;
    //private static final int DEF_IMAGE_HEIGHT = 2000;
    private static final int DEF_IMAGE_SIZE = 1200;


    // returns the image directory
    public static File getImageDirectory(Context context)
    {
        File extDir = context.getExternalFilesDir(null);
        File imgDir = new File(extDir, "images");

        if(!imgDir.exists())
            imgDir.mkdirs();

        return imgDir;
    }

    // returns the image-upload directory
    public static File getUploadDirectory(Context context)
    {
        File extDir = context.getExternalFilesDir(null);
        File imgDir = new File(extDir, "uploads");

        if(!imgDir.exists())
            imgDir.mkdirs();

        return imgDir;
    }

    // returns image file for the given name
    public static File getImageFile(File imageDir, String fileName)
    {
        File imageFile = new File(imageDir, fileName + ".jpg");
        return imageFile;
    }

    // creates temporary image file with the given name in the app's image directory
    public static File createTempImageFile(File imageDir)
            throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";

        //File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", imageDir);

        return imageFile;
    }

    // returns new take-picture intent with image going to temp file
    public static Intent getTakePictureIntent(Context context, File[] imageFile)
    {
        if(context == null || imageFile == null || imageFile.length == 0)
            return null;

        Intent takePictureIntent = null;
        PackageManager packageManager = context.getPackageManager();

        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            imageFile[0] = null;

            if (takePictureIntent.resolveActivity(packageManager) == null)
                return null;

            try
            {
                File imageDir = ImageManager.getImageDirectory(context);
                imageFile[0] = ImageManager.createTempImageFile(imageDir);
            }
            catch (IOException ex)
            {
                Log.w(TAG, "Error creating temp image file", ex);
            }

            // Continue only if the File was successfully created
            if (imageFile[0] != null)
            {
                Uri imageFileURI = FileProvider.getUriForFile(context, "org.rdo.wata.fileprovider", imageFile[0]);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileURI);

//                takePictureIntent.putExtra("crop", "true");
//                takePictureIntent.putExtra("aspectX", 1);
//                takePictureIntent.putExtra("aspectY", 1);
//
                //takePictureIntent.putExtra("scale", true);
                //takePictureIntent.putExtra("outputX", 150);
                //takePictureIntent.putExtra("outputY", 150);
                //takePictureIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            }
            else
            {
                takePictureIntent = null;
            }
        }

        return takePictureIntent;
    }

//    // returns new crop intent
//    public static Intent getCropIntent(Uri imageFilePath, int outW, int outH)
//    {
//        // call the standard crop action intent (the user device may not
//        // support it)
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        // indicate image type and Uri
//        cropIntent.setDataAndType(imageFilePath, "image/*");
//        // set crop properties
//        cropIntent.putExtra("crop", "true");
//        // indicate aspect of desired crop
//        cropIntent.putExtra("aspectX", (float)outW / (float)outH);
//        cropIntent.putExtra("aspectY", 1);
//        // indicate output X and Y
//        cropIntent.putExtra("outputX", outW);
//        cropIntent.putExtra("outputY", outH);
//        // retrieve data on return
//        cropIntent.putExtra("return-data", true);
//
//        return cropIntent;
//    }


    // returns new pick-gallery-image intent
    public static Intent getPickGalleryImageIntent()
    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        return intent;
    }


    // crops image in square bitmap
    public static Bitmap cropImageSquare(Bitmap source)
    {
        Bitmap destBmp = null;

        int srcW = source.getWidth();
        int srcH = source.getHeight();

//        if (srcW >= srcH)
//        {
//            destBmp = Bitmap.createBitmap(source, (srcW - srcH) / 2, 0, srcH, srcH);
//        }
//        else
//        {
//            destBmp = Bitmap.createBitmap(source, 0, (srcH - srcW) / 2, srcW, srcW);
//        }

        int squareWH = Math.min(srcW, srcH);
        destBmp = ThumbnailUtils.extractThumbnail(source, squareWH, squareWH);

        return destBmp;
    }


    // crops image in square bitmap
    public static Bitmap cropImageWithAspect(Bitmap source, int aspectW, int aspectH)
    {
        Bitmap destBmp = null;

        int srcW = source.getWidth();
        int srcH = source.getHeight();

        int imgW = srcW;
        int imgH = srcH;

        if (aspectW >= aspectH)
        {
            imgW = imgH * aspectW / aspectH;

            if(imgW > srcW)
            {
                imgW = srcW;
                imgH = imgW * aspectH / aspectW;

                if(imgH > srcH)  // should never happen
                    imgH = srcH;
            }
        }
        else
        {
            imgH = imgW * aspectH / aspectW;

            if(imgH > srcH)
            {
                imgH = srcH;
                imgW = imgH * aspectW / aspectH;

                if(imgW > srcW)  // should never happen
                    imgW = srcW;
            }
        }

        destBmp = Bitmap.createBitmap(source, (srcW - imgW) / 2, (srcH - imgH) / 2, imgW, imgH);

        return destBmp;
    }


    // rotates given bitmap
    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap rotatedBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return rotatedBitmap;
    }


    // fixes image rotation, if needed
    public static Bitmap fixImageRotation(File imageFile, boolean cropToSquare)
    {
        Bitmap rotatedBitmap = null;

        try
        {
            ExifInterface ei = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            if(cropToSquare)
            {
                bitmap = cropImageSquare(bitmap);
            }

            rotatedBitmap = fixImageRotation(bitmap, orientation, imageFile);
        }
        catch(Exception ex)
        {
            Log.w(TAG, "Error fixing image rotation", ex);
        }

        return rotatedBitmap;
    }


    // fixes image rotation, if needed
    public static Bitmap fixImageRotation(Bitmap bitmap, int orientation, File saveToFile)
    {
        Bitmap rotatedBitmap = null;

        try
        {
            switch(orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    // save cropped-only bitmap
                    rotatedBitmap = bitmap;
                    break;
            }

            // save to file, if needed
            if(rotatedBitmap != null && saveToFile != null)
            {
                saveImageBytes(getImageBytes(rotatedBitmap), saveToFile);
            }
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Error fixing image rotation", ex);
        }

        return rotatedBitmap;
    }


    // returns image bitmap from the image file
    public static Bitmap getImageBitmap(File imageFile)
    {
        if(imageFile == null)
            return null;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        return bitmap;
    }


    // resizes the given image file to the given dimensions
    public static Bitmap resizeImage(File imageFile, int destWidth, int destHeight)
    {
        if(imageFile == null)
            return null;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / destWidth, photoH / destHeight);
        if(scaleFactor <= 0)
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);

        return bitmap;
    }


    // returns the bitmap image bytes in JPEG format
    public static byte[] getImageBytes(Bitmap bitmap)
    {
        if(bitmap == null)
            return null;

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        bitmap.compress(Bitmap.CompressFormat.JPEG,70 , outStream);

        return outStream.toByteArray();
    }


    // saves image bytes to the given file
    public static boolean saveImageBytes(byte[] imageBytes, File imageFile)
    {
        boolean isSuccess = false;

        if(imageBytes == null || imageBytes.length == 0)
            return isSuccess;

        try
        {
            // write the image bytes to file
            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(imageBytes);
            fo.close();

            isSuccess = true;
        }
        catch(IOException ex)
        {
            Log.w(TAG, "Error saving image bytes.", ex);
        }

        return isSuccess;
    }



    // scales the image and returns the scaled bitmap. changes the image size, but doesn't change the aspect ratio!
    public static Bitmap getScaledBitmap(Bitmap bitmap, int destWidth, int destHeight)
    {
        if(bitmap == null)
            return null;

        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();

        int destImgW = destWidth;
        int destImgH = destHeight;

        if (bitmapW >= bitmapH)
        {
            destImgW = destImgH * bitmapW / bitmapH;

            // destWidth replaced with bitmapW in comparison below, destHeight with bitmapH
            if(destImgW > bitmapW)
            {
                destImgW = bitmapW;
                destImgH = destImgW * bitmapH / bitmapW;

                if(destImgH > bitmapH)  // should never happen
                    destImgH = bitmapH;
            }
        }
        else
        {
            destImgH = destImgW * bitmapH / bitmapW;

            // destHeight replaced with bitmapH in comparison below, destWidth with bitmapW
            if(destImgH > bitmapH)
            {
                destImgH = bitmapH;
                destImgW = destImgH * bitmapW / bitmapH;

                if(destImgW > bitmapW)  // should never happen
                    destImgW = bitmapW;
            }
        }

        Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destImgW, destImgH, false);

        return b2;
    }


    // scales the image and returns it as byte array. changes the image size and aspect ratio!
    public static byte[] getScaledImage(Bitmap bitmap, int destWidth, int destHeight)
    {
        if(bitmap == null)
            return null;

        Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);

        return outStream.toByteArray();
    }


//    // scales the image and saves it to the given file
//    public static boolean saveScaledImage(Bitmap bitmap, int destWidth, int destHeight, File saveTotFile)
//    {
//        boolean isSuccess = false;
//
//        if(bitmap == null)
//            return isSuccess;
//
//        try
//        {
//            Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
//            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//
//            // compress to the format you want, JPEG, PNG...
//            // 70 is the 0-100 quality percentage
//            b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
//
//            // create the file
//            saveTotFile.createNewFile();
//
//            // write the bitmap bytes to file
//            FileOutputStream fo = new FileOutputStream(saveTotFile);
//            fo.write(outStream.toByteArray());
//            fo.close();
//
//            isSuccess = true;
//        }
//        catch(IOException ex)
//        {
//            Log.w(TAG, "Error saving scaled image", ex);
//        }
//
//        return isSuccess;
//    }

    // loads image thumbnail from the database into given image-view
    public static void loadThumbImageView(final Context context, final String imageId, final ImageView imageView)
    {
        if(imageId == null || imageId.length() == 0)
        {
            if(imageView != null)
                Glide.with(imageView.getContext())
                        .load(R.drawable.no_photo)
                        .into(imageView);
            return;
        }

        if(imageView != null)
        {
            // set glide image to wait
            Glide.with(imageView.getContext())
                    .load(R.drawable.wait_icon)
                    .into(imageView);
        }

        // get image reference
        DocumentReference docRef = FirestoreManager.getFirestoreInstance().collection("image").document(imageId);
        Source source = Utils.isNetworkConnected(context) ? Source.DEFAULT : Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    Image image = document.toObject(Image.class);

                    try
                    {
                        if(image != null && image.getThumbnailData() != null)
                        {
                            byte[] thumbBytes = Base64.decode(image.getThumbnailData(), Base64.NO_WRAP);

                            if(imageView != null)
                                Glide.with(imageView.getContext())
                                        .load(thumbBytes)
                                        .into(imageView);

                            //Log.i(TAG, "Load thumb image succeeded for imageId: " + imageId);
                        }
                        else
                        {
                            //Log.w(TAG, "Load image succeeded, but no thumbnail data found for imageId: " + imageId);

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
                else
                {
                    try
                    {
                        Log.w(TAG, "Load thumb image failed for imageId: " + imageId);

                        if(imageView != null)
                            Glide.with(imageView.getContext())
                                    .load(R.drawable.no_photo)
                                    .into(imageView);
                    }
                    catch(Exception ex)
                    {
                        Log.w(TAG, "Glide error.", ex);
                    }
                }
            }
        });
    }


    // loads full image from the store (or thumb from database) into given image-view
    public static void loadFullImageView(final Context context, final String imageId, final ImageView imageView)
    {

        if(imageId == null || imageId.length() == 0)
        {
            if(imageView != null)
                Glide.with(imageView.getContext())
                        .load(R.drawable.no_photo)
                        .into(imageView);
            return;
        }

        if(imageView != null)
        {
            Log.w(TAG, "Load imani full image failed for imageId: " + imageId);
            // set glide image to wait
            Glide.with(imageView.getContext())
                    .load(R.drawable.wait_icon)//imageView
                    .into(imageView);




        }

        // get image reference
        DocumentReference docRef = FirestoreManager.getFirestoreInstance().collection("image").document(imageId);
        Source source = Utils.isNetworkConnected(context) ? Source.DEFAULT : Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    Image image = document.toObject(Image.class);



                    if(image != null && image.getFile_name() != null && image.getFile_name().length() != 0 &&
                            Utils.isNetworkConnected(context))
                    {
                     FirestoreManager.downloadImageFile(context, image, imageView);


                    }
                    else
                    {
                        if(imageView != null)
                        {
                            try
                            {
                                if(image != null && image.getThumbnailData() != null)
                                {
                                    Log.i(TAG, "Can't load the image file. Using thumbnail instead for imageId: " + imageId);
                                    byte[] thumbBytes = Base64.decode(image.getThumbnailData(), Base64.NO_WRAP);

                                    if(imageView != null)
//                                        Glide.with(imageView.getContext())
//                                                .load(thumbBytes)
//                                                .into(imageView);


                                        Glide
                                                .with(imageView.getContext()).asBitmap()
                                                .load(thumbBytes)
                                                .into(new SimpleTarget<Bitmap>() {

                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        theBitmap =resource;
                                                        theBitmap = rotateImage(theBitmap,90);
                                                        imageView.setImageBitmap(theBitmap);
                                                    }
                                                });

                                }
                                else
                                {
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

                        Log.w(TAG, "Load full image " + imageId + " failed.", task.getException());
                    }
                }
                else
                {
                    try
                    {
                        if(imageView != null)
                            Glide.with(imageView.getContext())
                                    .load(R.drawable.no_photo)
                                    .into(imageView);

                        Log.w(TAG, "Load full image failed for imageId: " + imageId);
                    }
                    catch(Exception ex)
                    {
                        Log.w(TAG, "Glide error.", ex);
                    }
                }
            }
        });
    }



    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // replaces document in the collection with the specified object's data
    public static Image saveImage(final Context context, final String imageId, final File imageFile,
                                  final String parentType, final String householdId, final ImageView imageView)
    {
        return saveImage(context, imageId, imageFile, 1, 1, parentType, householdId, imageView);
    }

    // replaces document in the collection with the specified object's data
    public static Image saveImage(final Context context, final String imageId, final File imageFile, int aspectX, int aspectY,
                                  final String parentType, final String householdId, final ImageView imageView)
    {
        boolean isSuccess = false;

        try
        {
            // try to fix image rotation first
            //Bitmap bitmap = fixImageRotation(imageFile, (aspectX == aspectY));

            // get image orientation
            ExifInterface ei = new ExifInterface(imageFile.getAbsolutePath());
            int imageOrient = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            if(imageOrient == ExifInterface.ORIENTATION_ROTATE_90 || imageOrient == ExifInterface.ORIENTATION_ROTATE_270)
            {
                // swap x & y aspects
                int temp = aspectX;
                aspectX = aspectY;
                aspectY = temp;
            }

            // resize the image with thumbnail size
            int thumbWidth = aspectX > aspectY ? DEF_THUMBNAIL_SIZE * aspectX / aspectY : DEF_THUMBNAIL_SIZE;
            int thumbHeight = aspectY > aspectX ? DEF_THUMBNAIL_SIZE * aspectY / aspectX : DEF_THUMBNAIL_SIZE;
            Bitmap thumbBitmap = resizeImage(imageFile, thumbWidth, thumbHeight);

            // crop the thumbnail image
            thumbBitmap = cropImageWithAspect(thumbBitmap, aspectX, aspectY);

            // rotate the thumbnail image, if needed
            if(imageOrient != ExifInterface.ORIENTATION_NORMAL)
            {
                thumbBitmap = fixImageRotation(thumbBitmap, imageOrient, null);
            }

            DocumentReference docRef = FirestoreManager.getFirestoreInstance().collection("image").document(imageId);

            Image docImage = new Image();
            docImage.setImage_id(imageId);

            docImage.setParent_type(parentType);
            docImage.setHousehold_id(householdId);

            byte[] thumbBytes = getImageBytes(thumbBitmap);
            if(thumbBytes != null)
            {
                String thumbData = Base64.encodeToString(thumbBytes, Base64.NO_WRAP);
                docImage.setThumbnailData(thumbData);
            }

            if(imageView != null)
            {
                try
                {
                    // refresh image view
                    if(thumbBytes != null)
                    {
                        Glide.with(imageView.getContext())
                                .load(thumbBytes)
                                .into(imageView);
                    }
                    else
                    {
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

            // clean-up thumb data
            thumbBytes = null;
            thumbBitmap.recycle();
            thumbBitmap = null;

//            // resize the image with thumbnail size
//            int imageWidth = aspectX > aspectY ? DEF_IMAGE_SIZE * aspectX / aspectY : DEF_IMAGE_SIZE;
//            int imageHeight = aspectY > aspectX ? DEF_IMAGE_SIZE * aspectY / aspectX : DEF_IMAGE_SIZE;
//            Bitmap imageBitmap = resizeImage(imageFile, imageWidth, imageHeight);
//
//            // crop the thumbnail image
//            imageBitmap = cropImageWithAspect(imageBitmap, aspectX, aspectY);
//
//            // rotate the thumbnail image, if needed
//            if(imageOrient != ExifInterface.ORIENTATION_NORMAL)
//            {
//                imageBitmap = fixImageRotation(imageBitmap, imageOrient, null);
//            }
//
//            byte[] imageBytes = getImageBytes(imageBitmap);
//
//            File uploadDir = getUploadDirectory(context);
//            File saveFilePath = new File(uploadDir, imageId + ".jpg");
//
//            if(saveImageBytes(imageBytes, saveFilePath))
//            {
//                FirestoreManager.uploadImageFile(context, saveFilePath);
//                docImage.setFile_name(imageId + ".jpg");
//            }
//
//            // clean-up image data
//            imageBytes = null;
//            imageBitmap.recycle();
//            imageBitmap = null;

            // delete the original image
            boolean bDeleted = Utils.deleteFile(imageFile);

            // set modified-fields
            FirestoreManager.setLastModified(docImage, false);

            WriteBatch batch = FirestoreManager.getFirestoreInstance().batch();
            batch.set(docRef, docImage);

            // Commit to Firestore
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Log.d(TAG, "Saving image " + imageId + " succeeded.");
                    }
                    else
                    {
                        Log.w(TAG, "Saving image " + imageId + " failed.", task.getException());
                        Utils.showErrorDialog(context, "Saving image failed.", task.getException());
                    }
                }
            });

            return docImage;
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Save image failed for imageId: " + imageId);
            Utils.showErrorDialog(context, "Save image failed for imageId: " + imageId, ex);
        }

        return null;
    }


}
