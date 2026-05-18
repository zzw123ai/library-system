/**
 * @param {(...args: unknown[]) => void} fn
 * @param {number} [wait=300]
 */
export function debounce(fn, wait = 300) {
  let timeoutId = null
  return function debounced(...args) {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    timeoutId = setTimeout(() => {
      timeoutId = null
      fn.apply(this, args)
    }, wait)
  }
}
