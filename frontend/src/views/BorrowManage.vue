<template>
  <div class="container">
    <h2>{{ pageTitle }}</h2>

    <el-alert
      v-if="overdueReminder.overdueCount > 0"
      type="warning"
      :closable="false"
      show-icon
      class="overdue-banner"
      :title="overdueBannerTitle"
    />

    <!-- 搜索和添加按钮 -->
    <div class="toolbar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        :placeholder="isAdmin ? '搜索书名或用户名...' : '搜索书名...'" 
        class="search-input"
        @input="loadBorrows"
      />
      <!-- 所有用户都可以借书 -->
      <button class="add-btn" @click="addBorrowHandler">
        {{ isAdmin ? '添加借阅' : '借阅图书' }}
      </button>
    </div>

    <!-- 借阅列表 -->
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>图书名称</th>
          <th v-if="isAdmin">借阅人</th>
          <th>借阅日期</th>
          <th>应还日期</th>
          <th>实际归还日期</th>
          <th>状态</th>
          <th>逾期天数</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="borrow in filteredBorrows"
          :key="borrow.id"
          :class="{ 'row-overdue': borrow.status === 'OVERDUE' }"
        >
          <td>{{ borrow.id }}</td>
          <td>{{ borrow.bookTitle }}</td>
          <td v-if="isAdmin">{{ borrow.username }}</td>
          <td>{{ borrow.borrowDate }}</td>
          <td>{{ borrow.dueDate }}</td>
          <td>{{ borrow.returnDate || '-' }}</td>
          <td>
            <span :class="getStatusClass(borrow.status)">{{ getStatusText(borrow.status) }}</span>
          </td>
          <td>
            <span v-if="borrow.status === 'OVERDUE'" class="overdue-days">
              {{ borrow.overdueDays ?? '-' }} 天
            </span>
            <span v-else>-</span>
          </td>
          <td>
            <!-- 管理员可以归还任何书籍，普通用户只能归还自己借的书 -->
            <button 
              v-if="canReturn(borrow)" 
              class="return-btn" 
              @click="returnBook(borrow.id)"
            >
              归还
            </button>
            <!-- 只有管理员可以删除借阅记录 -->
            <button v-if="isAdmin" class="delete-btn" @click="deleteBorrow(borrow.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 添加借阅模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h3>添加借阅</h3>
        <form @submit.prevent="saveBorrow">
          <div class="form-group">
            <label>选择图书</label>
            <select v-model="formData.bookId" required>
              <option value="">请选择图书</option>
              <option v-for="book in availableBooks" :key="book.id" :value="book.id">
                {{ book.title }} (库存: {{ book.available }})
              </option>
            </select>
          </div>
          <!-- 管理员可以选择用户，普通用户只能为自己借书 -->
          <div v-if="isAdmin" class="form-group">
            <label>选择用户</label>
            <select v-model="formData.userId" required>
              <option value="">请选择用户</option>
              <option v-for="user in users" :key="user.id" :value="user.id">
                {{ user.username }}
              </option>
            </select>
          </div>
          <div v-else class="form-group">
            <label>借阅人</label>
            <div class="readonly-value">当前用户</div>
          </div>
          <div class="form-group">
            <label>借阅日期</label>
            <input type="date" v-model="formData.borrowDate" required />
          </div>
          <div class="form-group">
            <label>应还日期</label>
            <input type="date" v-model="formData.dueDate" required />
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
import { getBorrows, getOverdueReminder, addBorrow, returnBorrow, deleteBorrow as apiDeleteBorrow } from '../api/borrow'
import { getBooks } from '../api/book'
import { getUsers } from '../api/user'
import { isAdmin as checkAdmin, getCurrentUserId } from '../utils/auth'

const borrows = ref([])
const isAdmin = computed(() => checkAdmin())
const pageTitle = computed(() => (isAdmin.value ? '借阅管理' : '我的借阅'))
const currentUserId = computed(() => getCurrentUserId())
const searchKeyword = ref('')
const overdueReminder = ref({ overdueCount: 0, records: [] })

const overdueBannerTitle = computed(() => {
  const n = overdueReminder.value.overdueCount
  return isAdmin.value
    ? `当前共有 ${n} 条逾期未还，请及时督促归还`
    : `您有 ${n} 本书已逾期，请尽快归还`
})

function canReturn(borrow) {
  const active = borrow.status === 'BORROWED' || borrow.status === 'OVERDUE'
  return active && (isAdmin.value || borrow.userId === currentUserId.value)
}

// 过滤借阅记录：管理员看到所有，普通用户只看到自己的
const filteredBorrows = computed(() => {
  if (isAdmin.value) {
    return borrows.value
  }
  return borrows.value.filter(borrow => borrow.userId === currentUserId.value)
})
const showAddModal = ref(false)
const availableBooks = ref([])
const users = ref([])
const formData = ref({
  bookId: null,
  userId: null,
  borrowDate: '',
  dueDate: ''
})

const loadOverdueReminder = async () => {
  const userId = isAdmin.value ? undefined : currentUserId.value
  const res = await getOverdueReminder(userId)
  if (res.data?.code === 200 && res.data.data) {
    overdueReminder.value = res.data.data
  }
}

const loadBorrows = async () => {
  const response = await getBorrows(searchKeyword.value)
  if (response.data && response.data.code === 200) {
    borrows.value = response.data.data
  }
  await loadOverdueReminder()
}

const loadBooksAndUsers = async () => {
  const booksRes = await getBooks()
  if (booksRes.data?.code === 200) {
    availableBooks.value = booksRes.data.data.filter((b) => b.available > 0)
  }
  if (isAdmin.value) {
    const usersRes = await getUsers()
    if (usersRes.data?.code === 200) {
      users.value = usersRes.data.data
    }
  }
}

const addBorrowHandler = async () => {
  await loadBooksAndUsers()
  formData.value = {
    bookId: null,
    // 普通用户默认选择自己，管理员需要选择用户
    userId: isAdmin.value ? null : currentUserId.value,
    borrowDate: new Date().toISOString().split('T')[0],
    dueDate: ''
  }
  showAddModal.value = true
}

const saveBorrow = async () => {
  const response = await addBorrow(formData.value)
  if (response.data && response.data.code === 200) {
    alert(response.data.message)
    closeModal()
    loadBorrows()
  } else {
    alert(response.data?.message || '操作失败')
  }
}

const returnBook = async (id) => {
  if (confirm('确定要归还此书吗？')) {
    const response = await returnBorrow(id)
    if (response.data && response.data.code === 200) {
      alert(response.data.message)
      loadBorrows()
    } else {
      alert(response.data?.message || '操作失败')
    }
  }
}

const deleteBorrow = async (id) => {
  if (confirm('确定要删除该借阅记录吗？')) {
    const response = await apiDeleteBorrow(id)
    if (response.data && response.data.code === 200) {
      alert(response.data.message)
      loadBorrows()
    } else {
      alert(response.data?.message || '删除失败')
    }
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'BORROWED': '借阅中',
    'RETURNED': '已归还',
    'OVERDUE': '已逾期'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    'BORROWED': 'status-borrowed',
    'RETURNED': 'status-returned',
    'OVERDUE': 'status-overdue'
  }
  return classMap[status] || ''
}

const closeModal = () => {
  showAddModal.value = false
}

onMounted(() => {
  loadBorrows()
  loadOverdueReminder()
})
</script>

<style scoped>
.container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.overdue-banner {
  margin-bottom: 16px;
}

.row-overdue {
  background-color: #fff5f5 !important;
}

.row-overdue:hover {
  background-color: #fed7d7 !important;
}

.overdue-days {
  color: #e53e3e;
  font-weight: 600;
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

.status-borrowed {
  background: linear-gradient(135deg, #ecc94b 0%, #d69e2e 100%);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #744210;
}

.status-returned {
  background: linear-gradient(135deg, #68d391 0%, #38a169 100%);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.status-overdue {
  background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.return-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  margin-right: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(237, 137, 54, 0.3);
}

.return-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(237, 137, 54, 0.4);
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
  width: 480px;
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

.form-group input, .form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #fff;
  box-sizing: border-box;
}

.form-group input:focus, .form-group select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.readonly-value {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background-color: #f7fafc;
  color: #718096;
  box-sizing: border-box;
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