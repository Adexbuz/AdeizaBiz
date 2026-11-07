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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.data.model.StockMovementType
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
import com.example.ui.theme.PureWhite

@Composable
fun InventoryScreen(
    products: List<Product>,
    stockMovements: List<StockMovement>,
    onAdjustStock: (Product, StockMovementType, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Inventory Levels, 1: Stock Movements History
    var adjustingProduct by remember { mutableStateOf<Product?>(null) }
    var adjustmentType by remember { mutableStateOf(StockMovementType.RESTOCK) }

    val totalValuation = products.sumOf { it.stockQuantity * it.purchasePrice }
    val lowStockCount = products.count { it.stockQuantity <= it.minStockLevel }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Inventory Header Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TOTAL INVENTORY VALUATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreenAccent)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatNaira(totalValuation),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${products.size} Catalog Items", fontSize = 12.sp, color = PureWhite)
                    Text(
                        "$lowStockCount items with low stock warning",
                        fontSize = 12.sp,
                        color = if (lowStockCount > 0) AmberWarning else MintGreenAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector: Levels vs Movement Audit
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PureWhite,
            contentColor = ForestGreenPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Current Stock (${products.size})", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Movement Audit (${stockMovements.size})", fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Current Stock List with Restock / Adjust actions
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.sku, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkCharcoal)
                                Text(
                                    "Min Level: %.0f %s • Unit Cost: %s".format(product.minStockLevel, product.unit, formatNaira(product.purchasePrice)),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (product.stockQuantity <= product.minStockLevel) AmberLight else LightGreyBg
                                ) {
                                    Text(
                                        text = "%.0f %s".format(product.stockQuantity, product.unit),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (product.stockQuantity <= product.minStockLevel) AmberWarning else DarkCharcoal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = {
                                        adjustingProduct = product
                                        adjustmentType = StockMovementType.RESTOCK
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Adjust", fontSize = 10.sp, color = PureWhite)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        } else {
            // Stock Movement Audit Log
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(stockMovements) { mov ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                val isPositive = mov.quantityChange > 0
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isPositive) EmeraldLight else CoralLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = if (isPositive) EmeraldSuccess else CoralError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(mov.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkCharcoal)
                                    Text("${mov.type.name} • ${mov.reason}", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        "${mov.performedBy} • ${formatDateTime(mov.timestamp)}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (mov.quantityChange > 0) "+%.0f".format(mov.quantityChange) else "%.0f".format(mov.quantityChange),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (mov.quantityChange > 0) EmeraldSuccess else CoralError
                                )
                                Text(
                                    text = "Now: %.0f".format(mov.newStock),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Stock Adjustment / Restock Dialog
    if (adjustingProduct != null) {
        val prod = adjustingProduct!!
        var qtyInput by remember { mutableStateOf("10") }
        var reasonInput by remember { mutableStateOf("Fresh store restock from supplier") }
        var type by remember { mutableStateOf(adjustmentType) }

        AlertDialog(
            onDismissRequest = { adjustingProduct = null },
            title = {
                Text("Stock Adjustment: ${prod.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Current stock: %.0f %s".format(prod.stockQuantity, prod.unit), fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            StockMovementType.RESTOCK to "Restock (+)",
                            StockMovementType.DAMAGE to "Damage (-)",
                            StockMovementType.EXPIRY to "Expiry (-)",
                            StockMovementType.ADJUSTMENT to "Audit (+/-)"
                        ).forEach { (mType, label) ->
                            val isSelected = type == mType
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        type = mType
                                        reasonInput = when (mType) {
                                            StockMovementType.RESTOCK -> "Stock purchased from distributor"
                                            StockMovementType.DAMAGE -> "Item damaged in store handling"
                                            StockMovementType.EXPIRY -> "Expired batch written off"
                                            else -> "Physical stocktaking discrepancy audit"
                                        }
                                    },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = qtyInput,
                        onValueChange = { qtyInput = it },
                        label = { Text("Quantity (${prod.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Reason / Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = qtyInput.toDoubleOrNull() ?: 0.0
                        val delta = if (type == StockMovementType.RESTOCK) qty else -qty
                        onAdjustStock(prod, type, delta, reasonInput.ifBlank { "Stock adjustment" })
                        adjustingProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Apply Adjustment", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { adjustingProduct = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
