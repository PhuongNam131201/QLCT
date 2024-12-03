package com.example.qlct

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class ActivityMoney : AppCompatActivity() {

    private lateinit var editTienThu: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)
        findViewById<ImageView>(R.id.imgAdda).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
        findViewById<ImageView>(R.id.imgbacka).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        // Khởi tạo EditText từ giao diện
        editTienThu = findViewById(R.id.editTienThu)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.menu)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.itemTextColor = null
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.category -> {
                    val intent = Intent(this, ActivityMoney::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // Hàm xử lý sự kiện khi người dùng nhấn nút "Xác nhận"
    fun onProceedClick(view: android.view.View) {
        val tienThu = editTienThu.text.toString().toDoubleOrNull() // Lấy giá trị nhập từ EditText và chuyển đổi thành Double

        if (tienThu == null || tienThu <= 0) {
            // Nếu người dùng không nhập số tiền hợp lệ, hiển thị thông báo
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
        } else {
            // Nếu đã nhập số tiền hợp lệ, lưu vào Firebase
            val database = FirebaseDatabase.getInstance().getReference("income")
            val incomeId = database.push().key
            incomeId?.let {
                // Tạo đối tượng Income với id và số tiền
                val income = Income(id = incomeId, amountt = tienThu)

                // Lưu đối tượng Income vào Firebase Realtime Database
                database.child(incomeId).setValue(income).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Trả kết quả về MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("amount", tienThu)
                        startActivity(intent)
                        finish()
                    } else {
                        // Nếu có lỗi, hiển thị thông báo lỗi
                        Toast.makeText(this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
