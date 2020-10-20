package com.wegdut.wegdut.data.student_verification

import android.graphics.Bitmap

interface StudentVerificationRepository {
    fun getCaptcha(username: String): Bitmap?
    fun testLogin(username: String, password: String, captcha: String?)
    fun saveStudentInfo(dto: StudentVerificationDto)
    fun isVerified(): Boolean
}