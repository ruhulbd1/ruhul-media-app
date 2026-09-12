package com.aistudio.youtube.cvyqmp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val listView = ListView(this)
        val videoList = arrayOf(
            "▶ ট্রেন্ডিং মিউজিক ভিডিও ২০২৬",
            "▶ অ্যান্ড্রয়েড অ্যাপ ডেভেলপমেন্ট টিউটোরিয়াল",
            "▶ এআই দিয়ে কীভাবে ভিডিও বানাবেন",
            "▶ টেকনোলজি নিউজ এবং আপডেট",
            "▶ লাইভ স্ট্রিমিং ও ব্লগিং গাইড"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, videoList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, "প্লে হচ্ছে: ${videoList[position]}", Toast.LENGTH_SHORT).show()
        }
        
        setContentView(listView)
    }
}
