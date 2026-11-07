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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
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
fun OnboardingScreen(
    onRegister: (
        businessName: String,
        ownerFullName: String,
        username: String,
        email: String,
        phone: String,
        category: String,
        address: String,
        currency: String
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ownerFullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Supermarket") }
    var address by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Nigeria") }
    var currency by remember { mutableStateOf("₦") }

    val categories = listOf("Supermarket", "Provision Store", "Pharmacy", "Boutique", "Electronics", "Groceries", "Restaurant")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkCharcoal)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Register New Store", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("30-Day Free Trial • Instant Cloud Activation", fontSize = 12.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Trial Features Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MintGreenAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("All-Inclusive 30-Day Free Trial", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("✓ Unlimited products, barcodes & inventory", color = MintGreenLight, fontSize = 11.sp)
                    Text("✓ Multi-cashier staff logins with custom permissions", color = MintGreenLight, fontSize = 11.sp)
                    Text("✓ Fast POS, digital receipts & WhatsApp sharing", color = MintGreenLight, fontSize = 11.sp)
                    Text("✓ Offline sales support with automatic database sync", color = MintGreenLight, fontSize = 11.sp)
                }
            }
        }

        // Registration Form Fields
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("STORE & OWNER DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business / Store Name *") },
                        placeholder = { Text("e.g. Divine Grace Supermarket") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ownerFullName,
                        onValueChange = { ownerFullName = it },
                        label = { Text("Owner Full Name *") },
                        placeholder = { Text("e.g. Musa Ibrahim") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username *") },
                            placeholder = { Text("musa24") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone *") },
                            placeholder = { Text("+234...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Account Password *") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Business Category Selection
                    Text("Business Category:", fontSize = 11.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Supermarket", "Pharmacy", "Provisions", "Boutique").forEach { cat ->
                            val isSelected = category.startsWith(cat)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { category = cat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Store Address / State") },
                        placeholder = { Text("e.g. Ikeja, Lagos, Nigeria") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Currency Selector
                    Text("Primary Currency:", fontSize = 11.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("₦" to "Naira (₦)", "$" to "USD ($)", "€" to "EUR (€)", "£" to "GBP (£)").forEach { (curr, label) ->
                            val isSelected = currency == curr
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { currency = curr },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (businessName.isNotBlank() && ownerFullName.isNotBlank() && username.isNotBlank()) {
                                onRegister(
                                    businessName.trim(),
                                    ownerFullName.trim(),
                                    username.trim().lowercase(),
                                    email.trim().ifBlank { "${username}@store.ng" },
                                    phone.trim(),
                                    category,
                                    address.trim(),
                                    currency
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = businessName.isNotBlank() && ownerFullName.isNotBlank() && username.isNotBlank()
                    ) {
                        Text("Launch Store & Start 30-Day Free Trial", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
