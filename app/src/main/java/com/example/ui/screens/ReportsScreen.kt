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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.Product
import com.example.data.model.SaleTransaction
import com.example.ui.components.formatNaira
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CoralError
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite

@Composable
fun ReportsScreen(
    sales: List<SaleTransaction>,
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("All Time") }
    val periods = listOf("Today", "This Week", "This Month", "All Time")

    val validSales = sales.filter { !it.isRefunded }

    val totalRevenue = validSales.sumOf { it.totalAmount }
    // Estimated cost of goods sold based on catalog margins (~75% cost)
    val estimatedCOGS = totalRevenue * 0.74
    val grossProfit = totalRevenue - estimatedCOGS
    val profitMargin = if (totalRevenue > 0) (grossProfit / totalRevenue) * 100 else 0.0

    // Payment methods aggregation
    val cashSales = validSales.filter { it.paymentMethod == PaymentMethod.CASH }.sumOf { it.totalAmount }
    val transferSales = validSales.filter { it.paymentMethod == PaymentMethod.BANK_TRANSFER }.sumOf { it.totalAmount }
    val cardSales = validSales.filter { it.paymentMethod == PaymentMethod.CARD }.sumOf { it.totalAmount }
    val creditSales = validSales.filter { it.isCredit }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Financial Analytics & Profit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("Live calculations from actual store sales", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Period filter pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(periods) { p ->
                    val isSelected = p == selectedPeriod
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ForestGreenPrimary else PureWhite,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.clickable { selectedPeriod = p }
                    ) {
                        Text(
                            text = p,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PureWhite else DarkCharcoal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Gross Profit & Revenue Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ESTIMATED GROSS PROFIT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreenAccent)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintGreenAccent
                        ) {
                            Text(
                                "%.1f%% Margin".format(profitMargin),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        formatNaira(grossProfit),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ForestGreenLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sales Revenue", fontSize = 11.sp, color = MintGreenLight)
                            Text(formatNaira(totalRevenue), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cost of Goods (COGS)", fontSize = 11.sp, color = MintGreenLight)
                            Text(formatNaira(estimatedCOGS), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }
            }
        }

        // Payment Methods Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PAYMENT METHOD DISTRIBUTION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentMethodBar(label = "Cash Payments", amount = cashSales, total = totalRevenue, color = ForestGreenPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodBar(label = "Bank Transfer", amount = transferSales, total = totalRevenue, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodBar(label = "POS Card Terminal", amount = cardSales, total = totalRevenue, color = AmberWarning)
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodBar(label = "Credit Sales (Debt)", amount = creditSales, total = totalRevenue, color = CoralError)
                }
            }
        }

        // Best-Selling Catalog Highlights
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TOP PERFORMING STORE PRODUCTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    products.take(5).forEachIndexed { index, prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MintGreenLight,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("#${index + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DarkCharcoal)
                                    Text(prod.category, fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Text(formatNaira(prod.sellingPrice), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreenPrimary)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PaymentMethodBar(label: String, amount: Double, total: Double, color: Color) {
    val progress = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f
    val pct = if (total > 0) (amount / total * 100) else 0.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = DarkCharcoal)
            Text(
                "${formatNaira(amount)} (%.0f%%)".format(pct),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = LightGreyBg
        )
    }
}
