package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdeizaBizDatabase
import com.example.data.model.Branch
import com.example.data.model.Business
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentProofSubmission
import com.example.data.model.PlatformConfig
import com.example.data.model.Product
import com.example.data.model.SaleTransaction
import com.example.data.model.StockMovement
import com.example.data.model.StockMovementType
import com.example.data.model.Subscription
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.repository.AdeizaBizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class ScreenDestination {
    DASHBOARD,
    PRODUCTS,
    ADD_PRODUCT,
    SELL_POS,
    SALES_HISTORY,
    INVENTORY,
    STAFF,
    CUSTOMERS,
    REPORTS,
    SUBSCRIPTION,
    SUPER_ADMIN,
    SETTINGS,
    BRANCHES,
    ONBOARDING
}

class AdeizaBizViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AdeizaBizDatabase.getDatabase(application, viewModelScope)
    val repository = AdeizaBizRepository(database.dao())

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(ScreenDestination.DASHBOARD)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _navBackStack = MutableStateFlow<List<ScreenDestination>>(emptyList())
    val navBackStack: StateFlow<List<ScreenDestination>> = _navBackStack.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            _navBackStack.value = _navBackStack.value + _currentScreen.value
            _currentScreen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        val stack = _navBackStack.value
        return if (stack.isNotEmpty()) {
            val previous = stack.last()
            _navBackStack.value = stack.dropLast(1)
            _currentScreen.value = previous
            true
        } else {
            false
        }
    }

    // Connectivity Status (Online / Offline simulator)
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun toggleOnlineStatus() {
        _isOnline.value = !_isOnline.value
    }

    // Active User & Business
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _currentBusiness = MutableStateFlow<Business?>(null)
    val currentBusiness: StateFlow<Business?> = _currentBusiness.asStateFlow()

    // Active Business ID
    private val businessId: String
        get() = _currentBusiness.value?.id ?: "biz_adeiza_01"

    // Data Flows
    val products: StateFlow<List<Product>> = repository.observeProducts("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.observeLowStock("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSales: StateFlow<List<SaleTransaction>> = repository.observeSales("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSales: StateFlow<List<SaleTransaction>> = repository.observeRecentSales("biz_adeiza_01", 10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Customer>> = repository.observeCustomers("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staffMembers: StateFlow<List<UserAccount>> = repository.observeUsers("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscription: StateFlow<Subscription?> = repository.observeSubscription("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val stockMovements: StateFlow<List<StockMovement>> = repository.observeStockMovements("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val branches: StateFlow<List<Branch>> = repository.observeBranches("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.observeNotifications("biz_adeiza_01")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Super Admin Flows
    val allBusinesses: StateFlow<List<Business>> = repository.observeBusinesses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubscriptions: StateFlow<List<Subscription>> = repository.observeAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentProofs: StateFlow<List<PaymentProofSubmission>> = repository.observeAllPaymentProofs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformConfig: StateFlow<PlatformConfig?> = repository.observePlatformConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Shopping Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartDiscount = MutableStateFlow(0.0)
    val cartDiscount: StateFlow<Double> = _cartDiscount.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    // Last Completed Receipt for Digital Receipt Modal
    private val _activeReceipt = MutableStateFlow<SaleTransaction?>(null)
    val activeReceipt: StateFlow<SaleTransaction?> = _activeReceipt.asStateFlow()

    // Banner message
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    init {
        // Initialize active user & business from DB
        viewModelScope.launch(Dispatchers.IO) {
            val owner = repository.getUserByUsername("adeiza")
            val biz = repository.getBusiness("biz_adeiza_01")
            _currentUser.value = owner
            _currentBusiness.value = biz
        }
    }

    // Role Quick Switcher (For easy evaluator / user testing)
    fun switchUserRole(role: UserRole) {
        viewModelScope.launch(Dispatchers.IO) {
            when (role) {
                UserRole.SUPER_ADMIN -> {
                    val user = repository.getUserByUsername("superadmin")
                    _currentUser.value = user
                    _currentScreen.value = ScreenDestination.SUPER_ADMIN
                }
                UserRole.BUSINESS_OWNER -> {
                    val user = repository.getUserByUsername("adeiza")
                    _currentUser.value = user
                    _currentScreen.value = ScreenDestination.DASHBOARD
                }
                UserRole.WORKER -> {
                    val user = repository.getUserByUsername("chidinma")
                    _currentUser.value = user
                    _currentScreen.value = ScreenDestination.SELL_POS
                }
            }
        }
    }

    // Cart Operations
    fun addToCart(product: Product, quantity: Double = 1.0, selectedUnit: String? = null) {
        val unitToUse = selectedUnit ?: product.unit
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.product.id == product.id && it.selectedUnit == unitToUse }

        val currentQtyInCart = if (existingIndex >= 0) currentList[existingIndex].quantity else 0.0
        val requestedQty = currentQtyInCart + quantity

        // Check stock availability
        val stockDeduction = if (unitToUse == product.unitChildName && product.unitConversionRate > 0) {
            requestedQty / product.unitConversionRate
        } else {
            requestedQty
        }

        if (stockDeduction > product.stockQuantity) {
            showFeedback("Insufficient stock! Available: ${product.stockQuantity} ${product.unit}")
            return
        }

        if (existingIndex >= 0) {
            val item = currentList[existingIndex]
            currentList[existingIndex] = item.copy(quantity = requestedQty)
        } else {
            currentList.add(CartItem(product = product, quantity = quantity, selectedUnit = unitToUse))
        }
        _cartItems.value = currentList
        showFeedback("Added ${product.name} to cart")
    }

    fun updateCartItemQuantity(index: Int, newQuantity: Double) {
        val currentList = _cartItems.value.toMutableList()
        if (index in currentList.indices) {
            if (newQuantity <= 0) {
                currentList.removeAt(index)
            } else {
                val item = currentList[index]
                val stockDeduction = if (item.selectedUnit == item.product.unitChildName && item.product.unitConversionRate > 0) {
                    newQuantity / item.product.unitConversionRate
                } else {
                    newQuantity
                }
                if (stockDeduction > item.product.stockQuantity) {
                    showFeedback("Cannot exceed available stock of ${item.product.stockQuantity}")
                    return
                }
                currentList[index] = item.copy(quantity = newQuantity)
            }
            _cartItems.value = currentList
        }
    }

    fun removeCartItem(index: Int) {
        val currentList = _cartItems.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _cartItems.value = currentList
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartDiscount.value = 0.0
        _selectedCustomer.value = null
    }

    fun setCartDiscount(discount: Double) {
        _cartDiscount.value = discount.coerceAtLeast(0.0)
    }

    fun selectCustomer(customer: Customer?) {
        _selectedCustomer.value = customer
    }

    // Complete Sale Checkout
    fun checkout(
        paymentMethod: PaymentMethod,
        amountPaid: Double,
        reference: String?,
        onSuccess: (SaleTransaction) -> Unit
    ) {
        val user = _currentUser.value ?: return
        val items = _cartItems.value
        if (items.isEmpty()) {
            showFeedback("Cart is empty!")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sale = repository.processSale(
                    businessId = businessId,
                    branchId = "branch_main",
                    cashier = user,
                    customer = _selectedCustomer.value,
                    cartItems = items,
                    discount = _cartDiscount.value,
                    paymentMethod = paymentMethod,
                    amountPaid = amountPaid,
                    paymentReference = reference
                )
                _activeReceipt.value = sale
                clearCart()
                showFeedback("Sale completed successfully! Receipt: ${sale.receiptNumber}")
                onSuccess(sale)
            } catch (e: Exception) {
                showFeedback("Error processing sale: ${e.message}")
            }
        }
    }

    fun viewReceipt(sale: SaleTransaction?) {
        _activeReceipt.value = sale
    }

    fun closeReceipt() {
        _activeReceipt.value = null
    }

    // Product Operations
    fun saveProduct(
        id: String?,
        name: String,
        category: String,
        barcode: String?,
        purchasePrice: Double,
        sellingPrice: Double,
        stockQuantity: Double,
        minStockLevel: Double,
        unit: String,
        unitConversionRate: Double,
        unitChildName: String?,
        unitChildPrice: Double?,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val isNew = id == null
            val prodId = id ?: UUID.randomUUID().toString()
            val existingProducts = products.value
            val nextSkuNumber = (existingProducts.size + 1).toString().padStart(6, '0')
            val sku = if (isNew) "ADB-$nextSkuNumber" else existingProducts.firstOrNull { it.id == id }?.sku ?: "ADB-$nextSkuNumber"

            val product = Product(
                id = prodId,
                businessId = businessId,
                sku = sku,
                barcode = barcode?.ifBlank { null },
                name = name,
                category = category,
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                stockQuantity = stockQuantity,
                minStockLevel = minStockLevel,
                unit = unit,
                unitConversionRate = unitConversionRate,
                unitChildName = unitChildName?.ifBlank { null },
                unitChildPrice = unitChildPrice,
                description = description
            )

            if (isNew) {
                repository.insertProduct(product)
                repository.adjustStock(
                    businessId = businessId,
                    product = product,
                    type = StockMovementType.INITIAL,
                    quantityDelta = stockQuantity,
                    reason = "New product registered",
                    performedBy = user.fullName
                )
                showFeedback("Product created with SKU $sku")
            } else {
                repository.updateProduct(product)
                showFeedback("Product ${product.name} updated")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            showFeedback("Product ${product.name} deleted")
        }
    }

    fun adjustStock(product: Product, type: StockMovementType, quantityDelta: Double, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            repository.adjustStock(businessId, product, type, quantityDelta, reason, user.fullName)
            showFeedback("Stock adjusted for ${product.name}")
        }
    }

    // Customer Operations
    fun saveCustomer(fullName: String, phone: String, email: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = Customer(
                id = UUID.randomUUID().toString(),
                businessId = businessId,
                fullName = fullName,
                phone = phone,
                email = email,
                address = address
            )
            repository.insertCustomer(customer)
            showFeedback("Customer $fullName registered")
        }
    }

    fun recordDebtPayment(customer: Customer, amount: Double, method: PaymentMethod, ref: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            repository.recordCustomerPayment(customer, amount, method, ref, user.fullName)
            showFeedback("Payment of ₦$amount recorded for ${customer.fullName}")
        }
    }

    // Staff Operations
    fun addStaffMember(
        fullName: String,
        username: String,
        phone: String,
        email: String,
        canEditProducts: Boolean,
        canViewProfit: Boolean,
        canManageStaff: Boolean,
        canAdjustStock: Boolean,
        canRefund: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserAccount(
                id = UUID.randomUUID().toString(),
                username = username,
                fullName = fullName,
                email = email,
                phone = phone,
                role = UserRole.WORKER,
                businessId = businessId,
                canProcessSales = true,
                canViewProducts = true,
                canEditProducts = canEditProducts,
                canViewProfitReports = canViewProfit,
                canManageStaff = canManageStaff,
                canAdjustStock = canAdjustStock,
                canAuthorizeRefunds = canRefund
            )
            repository.insertUser(user)
            showFeedback("Staff member $fullName created")
        }
    }

    fun toggleStaffStatus(user: UserAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUserActive(user.id, !user.isActive)
            showFeedback("${user.fullName} status updated")
        }
    }

    // Manual Payment Submission
    fun submitPaymentProof(amount: Double, durationMonths: Int, reference: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            val biz = _currentBusiness.value ?: return@launch
            val config = platformConfig.value
            repository.submitPaymentProof(
                businessId = biz.id,
                businessName = biz.name,
                ownerName = user.fullName,
                ownerEmail = user.email,
                amount = amount,
                durationMonths = durationMonths,
                reference = reference,
                bankName = config?.bankName ?: "First Bank of Nigeria",
                accountName = config?.accountName ?: "AdeizaBiz Technologies Ltd",
                notes = notes
            )
            showFeedback("Payment proof submitted for Super Admin review")
        }
    }

    // Super Admin Actions
    fun reviewPaymentProof(proof: PaymentProofSubmission, approve: Boolean, notes: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reviewPaymentProof(proof, approve, notes)
            showFeedback(if (approve) "Proof approved & subscription extended!" else "Proof rejected")
        }
    }

    fun updatePlatformConfig(bankName: String, accountName: String, accountNumber: String, monthlyPrice: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = platformConfig.value ?: PlatformConfig()
            val updated = current.copy(
                bankName = bankName,
                accountName = accountName,
                accountNumber = accountNumber,
                monthlyPriceNaira = monthlyPrice
            )
            repository.setPlatformConfig(updated)
            showFeedback("Platform configuration updated")
        }
    }

    // Refund Transaction
    fun refundTransaction(sale: SaleTransaction, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = _currentUser.value ?: return@launch
            repository.refundSale(sale, reason, user)
            showFeedback("Receipt ${sale.receiptNumber} refunded & inventory restored")
        }
    }

    // Register New Business Onboarding
    fun registerNewBusiness(
        businessName: String,
        ownerFullName: String,
        username: String,
        email: String,
        phone: String,
        category: String,
        address: String,
        currency: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val (biz, owner) = repository.registerBusiness(
                businessName = businessName,
                ownerFullName = ownerFullName,
                username = username,
                email = email,
                phone = phone,
                category = category,
                address = address,
                currency = currency
            )
            _currentBusiness.value = biz
            _currentUser.value = owner
            _currentScreen.value = ScreenDestination.DASHBOARD
            showFeedback("Welcome to AdeizaBiz! 30-day trial activated for $businessName")
        }
    }
}
