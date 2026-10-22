package br.unisanta.approomproduto.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.unisanta.approomproduto.dao.ProductDao
import br.unisanta.approomproduto.databinding.ActivityMainBinding
import br.unisanta.approomproduto.db.AppDatabase
import br.unisanta.approomproduto.model.Product
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao

    // ATENÇÃO: A variável global está declarada aqui para ser vista por todos os botões
    private var selectedProductId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        )
            .fallbackToDestructiveMigration()
            .build()
        productDao = db.productDao()

        // 1. INSERIR PRODUTO
        binding.btnInserirProduto.setOnClickListener {
            val name = binding.editProductName.text.toString().trim()
            val priceText = binding.editProductPrice.text.toString().trim()
            val description = binding.editProductDescription.text.toString().trim()
            val price = priceText.toDoubleOrNull()

            if (name.isNotEmpty() && price != null && price > 0.0) {
                val product = Product(name = name, price = price, description = description)

                lifecycleScope.launch {
                    productDao.insertAll(product)
                    Log.i("TAG", "Produto inserido: $name - R$$price")
                    Toast.makeText(this@MainActivity, "Produto inserido!", Toast.LENGTH_SHORT).show()

                    binding.editProductName.text.clear()
                    binding.editProductPrice.text.clear()
                    binding.editProductDescription.text.clear()
                    binding.editProductName.error = null
                    binding.editProductPrice.error = null
                    binding.editProductName.requestFocus()
                }
            } else {
                if (name.isEmpty()) binding.editProductName.error = "Informe o nome do produto"
                if (priceText.isEmpty() || price == null || price <= 0.0) {
                    binding.editProductPrice.error = "Informe um preço válido"
                }
                Toast.makeText(this, "Preencha os campos obrigatórios corretamente!", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. LISTAR PRODUTOS
        binding.btnListarProdutos.setOnClickListener {
            lifecycleScope.launch {
                val productList = productDao.getAll()
                val sb = StringBuilder()

                if (productList.isNotEmpty()) {
                    for (product in productList) {
                        sb.append("${product.name} - R$ ${product.price}\n")
                    }
                } else {
                    sb.append("Nenhum produto cadastrado.\n")
                }

                val totalQuantity = productList.size
                val totalPrice = productList.sumOf { it.price }
                sb.append("\nProdutos cadastrados: $totalQuantity | Total do estoque: R$ ${String.format("%.2f", totalPrice)}")

                binding.textProductsList.text = sb.toString()
            }
        }

        // 3. BUSCAR PRODUTO
        binding.btnBuscar.setOnClickListener {
            val searchText = binding.editProductName.text.toString().trim()
            val query = "%$searchText%"

            if (searchText.isEmpty()) {
                binding.editProductName.error = "Digite um nome para buscar"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val filteredList = productDao.buscarPorNome(query)

                if (filteredList.isNotEmpty()) {
                    val matchedProduct = filteredList.first()

                    binding.editProductName.setText(matchedProduct.name)
                    binding.editProductPrice.setText(matchedProduct.price.toString())
                    binding.editProductDescription.setText(matchedProduct.description)

                    // Atribui o ID para a variável global da classe
                    selectedProductId = matchedProduct.id

                    Toast.makeText(this@MainActivity, "Produto carregado para edição!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show()
                    selectedProductId = null
                }
            }
        }

        // 4. ATUALIZAR PRODUTO
        binding.btnAtualiza.setOnClickListener {
            val id = selectedProductId

            if (id == null) {
                Toast.makeText(this, "Busque um produto pelo nome antes de atualizar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = binding.editProductName.text.toString().trim()
            val priceText = binding.editProductPrice.text.toString().trim()
            val description = binding.editProductDescription.text.toString().trim()
            val price = priceText.toDoubleOrNull()

            if (name.isNotEmpty() && price != null && price > 0.0) {
                val updatedProduct = Product(id = id, name = name, price = price, description = description)

                lifecycleScope.launch {
                    productDao.update(updatedProduct)
                    Toast.makeText(this@MainActivity, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                    binding.editProductName.text.clear()
                    binding.editProductPrice.text.clear()
                    binding.editProductDescription.text.clear()
                    binding.editProductName.error = null
                    binding.editProductPrice.error = null

                    selectedProductId = null
                    binding.editProductName.requestFocus()

                    binding.btnListarProdutos.performClick()
                }
            } else {
                if (name.isEmpty()) binding.editProductName.error = "Informe o nome do produto"
                if (priceText.isEmpty() || price == null || price <= 0.0) {
                    binding.editProductPrice.error = "Informe um preço válido"
                }
            }
        }

        // 5. DELETAR TUDO
        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                productDao.deleteAll()
                Toast.makeText(this@MainActivity, "Todos os produtos foram excluídos!", Toast.LENGTH_SHORT).show()
                selectedProductId = null
                binding.editProductName.text.clear()
                binding.editProductPrice.text.clear()
                binding.editProductDescription.text.clear()
                binding.textProductsList.text = "Nenhum produto cadastrado.\n\nProdutos cadastrados: 0 | Total do estoque: R$ 0,00"
                binding.editProductName.requestFocus()
            }
        }
    }
}
