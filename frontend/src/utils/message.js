import { ElMessage, ElMessageBox } from 'element-plus'

/** 操作成功提示 */
export function notifySuccess(message = '操作成功') {
  ElMessage.success({ message, duration: 2500 })
}

/** 操作失败提示 */
export function notifyError(message = '操作失败') {
  ElMessage.error({ message, duration: 3000 })
}

/**
 * 确认对话框，用户取消时返回 false
 * @param {string} message
 * @param {string} [title='确认']
 */
export async function confirmAction(message, title = '确认') {
  try {
    await ElMessageBox.confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    return true
  } catch {
    return false
  }
}
