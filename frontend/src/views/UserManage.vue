<template>
  <div class="container">
    <h2>用户管理</h2>
    
    <!-- 搜索和添加按钮 -->
    <div class="toolbar">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索用户名..." 
        class="search-input"
        @input="loadUsers"
      />
      <button class="add-btn" @click="showAddModal = true">添加用户</button>
    </div>

    <!-- 用户列表 -->
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
          <td>{{ user.role === '1' ? '管理员' : '普通用户' }}</td>
          <td>
            <button class="edit-btn" @click="editUser(user)">编辑</button>
            <button class="delete-btn" @click="deleteUser(user.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 添加/编辑模态框 -->
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, addUser, updateUser, deleteUser as apiDeleteUser } from '../api/user'

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
    alert(response.data.message)
    closeModal()
    loadUsers()
  } else {
    alert(response.data?.message || '操作失败')
  }
}

const deleteUser = async (id) => {
  if (confirm('确定要删除该用户吗？')) {
    const response = await apiDeleteUser(id)
    if (response.data && response.data.code === 200) {
      alert(response.data.message)
      loadUsers()
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
  loadUsers()
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

.form-group input, .form-group select {
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
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

.submit-btn {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn:hover {
  background-color: #2980b9;
}
</style>