package com.example.qlct

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ExpenseAdapter(
    private val expenseList: MutableList<Expense>,
    private val onDeleteExpense: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        Log.d("ExpenseAdapter", "onCreateViewHolder: Created a new view for an expense item.")
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        // Log the data being bound to the view
        Log.d("ExpenseAdapter", "onBindViewHolder: Binding data to position $position - ${expense.type}, ${expense.name}, ${expense.amount}, ${expense.date}, ${expense.time}")

        holder.txtType.text = expense.type
        holder.txtName.text = expense.name

        // Format the amount to show it in the VND format with commas
        val formattedAmount = NumberFormat.getInstance(Locale("vi", "VN")).format(expense.amount)
        holder.txtAmount.text = "$formattedAmount VND"

        holder.txtDate.text = expense.date
        holder.txtTime.text = expense.time

        // Set the appropriate icon based on the expense type
        when (expense.type) {
            "Mua sắm" -> holder.iconExpense.setImageResource(R.drawable.shoping)
            "Di chuyển" -> holder.iconExpense.setImageResource(R.drawable.vespa)
            "Ăn uống" -> holder.iconExpense.setImageResource(R.drawable.cutlery)
            else -> holder.iconExpense.setImageResource(R.drawable.box)
        }

        // Set onClickListener for the item
        holder.itemView.setOnClickListener {
            // Show confirmation dialog before deleting
            onDeleteExpense(expense)
        }
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val txtName: TextView = itemView.findViewById(R.id.txtTitleMain)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtDate: TextView = itemView.findViewById(R.id.txtItemDay)
        val txtTime: TextView = itemView.findViewById(R.id.txtTimeMain)
        val iconExpense: ImageView = itemView.findViewById(R.id.iconType)
    }
}
