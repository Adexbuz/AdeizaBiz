package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.PaymentMethod
import com.example.ui.components.formatNaira
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CoralError
import com.example.ui.theme.CoralLight
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.PureWhite

@Composable
fun CustomerManagementScreen(
    customers: List<Customer>,
    onAddCustomer: (fullName: String, phone: String, email: String, address: String) -> Unit,
    onRecordDebtPayment: (customer: Customer, amount: Double, method: PaymentMethod, ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var customerForPayment by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = customers.filter {
        searchQuery.isBlank() ||
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    val totalCreditDebt = customers.sumOf { it.creditBalance }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header & Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Customers & Debt", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Text("${customers.size} registered customer accounts", fontSize = 12.sp, color = Color.Gray)
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MintGreenAccent)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Customer", fontSize = 12.sp, color = PureWhite)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total Outstanding Credit Ledger Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = if (totalCreditDebt > 0) CoralLight else ForestGreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "TOTAL OUTSTANDING CREDIT DEBT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalCreditDebt > 0) CoralError else MintGreenAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        formatNaira(totalCreditDebt),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (totalCreditDebt > 0) CoralError else PureWhite
                    )
                }

                Text(
                    text = "${customers.count { it.creditBalance > 0 }} debtors",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalCreditDebt > 0) CoralError else PureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by customer name or phone...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Customer List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredCustomers) { customer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (customer.creditBalance > 0) CoralLight else MintGreenAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (customer.creditBalance > 0) CoralError else ForestGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(customer.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                                    Text(customer.phone, fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (customer.creditBalance > 0) {
                                    Text(
                                        formatNaira(customer.creditBalance),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = CoralError
                                    )
                                    Text("Outstanding Debt", fontSize = 10.sp, color = CoralError, fontWeight = FontWeight.Bold)
                                } else {
                                    Text(
                                        formatNaira(customer.totalSpent),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ForestGreenPrimary
                                    )
                                    Text("Total Spent", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }

                        if (customer.address.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(customer.address, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                        }

                        // Action: Record Debt Payment
                        if (customer.creditBalance > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { customerForPayment = customer },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(14.dp), tint = MintGreenAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Payment", fontSize = 10.sp, color = PureWhite)
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add Customer Dialog
    if (showAddDialog) {
        var fullName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register Customer", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ForestGreenPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address / City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullName.isNotBlank() && phone.isNotBlank()) {
                            onAddCustomer(fullName.trim(), phone.trim(), email.trim(), address.trim())
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    enabled = fullName.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Register", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Record Debt Payment Dialog
    if (customerForPayment != null) {
        val cust = customerForPayment!!
        var payAmount by remember { mutableStateOf(cust.creditBalance.toString()) }
        var payMethod by remember { mutableStateOf(PaymentMethod.CASH) }
        var payRef by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { customerForPayment = null },
            title = {
                Text("Record Debt Payment: ${cust.fullName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreenPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Outstanding Balance: ${formatNaira(cust.creditBalance)}", fontWeight = FontWeight.Bold, color = CoralError)

                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("Payment Amount (₦) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(PaymentMethod.CASH to "Cash", PaymentMethod.BANK_TRANSFER to "Transfer").forEach { (method, label) ->
                            val isSelected = payMethod == method
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { payMethod = method },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    if (payMethod == PaymentMethod.BANK_TRANSFER) {
                        OutlinedTextField(
                            value = payRef,
                            onValueChange = { payRef = it },
                            label = { Text("Bank Payment Ref / Teller ID") },
                            placeholder = { Text("e.g. TRF-UBA-8812") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onRecordDebtPayment(cust, amount, payMethod, payRef)
                            customerForPayment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Confirm Payment", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { customerForPayment = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
