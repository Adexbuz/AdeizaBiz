package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    SUPER_ADMIN,
    BUSINESS_OWNER,
    WORKER
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    CARD,
    CREDIT
}

enum class StockMovementType {
    SALE,
    RESTOCK,
    DAMAGE,
    EXPIRY,
    ADJUSTMENT,
    INITIAL
}

enum class SubscriptionStatus {
    TRIAL_ACTIVE,
    ACTIVE,
    EXPIRED,
    PENDING_APPROVAL
}

enum class ProofStatus {
    PENDING,
    APPROVED,
    REJECTED
}

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val businessId: String,
    val branchId: String = "branch_main",
    val canProcessSales: Boolean = true,
    val canViewProducts: Boolean = true,
    val canEditProducts: Boolean = false,
    val canViewProfitReports: Boolean = false,
    val canManageStaff: Boolean = false,
    val canAdjustStock: Boolean = false,
    val canAuthorizeRefunds: Boolean = false,
    val isActive: Boolean = true
)

@Entity(tableName = "businesses")
data class Business(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val address: String,
    val phone: String,
    val email: String,
    val currency: String = "₦",
    val currencyCode: String = "NGN",
    val logoUrl: String? = null,
    val receiptHeader: String = "Thank you for shopping with us!",
    val receiptFooter: String = "Goods sold in good condition are not returnable.",
    val createdAt: Long = System.currentTimeMillis(),
    val isSuspended: Boolean = false
)

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey val id: String,
    val businessId: String,
    val name: String,
    val address: String,
    val phone: String,
    val isMain: Boolean = true
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val businessId: String,
    val branchId: String = "branch_main",
    val sku: String,
    val barcode: String? = null,
    val name: String,
    val category: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Double,
    val minStockLevel: Double = 5.0,
    val unit: String = "Pieces",
    val unitConversionRate: Double = 1.0,
    val unitChildName: String? = null,
    val unitChildPrice: Double? = null,
    val description: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales")
data class SaleTransaction(
    @PrimaryKey val id: String,
    val receiptNumber: String,
    val businessId: String,
    val branchId: String = "branch_main",
    val cashierId: String,
    val cashierName: String,
    val customerId: String? = null,
    val customerName: String? = null,
    val subtotal: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val amountPaid: Double,
    val changeAmount: Double = 0.0,
    val paymentMethod: PaymentMethod,
    val paymentReference: String? = null,
    val isCredit: Boolean = false,
    val isSynced: Boolean = true,
    val isRefunded: Boolean = false,
    val refundReason: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String = "" // Readable summary of items
)

@Entity(tableName = "sale_items")
data class SaleItemEntity(
    @PrimaryKey val id: String,
    val saleId: String,
    val productId: String,
    val productName: String,
    val sku: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val purchaseCost: Double,
    val subtotal: Double
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String,
    val businessId: String,
    val fullName: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val creditBalance: Double = 0.0,
    val totalSpent: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_payments")
data class CustomerPayment(
    @PrimaryKey val id: String,
    val customerId: String,
    val businessId: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val reference: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val recordedBy: String
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey val id: String,
    val businessId: String,
    val productId: String,
    val productName: String,
    val type: StockMovementType,
    val quantityChange: Double,
    val previousStock: Double,
    val newStock: Double,
    val reason: String,
    val performedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey val id: String,
    val businessId: String,
    val businessName: String,
    val planName: String = "30-Day Free Trial",
    val status: SubscriptionStatus = SubscriptionStatus.TRIAL_ACTIVE,
    val trialStartDate: Long,
    val trialEndDate: Long,
    val currentPeriodEnd: Long,
    val monthlyPriceNaira: Double = 2000.0
)

@Entity(tableName = "payment_proofs")
data class PaymentProofSubmission(
    @PrimaryKey val id: String,
    val businessId: String,
    val businessName: String,
    val ownerName: String,
    val ownerEmail: String,
    val amount: Double,
    val durationMonths: Int,
    val paymentReference: String,
    val bankName: String,
    val accountName: String,
    val status: ProofStatus = ProofStatus.PENDING,
    val proofDescription: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewNotes: String? = null
)

@Entity(tableName = "platform_config")
data class PlatformConfig(
    @PrimaryKey val id: String = "global_config",
    val bankName: String = "First Bank of Nigeria",
    val accountName: String = "AdeizaBiz Technologies Limited",
    val accountNumber: String = "3128945670",
    val monthlyPriceNaira: Double = 2000.0,
    val quarterlyPriceNaira: Double = 5500.0,
    val annualPriceNaira: Double = 20000.0,
    val supportPhone: String = "+234 803 123 4567",
    val supportEmail: String = "support@adeizabiz.ng"
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val businessId: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// UI Cart item helper
data class CartItem(
    val product: Product,
    var quantity: Double = 1.0,
    val customPrice: Double? = null,
    val selectedUnit: String = product.unit,
    val unitRate: Double = 1.0
) {
    val effectivePrice: Double
        get() = customPrice ?: if (selectedUnit == product.unitChildName && product.unitChildPrice != null) {
            product.unitChildPrice
        } else {
            product.sellingPrice
        }

    val itemTotal: Double
        get() = effectivePrice * quantity

    val actualStockDeduction: Double
        get() = if (selectedUnit == product.unitChildName && product.unitConversionRate > 0) {
            quantity / product.unitConversionRate
        } else {
            quantity
        }
}
