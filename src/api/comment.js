import request from '@/utils/request'
export const getCommentsByBlogId = (id) => request.get(`/comment/getCommentsByBlogId?id=${id}`)
export const addHeartNum= (id) => request.post(`/comment/addHeartNum?id=${id}`)
export const getImageByUsername= (username) => request.get(`/comment/getImageByUsername?username=${username}`)

export const addComment= (blogId,username,content) => request.post(`/comment/addComment?blogId=${blogId}&username=${username}&content=${content}`)
export const deleteComment= (id) => request.post(`/comment/deleteComment?id=${id}`)