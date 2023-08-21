package com.example.practicalappdev.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicalappdev.R
import com.example.practicalappdev.adapter.LocationAdapter
import com.example.practicalappdev.models.Address
import com.example.practicalappdev.databinding.ActivityLocationBinding
import com.example.practicalappdev.roomdb.LoggerLocalDataSource
import com.example.practicalappdev.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationActivity:AppCompatActivity(), View.OnClickListener,
    LocationAdapter.OnItemInteractionListener {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var adapter:LocationAdapter
    @Inject
    lateinit var logger: LoggerLocalDataSource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        clickEvent()
    }

    private fun clickEvent() {
        binding.tvAddLocation.setOnClickListener(this)
        binding.tvAddPoi.setOnClickListener(this)
        binding.includeHeader.imgFilter.setOnClickListener(this)
        binding.flNav.setOnClickListener(this)


//        binding. .setOnClickListener(this)
    }

    private fun init() {

        lifecycleScope.launch {

            logger.getAddressList { address ->

                adapter = LocationAdapter(this@LocationActivity,this@LocationActivity)
                binding.rvLocation.layoutManager = GridLayoutManager(this@LocationActivity,1,LinearLayoutManager.VERTICAL,false)
                binding.rvLocation.adapter = adapter
                adapter.setList(address = address)

                if(address.isEmpty()){
                    binding.llDataFound.isGone = true
                    binding.llNoData.isVisible = true
                    binding.includeHeader.imgFilter.isGone = true
                }
                else{
                    binding.includeHeader.imgFilter.isVisible = true
                    if(address.size==1){
                        binding.flNav.isGone = true
                    }
                    else{
                        binding.flNav.isVisible = true
                    }
                    binding.llDataFound.isVisible = true
                    binding.llNoData.isGone = true
                }
            }
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_add_location,R.id.tv_add_poi -> {
                val intent = Intent(this, SearchActivity::class.java)
                startForResult.launch(intent)
            }
            R.id.img_filter -> {
                setBottomSheetDialog()
            }
            R.id.fl_nav->{

                val intent = Intent(this, RouteActivity::class.java)
                startActivity(intent)
            }

        }
    }

    var shortFlag = 1

    private fun setBottomSheetDialog() {

        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        val btnClose = view.findViewById<ImageView>(R.id.iv_cancel)

        val radioGroup: RadioGroup = view.findViewById(R.id.rg_sort)

        val btnClear = view.findViewById<TextView>(R.id.btn_clear)
        val btnApply = view.findViewById<TextView>(R.id.btn_apply)

        val rbAsc = view.findViewById<RadioButton>(R.id.rb_asc)
        val rbDes = view.findViewById<RadioButton>(R.id.rb_des)


        if (shortFlag == 1){
            rbAsc.isChecked = true
        }else {
            rbDes.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_asc -> {
                    shortFlag = 1
                }

                R.id.rb_des -> {
                    shortFlag = 2

                }
            }

        }



        btnClear.setOnClickListener {
            logger.getAddressList {
                adapter.setList(it)
            }
            dialog.dismiss()
        }

        btnApply.setOnClickListener {
            if (shortFlag == 1){
                logger.getAscAddressList {
                    adapter.setList(it)
                }
            }else {
                logger.getDescAddressList {
                    adapter.setList(it)
                }
            }
            dialog.dismiss()
        }

        btnClose.setOnClickListener {

            dialog.dismiss()
        }


        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }



    override fun onResume() {
        super.onResume()

        logger.getAddressList { address ->
            if(address.size <= 1){
                binding.flNav.isGone = true
            }
            else{
                binding.flNav.isVisible = true
            }
        }
    }


    override fun onEditItem(position: Int, response: Address) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("position",position)
        intent.putExtra("data",response.placeId)
        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isPrimary", false) == true){
                logger.getAddressList {
                    for (address in it){
                        if (!address.isPrimary){
                            logger.updateAddressById(address.placeId, CommonUtils.distance(
                                it[0].latitude, it[0].longitude,
                                address.latitude,
                                address.longitude
                            ))
                        }
                    }
                    adapter.updateDistance(it)
                }

            }else {
                logger.getAddressList {
                    if (it.isNotEmpty()){
                        binding.llDataFound.isVisible = true
                        binding.llNoData.isGone = true
                    }
                    adapter.setList(it)
                }
            }
        }
    }


    override fun onDeleteItem(position: Int, response: Address) {
        logger.deleteAddress(response.id)
        logger.getAddressList {
            adapter.setList(it)
            if(it.size==1){
                binding.flNav.isGone = true
            }
            else{
                binding.flNav.isVisible = true
            }
        }

    }
}

