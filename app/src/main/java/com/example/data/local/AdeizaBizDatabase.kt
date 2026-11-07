package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Branch
import com.example.data.model.Business
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        UserAccount::class,
        Business::class,
        Branch::class,
        Product::class,
        SaleTransaction::class,
        SaleItemEntity::class,
        Customer::class,
        CustomerPayment::class,
        StockMovement::class,
        Subscription::class,
        PaymentProofSubmission::class,
        PlatformConfig::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AdeizaBizDatabase : RoomDatabase() {
    abstract fun dao(): AdeizaBizDao

    companion object {
        @Volatile
        private var INSTANCE: AdeizaBizDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AdeizaBizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdeizaBizDatabase::class.java,
                    "adeizabiz_database"
                )
                    .addCallback(AdeizaBizDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AdeizaBizDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: AdeizaBizDao) {
            val businessId = "biz_adeiza_01"
            val now = System.currentTimeMillis()
            val dayMillis = 86_400_000L

            // 1. Platform Config
            val platformConfig = PlatformConfig(
                id = "global_config",
                bankName = "First Bank of Nigeria",
                accountName = "AdeizaBiz Technologies Ltd",
                accountNumber = "3128945670",
                monthlyPriceNaira = 2000.0,
                quarterlyPriceNaira = 5500.0,
                annualPriceNaira = 20000.0
            )
            dao.setPlatformConfig(platformConfig)

            // 2. Main Business
            val business = Business(
                id = businessId,
                name = "Adeiza Mega Supermarket & Pharmacy",
                category = "Supermarket",
                address = "Plot 14, Commercial Avenue, Wuse 2, Abuja, Nigeria",
                phone = "+234 802 345 6789",
                email = "contact@adeizamega.ng",
                currency = "₦",
                currencyCode = "NGN",
                receiptHeader = "Adeiza Mega Supermarket & Pharmacy\nQuality Products at Affordable Prices",
                receiptFooter = "Thank you for shopping with us! Visit again."
            )
            dao.insertBusiness(business)

            // 2b. Branches
            val mainBranch = Branch(
                id = "branch_main",
                businessId = businessId,
                name = "Main Store - Wuse 2",
                address = "Plot 14, Commercial Avenue, Wuse 2, Abuja",
                phone = "+234 802 345 6789",
                isMain = true
            )
            val garkiBranch = Branch(
                id = "branch_garki",
                businessId = businessId,
                name = "Garki Annex Branch",
                address = "Area 11, Garki, Abuja",
                phone = "+234 805 987 6543",
                isMain = false
            )
            dao.insertBranch(mainBranch)
            dao.insertBranch(garkiBranch)

            // 3. User Accounts
            val superAdmin = UserAccount(
                id = "user_super_admin",
                username = "superadmin",
                fullName = "Engr. Mohammed Adeiza",
                email = "admin@adeizabiz.ng",
                phone = "+234 803 111 2233",
                role = UserRole.SUPER_ADMIN,
                businessId = "platform",
                canProcessSales = true,
                canViewProducts = true,
                canEditProducts = true,
                canViewProfitReports = true,
                canManageStaff = true,
                canAdjustStock = true,
                canAuthorizeRefunds = true
            )

            val owner = UserAccount(
                id = "user_owner_adeiza",
                username = "adeiza",
                fullName = "Dr. Gabriel Adeiza",
                email = "adeiza@adeizamega.ng",
                phone = "+234 802 345 6789",
                role = UserRole.BUSINESS_OWNER,
                businessId = businessId,
                canProcessSales = true,
                canViewProducts = true,
                canEditProducts = true,
                canViewProfitReports = true,
                canManageStaff = true,
                canAdjustStock = true,
                canAuthorizeRefunds = true
            )

            val workerChidinma = UserAccount(
                id = "user_worker_chidinma",
                username = "chidinma",
                fullName = "Chidinma Okafor",
                email = "chidinma@adeizamega.ng",
                phone = "+234 814 555 7890",
                role = UserRole.WORKER,
                businessId = businessId,
                canProcessSales = true,
                canViewProducts = true,
                canEditProducts = false,
                canViewProfitReports = false,
                canManageStaff = false,
                canAdjustStock = false,
                canAuthorizeRefunds = false
            )

            val workerEmeka = UserAccount(
                id = "user_worker_emeka",
                username = "emeka",
                fullName = "Emeka Adebayo",
                email = "emeka@adeizamega.ng",
                phone = "+234 809 333 4455",
                role = UserRole.WORKER,
                businessId = businessId,
                canProcessSales = true,
                canViewProducts = true,
                canEditProducts = false,
                canViewProfitReports = false,
                canManageStaff = false,
                canAdjustStock = false,
                canAuthorizeRefunds = false
            )

            dao.insertUser(superAdmin)
            dao.insertUser(owner)
            dao.insertUser(workerChidinma)
            dao.insertUser(workerEmeka)

            // 4. Products (Authentic Nigerian retail items with and without factory barcodes)
            val products = listOf(
                Product(
                    id = "prod_01",
                    businessId = businessId,
                    sku = "ADB-000001",
                    barcode = "615110001234",
                    name = "Golden Penny Spaghetti 500g",
                    category = "Food & Groceries",
                    purchasePrice = 850.0,
                    sellingPrice = 1100.0,
                    stockQuantity = 85.0,
                    minStockLevel = 15.0,
                    unit = "Pieces",
                    description = "Premium non-sticky Nigerian spaghetti pasta"
                ),
                Product(
                    id = "prod_02",
                    businessId = businessId,
                    sku = "ADB-000002",
                    barcode = "899277210101",
                    name = "Indomie Instant Noodles (Hungryman)",
                    category = "Food & Groceries",
                    purchasePrice = 400.0,
                    sellingPrice = 550.0,
                    stockQuantity = 140.0,
                    minStockLevel = 24.0,
                    unit = "Cartons",
                    unitConversionRate = 24.0,
                    unitChildName = "Pack",
                    unitChildPrice = 550.0,
                    description = "Carton contains 24 packs. Single pack ₦550, carton ₦12,500"
                ),
                Product(
                    id = "prod_03",
                    businessId = businessId,
                    sku = "ADB-000003",
                    barcode = "615200100456",
                    name = "Peak Evaporated Milk (Full Cream Tin)",
                    category = "Dairy",
                    purchasePrice = 680.0,
                    sellingPrice = 850.0,
                    stockQuantity = 42.0,
                    minStockLevel = 10.0,
                    unit = "Tins",
                    description = "Original rich and creamy Peak milk tin"
                ),
                Product(
                    id = "prod_04",
                    businessId = businessId,
                    sku = "ADB-000004",
                    barcode = null, // NON-BARCODE item
                    name = "Dangote Refined White Sugar (1kg Bag)",
                    category = "Food & Groceries",
                    purchasePrice = 1400.0,
                    sellingPrice = 1800.0,
                    stockQuantity = 4.0, // Low stock on purpose
                    minStockLevel = 10.0,
                    unit = "Bags",
                    description = "Pure white fortified refined sugar"
                ),
                Product(
                    id = "prod_05",
                    businessId = businessId,
                    sku = "ADB-000005",
                    barcode = "615300400500",
                    name = "Milo Chocolate Malt Drink 500g Refill",
                    category = "Beverages",
                    purchasePrice = 2800.0,
                    sellingPrice = 3400.0,
                    stockQuantity = 22.0,
                    minStockLevel = 8.0,
                    unit = "Packs",
                    description = "Energy food drink for active champions"
                ),
                Product(
                    id = "prod_06",
                    businessId = businessId,
                    sku = "ADB-000006",
                    barcode = null, // NON-BARCODE item
                    name = "Eva Natural Spring Water 75cl",
                    category = "Drinks",
                    purchasePrice = 150.0,
                    sellingPrice = 250.0,
                    stockQuantity = 3.0, // Low stock alert!
                    minStockLevel = 12.0,
                    unit = "Packs",
                    unitConversionRate = 12.0,
                    unitChildName = "Bottle",
                    unitChildPrice = 250.0,
                    description = "Bottled crisp pure water. Pack has 12 bottles."
                ),
                Product(
                    id = "prod_07",
                    businessId = businessId,
                    sku = "ADB-000007",
                    barcode = "600108735221",
                    name = "Dettol Antiseptic Soap (Original 120g)",
                    category = "Toiletries",
                    purchasePrice = 500.0,
                    sellingPrice = 700.0,
                    stockQuantity = 60.0,
                    minStockLevel = 15.0,
                    unit = "Pieces",
                    description = "Proven germ protection bar soap"
                ),
                Product(
                    id = "prod_08",
                    businessId = businessId,
                    sku = "ADB-000008",
                    barcode = null, // NON-BARCODE item
                    name = "Paracetamol 500mg Tablets (Emzor Blister)",
                    category = "Pharmacy",
                    purchasePrice = 120.0,
                    sellingPrice = 200.0,
                    stockQuantity = 95.0,
                    minStockLevel = 20.0,
                    unit = "Packs",
                    description = "Fast pain and fever relief"
                ),
                Product(
                    id = "prod_09",
                    businessId = businessId,
                    sku = "ADB-000009",
                    barcode = "544900000099",
                    name = "Coca-Cola Original 50cl PET",
                    category = "Drinks",
                    purchasePrice = 250.0,
                    sellingPrice = 350.0,
                    stockQuantity = 110.0,
                    minStockLevel = 24.0,
                    unit = "Bottles",
                    description = "Refreshing sparkling soft drink"
                ),
                Product(
                    id = "prod_10",
                    businessId = businessId,
                    sku = "ADB-000010",
                    barcode = null, // NON-BARCODE item
                    name = "Fresh Yam Tubers (Abuja Premium Large)",
                    category = "Food & Groceries",
                    purchasePrice = 2200.0,
                    sellingPrice = 3000.0,
                    stockQuantity = 2.0, // Low stock alert!
                    minStockLevel = 5.0,
                    unit = "Pieces",
                    description = "Locally sourced organic sweet white yam"
                )
            )
            products.forEach { dao.insertProduct(it) }

            // 5. Initial Stock Movement Records
            products.forEach { p ->
                dao.insertStockMovement(
                    StockMovement(
                        id = UUID.randomUUID().toString(),
                        businessId = businessId,
                        productId = p.id,
                        productName = p.name,
                        type = StockMovementType.INITIAL,
                        quantityChange = p.stockQuantity,
                        previousStock = 0.0,
                        newStock = p.stockQuantity,
                        reason = "Initial store inventory setup",
                        performedBy = "Dr. Gabriel Adeiza",
                        timestamp = now - (dayMillis * 3)
                    )
                )
            }

            // 6. Customers
            val customers = listOf(
                Customer(
                    id = "cust_01",
                    businessId = businessId,
                    fullName = "Alhaji Ibrahim Musa",
                    phone = "+234 803 444 8899",
                    email = "i.musa@gmail.com",
                    address = "House 12, 4th Avenue, Gwarinpa, Abuja",
                    creditBalance = 14500.0, // Credit Debt
                    totalSpent = 168000.0
                ),
                Customer(
                    id = "cust_02",
                    businessId = businessId,
                    fullName = "Mrs. Ngozi Eze",
                    phone = "+234 818 222 7711",
                    email = "ngozi.eze@outlook.com",
                    address = "Plot 55, Maitama Hills, Abuja",
                    creditBalance = 0.0,
                    totalSpent = 94500.0
                ),
                Customer(
                    id = "cust_03",
                    businessId = businessId,
                    fullName = "Barrister Funke Balogun",
                    phone = "+234 805 777 9900",
                    email = "f.balogun@lawfirm.ng",
                    address = "Central Business District, Abuja",
                    creditBalance = 0.0,
                    totalSpent = 52000.0
                )
            )
            customers.forEach { dao.insertCustomer(it) }

            // 7. Initial Sales and Receipts
            val sale1 = SaleTransaction(
                id = "sale_01",
                receiptNumber = "REC-20261106-001",
                businessId = businessId,
                cashierId = workerChidinma.id,
                cashierName = workerChidinma.fullName,
                customerId = "cust_02",
                customerName = "Mrs. Ngozi Eze",
                subtotal = 7300.0,
                discount = 300.0,
                totalAmount = 7000.0,
                amountPaid = 10000.0,
                changeAmount = 3000.0,
                paymentMethod = PaymentMethod.CASH,
                isCredit = false,
                timestamp = now - (3600_000L * 4),
                itemsSummary = "Golden Penny Spaghetti (x2), Peak Milk Tin (x4), Dettol Soap (x3)"
            )
            dao.insertSale(sale1)
            dao.insertSaleItems(
                listOf(
                    SaleItemEntity(
                        id = UUID.randomUUID().toString(),
                        saleId = sale1.id,
                        productId = "prod_01",
                        productName = "Golden Penny Spaghetti 500g",
                        sku = "ADB-000001",
                        quantity = 2.0,
                        unit = "Pieces",
                        unitPrice = 1100.0,
                        purchaseCost = 850.0,
                        subtotal = 2200.0
                    ),
                    SaleItemEntity(
                        id = UUID.randomUUID().toString(),
                        saleId = sale1.id,
                        productId = "prod_03",
                        productName = "Peak Evaporated Milk (Full Cream Tin)",
                        sku = "ADB-000003",
                        quantity = 4.0,
                        unit = "Tins",
                        unitPrice = 850.0,
                        purchaseCost = 680.0,
                        subtotal = 3400.0
                    ),
                    SaleItemEntity(
                        id = UUID.randomUUID().toString(),
                        saleId = sale1.id,
                        productId = "prod_07",
                        productName = "Dettol Antiseptic Soap (Original 120g)",
                        sku = "ADB-000007",
                        quantity = 3.0,
                        unit = "Pieces",
                        unitPrice = 700.0,
                        purchaseCost = 500.0,
                        subtotal = 2100.0
                    )
                )
            )

            val sale2 = SaleTransaction(
                id = "sale_02",
                receiptNumber = "REC-20261106-002",
                businessId = businessId,
                cashierId = workerEmeka.id,
                cashierName = workerEmeka.fullName,
                customerId = null,
                customerName = "Walk-in Customer",
                subtotal = 14350.0,
                discount = 0.0,
                totalAmount = 14350.0,
                amountPaid = 14350.0,
                changeAmount = 0.0,
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                paymentReference = "TRF-GTB-998822",
                isCredit = false,
                timestamp = now - (3600_000L * 2),
                itemsSummary = "Indomie Carton (x1), Milo Drink 500g (x1)"
            )
            dao.insertSale(sale2)
            dao.insertSaleItems(
                listOf(
                    SaleItemEntity(
                        id = UUID.randomUUID().toString(),
                        saleId = sale2.id,
                        productId = "prod_02",
                        productName = "Indomie Instant Noodles (Hungryman)",
                        sku = "ADB-000002",
                        quantity = 1.0,
                        unit = "Cartons",
                        unitPrice = 10950.0,
                        purchaseCost = 9600.0,
                        subtotal = 10950.0
                    ),
                    SaleItemEntity(
                        id = UUID.randomUUID().toString(),
                        saleId = sale2.id,
                        productId = "prod_05",
                        productName = "Milo Chocolate Malt Drink 500g Refill",
                        sku = "ADB-000005",
                        quantity = 1.0,
                        unit = "Packs",
                        unitPrice = 3400.0,
                        purchaseCost = 2800.0,
                        subtotal = 3400.0
                    )
                )
            )

            // 8. 30-Day Free Trial Subscription
            val trialSubscription = Subscription(
                id = "sub_adeiza_01",
                businessId = businessId,
                businessName = business.name,
                planName = "30-Day Free Trial",
                status = SubscriptionStatus.TRIAL_ACTIVE,
                trialStartDate = now - (dayMillis * 5),
                trialEndDate = now + (dayMillis * 25),
                currentPeriodEnd = now + (dayMillis * 25),
                monthlyPriceNaira = 2000.0
            )
            dao.insertSubscription(trialSubscription)

            // 9. Payment Proof Submission (A real pending proof from another store owner for Super Admin demonstration)
            val proofDemo = PaymentProofSubmission(
                id = "proof_01",
                businessId = "biz_katsina_store",
                businessName = "Katsina Provisions & Boutique",
                ownerName = "Umaru Farouk",
                ownerEmail = "u.farouk@gmail.com",
                amount = 2000.0,
                durationMonths = 1,
                paymentReference = "ZENITH-NIP-48392019",
                bankName = "First Bank of Nigeria",
                accountName = "AdeizaBiz Technologies Ltd",
                status = ProofStatus.PENDING,
                proofDescription = "Paid ₦2,000 via mobile banking app on 06 Nov 2026. Ref: ZENITH-NIP-48392019",
                submittedAt = now - (3600_000L * 3)
            )
            dao.insertPaymentProof(proofDemo)

            // 10. Initial Notifications
            dao.insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    businessId = businessId,
                    title = "Low Stock Alert",
                    message = "Dangote Sugar (4 remaining) and Eva Water (3 remaining) are below minimum stock level.",
                    type = "LOW_STOCK",
                    timestamp = now - (3600_000L * 6)
                )
            )
            dao.insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    businessId = businessId,
                    title = "Welcome to AdeizaBiz!",
                    message = "Your 30-day full access free trial is active. You have 25 days remaining.",
                    type = "SUBSCRIPTION",
                    timestamp = now - (dayMillis * 5)
                )
            )
        }
    }
}
