package com.example.linegrapher

import com.example.linegrapher.models.Dashboard
import com.example.linegrapher.models.LinkModel
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linegrapher.adapters.LinksAdapter
import com.example.linegrapher.adapters.StatusAdapter
import com.example.linegrapher.constants.Constants
import com.example.linegrapher.databinding.ActivityMainBinding
import com.example.linegrapher.models.DailyItemModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), LinksAdapter.LinksClickListener {
    var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    var topLinks: List<LinkModel>? = null
    var recentLinks: List<LinkModel>? = null

    var number = ""
    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setGreetings()
        val repository = Repository(this)
        viewModel = MyViewModel(repository, this)
        viewModel.getService()
        setObservers()
        setListeners()


    }

    private fun loadData(dashboard: Dashboard) {
        number = dashboard.support_whatsapp_number

        loadRecycler(
            dashboard.today_clicks.toString(),
            dashboard.top_location,
            dashboard.top_source,
            dashboard.startTime
        )
        recentLinks = dashboard.data.recent_links
        topLinks = dashboard.data.top_links
        binding.topLinks.isSelected = true
        binding.recentLinks.isSelected = false
        Log.e("VALUE", dashboard.data.overall_url_chart.toString())
        loadGraph(dashboard.data.overall_url_chart)
        loadLinksRecycler(true)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setListeners() {
        binding.whatsapp.setOnClickListener {
            if (!number.isEmpty()) {
                val url = "https://api.whatsapp.com/send?phone=$number"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } else {
                Toast.makeText(this, "No number found", Toast.LENGTH_SHORT).show()
            }
        }
        binding.topLinks.setOnClickListener {
            if (!it.isSelected)
                loadLinksRecycler(true)
        }
        binding.recentLinks.setOnClickListener {
            if (!it.isSelected)
                loadLinksRecycler(false)
        }
    }

    private fun loadRecycler(clicks: String, location: String, source: String, time: String) {

        val list = ArrayList<DailyItemModel>()
        list.add(DailyItemModel(clicks, "Today's Clicks", R.drawable.clicks))
        list.add(DailyItemModel(location, "Top Location", R.drawable.location))
        list.add(DailyItemModel(source, "Top Source", R.drawable.globe2))
        list.add(DailyItemModel(time, "Best Time", R.drawable.link))
        binding.itemsRecycler.adapter = StatusAdapter(list)
        binding.itemsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadLinksRecycler(con: Boolean) {
        if (con) {
            binding.topLinks.isSelected = true
            binding.recentLinks.isSelected = false
            if (topLinks != null) {
                binding.linksRecycler.adapter = LinksAdapter(topLinks!!, this, this)
                binding.linksRecycler.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }

        } else {
            binding.topLinks.isSelected = false
            binding.recentLinks.isSelected = true
            if (recentLinks != null) {
                binding.linksRecycler.adapter = LinksAdapter(recentLinks!!, this, this)
                binding.linksRecycler.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }

        }
    }

    @SuppressLint("ResourceType")
    private fun loadGraph(map: LinkedHashMap<String, Int>) {
        val entries: MutableList<Entry> = ArrayList()
        val xLabels  = kotlin.collections.ArrayList<String>()
        var x = 0f
        val s =
            Constants.getDateWithoutYear(map.entries.first().key) + " - " + Constants.getDateWithoutYear(
                map.entries.last().key
            )
        binding.graphDates.text = s
        for (entry in map.entries) {
            val value = entry.value
            entries.add(Entry(x, value.toFloat()))
            xLabels.add(Constants.getDateWithoutYear(entry.key))
            x += 1f
        }
        var dataSet = LineDataSet(entries, "Customized values")
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.lineWidth=2f
        dataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient)
        dataSet.color = ContextCompat.getColor(this, R.color.blue)
        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)
        val xAxis = binding.graphView.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        val data = LineData(dataSet)
        binding.graphView.data = data
        binding.graphView.invalidate()
    }

    private fun setObservers() {
        viewModel.dashboard.observe(this) {
            if (it != null) {
                loadData(it)
            }
        }
    }

    private fun setGreetings() {
        var time = "Good Morning"
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay in 0..11) {
            time = "Good Morning"
        } else if (timeOfDay in 12..15) {
            time = "Good Afternoon"
        } else if (timeOfDay in 16..20) {
            time = "Good Evening"
        } else if (timeOfDay in 21..23) {
            time = "Good Night"
        }
        binding.greetings.text = time
    }

    override fun linkCopyClicked(link: String) {
        if (link.isEmpty()) {
            return
        }
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Link", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun linkOpenClicked(link: String) {
        if (link.isEmpty()) {
            return
        }
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}