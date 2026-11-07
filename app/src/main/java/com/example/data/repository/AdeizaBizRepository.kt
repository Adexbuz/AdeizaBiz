package com.example.data.repository

import com.example.data.local.AdeizaBizDao
import com.example.data.model.Branch
import com.example.data.model.Business
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.CustomerPayment
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentProofSubmission
import com.example.data.model.PlatformConfig
import com.example.data.model.Product
import com.example.data.model.ProofStatus
import com.example.data.model.SaleItemEntity
import com.example.data.model.SaleTransaction
import com.example.data.model.StockMovement
import com.example.data.model.StockMovementType
import com.example.data.model.Subscription
import com.example.data.model.SubscriptionStatus
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AdeizaBizRepository(private val dao: AdeizaBizDao) {

    // --- Users ---
    suspend fun getUserByUsername(username: String): UserAccount? = dao.getUserByUsername(username)
    suspend fun getUserById(id: String): UserAccount? = dao.getUserById(id)
    fun observeUsers(businessId: String): Flow<List<UserAccount>> = dao.observeUsersForBusiness(businessId)
    suspend fun insertUser(user: UserAccount) = dao.insertUser(user)
    suspend fun updateUser(user: UserAccount) = dao.updateUser(user)
    suspend fun setUserActive(userId: String, isActive: Boolean) = dao.setUserActive(userId, isActive)

    // --- Businesses & Branches ---
    suspend fun getBusiness(id: String): Business? = dao.getBusinessById(id)
    fun observeBusinesses(): Flow<List<Business>> = dao.observeAllBusinesses()
    suspend fun updateBusiness(business: Business) = dao.updateBusiness(business)
    fun observeBranches(businessId: String): Flow<List<Branch>> = dao.observeBranches(businessId)
    suspend fun addBranch(branch: Branch) = dao.insertBranch(branch)

    // --- Products ---
    fun observeProducts(businessId: String): Flow<List<Product>> = dao.observeProducts(businessId)
    fun searchProducts(businessId: String, query: String): Flow<List<Product>> = dao.searchProducts(businessId, query)
    suspend fun getProductByBarcode(businessId: String, barcode: String): Product? = dao.getProductByBarcode(businessId, barcode)
    suspend fun getProductById(id: String): Product? = dao.getProductById(id)
    fun observeLowStock(businessId: String): Flow<List<Product>> = dao.observeLowStockProducts(businessId)
    suspend fun insertProduct(product: Product) = dao.insertProduct(product)
    suspend fun updateProduct(product: Product) = dao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)

    // --- Sales & Checkout ---
    fun observeSales(businessId: String): Flow<List<SaleTransaction>> = dao.observeSales(businessId)
    fun observeSalesByCashier(businessId: String, cashierId: String): Flow<List<SaleTransaction>> = dao.observeSalesByCashier(businessId, cashierId)
    fun observeRecentSales(businessId: String, limit: Int = 10): Flow<List<SaleTransaction>> = dao.observeRecentSales(businessId, limit)
    suspend fun getSaleItems(saleId: String): List<SaleItemEntity> = dao.getSaleItems(saleId)

    suspend fun processSale(
        businessId: String,
        branchId: String,
        cashier: UserAccount,
        customer: Customer?,
        cartItems: List<CartItem>,
        discount: Double,
        paymentMethod: PaymentMethod,
        amountPaid: Double,
        paymentReference: String?
    ): SaleTransaction {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val randomSuffix = (100..999).random()
        val receiptNumber = "REC-${dateFormat.format(Date(now))}-$randomSuffix"
        val saleId = UUID.randomUUID().toString()

        val subtotal = cartItems.sumOf { it.itemTotal }
        val grandTotal = (subtotal - discount).coerceAtLeast(0.0)
        val isCredit = paymentMethod == PaymentMethod.CREDIT
        val changeAmount = if (!isCredit && amountPaid >= grandTotal) amountPaid - grandTotal else 0.0

        // Build itemized entities & update stock atomically
        val saleItemEntities = mutableListOf<SaleItemEntity>()
        val itemSummaryList = mutableListOf<String>()

        for (item in cartItems) {
            val deduction = item.actualStockDeduction
            val currentProduct = dao.getProductById(item.product.id)
            val prevStock = currentProduct?.stockQuantity ?: item.product.stockQuantity
            val newStock = (prevStock - deduction).coerceAtLeast(0.0)

            // Deduct stock in DB
            dao.updateStock(item.product.id, -deduction, now)

            // Record stock movement
            dao.insertStockMovement(
                StockMovement(
                    id = UUID.randomUUID().toString(),
                    businessId = businessId,
                    productId = item.product.id,
                    productName = item.product.name,
                    type = StockMovementType.SALE,
                    quantityChange = -deduction,
                    previousStock = prevStock,
                    newStock = newStock,
                    reason = "Sale receipt $receiptNumber",
                    performedBy = cashier.fullName,
                    timestamp = now
                )
            )

            // Low stock check notification
            if (newStock <= item.product.minStockLevel) {
                dao.insertNotification(
                    NotificationItem(
                        id = UUID.randomUUID().toString(),
                        businessId = businessId,
                        title = "Low Stock Alert: ${item.product.name}",
                        message = "Only $newStock ${item.product.unit} remaining. Minimum level is ${item.product.minStockLevel}.",
                        type = "LOW_STOCK",
                        timestamp = now
                    )
                )
            }

            saleItemEntities.add(
                SaleItemEntity(
                    id = UUID.randomUUID().toString(),
                    saleId = saleId,
                    productId = item.product.id,
                    productName = item.product.name,
                    sku = item.product.sku,
                    quantity = item.quantity,
                    unit = item.selectedUnit,
                    unitPrice = item.effectivePrice,
                    purchaseCost = item.product.purchasePrice,
                    subtotal = item.itemTotal
                )
            )
            itemSummaryList.add("${item.product.name} (x${item.quantity})")
        }

        // Customer credit update if credit sale
        if (isCredit && customer != null) {
            dao.updateCustomerBalance(customer.id, debtDelta = grandTotal, spentDelta = grandTotal)
        } else if (customer != null) {
            dao.updateCustomerBalance(customer.id, debtDelta = 0.0, spentDelta = grandTotal)
        }

        val sale = SaleTransaction(
            id = saleId,
            receiptNumber = receiptNumber,
            businessId = businessId,
            branchId = branchId,
            cashierId = cashier.id,
            cashierName = cashier.fullName,
            customerId = customer?.id,
            customerName = customer?.fullName ?: "Walk-in Customer",
            subtotal = subtotal,
            discount = discount,
            totalAmount = grandTotal,
            amountPaid = if (isCredit) 0.0 else amountPaid,
            changeAmount = changeAmount,
            paymentMethod = paymentMethod,
            paymentReference = paymentReference,
            isCredit = isCredit,
            isSynced = true,
            timestamp = now,
            itemsSummary = itemSummaryList.joinToString(", ")
        )

        dao.insertSale(sale)
        dao.insertSaleItems(saleItemEntities)

        return sale
    }

    suspend fun refundSale(sale: SaleTransaction, reason: String, performedBy: UserAccount) {
        val now = System.currentTimeMillis()
        val items = dao.getSaleItems(sale.id)
        for (item in items) {
            val product = dao.getProductById(item.productId)
            val prevStock = product?.stockQuantity ?: 0.0
            val restoredStock = prevStock + item.quantity
            dao.updateStock(item.productId, item.quantity, now)
            dao.insertStockMovement(
                StockMovement(
                    id = UUID.randomUUID().toString(),
                    businessId = sale.businessId,
                    productId = item.productId,
                    productName = item.productName,
                    type = StockMovementType.ADJUSTMENT,
                    quantityChange = item.quantity,
                    previousStock = prevStock,
                    newStock = restoredStock,
                    reason = "Refund for ${sale.receiptNumber}: $reason",
                    performedBy = performedBy.fullName,
                    timestamp = now
                )
            )
        }
        dao.updateSale(sale.copy(isRefunded = true, refundReason = reason))
    }

    // --- Stock Movements & Adjustments ---
    fun observeStockMovements(businessId: String): Flow<List<StockMovement>> = dao.observeStockMovements(businessId)

    suspend fun adjustStock(
        businessId: String,
        product: Product,
        type: StockMovementType,
        quantityDelta: Double,
        reason: String,
        performedBy: String
    ) {
        val now = System.currentTimeMillis()
        val prevStock = product.stockQuantity
        val newStock = (prevStock + quantityDelta).coerceAtLeast(0.0)

        dao.updateStock(product.id, quantityDelta, now)
        dao.insertStockMovement(
            StockMovement(
                id = UUID.randomUUID().toString(),
                businessId = businessId,
                productId = product.id,
                productName = product.name,
                type = type,
                quantityChange = quantityDelta,
                previousStock = prevStock,
                newStock = newStock,
                reason = reason,
                performedBy = performedBy,
                timestamp = now
            )
        )
    }

    // --- Customers & Debt ---
    fun observeCustomers(businessId: String): Flow<List<Customer>> = dao.observeCustomers(businessId)
    suspend fun insertCustomer(customer: Customer) = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)
    fun observeCustomerPayments(customerId: String): Flow<List<CustomerPayment>> = dao.observeCustomerPayments(customerId)

    suspend fun recordCustomerPayment(
        customer: Customer,
        amount: Double,
        method: PaymentMethod,
        reference: String,
        recordedBy: String
    ) {
        val now = System.currentTimeMillis()
        dao.insertCustomerPayment(
            CustomerPayment(
                id = UUID.randomUUID().toString(),
                customerId = customer.id,
                businessId = customer.businessId,
                amount = amount,
                paymentMethod = method,
                reference = reference,
                timestamp = now,
                recordedBy = recordedBy
            )
        )
        // Deduct from debt
        dao.updateCustomerBalance(customer.id, debtDelta = -amount, spentDelta = 0.0)
    }

    // --- Subscriptions & Manual Payment Proofs ---
    fun observeSubscription(businessId: String): Flow<Subscription?> = dao.observeSubscription(businessId)
    fun observeAllSubscriptions(): Flow<List<Subscription>> = dao.observeAllSubscriptions()
    fun observeAllPaymentProofs(): Flow<List<PaymentProofSubmission>> = dao.observeAllPaymentProofs()
    fun observePaymentProofsForBusiness(businessId: String): Flow<List<PaymentProofSubmission>> = dao.observePaymentProofsForBusiness(businessId)

    suspend fun submitPaymentProof(
        businessId: String,
        businessName: String,
        ownerName: String,
        ownerEmail: String,
        amount: Double,
        durationMonths: Int,
        reference: String,
        bankName: String,
        accountName: String,
        notes: String
    ) {
        val proof = PaymentProofSubmission(
            id = UUID.randomUUID().toString(),
            businessId = businessId,
            businessName = businessName,
            ownerName = ownerName,
            ownerEmail = ownerEmail,
            amount = amount,
            durationMonths = durationMonths,
            paymentReference = reference,
            bankName = bankName,
            accountName = accountName,
            status = ProofStatus.PENDING,
            proofDescription = notes,
            submittedAt = System.currentTimeMillis()
        )
        dao.insertPaymentProof(proof)
    }

    suspend fun reviewPaymentProof(proof: PaymentProofSubmission, approved: Boolean, notes: String?) {
        val now = System.currentTimeMillis()
        val newStatus = if (approved) ProofStatus.APPROVED else ProofStatus.REJECTED
        dao.reviewPaymentProof(proof.id, newStatus, now, notes)

        if (approved) {
            val sub = dao.getSubscription(proof.businessId)
            val currentEnd = if (sub != null && sub.currentPeriodEnd > now) sub.currentPeriodEnd else now
            val extensionMillis = proof.durationMonths * 30L * 86_400_000L
            val newEnd = currentEnd + extensionMillis

            if (sub != null) {
                dao.extendSubscription(proof.businessId, SubscriptionStatus.ACTIVE, newEnd)
            } else {
                dao.insertSubscription(
                    Subscription(
                        id = UUID.randomUUID().toString(),
                        businessId = proof.businessId,
                        businessName = proof.businessName,
                        planName = "${proof.durationMonths}-Month Pro Plan",
                        status = SubscriptionStatus.ACTIVE,
                        trialStartDate = now,
                        trialEndDate = now,
                        currentPeriodEnd = newEnd,
                        monthlyPriceNaira = 2000.0
                    )
                )
            }

            dao.insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    businessId = proof.businessId,
                    title = "Subscription Payment Approved!",
                    message = "Your payment of ₦${proof.amount} has been verified. Subscription extended until ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(newEnd))}.",
                    type = "PAYMENT",
                    timestamp = now
                )
            )
        }
    }

    // --- Platform Config ---
    fun observePlatformConfig(): Flow<PlatformConfig?> = dao.observePlatformConfig()
    suspend fun setPlatformConfig(config: PlatformConfig) = dao.setPlatformConfig(config)

    // --- Notifications ---
    fun observeNotifications(businessId: String): Flow<List<NotificationItem>> = dao.observeNotifications(businessId)
    suspend fun markAllNotificationsAsRead(businessId: String) = dao.markAllNotificationsAsRead(businessId)

    // --- Onboarding New Business ---
    suspend fun registerBusiness(
        businessName: String,
        ownerFullName: String,
        username: String,
        email: String,
        phone: String,
        category: String,
        address: String,
        currency: String = "₦"
    ): Pair<Business, UserAccount> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        val bizId = "biz_${UUID.randomUUID().toString().take(8)}"
        val userId = "user_${UUID.randomUUID().toString().take(8)}"

        val business = Business(
            id = bizId,
            name = businessName,
            category = category,
            address = address,
            phone = phone,
            email = email,
            currency = currency,
            currencyCode = if (currency == "₦") "NGN" else "USD",
            receiptHeader = "$businessName\nQuality Service & Value",
            receiptFooter = "Thank you for your patronage!",
            createdAt = now
        )
        dao.insertBusiness(business)

        val mainBranch = Branch(
            id = "branch_${bizId}_main",
            businessId = bizId,
            name = "Main Store",
            address = address,
            phone = phone,
            isMain = true
        )
        dao.insertBranch(mainBranch)

        val owner = UserAccount(
            id = userId,
            username = username,
            fullName = ownerFullName,
            email = email,
            phone = phone,
            role = UserRole.BUSINESS_OWNER,
            businessId = bizId,
            branchId = mainBranch.id,
            canProcessSales = true,
            canViewProducts = true,
            canEditProducts = true,
            canViewProfitReports = true,
            canManageStaff = true,
            canAdjustStock = true,
            canAuthorizeRefunds = true
        )
        dao.insertUser(owner)

        // 30-day trial
        val trial = Subscription(
            id = "sub_${bizId}",
            businessId = bizId,
            businessName = businessName,
            planName = "30-Day Free Trial",
            status = SubscriptionStatus.TRIAL_ACTIVE,
            trialStartDate = now,
            trialEndDate = now + (30 * dayMillis),
            currentPeriodEnd = now + (30 * dayMillis),
            monthlyPriceNaira = 2000.0
        )
        dao.insertSubscription(trial)

        dao.insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                businessId = bizId,
                title = "Welcome to AdeizaBiz!",
                message = "Your 30-day free trial has started. Begin by adding your store products or scanning barcodes.",
                type = "SUBSCRIPTION",
                timestamp = now
            )
        )

        return Pair(business, owner)
    }
}
