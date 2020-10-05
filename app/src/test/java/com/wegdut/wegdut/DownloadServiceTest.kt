package com.wegdut.wegdut

import com.wegdut.wegdut.service.DownloadService
import com.wegdut.wegdut.utils.ApiUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class DownloadServiceTest {

    @Before
    fun setup() {
        ApiUtils.initTest()
    }

    @Test
    fun `单位计算测试`() {
        val test = listOf(
            102f,
            1.1f * DownloadService.K,
            1.2f * DownloadService.M,
            25.67f * DownloadService.G
        )
        val expected = listOf(
            "102B", "1KB", "1.2MB", "25.67GB"
        )
        val result = test.map { DownloadService.format(it.toLong()) }
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `下载测试`() {
        val testUrl =
            "https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_500kB.jpg"
        val file = File.createTempFile("test", null)
        val downloadService = DownloadService()
        downloadService.download(testUrl, file, chunkSize = DownloadService.M.toInt()) {
            println(it)
        }
    }
}