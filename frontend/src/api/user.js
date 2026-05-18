import request from '../utils/request.js'

export function login(payload) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data: payload,
  })
}

export function getMe() {
  return request({
    url: '/api/user/me',
    method: 'get',
  })
}

export function logout() {
  return request({
    url: '/api/user/logout',
    method: 'post',
  })
}

export function getUsers(keyword) {
  return request({
    url: keyword ? '/api/user/search' : '/api/user/list',
    method: 'get',
    params: keyword ? { username: keyword } : {},
  })
}

export function getUserById(id) {
  return request({
    url: `/api/user/find/${id}`,
    method: 'get',
  })
}

export function addUser(data) {
  return request({
    url: '/api/user/add',
    method: 'post',
    data,
  })
}

export function updateUser(data) {
  return request({
    url: '/api/user/update',
    method: 'put',
    data,
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/user/delete/${id}`,
    method: 'delete',
  })
}
