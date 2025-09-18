export function getToken() {
  return localStorage.getItem('token') || ''
}
export function setToken(token) {
  if (token) localStorage.setItem('token', token)
  else localStorage.removeItem('token')
}
export function removeToken() {
  localStorage.removeItem('token')
}
export function getUserId() {
  return localStorage.getItem('userId') || ''
}
export function getRole() {
  return localStorage.getItem('role') || ''
}
export function getNickname() {
  return localStorage.getItem('nickname') || ''
}
export function getUsername() {
  return localStorage.getItem('username') || ''
}
