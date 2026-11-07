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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.Business
import com.example.data.model.PaymentProofSubmission
import com.example.data.model.PlatformConfig
import com.example.data.model.ProofStatus
import com.example.data.model.Subscription
import com.example.data.model.SubscriptionStatus
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

@Composable
fun SuperAdminScreen(
    businesses: List<Business>,
    subscriptions: List<Subscription>,
    paymentProofs: List<PaymentProofSubmission>,
    platformConfig: PlatformConfig?,
    onReviewProof: (PaymentProofSubmission, Boolean, String?) -> Unit,
    onUpdatePlatformConfig: (bankName: String, accountName: String, accountNumber: String, monthlyPrice: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfigModal by remember { mutableStateOf(false) }
    val pendingProofs = paymentProofs.filter { it.status == ProofStatus.PENDING }
    val activeCount = subscriptions.count { it.status == SubscriptionStatus.ACTIVE }
    val trialCount = subscriptions.count { it.status == SubscriptionStatus.TRIAL_ACTIVE }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Super Admin Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }
                    Text("AdeizaBiz SaaS Platform Command Center", fontSize = 12.sp, color = Color.Gray)
                }

                Button(
                    onClick = { showConfigModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PriceChange, contentDescription = null, modifier = Modifier.size(16.dp), tint = MintGreenAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pricing & Bank", fontSize = 11.sp, color = PureWhite)
                }
            }
        }

        // Platform Summary Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PLATFORM REVENUE & TENANTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintGreenAccent)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "${businesses.size} Businesses",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                            Text("$trialCount on 30-day trial • $activeCount paid", fontSize = 11.sp, color = MintGreenLight)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (pendingProofs.isNotEmpty()) AmberWarning else MintGreenAccent
                        ) {
                            Text(
                                "${pendingProofs.size} Proofs Pending",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingProofs.isNotEmpty()) PureWhite else ForestGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ForestGreenLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Pricing:", fontSize = 12.sp, color = MintGreenLight)
                        Text(
                            "${formatNaira(platformConfig?.monthlyPriceNaira ?: 2000.0)} / month",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Section: PENDING PAYMENT PROOFS AWAITING APPROVAL
        item {
            Text(
                "PENDING PAYMENT PROOFS (${pendingProofs.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
        }

        if (pendingProofs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No pending payments awaiting review. All store payments are up to date.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(pendingProofs) { proof ->
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
                            Column {
                                Text(proof.businessName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                                Text("Owner: ${proof.ownerName} • ${proof.ownerEmail}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(formatNaira(proof.amount), fontWeight = FontWeight.Black, fontSize = 16.sp, color = ForestGreenPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LightGreyBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Payment Ref: ${proof.paymentReference}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DarkCharcoal)
                                Text("Submitted: ${formatDateTime(proof.submittedAt)}", fontSize = 10.sp, color = Color.Gray)
                                if (proof.proofDescription.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Proof note: ${proof.proofDescription}", fontSize = 11.sp, color = DarkCharcoal)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Approve vs Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReviewProof(proof, false, "Payment reference unconfirmed") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralError),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reject", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onReviewProof(proof, true, "Verified by Mohammed Adeiza") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve & Extend", fontSize = 12.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: REGISTERED BUSINESSES
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("REGISTERED BUSINESS TENANTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        items(businesses) { biz ->
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
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(biz.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                        Text("${biz.category} • ${biz.phone}", fontSize = 11.sp, color = Color.Gray)
                        Text(biz.address, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (biz.isSuspended) CoralLight else EmeraldLight
                    ) {
                        Text(
                            text = if (biz.isSuspended) "Suspended" else "Active Store",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (biz.isSuspended) CoralError else EmeraldSuccess,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Configure Pricing & Bank Details Modal
    if (showConfigModal) {
        var bank by remember { mutableStateOf(platformConfig?.bankName ?: "First Bank of Nigeria") }
        var accName by remember { mutableStateOf(platformConfig?.accountName ?: "AdeizaBiz Technologies Ltd") }
        var accNum by remember { mutableStateOf(platformConfig?.accountNumber ?: "3128945670") }
        var priceInput by remember { mutableStateOf(platformConfig?.monthlyPriceNaira?.toString() ?: "2000") }

        AlertDialog(
            onDismissRequest = { showConfigModal = false },
            title = {
                Text("Configure Platform Pricing & Bank", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ForestGreenPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Monthly Subscription Price (₦) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bank,
                        onValueChange = { bank = it },
                        label = { Text("Receiving Bank Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = accName,
                        onValueChange = { accName = it },
                        label = { Text("Receiving Account Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = accNum,
                        onValueChange = { accNum = it },
                        label = { Text("Receiving Account Number *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceInput.toDoubleOrNull() ?: 2000.0
                        onUpdatePlatformConfig(bank.trim(), accName.trim(), accNum.trim(), p)
                        showConfigModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Save Settings", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigModal = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
