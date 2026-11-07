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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethod
import com.example.data.model.SaleTransaction
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.components.formatDateTime
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
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite

@Composable
fun SalesHistoryScreen(
    sales: List<SaleTransaction>,
    currentUser: UserAccount?,
    onViewReceipt: (SaleTransaction) -> Unit,
    onRefundSale: (SaleTransaction, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var saleToRefund by remember { mutableStateOf<SaleTransaction?>(null) }
    var refundReason by remember { mutableStateOf("") }

    val filterOptions = listOf("ALL", "CASH", "TRANSFER", "CARD", "CREDIT", "REFUNDED")

    val filteredSales = sales.filter { sale ->
        val matchesFilter = when (selectedFilter) {
            "ALL" -> true
            "CASH" -> sale.paymentMethod == PaymentMethod.CASH
            "TRANSFER" -> sale.paymentMethod == PaymentMethod.BANK_TRANSFER
            "CARD" -> sale.paymentMethod == PaymentMethod.CARD
            "CREDIT" -> sale.isCredit
            "REFUNDED" -> sale.isRefunded
            else -> true
        }

        val matchesSearch = searchQuery.isBlank() ||
                sale.receiptNumber.contains(searchQuery, ignoreCase = true) ||
                sale.cashierName.contains(searchQuery, ignoreCase = true) ||
                (sale.customerName != null && sale.customerName.contains(searchQuery, ignoreCase = true))

        matchesFilter && matchesSearch
    }

    val totalCompletedRevenue = filteredSales.filter { !it.isRefunded }.sumOf { it.totalAmount }
    val canRefund = currentUser?.role != UserRole.WORKER || currentUser.canAuthorizeRefunds

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Total Revenue Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
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
                    Text("TOTAL REVENUE (FILTERED)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreenAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        formatNaira(totalCompletedRevenue),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${filteredSales.size} Receipts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    Text("Complete audit trail", fontSize = 11.sp, color = MintGreenLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by receipt no, cashier or customer...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filterOptions) { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) ForestGreenPrimary else PureWhite,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else DarkCharcoal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sales List
        if (filteredSales.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No sales matching criteria.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredSales) { sale ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewReceipt(sale) }
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (sale.isRefunded) CoralLight else MintGreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = if (sale.isRefunded) CoralError else ForestGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = sale.receiptNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DarkCharcoal
                                        )
                                        Text(
                                            text = formatDateTime(sale.timestamp),
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatNaira(sale.totalAmount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (sale.isRefunded) CoralError else ForestGreenPrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (sale.isRefunded) CoralLight else LightGreyBg
                                    ) {
                                        Text(
                                            text = if (sale.isRefunded) "Refunded" else sale.paymentMethod.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (sale.isRefunded) CoralError else DarkCharcoal,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Cashier: ${sale.cashierName} • Customer: ${sale.customerName ?: "Walk-in"}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            if (sale.itemsSummary.isNotBlank()) {
                                Text(
                                    text = sale.itemsSummary,
                                    fontSize = 11.sp,
                                    color = DarkCharcoal,
                                    maxLines = 1
                                )
                            }

                            // Refund Action Button if not refunded yet
                            if (!sale.isRefunded && canRefund) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            saleToRefund = sale
                                            refundReason = "Customer returned items in good condition"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralError)
                                    ) {
                                        Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Refund & Restock", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Refund Confirmation Dialog
    if (saleToRefund != null) {
        val targetSale = saleToRefund!!
        AlertDialog(
            onDismissRequest = { saleToRefund = null },
            title = {
                Text("Authorise Refund: ${targetSale.receiptNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CoralError)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Refunding this transaction of ${formatNaira(targetSale.totalAmount)} will automatically return the sold items back to store inventory.",
                        fontSize = 12.sp,
                        color = DarkCharcoal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = refundReason,
                        onValueChange = { refundReason = it },
                        label = { Text("Reason for Refund *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRefundSale(targetSale, refundReason.ifBlank { "Store refund" })
                        saleToRefund = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralError)
                ) {
                    Text("Confirm Refund", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { saleToRefund = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
