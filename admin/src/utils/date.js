/**
 * 日期格式化工具
 */

/**
 * 格式化日期为友好显示
 * @param {string|Date} date 日期
 * @param {string} format 格式类型：full, datetime, date, time, relative
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'datetime') {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const now = new Date()
  const diff = now - d
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  const months = Math.floor(days / 30)
  const years = Math.floor(months / 12)

  // 相对时间格式
  if (format === 'relative') {
    if (seconds < 60) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`
    if (days < 30) return `${Math.floor(days / 7)}周前`
    if (months < 12) return `${months}个月前`
    return `${years}年前`
  }

  // 格式化函数
  const pad = (n) => String(n).padStart(2, '0')
  const year = d.getFullYear()
  const month = pad(d.getMonth() + 1)
  const day = pad(d.getDate())
  const hour = pad(d.getHours())
  const minute = pad(d.getMinutes())
  const second = pad(d.getSeconds())

  switch (format) {
    case 'full':
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    case 'datetime':
      return `${year}-${month}-${day} ${hour}:${minute}`
    case 'date':
      return `${year}-${month}-${day}`
    case 'time':
      return `${hour}:${minute}:${second}`
    case 'short':
      return `${month}-${day} ${hour}:${minute}`
    case 'chinese':
      return `${year}年${month}月${day}日 ${hour}:${minute}`
    case 'chineseDate':
      return `${year}年${month}月${day}日`
    default:
      return `${year}-${month}-${day} ${hour}:${minute}`
  }
}

/**
 * 格式化日期为相对时间（如：3小时前）
 * @param {string|Date} date 日期
 * @returns {string} 相对时间字符串
 */
export function formatRelativeTime(date) {
  return formatDate(date, 'relative')
}

/**
 * 格式化日期为完整时间
 * @param {string|Date} date 日期
 * @returns {string} 完整时间字符串
 */
export function formatFullDateTime(date) {
  return formatDate(date, 'full')
}

/**
 * 格式化日期为日期时间
 * @param {string|Date} date 日期
 * @returns {string} 日期时间字符串
 */
export function formatDateTime(date) {
  return formatDate(date, 'datetime')
}

/**
 * 格式化日期为日期
 * @param {string|Date} date 日期
 * @returns {string} 日期字符串
 */
export function formatDateOnly(date) {
  return formatDate(date, 'date')
}
