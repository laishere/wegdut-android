package com.wegdut.wegdut.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitArray

object QRCodeUtils {
    fun generate(text: String, width: Int, height: Int) : Bitmap {
        val matrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
        val pixels = IntArray(width * height)
        var bitArray: BitArray? = null
        var i = 0
        for (y in 0 until height) {
            bitArray = matrix.getRow(y, bitArray)
            for (x in 0 until width) {
                if (bitArray[x])
                    pixels[i] = Color.BLACK
                else pixels[i] = Color.WHITE
                i++
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}