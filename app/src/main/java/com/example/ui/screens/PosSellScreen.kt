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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.SaleTransaction
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.CameraBarcodeScannerModal
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosSellScreen(
    products: List<Product>,
    cartItems: List<CartItem>,
    cartDiscount: Double,
    selectedCustomer: Customer?,
    customers: List<Customer>,
    onAddToCart: (Product, Double, String?) -> Unit,
    onUpdateCartQty: (Int, Double) -> Unit,
    onRemoveCartItem: (Int) -> Unit,
    onClearCart: () -> Unit,
    onSetDiscount: (Double) -> Unit,
    onSelectCustomer: (Customer?) -> Unit,
    onCheckout: (PaymentMethod, Double, String?, (SaleTransaction) -> Unit) -> Unit,
    onBarcodeScanned: (String) -> Unit,
    onNavigateToAddProductWithBarcode: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showScannerDialog by remember { mutableStateOf(false) }
    var showCartSheet by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showCustomerPicker by remember { mutableStateOf(false) }

    val categories = listOf("All") + products.map { it.category }.distinct()

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.sku.contains(searchQuery, ignoreCase = true) ||
                (product.barcode != null && product.barcode.contains(searchQuery, ignoreCase = true))
        matchesCategory && matchesQuery
    }

    val cartTotalCount = cartItems.sumOf { it.quantity }
    val cartSubtotal = cartItems.sumOf { it.itemTotal }
    val cartGrandTotal = (cartSubtotal - cartDiscount).coerceAtLeast(0.0)

    Box(modifier = modifier.fillMaxSize().background(LightGreyBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar & Barcode Scanner Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search name, SKU, or code...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pos_search_input")
                )

                // Barcode Scan Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ForestGreenPrimary,
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { showScannerDialog = true }
                        .testTag("pos_barcode_scan_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Barcode",
                            tint = MintGreenAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) ForestGreenPrimary else PureWhite,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PureWhite else DarkCharcoal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No products found", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("Try a different search term or category.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pos_product_grid")
                ) {
                    items(filteredProducts) { product ->
                        PosProductCard(
                            product = product,
                            onAddToCart = { qty, unit -> onAddToCart(product, qty, unit) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Floating Cart Bar (Bottom)
        if (cartItems.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 72.dp)
                    .fillMaxWidth()
                    .clickable { showCartSheet = true }
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .testTag("pos_floating_cart_bar"),
                color = ForestGreenPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MintGreenAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "%.0f items in cart".format(cartTotalCount),
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Tap to review & checkout",
                                color = MintGreenLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatNaira(cartGrandTotal),
                            fontWeight = FontWeight.Bold,
                            color = MintGreenAccent,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showCheckoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreenAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("pos_checkout_button")
                        ) {
                            Text("Checkout", color = ForestGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Live Camera Barcode Scanner with Auto-Upload to Cart
    if (showScannerDialog) {
        CameraBarcodeScannerModal(
            products = products,
            onBarcodeScannedAndUpload = { code ->
                onBarcodeScanned(code)
                val matched = products.firstOrNull { it.barcode == code || it.sku.equals(code, ignoreCase = true) }
                if (matched != null) {
                    onAddToCart(matched, 1.0, null)
                }
            },
            onNavigateToAddProductWithBarcode = { code ->
                showScannerDialog = false
                onNavigateToAddProductWithBarcode?.invoke(code)
            },
            onDismiss = { showScannerDialog = false }
        )
    }

    // Cart Bottom Sheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            containerColor = PureWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Shopping Cart", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkCharcoal)
                    TextButton(onClick = onClearCart) {
                        Text("Clear All", color = CoralError)
                    }
                }

                // Customer Selection
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightGreyBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomerPicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCustomer?.fullName ?: "Walk-in Customer (Tap to assign)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkCharcoal
                            )
                        }
                        if (selectedCustomer != null) {
                            IconButton(onClick = { onSelectCustomer(null) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Customer", tint = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(cartItems) { index, item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LightGreyBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                                    Text(
                                        "${formatNaira(item.effectivePrice)} per ${item.selectedUnit}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "Total: ${formatNaira(item.itemTotal)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ForestGreenPrimary
                                    )
                                }

                                // Quantity Controls
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateCartQty(index, item.quantity - 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = DarkCharcoal)
                                    }

                                    Text(
                                        text = "%.0f".format(item.quantity),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = { onUpdateCartQty(index, item.quantity + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = ForestGreenPrimary)
                                    }

                                    IconButton(
                                        onClick = { onRemoveCartItem(index) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralError)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(10.dp))

                // Totals
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", fontSize = 13.sp, color = Color.Gray)
                    Text(formatNaira(cartSubtotal), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Grand Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                    Text(formatNaira(cartGrandTotal), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showCartSheet = false
                        showCheckoutDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Proceed to Checkout", color = PureWhite, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Customer Picker Dialog
    if (showCustomerPicker) {
        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            title = { Text("Select Customer", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Assign to registered customer for purchase history or credit sale:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.height(240.dp)) {
                        items(customers) { c ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSelectCustomer(c)
                                        showCustomerPicker = false
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = LightGreyBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(c.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(c.phone, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    if (c.creditBalance > 0) {
                                        Text(
                                            "Debt: ${formatNaira(c.creditBalance)}",
                                            fontSize = 11.sp,
                                            color = CoralError,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerPicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Checkout Dialog
    if (showCheckoutDialog) {
        CheckoutModal(
            grandTotal = cartGrandTotal,
            subtotal = cartSubtotal,
            selectedCustomer = selectedCustomer,
            onDismiss = { showCheckoutDialog = false },
            onConfirmCheckout = { method, amountPaid, ref ->
                onCheckout(method, amountPaid, ref) {
                    showCheckoutDialog = false
                    showCartSheet = false
                }
            }
        )
    }
}

@Composable
private fun PosProductCard(
    product: Product,
    onAddToCart: (Double, String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddToCart(1.0, null) }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Category & SKU Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.sku,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                if (product.stockQuantity <= product.minStockLevel) {
                    Text(
                        text = "Low Stock",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWarning
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = DarkCharcoal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatNaira(product.sellingPrice),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = ForestGreenPrimary
            )

            Text(
                text = "Stock: %.0f %s".format(product.stockQuantity, product.unit),
                fontSize = 11.sp,
                color = if (product.stockQuantity <= 0) CoralError else Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-unit quick button if carton/pack conversion exists
            if (product.unitChildName != null && product.unitChildPrice != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { onAddToCart(1.0, product.unit) },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Text(product.unit.take(4), fontSize = 10.sp, color = PureWhite)
                    }

                    OutlinedButton(
                        onClick = { onAddToCart(1.0, product.unitChildName) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Text(product.unitChildName.take(6), fontSize = 10.sp)
                    }
                }
            } else {
                Button(
                    onClick = { onAddToCart(1.0, null) },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = MintGreenAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Cart", fontSize = 11.sp, color = PureWhite)
                }
            }
        }
    }
}

@Composable
private fun CheckoutModal(
    grandTotal: Double,
    subtotal: Double,
    selectedCustomer: Customer?,
    onDismiss: () -> Unit,
    onConfirmCheckout: (PaymentMethod, Double, String?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var cashPaidInput by remember { mutableStateOf(grandTotal.toString()) }
    var transferRefInput by remember { mutableStateOf("") }
    var cardRefInput by remember { mutableStateOf("") }

    val amountReceived = cashPaidInput.toDoubleOrNull() ?: 0.0
    val changeAmount = (amountReceived - grandTotal).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Complete Sale", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ForestGreenPrimary)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Grand Total Display
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MintGreenLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL PAYABLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        Text(
                            formatNaira(grandTotal),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        PaymentMethod.CASH to "Cash",
                        PaymentMethod.BANK_TRANSFER to "Transfer",
                        PaymentMethod.CARD to "Card",
                        PaymentMethod.CREDIT to "Credit"
                    ).forEach { (method, label) ->
                        val isSelected = selectedMethod == method
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ForestGreenPrimary else LightGreyBg,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PureWhite else DarkCharcoal,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedMethod) {
                    PaymentMethod.CASH -> {
                        OutlinedTextField(
                            value = cashPaidInput,
                            onValueChange = { cashPaidInput = it },
                            label = { Text("Amount Received (₦)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick cash denominations
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(1000.0, 2000.0, 5000.0, 10000.0).forEach { denom ->
                                OutlinedButton(
                                    onClick = { cashPaidInput = denom.toString() },
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("₦%.0fk".format(denom / 1000), fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Change calculation
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LightGreyBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Change Returned:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(
                                    formatNaira(changeAmount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }

                    PaymentMethod.BANK_TRANSFER -> {
                        OutlinedTextField(
                            value = transferRefInput,
                            onValueChange = { transferRefInput = it },
                            label = { Text("Bank Payment Reference / Session ID") },
                            placeholder = { Text("e.g. TRF-GTB-492019") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    PaymentMethod.CARD -> {
                        OutlinedTextField(
                            value = cardRefInput,
                            onValueChange = { cardRefInput = it },
                            label = { Text("POS Terminal Approval Code / RRN") },
                            placeholder = { Text("e.g. STANBIC-RRN-9922") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    PaymentMethod.CREDIT -> {
                        if (selectedCustomer == null) {
                            Text(
                                "Warning: You must assign a customer before recording a credit sale.",
                                color = CoralError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "This sale of ${formatNaira(grandTotal)} will be added to ${selectedCustomer.fullName}'s outstanding credit ledger.",
                                fontSize = 12.sp,
                                color = DarkCharcoal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ref = when (selectedMethod) {
                        PaymentMethod.BANK_TRANSFER -> transferRefInput.ifBlank { "TRF-APP-VERIFIED" }
                        PaymentMethod.CARD -> cardRefInput.ifBlank { "POS-CARD-VERIFIED" }
                        else -> null
                    }
                    val paid = if (selectedMethod == PaymentMethod.CASH) amountReceived else grandTotal
                    onConfirmCheckout(selectedMethod, paid, ref)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                enabled = selectedMethod != PaymentMethod.CREDIT || selectedCustomer != null
            ) {
                Text("Complete Sale", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
