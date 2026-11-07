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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.SaleTransaction
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.components.formatNaira
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CoralError
import com.example.ui.theme.CoralLight
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PureWhite

@Composable
fun StaffManagementScreen(
    staffMembers: List<UserAccount>,
    sales: List<SaleTransaction>,
    onAddStaff: (
        fullName: String,
        username: String,
        phone: String,
        email: String,
        canEditProducts: Boolean,
        canViewProfit: Boolean,
        canManageStaff: Boolean,
        canAdjustStock: Boolean,
        canRefund: Boolean
    ) -> Unit,
    onToggleStaffStatus: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

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
                Text("Staff & Permissions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Text("${staffMembers.size} store accounts configured", fontSize = 12.sp, color = Color.Gray)
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MintGreenAccent)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Staff", fontSize = 12.sp, color = PureWhite)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Staff Performance Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MintGreenAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CASHIER PERFORMANCE LEADERBOARD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreenAccent)
                }

                Spacer(modifier = Modifier.height(8.dp))

                staffMembers.filter { it.role == UserRole.WORKER }.forEach { worker ->
                    val workerSales = sales.filter { it.cashierId == worker.id && !it.isRefunded }
                    val totalValue = workerSales.sumOf { it.totalAmount }
                    val count = workerSales.size

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(worker.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PureWhite)
                            Text("$count completed sales", fontSize = 11.sp, color = MintGreenLight)
                        }
                        Text(
                            formatNaira(totalValue),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MintGreenAccent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("REGISTERED EMPLOYEES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        // Staff List with Permission Badges
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(staffMembers) { staff ->
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
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (staff.isActive) MintGreenLight else CoralLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (staff.isActive) ForestGreenPrimary else CoralError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(staff.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                                    Text("@${staff.username} • ${staff.phone}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (staff.isActive) EmeraldLight else CoralLight
                            ) {
                                Text(
                                    text = if (staff.isActive) "Active" else "Suspended",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (staff.isActive) EmeraldSuccess else CoralError,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Permissions Chips
                        Text("Configured Permissions:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            PermissionPill(label = "POS Sell", granted = staff.canProcessSales)
                            PermissionPill(label = "Edit Products", granted = staff.canEditProducts)
                            PermissionPill(label = "Profit Reports", granted = staff.canViewProfitReports)
                            PermissionPill(label = "Refunds", granted = staff.canAuthorizeRefunds)
                        }

                        if (staff.role == UserRole.WORKER) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onToggleStaffStatus(staff) }) {
                                    Text(
                                        if (staff.isActive) "Deactivate Account" else "Activate Account",
                                        color = if (staff.isActive) CoralError else EmeraldSuccess,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add Staff Modal
    if (showAddDialog) {
        AddStaffDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { fullName, username, phone, email, canEdit, canProfit, canManage, canAdjust, canRefund ->
                onAddStaff(fullName, username, phone, email, canEdit, canProfit, canManage, canAdjust, canRefund)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PermissionPill(label: String, granted: Boolean) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (granted) MintGreenLight else LightGreyBg
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (granted) ForestGreenPrimary else Color.Gray,
            fontWeight = if (granted) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AddStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        fullName: String,
        username: String,
        phone: String,
        email: String,
        canEdit: Boolean,
        canProfit: Boolean,
        canManage: Boolean,
        canAdjust: Boolean,
        canRefund: Boolean
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var canEditProducts by remember { mutableStateOf(false) }
    var canViewProfit by remember { mutableStateOf(false) }
    var canManageStaff by remember { mutableStateOf(false) }
    var canAdjustStock by remember { mutableStateOf(false) }
    var canRefund by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Employee Account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ForestGreenPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Login Password *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
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

                Spacer(modifier = Modifier.height(4.dp))
                Text("CUSTOM WORKER PERMISSIONS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

                PermissionToggleRow("Can Edit Products & Prices", canEditProducts) { canEditProducts = it }
                PermissionToggleRow("Can View Owner Profit Reports", canViewProfit) { canViewProfit = it }
                PermissionToggleRow("Can Manage Other Staff", canManageStaff) { canManageStaff = it }
                PermissionToggleRow("Can Adjust Inventory / Damage", canAdjustStock) { canAdjustStock = it }
                PermissionToggleRow("Can Authorise Customer Refunds", canRefund) { canRefund = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && username.isNotBlank()) {
                        onConfirm(
                            fullName.trim(),
                            username.trim().lowercase(),
                            phone.trim(),
                            email.trim(),
                            canEditProducts,
                            canViewProfit,
                            canManageStaff,
                            canAdjustStock,
                            canRefund
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                enabled = fullName.isNotBlank() && username.isNotBlank()
            ) {
                Text("Create Account", color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun PermissionToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = DarkCharcoal, modifier = Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = ForestGreenPrimary)
        )
    }
}
