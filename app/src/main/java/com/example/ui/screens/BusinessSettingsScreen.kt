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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Branch
import com.example.data.model.Business
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun BusinessSettingsScreen(
    business: Business?,
    currentUser: UserAccount?,
    branches: List<Branch>,
    onUpdateBusiness: (Business) -> Unit,
    onAddBranch: (Branch) -> Unit,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditInfoModal by remember { mutableStateOf(false) }
    var showAddBranchModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Settings & Modules", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
            Text("Manage store configuration, staff, branches & account", fontSize = 12.sp, color = Color.Gray)
        }

        // Store Identity Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MintGreenAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(business?.name ?: "Adeiza Mega Supermarket", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                                Text("${business?.category ?: "Supermarket"} • Currency: ${business?.currency ?: "₦"}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        if (currentUser?.role != UserRole.WORKER) {
                            Button(
                                onClick = { showEditInfoModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Edit", fontSize = 11.sp, color = PureWhite)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(business?.address ?: "Plot 14, Commercial Avenue, Wuse 2, Abuja", fontSize = 12.sp, color = DarkCharcoal)
                    Text("Tel: ${business?.phone ?: "+234 802 345 6789"} • Email: ${business?.email ?: "info@store.ng"}", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        // Quick Module Navigation Links
        item {
            Text("BUSINESS MODULES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column {
                    SettingsNavigationRow(
                        title = "Staff Management & Permissions",
                        subtitle = "Configure employees, cashiers and access rules",
                        icon = Icons.Default.People,
                        onClick = { onNavigate(ScreenDestination.STAFF) }
                    )
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(horizontal = 14.dp))

                    SettingsNavigationRow(
                        title = "Customer Directory & Credit Ledger",
                        subtitle = "Customer accounts, debt tracking and repayments",
                        icon = Icons.Default.Receipt,
                        onClick = { onNavigate(ScreenDestination.CUSTOMERS) }
                    )
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(horizontal = 14.dp))

                    SettingsNavigationRow(
                        title = "Subscription & Bank Transfer Billing",
                        subtitle = "30-day free trial, renewal proofs & invoice status",
                        icon = Icons.Default.CreditCard,
                        onClick = { onNavigate(ScreenDestination.SUBSCRIPTION) }
                    )
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(horizontal = 14.dp))

                    SettingsNavigationRow(
                        title = "Register Another Business Workspace",
                        subtitle = "Create a new tenant with instant 30-day trial",
                        icon = Icons.Default.AddBusiness,
                        onClick = { onNavigate(ScreenDestination.ONBOARDING) }
                    )
                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(horizontal = 14.dp))

                    SettingsNavigationRow(
                        title = "Platform Super Admin Console",
                        subtitle = "Platform overview, payment approvals & pricing",
                        icon = Icons.Default.Security,
                        onClick = { onNavigate(ScreenDestination.SUPER_ADMIN) }
                    )
                }
            }
        }

        // Multiple Branches Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BUSINESS BRANCHES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                TextButton(onClick = { showAddBranchModal = true }) {
                    Text("+ Add Branch", color = ForestGreenPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    branches.forEachIndexed { idx, branch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(branch.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkCharcoal)
                                    Text(branch.address, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            if (branch.isMain) {
                                Surface(shape = RoundedCornerShape(6.dp), color = MintGreenLight) {
                                    Text("Main", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        if (idx < branches.size - 1) {
                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Edit Business Details Modal
    if (showEditInfoModal && business != null) {
        var storeName by remember { mutableStateOf(business.name) }
        var phone by remember { mutableStateOf(business.phone) }
        var email by remember { mutableStateOf(business.email) }
        var address by remember { mutableStateOf(business.address) }
        var currency by remember { mutableStateOf(business.currency) }
        var header by remember { mutableStateOf(business.receiptHeader) }
        var footer by remember { mutableStateOf(business.receiptFooter) }

        AlertDialog(
            onDismissRequest = { showEditInfoModal = false },
            title = { Text("Edit Business Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreenPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("Business Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("₦", "$", "€", "£").forEach { curr ->
                            val isSelected = currency == curr
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { currency = curr },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg
                            ) {
                                Text(
                                    text = curr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = footer,
                        onValueChange = { footer = it },
                        label = { Text("Receipt Footer Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateBusiness(
                            business.copy(
                                name = storeName,
                                phone = phone,
                                email = email,
                                address = address,
                                currency = currency,
                                receiptFooter = footer
                            )
                        )
                        showEditInfoModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Save Changes", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditInfoModal = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Add Branch Modal
    if (showAddBranchModal && business != null) {
        var branchName by remember { mutableStateOf("") }
        var branchAddress by remember { mutableStateOf("") }
        var branchPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBranchModal = false },
            title = { Text("Add Business Branch", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreenPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        label = { Text("Branch Name (e.g. Maitama Branch) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = branchAddress,
                        onValueChange = { branchAddress = it },
                        label = { Text("Branch Address *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = branchPhone,
                        onValueChange = { branchPhone = it },
                        label = { Text("Branch Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (branchName.isNotBlank() && branchAddress.isNotBlank()) {
                            onAddBranch(
                                Branch(
                                    id = "branch_${System.currentTimeMillis()}",
                                    businessId = business.id,
                                    name = branchName.trim(),
                                    address = branchAddress.trim(),
                                    phone = branchPhone.trim(),
                                    isMain = false
                                )
                            )
                            showAddBranchModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    enabled = branchName.isNotBlank() && branchAddress.isNotBlank()
                ) {
                    Text("Add Location", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBranchModal = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MintGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkCharcoal)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}
