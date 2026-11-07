package com.example

import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.ui.components.formatNaira
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNairaFormatting() {
        assertEquals("₦2,500", formatNaira(2500.0))
        assertEquals("₦14,350", formatNaira(14350.0))
        assertEquals("₦0", formatNaira(0.0))
    }

    @Test
    fun testCartItemCalculation() {
        val product = Product(
            id = "prod_test_01",
            businessId = "biz_01",
            sku = "ADB-000001",
            name = "Peak Evaporated Milk",
            category = "Dairy",
            purchasePrice = 650.0,
            sellingPrice = 850.0,
            stockQuantity = 50.0,
            unit = "Tins"
        )

        val cartItem = CartItem(
            product = product,
            quantity = 4.0
        )

        assertEquals(3400.0, cartItem.itemTotal, 0.01)
        assertEquals(4.0, cartItem.actualStockDeduction, 0.01)
    }

    @Test
    fun testUnitConversionCartCalculation() {
        val product = Product(
            id = "prod_test_02",
            businessId = "biz_01",
            sku = "ADB-000002",
            name = "Indomie Instant Noodles",
            category = "Food",
            purchasePrice = 9500.0,
            sellingPrice = 12000.0,
            stockQuantity = 10.0,
            unit = "Cartons",
            unitConversionRate = 24.0,
            unitChildName = "Pack",
            unitChildPrice = 550.0
        )

        // When selling 12 individual packs:
        val cartItemSubUnit = CartItem(
            product = product,
            quantity = 12.0,
            selectedUnit = "Pack"
        )

        assertEquals(6600.0, cartItemSubUnit.itemTotal, 0.01)
        // 12 packs / 24 per carton = 0.5 cartons deducted from inventory
        assertEquals(0.5, cartItemSubUnit.actualStockDeduction, 0.01)
    }
}
