package com.teavaro.ecommDemoApp.core.dataClases

data class InfoResponse(
    val umid: String? = null,
    val state: Int? = null,
    val permissions: Map<String, Boolean>? = null,
    val permissionsLastModification: Map<String, String>? = null,
    val notificationHistory: List<Unit>? = null,
    val notificationStatus: Map<String, Boolean>? = null,
    val attributes: Map<String, String>? = null,
    )