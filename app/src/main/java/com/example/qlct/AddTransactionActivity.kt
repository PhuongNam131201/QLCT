package com.example.qlct

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtAmount: EditText
    private lateinit var edtDescribe: EditText
    private lateinit var btnSave: Button
    private lateinit var spinnerType: Spinner
    private lateinit var iconTypeAdd: ImageView
    private lateinit var txtDate: TextView
    private lateinit var txtTime: TextView
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        findViewById<ImageView>(R.id.imgback).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        edtName = findViewById(R.id.edtName)
        edtAmount = findViewById(R.id.edtAmount)
        edtDescribe = findViewById(R.id.edtDescribe)
        btnSave = findViewById(R.id.btnSave)
        spinnerType = findViewById(R.id.txtTypeAdd)
        iconTypeAdd = findViewById(R.id.iconTypeAdd)
        txtDate = findViewById(R.id.txtDateAdd)
        txtTime = findViewById(R.id.txtHourAdd)

        database = FirebaseDatabase.getInstance().getReference("expenses")

        // Log initialization
        Log.d("AddTransactionActivity", "Activity initialized successfully")

        // Set current date and time
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        txtDate.text = currentDate
        txtTime.text = currentTime

        // Log current date and time
        Log.d("AddTransactionActivity", "Current Date: $currentDate")
        Log.d("AddTransactionActivity", "Current Time: $currentTime")

        // Setup Spinner with a listener
        val expenseTypes = resources.getStringArray(R.array.transaction_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, expenseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Log spinner setup
        Log.d("AddTransactionActivity", "Spinner populated with types: ${expenseTypes.joinToString()}")

        // Change icon when a type is selected
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Log position to debug
                Log.d("AddTransactionActivity", "Selected position: $position")

                when (position) {
                    0 -> iconTypeAdd.setImageResource(R.drawable.shoping)  // Mua sắm
                    1 -> iconTypeAdd.setImageResource(R.drawable.cutlery)  // Di chuyển
                    2 -> iconTypeAdd.setImageResource(R.drawable.vespa)  // Ăn uống
                    else -> iconTypeAdd.setImageResource(R.drawable.box)  // Khác
                }

                // Log the icon set
                Log.d("AddTransactionActivity", "Icon set for position $position")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
                Log.d("AddTransactionActivity", "No item selected in spinner")
            }
        }

        // Save data to Firebase when button is clicked
        btnSave.setOnClickListener {
            val type = spinnerType.selectedItem.toString()
            val name = edtName.text.toString()
            val amount = edtAmount.text.toString().toDoubleOrNull() ?: 0.0
            val description = edtDescribe.text.toString()

            // Log input values
            Log.d("AddTransactionActivity", "Save clicked with data - Type: $type, Name: $name, Amount: $amount, Description: $description")

            val expense = Expense(
                id = "",  // Khi tạo đối tượng Expense, để id là một chuỗi rỗng
                type = type,
                name = name,
                amount = amount,
                date = currentDate,
                time = currentTime,
                description = description
            )

            val expenseId = database.push().key // Tạo id tự động từ Firebase
            expenseId?.let {
                // Cập nhật id của expense
                val expenseWithId = expense.copy(id = it)

                // Lưu expense vào Firebase
                database.child(it).setValue(expenseWithId)
                // Log successful save
                Log.d("AddTransactionActivity", "Expense saved with ID: $it")
            } ?: run {
                // Log error if ID is null
                Log.e("AddTransactionActivity", "Error: Failed to generate database key")
            }

            finish() // Close the activity after saving data
            Log.d("AddTransactionActivity", "Activity finished after saving data")
        }

    }
}
