import request from '../utils/request.js'

export function getBooks(keyword) {
  return request({
    url: keyword ? '/api/book/search' : '/api/book/list',
    method: 'get',
    params: keyword ? { title: keyword } : {},
  })
}

export function getBookById(id) {
  return request({
    url: `/api/book/find/${id}`,
    method: 'get',
  })
}

export function getAvailableBooks() {
  return request({
    url: '/api/book/available',
    method: 'get',
  })
}

export function addBook(data) {
  return request({
    url: '/api/book/add',
    method: 'post',
    data,
  })
}

export function updateBook(data) {
  return request({
    url: '/api/book/update',
    method: 'put',
    data,
  })
}

export function deleteBook(id) {
  return request({
    url: `/api/book/delete/${id}`,
    method: 'delete',
  })
}
