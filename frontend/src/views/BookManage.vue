<template>
  <div class="container">
    <h2 class="page-title">{{ pageTitle }}</h2>
    
    <!-- 搜索和添加按钮 -->
    <div class="toolbar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索书名、作者、ISBN 或出版社..." 
        class="search-input"
        @input="loadBooksDebounced"
      />
      <div>
        <button v-if="isAdmin" class="add-btn" @click="showAddModal = true">添加图书</button>
        <button v-if="isAdmin" class="remove-dup-btn" @click="handleRemoveDuplicates">删除重复书本</button>
      </div>
    </div>

    <!-- 图书列表 -->
    <div class="table-wrap">
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
    </div>

    <!-- 添加/编辑模态框 -->
    <transition name="modal-fade">
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
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getBooks, addBook, updateBook, deleteBook as apiDeleteBook, removeDuplicateBooks } from '../api/book'
import { isAdmin as checkAdmin } from '../utils/auth'
import { notifySuccess, notifyError, confirmAction } from '../utils/message.js'
import { debounce } from '../utils/debounce.js'

const books = ref([])
const isAdmin = computed(() => checkAdmin())
const pageTitle = computed(() => (isAdmin.value ? '图书管理' : '图书检索'))
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

const loadBooksDebounced = debounce(loadBooks, 300)

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
    notifySuccess(response.data.message)
    closeModal()
    loadBooks()
  } else {
    notifyError(response.data?.message || '操作失败')
  }
}

const deleteBook = async (id) => {
  const ok = await confirmAction('确定要删除该图书吗？', '删除图书')
  if (!ok) return
  const response = await apiDeleteBook(id)
  if (response.data && response.data.code === 200) {
    notifySuccess(response.data.message)
    loadBooks()
  } else {
    notifyError(response.data?.message || '删除失败')
  }
}

const handleRemoveDuplicates = async () => {
  const ok = await confirmAction(
    '确定要删除所有重复的图书吗？系统将保留每组重复书籍中 ID 最小的那本。',
    '删除重复书本'
  )
  if (!ok) return
  try {
    const response = await removeDuplicateBooks()
    if (response.data && response.data.code === 200) {
      notifySuccess(response.data.data || response.data.message)
      loadBooks()
    } else {
      notifyError(response.data?.message || '删除失败')
    }
  } catch {
    notifyError('删除重复书籍时出错')
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
  padding: 2px;
}

.page-title {
  margin-bottom: 16px;
  color: #111827;
  font-size: 22px;
  font-weight: 600;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 14px;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  padding: 10px 12px;
  border: 1px solid rgba(99, 102, 241, 0.22);
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  width: min(420px, 90vw);
  box-shadow: 0 8px 16px rgba(17, 24, 39, 0.04);
}

.search-input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.14);
}

.add-btn {
  padding: 8px 14px;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s ease;
  box-shadow: 0 8px 16px rgba(91, 141, 239, 0.2);
}

.add-btn:hover {
  transform: translateY(-1px);
}

.remove-dup-btn {
  padding: 8px 14px;
  background: linear-gradient(120deg, #8db6e6, #74a6da);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s ease;
  margin-left: 8px;
}

.remove-dup-btn:hover {
  transform: translateY(-1px);
}

.table-wrap {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(99, 102, 241, 0.12);
  border-radius: 12px;
  box-shadow: 0 10px 22px rgba(17, 24, 39, 0.06);
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  min-width: 760px;
}

.data-table th, .data-table td {
  padding: 12px 14px;
  text-align: left;
  border-bottom: 1px solid rgba(99, 102, 241, 0.1);
}

.data-table th {
  background: #edf4ff;
  color: #355ea8;
  font-weight: 600;
  font-size: 13px;
  position: sticky;
  top: 0;
  z-index: 1;
}

.data-table tbody tr:hover {
  background-color: #f8faff;
}

.edit-btn {
  padding: 7px 10px;
  background: linear-gradient(120deg, #9ab7e6, #86abd8);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 8px;
  transition: 0.2s ease;
}

.edit-btn:hover {
  transform: translateY(-1px);
}

.delete-btn {
  padding: 7px 10px;
  background: linear-gradient(120deg, #e9a0aa, #e58f9b);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  transition: 0.2s ease;
}

.delete-btn:hover {
  transform: translateY(-1px);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.48);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.modal {
  width: min(460px, 94vw);
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(99, 102, 241, 0.18);
  box-shadow: 0 20px 44px rgba(15, 23, 42, 0.2);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.22s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .modal,
.modal-fade-leave-to .modal {
  opacity: 0;
  transform: translateY(14px) scale(0.98);
}

.modal-fade-enter-to .modal,
.modal-fade-leave-from .modal {
  opacity: 1;
  transform: translateY(0) scale(1);
  transition: transform 0.24s cubic-bezier(0.22, 1, 0.36, 1), opacity 0.2s ease;
}

.modal h3 {
  margin-bottom: 14px;
  font-size: 18px;
  color: #111827;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 13px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 9px 11px;
  border: 1px solid rgba(99, 102, 241, 0.22);
  border-radius: 8px;
  font-size: 13px;
  transition: 0.2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.14);
}

.modal-actions {
  margin-top: 16px;
  text-align: right;
}

.cancel-btn {
  padding: 8px 12px;
  background-color: #e5e7eb;
  color: #374151;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s ease;
}

.cancel-btn:hover {
  background-color: #d1d5db;
}

.submit-btn {
  padding: 8px 14px;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: 0.2s ease;
}

.submit-btn:hover {
  transform: translateY(-1px);
}
</style>