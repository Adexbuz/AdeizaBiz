package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.SaleTransaction
import com.example.data.model.Subscription
import com.example.data.model.SubscriptionStatus
import com.example.data.model.UserAccount
import com.example.ui.components.SummaryStatCard
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
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun DashboardScreen(
    currentUser: UserAccount?,
    products: List<Product>,
    lowStockProducts: List<Product>,
    recentSales: List<SaleTransaction>,
    subscription: Subscription?,
    onNavigate: (ScreenDestination) -> Unit,
    onViewReceipt: (SaleTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate live database metrics
    val totalProductsCount = products.size
    val totalStockSum = products.sumOf { it.stockQuantity }
    val todaySalesSum = recentSales.filter { !it.isRefunded }.sumOf { it.totalAmount }
    val todayTransactionsCount = recentSales.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Store Subtitle
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Owner"} 👋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Text(
                        text = "Sell Smarter. Manage Better.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // New Sale shortcut pill
                Button(
                    onClick = { onNavigate(ScreenDestination.SELL_POS) },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("quick_action_sell_top")
                ) {
                    Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp), tint = MintGreenAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Sale", fontSize = 12.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 30-Day Free Trial / Subscription Banner
        item {
            val daysRemaining = if (subscription != null) {
                val diff = subscription.currentPeriodEnd - System.currentTimeMillis()
                (diff / (86_400_000L)).coerceAtLeast(0)
            } else 25

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MintGreenAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("AdeizaBiz 30-Day Free Trial", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("$daysRemaining days remaining on full-featured trial", color = MintGreenLight, fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { onNavigate(ScreenDestination.SUBSCRIPTION) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MintGreenAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintGreenAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Manage", fontSize = 11.sp)
                    }
                }
            }
        }

        // Section: 4 Summary Cards (Exact 2x2 grid from reference video)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. TOTAL PRODUCTS
                    SummaryStatCard(
                        title = "Total Products",
                        value = totalProductsCount.toString(),
                        subtitle = "Active in catalog",
                        icon = Icons.Default.Inventory,
                        iconBgColor = MintGreenLight,
                        iconTint = ForestGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // 2. TOTAL STOCK
                    SummaryStatCard(
                        title = "Total Stock",
                        value = "%.0f units".format(totalStockSum),
                        subtitle = "Inventory available",
                        icon = Icons.Default.Inventory2,
                        iconBgColor = EmeraldLight,
                        iconTint = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 3. TODAY'S SALES
                    SummaryStatCard(
                        title = "Today's Sales",
                        value = formatNaira(todaySalesSum),
                        subtitle = "Total revenue today",
                        icon = Icons.Default.AttachMoney,
                        iconBgColor = AmberLight,
                        iconTint = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )

                    // 4. TRANSACTIONS
                    SummaryStatCard(
                        title = "Transactions",
                        value = todayTransactionsCount.toString(),
                        subtitle = "Completed receipts",
                        icon = Icons.Default.ReceiptLong,
                        iconBgColor = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: QUICK ACTIONS (Four buttons from reference video)
        item {
            Column {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = "New Sale",
                        icon = Icons.Default.PointOfSale,
                        containerColor = ForestGreenPrimary,
                        contentColor = PureWhite,
                        onClick = { onNavigate(ScreenDestination.SELL_POS) },
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        title = "Add Product",
                        icon = Icons.Default.AddShoppingCart,
                        containerColor = PureWhite,
                        contentColor = DarkCharcoal,
                        onClick = { onNavigate(ScreenDestination.ADD_PRODUCT) },
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        title = "Products",
                        icon = Icons.Default.Inventory,
                        containerColor = PureWhite,
                        contentColor = DarkCharcoal,
                        onClick = { onNavigate(ScreenDestination.PRODUCTS) },
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        title = "History",
                        icon = Icons.Default.History,
                        containerColor = PureWhite,
                        contentColor = DarkCharcoal,
                        onClick = { onNavigate(ScreenDestination.SALES_HISTORY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: LOW STOCK ALERTS (From reference video)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOW STOCK ITEMS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkCharcoal
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CoralLight
                        ) {
                            Text(
                                text = "${lowStockProducts.size} items",
                                color = CoralError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (lowStockProducts.isEmpty()) {
                        Text(
                            text = "All products are well-stocked above minimum levels.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        lowStockProducts.take(4).forEach { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { onNavigate(ScreenDestination.INVENTORY) },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = DarkCharcoal,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Min stock: ${product.minStockLevel} ${product.unit}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (product.stockQuantity <= 0) CoralLight else AmberLight
                                ) {
                                    Text(
                                        text = "${product.stockQuantity} ${product.unit}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (product.stockQuantity <= 0) CoralError else AmberWarning,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: RECENT TRANSACTIONS (From reference video)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT TRANSACTIONS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )

                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            color = ForestGreenPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigate(ScreenDestination.SALES_HISTORY) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (recentSales.isEmpty()) {
                        Text(
                            text = "No sales recorded yet. Start by tapping 'New Sale'.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        recentSales.take(5).forEach { sale ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { onViewReceipt(sale) },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MintGreenLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = ForestGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = sale.receiptNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DarkCharcoal
                                        )
                                        Text(
                                            text = "${sale.cashierName} • ${sale.paymentMethod.name}",
                                            fontSize = 11.sp,
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
                                    Text(
                                        text = if (sale.isRefunded) "Refunded" else "Completed",
                                        fontSize = 10.sp,
                                        color = if (sale.isRefunded) CoralError else EmeraldSuccess,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (containerColor == ForestGreenPrimary) MintGreenAccent else ForestGreenPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
