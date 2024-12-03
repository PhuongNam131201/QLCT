package com.example.qlct

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var database: DatabaseReference
    private lateinit var totalSpendTextView: TextView
    private lateinit var txtSum: TextView // TextView để hiển thị tổng thu - chi
    private val expenseList = mutableListOf<Expense>()
    private lateinit var incomeDatabase: DatabaseReference  // Reference for income data
    private val incomeList = mutableListOf<Income>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        totalSpendTextView = findViewById(R.id.txtSpend)
        txtSum = findViewById(R.id.txtSum) // Khởi tạo TextView cho tổng thu - chi

        recyclerView.layoutManager = LinearLayoutManager(this)

        expenseAdapter = ExpenseAdapter(expenseList, ::showDeleteConfirmationDialog)
        recyclerView.adapter = expenseAdapter

        database = FirebaseDatabase.getInstance().getReference("expenses")
        incomeDatabase = FirebaseDatabase.getInstance().getReference("incomes")

        // Load expense data
        loadExpenses()

        // Load income data
        loadIncomes()

        // Handle add transaction button click
        findViewById<ImageView>(R.id.imgAdd).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Get the current date
        val txtDate: TextView = findViewById(R.id.txtDate)
        val currentDate = getCurrentDate()
        txtDate.text = currentDate

        // Set up BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.menu)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.itemTextColor = null
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.category -> {
                    val intent = Intent(this, ActivityMoney::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadExpenses() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                var totalSpend = 0.0
                for (expenseSnapshot in snapshot.children) {
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.let {
                        expenseList.add(it)
                        totalSpend += it.amount
                    }
                }
                Log.d("Total Income", "totalSpend: $totalSpend")
                expenseAdapter.notifyDataSetChanged()

                // Format the total spend amount to show it in the VND format
                val formattedTotalSpend = NumberFormat.getInstance(Locale("vi", "VN")).format(totalSpend)
                totalSpendTextView.text = "$formattedTotalSpend VND"

                // Cập nhật tổng thu chi vào TextView
                updateTotalSum()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Error loading expenses: ${error.message}")
            }
        })
    }

    private fun loadIncomes() {
        val incomeDatabase = FirebaseDatabase.getInstance().getReference("income")

        incomeDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalIncome = 0.0
                for (incomeSnapshot in snapshot.children) {
                    val income = incomeSnapshot.getValue(Income::class.java)
                    income?.let {
                        totalIncome += it.amountt
                    }
                }
                // Log ra giá trị tổng thu nhập
                Log.d("Total Income", "Total Income: $totalIncome")

                // Hiển thị tổng thu nhập trong TextView
                val formattedTotalIncome = NumberFormat.getInstance(Locale("vi", "VN")).format(totalIncome)
                findViewById<TextView>(R.id.txtIncome).text = "$formattedTotalIncome VND"
                updateTotalSum()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Error loading incomes: ${error.message}")
            }
        })
    }
    private fun updateTotalSum() {
        // Lấy giá trị tổng thu nhập từ Firebase
        val incomeText = findViewById<TextView>(R.id.txtIncome).text.toString()
        Log.d("Income Text Before Processing", incomeText)

        // Loại bỏ các ký tự " VND", dấu phẩy và dấu chấm
        val cleanedIncome = incomeText.replace(" VND", "").replace(",", "").replace(".", "").trim()
        Log.d("Cleaned Income", "Cleaned Income: $cleanedIncome")

        // Kiểm tra nếu cleanedIncome không phải là một số hợp lệ
        val totalIncome = cleanedIncome.toDoubleOrNull() ?: 0.0
        Log.d("Parsed Income", "Parsed Total Income: $totalIncome")

        // Lấy giá trị tổng chi tiêu từ TextView
        val spendText = totalSpendTextView.text.toString()
        Log.d("Spend Text Before Cleaning", "Text: $spendText")

        // Loại bỏ các ký tự " VND", dấu phẩy và dấu chấm
        val cleanedSpend = spendText.replace(" VND", "").replace(",", "").replace(".", "").trim()
        Log.d("Cleaned Spend", "Cleaned Spend: $cleanedSpend")

        // Kiểm tra nếu cleanedSpend không phải là một số hợp lệ
        val totalSpend = cleanedSpend.toDoubleOrNull() ?: 0.0
        Log.d("Parsed Spend", "Parsed Total Spend: $totalSpend")

        // Tính tổng thu - chi
        val total = totalIncome - totalSpend
        Log.d("Total Calculation", "Total: $total")

        // Hiển thị kết quả tổng thu chi vào TextView txtSum
        val formattedTotal = NumberFormat.getInstance(Locale("vi", "VN")).format(total)
        txtSum.text = "Tổng: $formattedTotal VND"
    }






    private fun showDeleteConfirmationDialog(expense: Expense) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Xóa Mục")
            .setMessage("Bạn có chắc chắn muốn xóa mục này?")
            .setPositiveButton("Xóa") { _, _ -> deleteExpense(expense) }
            .setNegativeButton("Hủy", null)
            .create()
        dialog.show()
    }

    private fun deleteExpense(expense: Expense) {
        val expenseId = expense.id // Assuming Expense has an 'id' property
        database.child(expenseId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Mục đã được xóa", Toast.LENGTH_SHORT).show()
                loadExpenses() // Reload the data after deletion
            } else {
                Toast.makeText(this, "Xóa không thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
