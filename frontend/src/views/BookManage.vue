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
      <button class="add-btn" @click="showAddModal = true">添加图书</button>
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
          <th>操作</th>
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
          <td>
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
import { ref, onMounted } from 'vue'
import { getBooks, addBook, updateBook, deleteBook as apiDeleteBook } from '../api/book'

const books = ref([])
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
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 24px;
  color: #1a202c;
  font-size: 24px;
  font-weight: 600;
  border-left: 4px solid #4299e1;
  padding-left: 12px;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 350px;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.add-btn {
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

.add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.4);
}

.data-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.data-table th, .data-table td {
  padding: 16px 20px;
  text-align: left;
}

.data-table th {
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  color: #fff;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.data-table th:first-child {
  border-radius: 12px 0 0 0;
}

.data-table th:last-child {
  border-radius: 0 12px 0 0;
}

.data-table tr {
  border-bottom: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.data-table tr:hover {
  background-color: #f7fafc;
  transform: scale(1.002);
}

.data-table tr:last-child td:first-child {
  border-radius: 0 0 0 12px;
}

.data-table tr:last-child td:last-child {
  border-radius: 0 0 12px 0;
}

.data-table tr:last-child {
  border-bottom: none;
}

.data-table td {
  color: #4a5568;
  font-size: 14px;
}

.data-table td:first-child {
  font-weight: 600;
  color: #2d3748;
}

.edit-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  margin-right: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(72, 187, 120, 0.3);
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(72, 187, 120, 0.4);
}

.delete-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(252, 129, 129, 0.3);
}

.delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(252, 129, 129, 0.4);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background-color: #fff;
  padding: 28px;
  border-radius: 16px;
  width: 450px;
  max-width: 90%;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal h3 {
  margin-bottom: 24px;
  color: #1a202c;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4a5568;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #fff;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
}

.cancel-btn {
  padding: 12px 24px;
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 8px;
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