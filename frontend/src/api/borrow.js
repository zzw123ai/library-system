import request from '../utils/request.js'

export function getBorrows(keyword) {
  return request({
    url: '/api/borrow/list',
    method: 'get',
  })
}

export function getUserBorrows(userId) {
  return request({
    url: `/api/borrow/user/${userId}`,
    method: 'get',
  })
}

export function addBorrow(data) {
  return request({
    url: '/api/borrow/borrow',
    method: 'post',
    params: { userId: data.userId, bookId: data.bookId, dueDate: data.dueDate },
  })
}

export function returnBorrow(recordId) {
  return request({
    url: '/api/borrow/return',
    method: 'post',
    params: { recordId },
  })
}

export function deleteBorrow(id) {
  return request({
    url: `/api/borrow/delete/${id}`,
    method: 'delete',
  })
}
