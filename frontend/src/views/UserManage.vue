<template>
  <div class="container">
    <h2 class="page-title">用户管理</h2>
    
    <!-- 搜索和添加按钮 -->
    <div class="toolbar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索用户名..." 
        class="search-input"
        @input="loadUsersDebounced"
      />
      <button class="add-btn" @click="showAddModal = true">添加用户</button>
    </div>

    <!-- 用户列表 -->
    <div class="table-wrap">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.role === '1' ? '管理员' : '读者' }}</td>
            <td>
              <button class="edit-btn" @click="editUser(user)">编辑</button>
              <button class="delete-btn" @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑模态框 -->
    <transition name="modal-fade">
      <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <h3>{{ isEdit ? '编辑用户' : '添加用户' }}</h3>
          <form @submit.prevent="saveUser">
            <div class="form-group">
              <label>用户名</label>
              <input type="text" v-model="formData.username" required />
            </div>
            <div class="form-group">
              <label>密码</label>
              <input type="password" v-model="formData.password" :required="!isEdit" :placeholder="isEdit ? '不填则保持原密码' : ''" />
            </div>
            <div class="form-group">
              <label>角色</label>
              <select v-model="formData.role">
                <option value="0">普通用户</option>
                <option value="1">管理员</option>
              </select>
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
import { ref, onMounted } from 'vue'
import { getUsers, addUser, updateUser, deleteUser as apiDeleteUser } from '../api/user'
import { notifySuccess, notifyError, confirmAction } from '../utils/message.js'
import { debounce } from '../utils/debounce.js'

const users = ref([])
const searchKeyword = ref('')
const showAddModal = ref(false)
const isEdit = ref(false)
const formData = ref({
  id: null,
  username: '',
  password: '',
  role: '0'
})

const loadUsers = async () => {
  const response = await getUsers(searchKeyword.value)
  if (response.data && response.data.code === 200) {
    users.value = response.data.data
  }
}

const loadUsersDebounced = debounce(loadUsers, 300)

const addUserHandler = () => {
  isEdit.value = false
  formData.value = {
    id: null,
    username: '',
    password: '',
    role: '0'
  }
  showAddModal.value = true
}

const editUser = (user) => {
  isEdit.value = true
  formData.value = {
    id: user.id,
    username: user.username,
    password: '',
    role: user.role || '0'
  }
  showAddModal.value = true
}

const saveUser = async () => {
  let response
  if (isEdit.value) {
    response = await updateUser(formData.value)
  } else {
    response = await addUser(formData.value)
  }
  
  if (response.data && response.data.code === 200) {
    notifySuccess(response.data.message)
    closeModal()
    loadUsers()
  } else {
    notifyError(response.data?.message || '操作失败')
  }
}

const deleteUser = async (id) => {
  const ok = await confirmAction('确定要删除该用户吗？', '删除用户')
  if (!ok) return
  const response = await apiDeleteUser(id)
  if (response.data && response.data.code === 200) {
    notifySuccess(response.data.message)
    loadUsers()
  } else {
    notifyError(response.data?.message || '删除失败')
  }
}

const closeModal = () => {
  showAddModal.value = false
  isEdit.value = false
}

onMounted(() => {
  loadUsers()
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
  letter-spacing: 0.01em;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 14px;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  min-width: 260px;
  max-width: 400px;
  width: 40%;
  padding: 10px 12px;
  border: 1px solid rgba(99, 102, 241, 0.24);
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  color: #111827;
  box-shadow: 0 8px 16px rgba(17, 24, 39, 0.04);
}

.search-input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.add-btn {
  padding: 8px 14px;
  background: linear-gradient(120deg, #5b8def, #7aa6ff);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: 0.2s ease;
  box-shadow: 0 8px 16px rgba(91, 141, 239, 0.2);
}

.add-btn:hover {
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
  min-width: 660px;
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
  background: #f8faff;
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
  border: 1px solid rgba(99, 102, 241, 0.2);
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

.form-group input, .form-group select {
  width: 100%;
  padding: 9px 11px;
  border: 1px solid rgba(99, 102, 241, 0.22);
  border-radius: 8px;
  font-size: 13px;
  transition: 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.14);
}

.modal-actions {
  margin-top: 16px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
}

.submit-btn:hover {
  transform: translateY(-1px);
}
</style>