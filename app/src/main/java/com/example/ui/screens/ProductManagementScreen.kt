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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import com.example.ui.components.CameraBarcodeScannerModal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.components.formatNaira
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CoralError
import com.example.ui.theme.CoralLight
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    products: List<Product>,
    currentUser: UserAccount?,
    onSaveProduct: (
        id: String?,
        name: String,
        category: String,
        barcode: String?,
        cost: Double,
        selling: Double,
        stock: Double,
        minStock: Double,
        unit: String,
        unitConversion: Double,
        childName: String?,
        childPrice: Double?,
        desc: String
    ) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    initialBarcodeToRegister: String? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var isCreatingNew by remember { mutableStateOf(initialBarcodeToRegister != null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val categories = listOf("All") + products.map { it.category }.distinct()

    val filtered = products.filter { p ->
        val matchesCategory = selectedCategory == "All" || p.category.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.sku.contains(searchQuery, ignoreCase = true) ||
                (p.barcode != null && p.barcode.contains(searchQuery, ignoreCase = true))
        matchesCategory && matchesQuery
    }

    val canEdit = currentUser?.role != UserRole.WORKER || currentUser.canEditProducts

    Box(modifier = modifier.fillMaxSize().background(LightGreyBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Products Catalog", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Text("${products.size} total items registered", fontSize = 12.sp, color = Color.Gray)
                }

                if (canEdit) {
                    Button(
                        onClick = { isCreatingNew = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_product_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MintGreenAccent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item", fontSize = 12.sp, color = PureWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, SKU or barcode...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories Filter
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) ForestGreenPrimary else PureWhite,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            color = if (isSelected) PureWhite else DarkCharcoal,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Products List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered) { product ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(product.sku, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                                    if (product.barcode != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = LightGreyBg
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color.Gray)
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(product.barcode, fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("(No Barcode)", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DarkCharcoal)
                                Text(
                                    "Cost: ${formatNaira(product.purchasePrice)} • Selling: ${formatNaira(product.sellingPrice)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                if (product.unitChildName != null) {
                                    Text(
                                        "Unit conversion: 1 ${product.unit} = %.0f ${product.unitChildName}s".format(product.unitConversionRate),
                                        fontSize = 10.sp,
                                        color = ForestGreenPrimary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (product.stockQuantity <= product.minStockLevel) AmberLight else LightGreyBg
                                ) {
                                    Text(
                                        text = "%.0f %s".format(product.stockQuantity, product.unit),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (product.stockQuantity <= product.minStockLevel) AmberWarning else DarkCharcoal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                if (canEdit) {
                                    Row {
                                        IconButton(onClick = { editingProduct = product }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(onClick = { productToDelete = product }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralError, modifier = Modifier.size(16.dp))
                                        }
                                    }
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
    }

    // Add or Edit Product Dialog
    if (isCreatingNew || editingProduct != null) {
        val target = editingProduct
        ProductFormDialog(
            product = target,
            initialBarcode = if (target == null) initialBarcodeToRegister else null,
            products = products,
            onDismiss = {
                isCreatingNew = false
                editingProduct = null
            },
            onSave = { name, category, barcode, cost, selling, stock, minStock, unit, unitConversion, childName, childPrice, desc ->
                onSaveProduct(
                    target?.id,
                    name,
                    category,
                    barcode,
                    cost,
                    selling,
                    stock,
                    minStock,
                    unit,
                    unitConversion,
                    childName,
                    childPrice,
                    desc
                )
                isCreatingNew = false
                editingProduct = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${productToDelete?.name}' from the store catalog?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { onDeleteProduct(it) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralError)
                ) {
                    Text("Delete", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun ProductFormDialog(
    product: Product?,
    initialBarcode: String? = null,
    products: List<Product> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: String,
        barcode: String?,
        cost: Double,
        selling: Double,
        stock: Double,
        minStock: Double,
        unit: String,
        unitConversion: Double,
        childName: String?,
        childPrice: Double?,
        desc: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Food & Groceries") }
    var barcode by remember { mutableStateOf(product?.barcode ?: initialBarcode ?: "") }
    var showScanner by remember { mutableStateOf(false) }
    var costPrice by remember { mutableStateOf(product?.purchasePrice?.toString() ?: "0") }
    var sellingPrice by remember { mutableStateOf(product?.sellingPrice?.toString() ?: "0") }
    var initialStock by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "10") }
    var minStock by remember { mutableStateOf(product?.minStockLevel?.toString() ?: "5") }
    var unit by remember { mutableStateOf(product?.unit ?: "Pieces") }
    var unitConversionRate by remember { mutableStateOf(product?.unitConversionRate?.toString() ?: "1") }
    var childName by remember { mutableStateOf(product?.unitChildName ?: "") }
    var childPrice by remember { mutableStateOf(product?.unitChildPrice?.toString() ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }

    val unitOptions = listOf("Pieces", "Bottles", "Packs", "Cartons", "Bags", "Kilograms", "Litres", "Tins")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (product == null) "Add New Product" else "Edit Product",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ForestGreenPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Food, Drinks, Pharmacy) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode (Scan with Camera or Type)") },
                        placeholder = { Text("Factory barcode code") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ForestGreenPrimary,
                        modifier = Modifier
                            .size(52.dp)
                            .clickable { showScanner = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan with Camera", tint = MintGreenAccent, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                if (showScanner) {
                    CameraBarcodeScannerModal(
                        products = products,
                        onBarcodeScannedAndUpload = { code ->
                            barcode = code
                            showScanner = false
                        },
                        onDismiss = { showScanner = false }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text("Cost Price (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = sellingPrice,
                        onValueChange = { sellingPrice = it },
                        label = { Text("Selling Price (₦) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = initialStock,
                        onValueChange = { initialStock = it },
                        label = { Text("Initial Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text("Min Alert Level") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit of Measurement (e.g. Pieces, Cartons)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Sub-unit unit conversion (e.g. 1 carton contains 24 packs)
                Text("UNIT CONVERSION (OPTIONAL):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = unitConversionRate,
                        onValueChange = { unitConversionRate = it },
                        label = { Text("Rate (e.g. 12 or 24)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = childName,
                        onValueChange = { childName = it },
                        label = { Text("Sub-unit (e.g. Bottle, Pack)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = childPrice,
                    onValueChange = { childPrice = it },
                    label = { Text("Sub-unit Price (₦)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("e.g. ₦250 per bottle") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name.trim(),
                            category.trim(),
                            barcode.trim().ifBlank { null },
                            costPrice.toDoubleOrNull() ?: 0.0,
                            sellingPrice.toDoubleOrNull() ?: 0.0,
                            initialStock.toDoubleOrNull() ?: 0.0,
                            minStock.toDoubleOrNull() ?: 5.0,
                            unit.trim(),
                            unitConversionRate.toDoubleOrNull() ?: 1.0,
                            childName.trim().ifBlank { null },
                            childPrice.toDoubleOrNull(),
                            description.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                enabled = name.isNotBlank() && sellingPrice.isNotBlank()
            ) {
                Text("Save Product", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
