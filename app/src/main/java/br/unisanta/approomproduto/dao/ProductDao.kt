package br.unisanta.approomproduto.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.unisanta.approomproduto.model.Product
@Dao
interface ProductDao {
    // suspend: o Room não permite consultas na main thread
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>
    @Query("SELECT * FROM product WHERE name LIKE :nome") suspend fun buscarPorNome(nome: String): List<Product>
    @Query("DELETE FROM product") suspend fun deleteAll()
    @Update suspend fun update(product: Product)
    @Insert
    suspend fun insertAll(vararg products: Product)
}