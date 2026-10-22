# AppRoomProduto

> **Aluno(a):** _preencher nome_  
> **RA:** _preencher_  
> **Disciplina / Professor(a):** _preencher_

## 1. Descrição

Aplicativo Android desenvolvido em Kotlin que demonstra o uso da biblioteca **Room** (camada de abstração sobre o SQLite) para persistência local de dados. O app permite cadastrar, listar, buscar e excluir produtos.

## 2. Tecnologias

- Kotlin 1.9.0
- Android Gradle Plugin 8.5.1 (minSdk 24)
- Room 2.6.1 (`room-runtime`, `room-ktx`, `room-compiler` via kapt)
- ViewBinding
- Coroutines (`lifecycleScope`)

## 3. Estrutura do projeto

```
app/src/main/java/br/unisanta/approomproduto/
├── model/Product.kt        # Entidade
├── dao/ProductDao.kt       # Acesso aos dados (consultas)
├── db/AppDatabase.kt       # Banco de dados Room
└── view/MainActivity.kt    # Tela e lógica dos botões
```

## 4. Funcionalidades

| Botão | O que faz |
|-------|-----------|
| **Inserir Produto** | Valida nome e preço (> 0) e grava o produto no banco |
| **Listar Produtos** | Exibe todos os produtos, a quantidade e a soma dos preços |
| **Buscar** | Procura produtos pelo nome digitado e mostra os resultados na lista (não altera nada) |
| **Deleta** | Remove todos os produtos do banco |

## 5. Código principal

### Entidade — `Product.kt`

```kotlin
@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "description") val description: String?
)
```

### DAO — `ProductDao.kt`

```kotlin
@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE name LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Product>

    @Query("DELETE FROM product")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(vararg products: Product)
}
```

### Banco — `AppDatabase.kt`

```kotlin
@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
```

## 6. Prints do aplicativo

Salvar as imagens `.png` em `docs/prints/` e ajustar os nomes abaixo.

| Tela | Print |
|------|-------|
| Tela inicial | ![Tela inicial](docs/prints/tela-inicial.png) |
| Listagem | ![Listar](docs/prints/lista.png) |


## 7. Respostas

1B) Persistência de dados é quando as informações permanecem mesmo que o app seja reiniciado, se eles fossem guardados na MutableList os dados seriam perdidos quando o app fosse reiniciado.

2B) A diferença é que o SQLite é um banco de dados que permite consultar por meio de comandos já a ROOM ela é uma biblioteca que facilita o uso dele. Suas vantagens são ele ajuda a achar erros quando é compilado é também permite usar DAOs fazendo assim o código ser mais simples.

3B) Entity = Uma tabela do banco de dados assim definindo os Campos por preço, nome dentre outros.
DataBase = Ela representa o banco de dados dando acesso a DAO.
DAO = Ela exclui, consulta e atualiza dados no banco de dados.

4B) Ele não permite pois o banco de dados thread ela pode impedir que a interface responda os comandos do usuário.
ANR = Acontece quando o aplicativo não responde por tempo excessivo
Suspend = Realiza operações assíncronas sem bloquear a thread principal
LifecycleScope.launch = Ele evita execuções desnecessárias quando a atividade é encerrada.

5B) Ele permite que a ROOM recrie o banco de dados quando acontece uma alteração de versão. Ele pode apagar as informações salvas no banco por conta da alteração da versão.

6B) ViewBinding = Ele facilita o acesso dos componentes na interface por meio de referências.
FindViewByID = Reduz a necessidade de procurar cada coisa pelo seu id
Vantagem = Evita erros ao procurar por componentes de tipos diferentes.

7B)Ela evita a criação desnecessária de várias instâncias e melhora a organização e o gerenciamento dos recursos. Se cada tela criasse sua própria instância, poderiam ocorrer desperdício de recursos.

8B) A maior complexidade foi buscar por nome os itens

