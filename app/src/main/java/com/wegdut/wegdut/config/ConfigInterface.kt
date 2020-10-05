package com.wegdut.wegdut.config

/**
 * APP 配置接口
 * @property apiBaseUrl API基址
 * @property commentReplyCountPerFetch 每次加载回复的数量
 * @property ossBucketName OSS Bucket名
 * @property ossImagePrefix OSS存储图片的地址前缀
 * @property ossEndpoint OSS endpoint
 * @property fileProviderAuthorities 作为选择图片的file provider参数
 * @property homeNewsLimit 首页校内通知数量
 */
interface ConfigInterface {
    val apiBaseUrl: String
    val commentReplyCountPerFetch: Int
    val ossBucketName: String
    val ossImagePrefix: String
    val ossEndpoint: String
    val fileProviderAuthorities: String
    val homeNewsLimit: Int
}