package com.wegdut.wegdut.data.remote_storage

import android.net.Uri

interface StorageRepository {
    fun uploadImage(image: Uri, quality: Int = 70, sizeLimit: Int = 1080): String
}