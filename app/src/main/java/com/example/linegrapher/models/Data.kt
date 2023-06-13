package com.example.linegrapher.models

data class Data(
    val overall_url_chart: java.util.LinkedHashMap<String, Int>,
    val recent_links: List<LinkModel>,
    val top_links: List<LinkModel>
)