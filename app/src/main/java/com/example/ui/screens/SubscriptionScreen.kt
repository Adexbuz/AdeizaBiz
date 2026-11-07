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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SubscriptionScreen(
    subscription: Subscription?,
    platformConfig: PlatformConfig?,
    paymentProofs: List<PaymentProofSubmission>,
    onSubmitProof: (amount: Double, months: Int, ref: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDurationMonths by remember { mutableStateOf(1) } // 1, 3, 12
    var paymentReference by remember { mutableStateOf("") }
    var transferNotes by remember { mutableStateOf("") }

    val monthlyPrice = platformConfig?.monthlyPriceNaira ?: 2000.0
    val quarterlyPrice = platformConfig?.quarterlyPriceNaira ?: 5500.0
    val annualPrice = platformConfig?.annualPriceNaira ?: 20000.0

    val payableAmount = when (selectedDurationMonths) {
        1 -> monthlyPrice
        3 -> quarterlyPrice
        12 -> annualPrice
        else -> monthlyPrice
    }

    val daysRemaining = if (subscription != null) {
        val diff = subscription.currentPeriodEnd - System.currentTimeMillis()
        (diff / (86_400_000L)).coerceAtLeast(0)
    } else 25

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightGreyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Subscription & Billing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
            Text("Affordable flat-rate access for store owner and all employees", fontSize = 12.sp, color = Color.Gray)
        }

        // Current Plan Status Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Text(subscription?.planName ?: "30-Day Free Trial", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    if (subscription?.status == SubscriptionStatus.ACTIVE) "Active Paid Plan" else "Free Trial Active",
                                    color = MintGreenLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintGreenAccent
                        ) {
                            Text(
                                "$daysRemaining Days Left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary,
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
                        Text("Standard Renewal Price:", fontSize = 12.sp, color = MintGreenLight)
                        Text(
                            "${formatNaira(monthlyPrice)} / month",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Renewal Bank Transfer Instructions Card
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
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BANK TRANSFER PAYMENT INSTRUCTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "To renew or extend your business subscription, make a transfer to the official AdeizaBiz account below and submit your payment reference for verification:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightGreyBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Bank:", fontSize = 12.sp, color = Color.Gray)
                                Text(platformConfig?.bankName ?: "First Bank of Nigeria", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Account Name:", fontSize = 12.sp, color = Color.Gray)
                                Text(platformConfig?.accountName ?: "AdeizaBiz Technologies Ltd", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Account Number:", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    platformConfig?.accountNumber ?: "3128945670",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ForestGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Submit Payment Proof Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SUBMIT PAYMENT VERIFICATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Select Subscription Duration:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            1 to "1 Month (${formatNaira(monthlyPrice)})",
                            3 to "3 Months (${formatNaira(quarterlyPrice)})",
                            12 to "1 Year (${formatNaira(annualPrice)})"
                        ).forEach { (m, label) ->
                            val isSelected = selectedDurationMonths == m
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDurationMonths = m },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForestGreenPrimary else LightGreyBg,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PureWhite else DarkCharcoal,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = paymentReference,
                        onValueChange = { paymentReference = it },
                        label = { Text("Bank Payment Reference / Session ID *") },
                        placeholder = { Text("e.g. FBN-NIP-9988223344") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = transferNotes,
                        onValueChange = { transferNotes = it },
                        label = { Text("Notes / Transfer Confirmation Details") },
                        placeholder = { Text("e.g. Paid ₦2,000 from Zenith Bank app at 3:15 PM") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (paymentReference.isNotBlank()) {
                                onSubmitProof(
                                    payableAmount,
                                    selectedDurationMonths,
                                    paymentReference.trim(),
                                    transferNotes.trim()
                                )
                                paymentReference = ""
                                transferNotes = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = paymentReference.isNotBlank()
                    ) {
                        Text("Submit Payment for Super Admin Approval", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Payment Proof History & Verification Status
        item {
            Text("SUBMITTED PAYMENT PROOFS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        if (paymentProofs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No payment submissions yet. You are currently on the 30-day free trial.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(paymentProofs) { proof ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(formatNaira(proof.amount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                            Text("Ref: ${proof.paymentReference}", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "Duration: ${proof.durationMonths} month(s) • ${formatDateTime(proof.submittedAt)}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (proof.status) {
                                ProofStatus.APPROVED -> EmeraldLight
                                ProofStatus.PENDING -> AmberLight
                                ProofStatus.REJECTED -> CoralLight
                            }
                        ) {
                            Text(
                                text = proof.status.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (proof.status) {
                                    ProofStatus.APPROVED -> EmeraldSuccess
                                    ProofStatus.PENDING -> AmberWarning
                                    ProofStatus.REJECTED -> CoralError
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
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
