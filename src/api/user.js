import request from '@/utils/request'

export const registerUser = (data) => request.post('/user/register', data)
export const loginUser = (username, password) => request.post(`/user/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`)
export const logOut = () => request.post('/user/logout')
export const getUserInfo = (id) => request.get(`/user/getUserInfo?id=${id}`)
export const updateUserInfo = (data) => request.post('/user/updateUserInfo', data)