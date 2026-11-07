package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Business
import com.example.data.model.Product
import com.example.data.model.SaleTransaction
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CoralError
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.ScreenDestination
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatNaira(amount: Double, currency: String = "₦"): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 2
    return "$currency${formatter.format(amount)}"
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun AdeizaTopBar(
    business: Business?,
    currentUser: UserAccount?,
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    unreadNotificationsCount: Int,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ForestGreenPrimary,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Business Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Original AdeizaBiz Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MintGreenAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AB",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = ForestGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AdeizaBiz",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Online / Offline Indicator Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOnline) EmeraldSuccess else AmberWarning,
                            modifier = Modifier.clickable { onToggleOnline() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                    contentDescription = if (isOnline) "Online" else "Offline POS",
                                    tint = PureWhite,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isOnline) "Online" else "Offline",
                                    color = PureWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = if (currentUser?.role == UserRole.SUPER_ADMIN) "Platform Super Admin" else (business?.name ?: "Adeiza Mega Supermarket"),
                        fontSize = 12.sp,
                        color = MintGreenLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Actions: Role Badge & Notifications
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Switcher Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MintGreenAccent.copy(alpha = 0.2f),
                    modifier = Modifier
                        .clickable { onOpenRoleSwitcher() }
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val roleLabel = when (currentUser?.role) {
                            UserRole.SUPER_ADMIN -> "Super Admin"
                            UserRole.BUSINESS_OWNER -> "Owner"
                            UserRole.WORKER -> "Cashier"
                            null -> "Guest"
                        }
                        Text(
                            text = roleLabel,
                            color = MintGreenAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            tint = MintGreenAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.size(36.dp)
                ) {
                    if (unreadNotificationsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AmberWarning,
                                    contentColor = PureWhite
                                ) {
                                    Text(text = "$unreadNotificationsCount", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = PureWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = PureWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdeizaBottomBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit,
    userRole: UserRole?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = PureWhite,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = PureWhite,
            contentColor = ForestGreenPrimary
        ) {
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.DASHBOARD,
                onClick = { onNavigate(ScreenDestination.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                label = { Text("Home", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreenPrimary,
                    selectedTextColor = ForestGreenPrimary,
                    indicatorColor = MintGreenAccent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_home")
            )

            NavigationBarItem(
                selected = currentScreen == ScreenDestination.PRODUCTS,
                onClick = { onNavigate(ScreenDestination.PRODUCTS) },
                icon = { Icon(Icons.Default.Inventory, contentDescription = "Products") },
                label = { Text("Products", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreenPrimary,
                    selectedTextColor = ForestGreenPrimary,
                    indicatorColor = MintGreenAccent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_products")
            )

            // Prominent "Sell" POS button in the center
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.SELL_POS,
                onClick = { onNavigate(ScreenDestination.SELL_POS) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ForestGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = "Sell",
                            tint = MintGreenAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = { Text("Sell", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ForestGreenPrimary) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.testTag("nav_sell")
            )

            NavigationBarItem(
                selected = currentScreen == ScreenDestination.REPORTS,
                onClick = { onNavigate(ScreenDestination.REPORTS) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
                label = { Text("Reports", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreenPrimary,
                    selectedTextColor = ForestGreenPrimary,
                    indicatorColor = MintGreenAccent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_reports")
            )

            NavigationBarItem(
                selected = currentScreen == ScreenDestination.SETTINGS || currentScreen == ScreenDestination.STAFF || currentScreen == ScreenDestination.CUSTOMERS || currentScreen == ScreenDestination.SUPER_ADMIN,
                onClick = { onNavigate(ScreenDestination.SETTINGS) },
                icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                label = { Text("More", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ForestGreenPrimary,
                    selectedTextColor = ForestGreenPrimary,
                    indicatorColor = MintGreenAccent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_more")
            )
        }
    }
}

@Composable
fun SummaryStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RoleSwitcherDialog(
    currentUser: UserAccount?,
    onSwitchRole: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Demo Account", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text(
                    "Test all 3 role levels and permission constraints instantly:",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Role 1: Super Admin
                RoleOptionCard(
                    roleName = "Super Admin (Platform Owner)",
                    personName = "Engr. Mohammed Adeiza",
                    description = "Manage businesses, approve payments, platform prices",
                    isSelected = currentUser?.role == UserRole.SUPER_ADMIN,
                    onClick = {
                        onSwitchRole(UserRole.SUPER_ADMIN)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Role 2: Business Owner
                RoleOptionCard(
                    roleName = "Business Owner",
                    personName = "Dr. Gabriel Adeiza (Store Owner)",
                    description = "Full control, financial profits, inventory, staff, sub",
                    isSelected = currentUser?.role == UserRole.BUSINESS_OWNER,
                    onClick = {
                        onSwitchRole(UserRole.BUSINESS_OWNER)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Role 3: Worker / Cashier
                RoleOptionCard(
                    roleName = "Business Worker (Cashier)",
                    personName = "Chidinma Okafor (Cashier)",
                    description = "Sell POS, scan barcode, own sales. Restricted from profit/settings",
                    isSelected = currentUser?.role == UserRole.WORKER,
                    onClick = {
                        onSwitchRole(UserRole.WORKER)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun RoleOptionCard(
    roleName: String,
    personName: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MintGreenLight else LightGreyBg,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ForestGreenPrimary) else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roleName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) ForestGreenPrimary else DarkCharcoal
                )
                Text(text = personName, fontSize = 12.sp, color = DarkCharcoal)
                Text(text = description, fontSize = 11.sp, color = Color.Gray)
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DigitalReceiptDialog(
    sale: SaleTransaction,
    business: Business?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MintGreenAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = business?.name ?: "Adeiza Mega Supermarket",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = DarkCharcoal
                )

                Text(
                    text = business?.address ?: "Wuse 2, Abuja, Nigeria",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Tel: ${business?.phone ?: "+234 802 345 6789"}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Receipt Meta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Receipt No:", fontSize = 12.sp, color = Color.Gray)
                    Text(sale.receiptNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Date & Time:", fontSize = 12.sp, color = Color.Gray)
                    Text(formatDateTime(sale.timestamp), fontSize = 12.sp, color = DarkCharcoal)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cashier:", fontSize = 12.sp, color = Color.Gray)
                    Text(sale.cashierName, fontSize = 12.sp, color = DarkCharcoal)
                }

                if (sale.customerName != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Customer:", fontSize = 12.sp, color = Color.Gray)
                        Text(sale.customerName, fontSize = 12.sp, color = DarkCharcoal)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Items Summary
                Text(
                    text = "PURCHASED ITEMS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = sale.itemsSummary.ifBlank { "Items breakdown recorded in database." },
                    fontSize = 12.sp,
                    color = DarkCharcoal,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Financial Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", fontSize = 13.sp, color = DarkCharcoal)
                    Text(formatNaira(sale.subtotal), fontSize = 13.sp)
                }

                if (sale.discount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", fontSize = 13.sp, color = CoralError)
                        Text("-${formatNaira(sale.discount)}", fontSize = 13.sp, color = CoralError)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL AMOUNT", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                    Text(
                        formatNaira(sale.totalAmount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Method:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = sale.paymentMethod.name.replace("_", " "),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkCharcoal
                    )
                }

                if (sale.paymentReference != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reference:", fontSize = 12.sp, color = Color.Gray)
                        Text(sale.paymentReference, fontSize = 12.sp, color = DarkCharcoal)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Amount Paid:", fontSize = 12.sp, color = Color.Gray)
                    Text(formatNaira(sale.amountPaid), fontSize = 12.sp, color = DarkCharcoal)
                }

                if (sale.changeAmount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Change Returned:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                        Text(formatNaira(sale.changeAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Footer note
                Text(
                    text = business?.receiptFooter ?: "Thank you for shopping with us! Visit again.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: WhatsApp Share & Print / Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareBody = """
                                *ADEIZABIZ DIGITAL RECEIPT*
                                *${business?.name ?: "Adeiza Mega Supermarket"}*
                                Receipt: ${sale.receiptNumber}
                                Date: ${formatDateTime(sale.timestamp)}
                                Cashier: ${sale.cashierName}
                                
                                Items: ${sale.itemsSummary}
                                Total: ${formatNaira(sale.totalAmount)}
                                Payment: ${sale.paymentMethod.name}
                                
                                Thank you for your patronage!
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Receipt via WhatsApp / SMS"))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Text("Done", color = PureWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun BarcodeScannerDialog(
    products: List<Product>,
    onBarcodeScanned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Barcode Scanner", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Scan with camera, connect external USB/Bluetooth scanner, or tap a sample product barcode below:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    label = { Text("Enter / Scan Barcode") },
                    placeholder = { Text("e.g. 615110001234") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (manualBarcode.isNotBlank()) {
                            onBarcodeScanned(manualBarcode.trim())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    enabled = manualBarcode.isNotBlank()
                ) {
                    Text("Search Barcode", color = PureWhite)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("QUICK TEST BARCODES:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                // List barcoded items for quick test
                val barcodedItems = products.filter { !it.barcode.isNullOrBlank() }.take(4)
                barcodedItems.forEach { p ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onBarcodeScanned(p.barcode ?: "")
                                onDismiss()
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = LightGreyBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text("Code: ${p.barcode}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(formatNaira(p.sellingPrice), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun NotificationsDialog(
    notifications: List<com.example.data.model.NotificationItem>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Store Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkCharcoal)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (notifications.isEmpty()) {
                    Text("No notifications at this time.", fontSize = 13.sp, color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = LightGreyBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkCharcoal)
                                        Text(formatDateTime(notif.timestamp), fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(notif.message, fontSize = 11.sp, color = DarkCharcoal)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

