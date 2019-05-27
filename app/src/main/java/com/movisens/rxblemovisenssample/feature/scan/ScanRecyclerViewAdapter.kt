package com.movisens.rxblemovisenssample.feature.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movisens.rxblemovisenssample.R
import kotlinx.android.synthetic.main.card_ble_device.view.*

/**
 * Created by Robert Zetzsche on 21.05.2019.
 */
class ScanRecyclerViewAdapter(private val itemClick: (ScanViewModel.MovisensDevice) -> Unit) :
    RecyclerView.Adapter<ScanResultViewHolder>() {

    var movisensDeviceList: ArrayList<ScanViewModel.MovisensDevice> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addDevice(device: ScanViewModel.MovisensDevice) {
        for (movisensDevice in movisensDeviceList) {
            if (movisensDevice.mac == device.mac) {
                val index = movisensDeviceList.indexOf(movisensDevice)
                movisensDeviceList.remove(movisensDevice)
                movisensDeviceList.add(index, device)
                notifyItemChanged(index)
                return
            }
        }
        movisensDeviceList.add(device)
        notifyItemInserted(movisensDeviceList.size - 1);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_ble_device, parent, false)
        return ScanResultViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return movisensDeviceList.size
    }

    override fun onBindViewHolder(holder: ScanResultViewHolder, position: Int) {
        val movisensDevice = movisensDeviceList[position]
        holder.itemView.apply {
            ble_name_text.text = movisensDevice.name
            mac_name_text.text = movisensDevice.mac
            rssi_name_text.text = movisensDevice.rssi.toString()
            setOnClickListener { itemClick.invoke(movisensDevice) }
        }
    }
}

class ScanResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
