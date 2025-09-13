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

