package com.example.notes.activities.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import java.io.InputStream

/**
 * @param context Context
 *  just pass a `this` keyword from activity
 * @param message String
 *  message to show
 *
 * Simple show-toast function to reduce code
 */
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * @param context Context
 * @param permission String
 *  permission from android.Manifest
 *
 * @return Boolean
 */
fun checkPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * @param activity Activity
 * @param packageManager PackageManager
 * @param requestCode Int
 *
 * Select image from storage
 *
 * this method will start an activity for result, so to get the data, overrides an
 * `onActivityResult` method inside activity and find the specific requestCode
 */
fun selectImage(activity: Activity, packageManager: PackageManager, requestCode: Int) {
    val intent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    if (intent.resolveActivity(packageManager) != null) {
        startActivityForResult(activity, intent, requestCode, null)
    }
}

/**
 * Get selected image path
 *
 * selected image from method above will passed here to extract the
 * image's path
 */
fun getSelectedImagePath(contentResolver: ContentResolver, contentUri: Uri): String {
    val filePath: String
    val cursor: Cursor? = contentResolver.query(
        contentUri,
        null,
        null,
        null,
        null
    )

    if (cursor == null) {
        filePath = contentUri.path!!
    } else {
        cursor.moveToFirst()
        val index: Int = cursor.getColumnIndex("_data")
        filePath = cursor.getString(index)
        cursor.close()
    }

    return filePath
}