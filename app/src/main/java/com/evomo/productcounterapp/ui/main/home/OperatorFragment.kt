package com.evomo.productcounterapp.ui.main.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.LinearGauge
import com.anychart.enums.*
import com.anychart.scales.OrdinalColor
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


class OperatorFragment : Fragment() {
    private lateinit var binding: FragmentOperatorBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var nameList: List<String>
    private lateinit var machineList: List<Machine>

    var machineTextView: AutoCompleteTextView? = null
    var machineAdapterItems: ArrayAdapter<String>? = null
    private var selectedMachine: String? = null

    private lateinit var OEESocketListener: WebSocketListener
    private lateinit var QuantitySocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    private var OEESocket: WebSocket? = null
    private var QuantitySocket: WebSocket? = null

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

            settingViewModel.getUID().observe(viewLifecycleOwner) { userId ->
                viewModel.getRuntime(userId)
                UID = userId
            }

            viewModel.listRuntime.observe(viewLifecycleOwner) { dataRuntime ->
                Log.d("RUNTIME", dataRuntime.currentRuntime[0].name)

                OEESocket = okHttpClient.newWebSocket(createRequestOEE(), OEESocketListener)
                QuantitySocket = okHttpClient.newWebSocket(createRequestQuantity(), QuantitySocketListener)

                binding.machineName.text = dataRuntime.currentRuntime[0].machine.name
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

            viewModel.oee.observe(viewLifecycleOwner) { dataOEE ->
//                Log.d("DATDATDATDTATDA", dataOEE.toString())

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
                Log.d("QUQUQUQUQU", dataQuantity.toString())

                binding.totalNum.text = dataQuantity.data.total.toString()
                binding.goodNum.text = dataQuantity.data.good.toString()
                binding.rejectNum.text = dataQuantity.data.defect.toString()
            }
//            viewModel.getMachines()
//
//            viewModel.isLoading.observe(activity as AppCompatActivity) { loading ->
//                showLoading(loading)
//            }
//
//            machineTextView = binding.autocompleteMesinOperator
//            machineTextView!!.inputType = InputType.TYPE_NULL
//
//            viewModel.listMachine.observe(activity as AppCompatActivity) { list ->
//                if (list.isEmpty()) {
//                    viewModel.getMachines()
//                }
//                else {
//                    machineList = list
//                    nameList = list.map { item ->
//                        item.name
//                    }
//
//                    machineAdapterItems =
//                        context?.let { ArrayAdapter(it, R.layout.dropdown_items, nameList) }
//                    machineTextView!!.setAdapter(machineAdapterItems)
//                    machineTextView!!.onItemClickListener =
//                        AdapterView.OnItemClickListener { parent, view, position, id ->
//                            val item = parent.getItemAtPosition(position).toString()
//                            selectedMachine = item
//                        }
//                }
//            }
        }

        val bulletChartView: BulletChartView = binding.bulletChart
        bulletChartView.targetValue = 200f
        bulletChartView.currentValue = 150f
        bulletChartView.comparativeValue = 100f

        val tableLayout = binding.tableLayout
        addHeader(tableLayout)
        // Add rows dynamically
        addRowToTable(tableLayout, "07:00:00", "Downtime 1", "00:08:00")
        addRowToTable(tableLayout, "07:08:00", "Downtime 2", "00:00:30")
        addRowToTable(tableLayout, "07:53:00", "Downtime 3", "00:01:00")
    }

    private fun addRowToTable(tableLayout: TableLayout, item1: String, item2: String, item3: String) {
        val tableRow = TableRow(activity)
        val layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
        )
        tableRow.layoutParams = layoutParams
        tableRow.setPadding(24, 8, 8, 16)

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
        tableRow.setPadding(24, 24, 8, 16)

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

    override fun onDestroyView() {
        super.onDestroyView()
        okHttpClient.dispatcher.executorService.shutdown()
    }
}