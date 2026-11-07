package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.AdeizaBottomBar
import com.example.ui.components.AdeizaTopBar
import com.example.ui.components.DigitalReceiptDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.screens.BusinessSettingsScreen
import com.example.ui.screens.CustomerManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PosSellScreen
import com.example.ui.screens.ProductManagementScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesHistoryScreen
import com.example.ui.screens.StaffManagementScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.SuperAdminScreen
import com.example.ui.theme.AdeizaBizTheme
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.LightGreyBg
import com.example.ui.theme.MintGreenAccent
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdeizaBizViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: AdeizaBizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdeizaBizTheme {
                AdeizaBizApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdeizaBizApp(viewModel: AdeizaBizViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentBusiness by viewModel.currentBusiness.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()

    val products by viewModel.products.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()
    val recentSales by viewModel.recentSales.collectAsState()
    val allSales by viewModel.allSales.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartDiscount by viewModel.cartDiscount.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val staffMembers by viewModel.staffMembers.collectAsState()
    val subscription by viewModel.subscription.collectAsState()
    val stockMovements by viewModel.stockMovements.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val activeReceipt by viewModel.activeReceipt.collectAsState()

    // Super Admin state
    val allBusinesses by viewModel.allBusinesses.collectAsState()
    val allSubscriptions by viewModel.allSubscriptions.collectAsState()
    val allPaymentProofs by viewModel.allPaymentProofs.collectAsState()
    val platformConfig by viewModel.platformConfig.collectAsState()

    var showRoleSwitcher by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var pendingBarcodeToRegister by remember { mutableStateOf<String?>(null) }

    // Android back handler
    BackHandler(enabled = currentScreen != ScreenDestination.DASHBOARD) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenDestination.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightGreyBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                AdeizaTopBar(
                    business = currentBusiness,
                    currentUser = currentUser,
                    isOnline = isOnline,
                    onToggleOnline = { viewModel.toggleOnlineStatus() },
                    onOpenRoleSwitcher = { showRoleSwitcher = true },
                    unreadNotificationsCount = notifications.count { !it.isRead },
                    onOpenNotifications = { showNotificationsDialog = true }
                )

                // Feedback banner
                AnimatedVisibility(
                    visible = feedbackMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        color = ForestGreenPrimary,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = feedbackMessage ?: "",
                            color = PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (currentScreen != ScreenDestination.ONBOARDING) {
                AdeizaBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    userRole = currentUser?.role
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.DASHBOARD -> {
                    DashboardScreen(
                        currentUser = currentUser,
                        products = products,
                        lowStockProducts = lowStockProducts,
                        recentSales = recentSales,
                        subscription = subscription,
                        onNavigate = { viewModel.navigateTo(it) },
                        onViewReceipt = { viewModel.viewReceipt(it) }
                    )
                }

                ScreenDestination.SELL_POS -> {
                    PosSellScreen(
                        products = products,
                        cartItems = cartItems,
                        cartDiscount = cartDiscount,
                        selectedCustomer = selectedCustomer,
                        customers = customers,
                        onAddToCart = { prod, qty, unit -> viewModel.addToCart(prod, qty, unit) },
                        onUpdateCartQty = { idx, qty -> viewModel.updateCartItemQuantity(idx, qty) },
                        onRemoveCartItem = { viewModel.removeCartItem(it) },
                        onClearCart = { viewModel.clearCart() },
                        onSetDiscount = { viewModel.setCartDiscount(it) },
                        onSelectCustomer = { viewModel.selectCustomer(it) },
                        onCheckout = { method, amount, ref, onSuccess ->
                            viewModel.checkout(method, amount, ref, onSuccess)
                        },
                        onBarcodeScanned = { code ->
                            val matched = products.firstOrNull { it.barcode == code || it.sku.equals(code, ignoreCase = true) }
                            if (matched != null) {
                                viewModel.addToCart(matched, 1.0, null)
                            } else {
                                viewModel.showFeedback("Barcode $code not found in catalog")
                            }
                        },
                        onNavigateToAddProductWithBarcode = { code ->
                            pendingBarcodeToRegister = code
                            viewModel.navigateTo(ScreenDestination.PRODUCTS)
                        }
                    )
                }

                ScreenDestination.PRODUCTS, ScreenDestination.ADD_PRODUCT -> {
                    ProductManagementScreen(
                        products = products,
                        currentUser = currentUser,
                        initialBarcodeToRegister = pendingBarcodeToRegister,
                        onSaveProduct = { id, name, cat, barcode, cost, selling, stock, min, unit, conv, child, childPrice, desc ->
                            viewModel.saveProduct(id, name, cat, barcode, cost, selling, stock, min, unit, conv, child, childPrice, desc)
                            pendingBarcodeToRegister = null
                        },
                        onDeleteProduct = { viewModel.deleteProduct(it) }
                    )
                }

                ScreenDestination.INVENTORY -> {
                    InventoryScreen(
                        products = products,
                        stockMovements = stockMovements,
                        onAdjustStock = { prod, type, delta, reason ->
                            viewModel.adjustStock(prod, type, delta, reason)
                        }
                    )
                }

                ScreenDestination.SALES_HISTORY -> {
                    SalesHistoryScreen(
                        sales = allSales,
                        currentUser = currentUser,
                        onViewReceipt = { viewModel.viewReceipt(it) },
                        onRefundSale = { sale, reason -> viewModel.refundTransaction(sale, reason) }
                    )
                }

                ScreenDestination.STAFF -> {
                    StaffManagementScreen(
                        staffMembers = staffMembers,
                        sales = allSales,
                        onAddStaff = { fullName, username, phone, email, canEdit, canProfit, canManage, canAdjust, canRefund ->
                            viewModel.addStaffMember(fullName, username, phone, email, canEdit, canProfit, canManage, canAdjust, canRefund)
                        },
                        onToggleStaffStatus = { viewModel.toggleStaffStatus(it) }
                    )
                }

                ScreenDestination.CUSTOMERS -> {
                    CustomerManagementScreen(
                        customers = customers,
                        onAddCustomer = { name, phone, email, address ->
                            viewModel.saveCustomer(name, phone, email, address)
                        },
                        onRecordDebtPayment = { cust, amount, method, ref ->
                            viewModel.recordDebtPayment(cust, amount, method, ref)
                        }
                    )
                }

                ScreenDestination.REPORTS -> {
                    ReportsScreen(
                        sales = allSales,
                        products = products
                    )
                }

                ScreenDestination.SUBSCRIPTION -> {
                    SubscriptionScreen(
                        subscription = subscription,
                        platformConfig = platformConfig,
                        paymentProofs = allPaymentProofs.filter { it.businessId == currentBusiness?.id },
                        onSubmitProof = { amount, months, ref, notes ->
                            viewModel.submitPaymentProof(amount, months, ref, notes)
                        }
                    )
                }

                ScreenDestination.SUPER_ADMIN -> {
                    SuperAdminScreen(
                        businesses = allBusinesses,
                        subscriptions = allSubscriptions,
                        paymentProofs = allPaymentProofs,
                        platformConfig = platformConfig,
                        onReviewProof = { proof, approved, notes ->
                            viewModel.reviewPaymentProof(proof, approved, notes)
                        },
                        onUpdatePlatformConfig = { bank, accName, accNum, price ->
                            viewModel.updatePlatformConfig(bank, accName, accNum, price)
                        }
                    )
                }

                ScreenDestination.SETTINGS, ScreenDestination.BRANCHES -> {
                    BusinessSettingsScreen(
                        business = currentBusiness,
                        currentUser = currentUser,
                        branches = branches,
                        onUpdateBusiness = {
                            viewModel.showFeedback("Business details updated")
                        },
                        onAddBranch = {
                            viewModel.showFeedback("New branch ${it.name} added")
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }

                ScreenDestination.ONBOARDING -> {
                    OnboardingScreen(
                        onRegister = { name, owner, user, email, phone, cat, addr, curr ->
                            viewModel.registerNewBusiness(name, owner, user, email, phone, cat, addr, curr)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    // Role Switcher Modal
    if (showRoleSwitcher) {
        RoleSwitcherDialog(
            currentUser = currentUser,
            onSwitchRole = { role ->
                viewModel.switchUserRole(role)
            },
            onDismiss = { showRoleSwitcher = false }
        )
    }

    // Digital Receipt Modal
    if (activeReceipt != null) {
        DigitalReceiptDialog(
            sale = activeReceipt!!,
            business = currentBusiness,
            onDismiss = { viewModel.closeReceipt() }
        )
    }

    // Store Notifications Modal
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotificationsDialog = false }
        )
    }
}
