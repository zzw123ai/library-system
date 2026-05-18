import request from '../utils/request.js'

export function getBorrows(keyword) {
  const trimmed = typeof keyword === 'string' ? keyword.trim() : ''
  return request({
    url: '/api/borrow/list',
    method: 'get',
    params: trimmed ? { keyword: trimmed } : {},
  })
}

/** 逾期提醒：userId 可选，读者传自己的 id；管理员不传则查全部逾期 */
export function getOverdueReminder(userId) {
  return request({
    url: '/api/borrow/overdue/reminder',
    method: 'get',
    params: userId != null ? { userId } : {},
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
