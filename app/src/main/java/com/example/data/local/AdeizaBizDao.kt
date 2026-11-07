package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Branch
import com.example.data.model.Business
import com.example.data.model.Customer
import com.example.data.model.CustomerPayment
import com.example.data.model.NotificationItem
import com.example.data.model.PaymentProofSubmission
import com.example.data.model.PlatformConfig
import com.example.data.model.Product
import com.example.data.model.ProofStatus
import com.example.data.model.SaleItemEntity
import com.example.data.model.SaleTransaction
import com.example.data.model.StockMovement
import com.example.data.model.Subscription
import com.example.data.model.SubscriptionStatus
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface AdeizaBizDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccount?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserAccount?

    @Query("SELECT * FROM users WHERE businessId = :businessId")
    fun observeUsersForBusiness(businessId: String): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun setUserActive(userId: String, isActive: Boolean)

    // --- Businesses & Branches ---
    @Query("SELECT * FROM businesses WHERE id = :id LIMIT 1")
    suspend fun getBusinessById(id: String): Business?

    @Query("SELECT * FROM businesses")
    fun observeAllBusinesses(): Flow<List<Business>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business)

    @Update
    suspend fun updateBusiness(business: Business)

    @Query("SELECT * FROM branches WHERE businessId = :businessId")
    fun observeBranches(businessId: String): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    // --- Products ---
    @Query("SELECT * FROM products WHERE businessId = :businessId ORDER BY name ASC")
    fun observeProducts(businessId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%') ORDER BY name ASC")
    fun searchProducts(businessId: String, query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(businessId: String, barcode: String): Product?

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): Product?

    @Query("SELECT * FROM products WHERE businessId = :businessId AND stockQuantity <= minStockLevel ORDER BY stockQuantity ASC")
    fun observeLowStockProducts(businessId: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantityDelta, updatedAt = :time WHERE id = :productId")
    suspend fun updateStock(productId: String, quantityDelta: Double, time: Long = System.currentTimeMillis())

    // --- Sales Transactions ---
    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY timestamp DESC")
    fun observeSales(businessId: String): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sales WHERE businessId = :businessId AND cashierId = :cashierId ORDER BY timestamp DESC")
    fun observeSalesByCashier(businessId: String, cashierId: String): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sales WHERE businessId = :businessId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentSales(businessId: String, limit: Int = 10): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sales WHERE id = :saleId LIMIT 1")
    suspend fun getSaleById(saleId: String): SaleTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleTransaction)

    @Update
    suspend fun updateSale(sale: SaleTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getSaleItems(saleId: String): List<SaleItemEntity>

    // --- Stock Movements ---
    @Query("SELECT * FROM stock_movements WHERE businessId = :businessId ORDER BY timestamp DESC")
    fun observeStockMovements(businessId: String): Flow<List<StockMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovement)

    // --- Customers & Debt Management ---
    @Query("SELECT * FROM customers WHERE businessId = :businessId ORDER BY fullName ASC")
    fun observeCustomers(businessId: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("UPDATE customers SET creditBalance = creditBalance + :debtDelta, totalSpent = totalSpent + :spentDelta WHERE id = :customerId")
    suspend fun updateCustomerBalance(customerId: String, debtDelta: Double, spentDelta: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerPayment(payment: CustomerPayment)

    @Query("SELECT * FROM customer_payments WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun observeCustomerPayments(customerId: String): Flow<List<CustomerPayment>>

    // --- Subscriptions ---
    @Query("SELECT * FROM subscriptions WHERE businessId = :businessId LIMIT 1")
    fun observeSubscription(businessId: String): Flow<Subscription?>

    @Query("SELECT * FROM subscriptions WHERE businessId = :businessId LIMIT 1")
    suspend fun getSubscription(businessId: String): Subscription?

    @Query("SELECT * FROM subscriptions")
    fun observeAllSubscriptions(): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription)

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Query("UPDATE subscriptions SET status = :status, currentPeriodEnd = :newEnd WHERE businessId = :businessId")
    suspend fun extendSubscription(businessId: String, status: SubscriptionStatus, newEnd: Long)

    // --- Payment Proof Submissions ---
    @Query("SELECT * FROM payment_proofs ORDER BY submittedAt DESC")
    fun observeAllPaymentProofs(): Flow<List<PaymentProofSubmission>>

    @Query("SELECT * FROM payment_proofs WHERE businessId = :businessId ORDER BY submittedAt DESC")
    fun observePaymentProofsForBusiness(businessId: String): Flow<List<PaymentProofSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentProof(proof: PaymentProofSubmission)

    @Update
    suspend fun updatePaymentProof(proof: PaymentProofSubmission)

    @Query("UPDATE payment_proofs SET status = :status, reviewedAt = :reviewedAt, reviewNotes = :notes WHERE id = :proofId")
    suspend fun reviewPaymentProof(proofId: String, status: ProofStatus, reviewedAt: Long, notes: String?)

    // --- Platform Configuration ---
    @Query("SELECT * FROM platform_config WHERE id = 'global_config' LIMIT 1")
    fun observePlatformConfig(): Flow<PlatformConfig?>

    @Query("SELECT * FROM platform_config WHERE id = 'global_config' LIMIT 1")
    suspend fun getPlatformConfig(): PlatformConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPlatformConfig(config: PlatformConfig)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE businessId = :businessId ORDER BY timestamp DESC")
    fun observeNotifications(businessId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE businessId = :businessId")
    suspend fun markAllNotificationsAsRead(businessId: String)
}
