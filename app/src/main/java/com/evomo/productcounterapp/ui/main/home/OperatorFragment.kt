package com.evomo.productcounterapp.ui.main.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ekn.gruzer.gaugelibrary.Range
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.data.model.Machine
import com.evomo.productcounterapp.data.remote.WebSocketListener
import com.evomo.productcounterapp.databinding.FragmentOperatorBinding
import com.evomo.productcounterapp.ui.TokenViewModelFactory
import com.evomo.productcounterapp.ui.login.LoginActivity
import com.evomo.productcounterapp.ui.main.MainActivity
import com.evomo.productcounterapp.ui.main.customview.BulletChartView
import com.evomo.productcounterapp.ui.main.customview.CircularProgressBar
import com.evomo.productcounterapp.ui.main.customview.CircularProgressBarPerf
import com.evomo.productcounterapp.utils.DateHelper
import com.evomo.productcounterapp.utils.SettingPreferences
import com.evomo.productcounterapp.utils.SettingViewModel
import com.evomo.productcounterapp.utils.SettingViewModelFactory
import com.github.mikephil.charting.data.PieEntry
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class OperatorFragment : Fragment() {
    private lateinit var binding: FragmentOperatorBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private var timeList: MutableList<String> = mutableListOf<String>()
    private lateinit var machineList: List<Machine>

    var timeTextView: AutoCompleteTextView? = null
    var timeAdapterItems: ArrayAdapter<String>? = null
    private var selectedTime: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null

    private lateinit var OEESocketListener: WebSocketListener
    private lateinit var QuantitySocketListener: WebSocketListener
    private lateinit var DowntimeSocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    private var OEESocket: WebSocket? = null
    private var QuantitySocket: WebSocket? = null
    private var DowntimeSocket: WebSocket? = null

    private val timeFormatNoSeconds = DateTimeFormatter.ofPattern("HH:mm")
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    lateinit var UID : String
    var scope = "operator"
    var consumerCustomId = "5e4b58ba2b91b5525a1bf8a1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.date.text = DateHelper.getCurrentDateNoTime()

        // Calculate half of the screen width and height
        val displayMetrics = DisplayMetrics()
        (binding.progressBar.context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val halfScreenWidth = screenWidth / 2
        val halfScreenHeight = screenHeight / 2

        // Set the margin to half of the screen size
        setMarginToView(binding.progressBar, halfScreenWidth, halfScreenHeight)

        val bulletChartView: BulletChartView = binding.bulletChart
        bulletChartView.setMaxValue(100f)
        bulletChartView.setPerformanceRange(20f, 100f)

        // set dropdown timeline
        for (hour in 0..23) {
            val startTime = String.format("%02d:00", hour)
            val endTime = String.format("%02d:00", (hour + 1) % 24)
            timeList.add("$startTime - $endTime")
        }

        timeTextView = binding.autocompleteTimelineOperator
        timeTextView!!.inputType = InputType.TYPE_NULL
        timeAdapterItems =
            context?.let { ArrayAdapter(it, R.layout.dropdown_items, timeList) }
        timeTextView!!.setAdapter(timeAdapterItems)

        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )

        settingViewModel.getToken().observe(viewLifecycleOwner) { token ->
            Log.d("TOKEN CHECK", token)
            if (token == "Not Set") {
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            val viewModel by viewModels<OperatorViewModel>(){
                TokenViewModelFactory((activity as MainActivity).application, token)
            }

            OEESocketListener = WebSocketListener(viewModel, "OEE")
            QuantitySocketListener = WebSocketListener(viewModel, "Quantity")
            DowntimeSocketListener = WebSocketListener(viewModel, "Downtime")

            settingViewModel.getUID().observe(viewLifecycleOwner) { userId ->
                viewModel.getRuntime(userId)
                UID = userId
            }

            viewModel.listRuntime.observe(viewLifecycleOwner) { dataRuntime ->
                if (dataRuntime.currentRuntime != null) {
                    OEESocket = okHttpClient.newWebSocket(createRequestOEE(), OEESocketListener)
                    QuantitySocket = okHttpClient.newWebSocket(createRequestQuantity(), QuantitySocketListener)
                    DowntimeSocket = okHttpClient.newWebSocket(createRequestDowntime(), DowntimeSocketListener)

                    val jsonObject = JSONObject()
                    jsonObject.put("sort", "asc");
                    jsonObject.put("machine_id", dataRuntime.currentRuntime[0].machine.id);
                    jsonObject.put("runtime_id", dataRuntime.currentRuntime[0].id);
                    DowntimeSocket!!.send(
                        jsonObject.toString()
                    )

                    binding.machineName.text = dataRuntime.currentRuntime[0].machine.name
                }
                else {
                    binding.homeScroll.setScrolling(false)
                    binding.noRuntime.visibility = VISIBLE
                    setPaddingToBottomOfScreen(binding.noRuntime)
                    binding.machineName.visibility = GONE
                    binding.date.visibility = GONE
                    binding.cardInfo.visibility = GONE
                    binding.cardGauge.visibility = GONE
                    binding.cardTimeline.visibility = GONE
                    showLoading(false)
                }
            }

            val range = Range()
            range.color = ContextCompat.getColor(requireContext(), R.color.red)
            range.from = 0.0
            range.to = 50.0

            val range2 = Range()
            range2.color = ContextCompat.getColor(requireContext(), R.color.orange_500)
            range2.from = 50.0
            range2.to = 75.0

            val range3 = Range()
            range3.color = ContextCompat.getColor(requireContext(), R.color.green_700)
            range3.from = 75.0
            range3.to = 100.0

            var halfGauge = binding.halfGauge

            //add color ranges to gauge
            halfGauge.addRange(range)
            halfGauge.addRange(range2)
            halfGauge.addRange(range3)

            //set min max and current value
            halfGauge.minValue = 0.0
            halfGauge.maxValue = 100.0

            val tableLayout = binding.tableLayout
            addHeader(tableLayout)

            viewModel.socketStatus.observe(viewLifecycleOwner) { status ->
                showLoading(!status)
            }

            viewModel.oee.observe(viewLifecycleOwner) { dataOEE ->
                halfGauge.value = dataOEE.data.oee
                val formattedPercentage = String.format("%.1f %%", dataOEE.data.oee)
                binding.gaugePercentage.text = formattedPercentage

                val availPercent: CircularProgressBar = binding.availPercentage
                availPercent.setProgress(dataOEE.data.availability.toInt())

                val performancePercent: CircularProgressBarPerf = binding.performancePercentage
                performancePercent.setProgress(dataOEE.data.performance.toInt())

                val qualityPercent: CircularProgressBar = binding.qualityPercentage
                qualityPercent.setProgress(dataOEE.data.quality.toInt())
            }

            viewModel.quantity.observe(viewLifecycleOwner) { dataQuantity ->
                binding.totalNum.text = dataQuantity.data.total.toString()
                binding.goodNum.text = dataQuantity.data.good.toString()
                binding.rejectNum.text = dataQuantity.data.defect.toString()
            }

            viewModel.downtime.observe(viewLifecycleOwner) { dataDowntime ->
                while (tableLayout.childCount > 1) {
                    tableLayout.removeView(tableLayout.getChildAt(tableLayout.childCount - 1))
                }

                for (i in 0 until dataDowntime.data.size) {
                    val downtime = dataDowntime.data[i]
                    val instantStart = Instant.parse(downtime.startTime)
                    val instantEnd = Instant.parse(downtime.endTime)

                    val localStartTime = instantStart.atZone(ZoneId.systemDefault())
                    val localEndTime = instantEnd.atZone(ZoneId.systemDefault())

                    val formattedStartTime = timeFormat.format(localStartTime)
                    val formattedEndTime = timeFormat.format(localEndTime)
                    addRowToTable(tableLayout,formattedStartTime , "Downtime ${i}", formattedEndTime)
                }

                timeTextView!!.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val item = parent.getItemAtPosition(position).toString()
                        val times = item.split(" - ")
                        selectedStartTime = times[0]
                        selectedEndTime = times[1]

                        val startTime = LocalTime.parse(selectedStartTime, timeFormatNoSeconds)
                        val endTime = LocalTime.parse(selectedEndTime, timeFormatNoSeconds)
                        val durationsList: MutableList<Pair<Float, Float>> = mutableListOf()

                        for (i in 0 until dataDowntime.data.size) {
                            val downtime = dataDowntime.data[i]

                            val instantStart = Instant.parse(downtime.startTime)
                            val instantEnd = Instant.parse(downtime.endTime)

                            val localStartTime = instantStart.atZone(ZoneId.systemDefault()).toLocalTime()
                            val localEndTime = instantEnd.atZone(ZoneId.systemDefault()).toLocalTime()

                            if (localStartTime.hour == startTime.hour) {
                                var startMinute = localStartTime.minute.toFloat()
                                var endMinute = localEndTime.minute.toFloat()

                                if ((localEndTime.hour == endTime.hour) && (endMinute > 0)) {
                                    durationsList.add(startMinute to 60f)
                                }
                                else {
                                    durationsList.add(startMinute to localEndTime.minute.toFloat())
                                }
                            }
                            else if (localEndTime.hour == startTime.hour) {
                                durationsList.add(0f to localEndTime.minute.toFloat())
                            }
                        }
                        bulletChartView.clearDurations()

                        if (durationsList.isNotEmpty()) {
                            bulletChartView.setDurations(
                                *durationsList.toTypedArray()
                            )
                        }
                    }
            }
        }
    }

    private fun setPaddingToBottomOfScreen(textView: TextView) {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val paddingToBottom = screenHeight - textView.height
        textView.setPadding(textView.paddingLeft, textView.paddingTop, textView.paddingRight, paddingToBottom)
    }

    private fun setMarginToView(view: View, marginWidth: Int, marginHeight: Int) {
        // Create a new layout params to apply margins
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(marginWidth, marginHeight, marginWidth, marginHeight)

        // Apply the layout params to the view
        view.layoutParams = layoutParams
    }

    private fun addRowToTable(tableLayout: TableLayout, item1: String, item2: String, item3: String) {
        val tableRow = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
        )
        tableRow.layoutParams = layoutParams
        tableRow.setPadding(48, 8, 8, 16)

        val textView1 = TextView(activity)
        textView1.text = item1
        textView1.setTextAppearance(R.style.TableColumn)

        val textView2 = TextView(activity)
        textView2.text = item2
        textView2.setTextAppearance(R.style.TableColumn)

        val textView3 = TextView(activity)
        textView3.text = item3
        textView3.setTextAppearance(R.style.TableColumn)

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)

        tableLayout.addView(tableRow)
    }

    private fun addHeader(tableLayout: TableLayout) {
        val tableRow = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
        )
        tableRow.layoutParams = layoutParams
        tableRow.setPadding(48, 24, 8, 16)

        val textView1 = TextView(activity)
        textView1.text = getString(R.string.stop_time)
        textView1.setTextAppearance(R.style.TableHeader)

        val textView2 = TextView(activity)
        textView2.text = getString(R.string.downtime)
        textView2.setTextAppearance(R.style.TableHeader)

        val textView3 = TextView(activity)
        textView3.text = getString(R.string.time)
        textView3.setTextAppearance(R.style.TableHeader)

        tableRow.addView(textView1)
        tableRow.addView(textView2)
        tableRow.addView(textView3)

        tableLayout.addView(tableRow)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        binding.homeScroll.setScrolling(!isLoading)
    }

    private fun createRequestOEE(): Request {
        val websocketURL = "wss://oeesocket.jeager.io/machine/stream/oee?x-authenticated-userid=${UID}&x-authenticated-scope=${scope}&x-consumer-custom-id=${consumerCustomId}"
        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    private fun createRequestQuantity(): Request {
        val websocketURL = "wss://oeesocket.jeager.io/machine/stream/quantity?x-authenticated-userid=${UID}&x-authenticated-scope=${scope}&x-consumer-custom-id=${consumerCustomId}"
        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    private fun createRequestDowntime(): Request {
        val websocketURL = "wss://oeesocket.jeager.io/machine/stream/downtime?x-authenticated-userid=${UID}&x-authenticated-scope=${scope}&x-consumer-custom-id=${consumerCustomId}"
        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OEESocket?.close(1000, "Canceled manually.")
        QuantitySocket?.close(1000, "Canceled manually.")
        okHttpClient.dispatcher.executorService.shutdown()
    }

    override fun onPause() {
        super.onPause()
        OEESocket?.close(1000, "Canceled manually.")
        QuantitySocket?.close(1000, "Canceled manually.")
    }
}