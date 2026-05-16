<template>
  <div class="container">
    <h2>图书管理</h2>
    
    <!-- 搜索和添加按钮 -->
    <div class="toolbar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索书名或ISBN..." 
        class="search-input"
        @input="loadBooks"
      />
      <!-- 只有管理员可以添加图书 -->
      <button v-if="isAdmin" class="add-btn" @click="showAddModal = true">添加图书</button>
    </div>

    <!-- 图书列表 -->
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>书名</th>
          <th>ISBN</th>
          <th>作者</th>
          <th>出版社</th>
          <th>库存</th>
          <th v-if="isAdmin">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in books" :key="book.id">
          <td>{{ book.id }}</td>
          <td>{{ book.title }}</td>
          <td>{{ book.isbn }}</td>
          <td>{{ book.author }}</td>
          <td>{{ book.publisher }}</td>
          <td>{{ book.available }}</td>
          <td v-if="isAdmin">
            <button class="edit-btn" @click="editBook(book)">编辑</button>
            <button class="delete-btn" @click="deleteBook(book.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 添加/编辑模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h3>{{ isEdit ? '编辑图书' : '添加图书' }}</h3>
        <form @submit.prevent="saveBook">
          <div class="form-group">
            <label>书名</label>
            <input type="text" v-model="formData.title" required />
          </div>
          <div class="form-group">
            <label>ISBN</label>
            <input type="text" v-model="formData.isbn" required />
          </div>
          <div class="form-group">
            <label>作者</label>
            <input type="text" v-model="formData.author" required />
          </div>
          <div class="form-group">
            <label>出版社</label>
            <input type="text" v-model="formData.publisher" required />
          </div>
          <div class="form-group">
            <label>库存数量</label>
            <input type="number" v-model="formData.available" required min="0" />
          </div>
          <div class="modal-actions">
            <button type="button" class="cancel-btn" @click="closeModal">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getBooks, addBook, updateBook, deleteBook as apiDeleteBook } from '../api/book'
import { isAdmin as checkAdmin } from '../utils/auth'

const books = ref([])
const isAdmin = computed(() => checkAdmin())
const searchKeyword = ref('')
const showAddModal = ref(false)
const isEdit = ref(false)
const formData = ref({
  id: null,
  title: '',
  isbn: '',
  author: '',
  publisher: '',
  available: 0
})

const loadBooks = async () => {
  const response = await getBooks(searchKeyword.value)
  if (response.data && response.data.code === 200) {
    books.value = response.data.data
  }
}

const addBookHandler = () => {
  isEdit.value = false
  formData.value = {
    id: null,
    title: '',
    isbn: '',
    author: '',
    publisher: '',
    available: 0
  }
  showAddModal.value = true
}

const editBook = (book) => {
  isEdit.value = true
  formData.value = {
    id: book.id,
    title: book.title,
    isbn: book.isbn,
    author: book.author,
    publisher: book.publisher,
    available: book.available || 0
  }
  showAddModal.value = true
}

const saveBook = async () => {
  let response
  if (isEdit.value) {
    response = await updateBook(formData.value)
  } else {
    response = await addBook(formData.value)
  }
  
  if (response.data && response.data.code === 200) {
    alert(response.data.message)
    closeModal()
    loadBooks()
  } else {
    alert(response.data?.message || '操作失败')
  }
}

const deleteBook = async (id) => {
  if (confirm('确定要删除该图书吗？')) {
    const response = await apiDeleteBook(id)
    if (response.data && response.data.code === 200) {
      alert(response.data.message)
      loadBooks()
    } else {
      alert(response.data?.message || '删除失败')
    }
  }
}

const closeModal = () => {
  showAddModal.value = false
  isEdit.value = false
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.container {
  padding: 0;
}

h2 {
  margin-bottom: 25px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
  align-items: center;
}

.search-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 250px;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
}

.add-btn {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.add-btn:hover {
  background-color: #2980b9;
}

.table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.data-table tbody tr:hover {
  background-color: #f8f9fa;
}

.edit-btn {
  padding: 8px 16px;
  background-color: #f39c12;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 5px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background-color: #d68910;
}

.delete-btn {
  padding: 8px 16px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  width: 450px;
  background: #fff;
  border-radius: 8px;
  padding: 25px;
}

.modal h3 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.modal-actions {
  margin-top: 20px;
  text-align: right;
}

.cancel-btn {
  padding: 8px 16px;
  background-color: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.3);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.4);
}
</style>