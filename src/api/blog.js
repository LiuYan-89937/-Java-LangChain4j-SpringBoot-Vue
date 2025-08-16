import request from '@/utils/request'
export const getBlogs = () => request.get('/blog/getBlogs')
export const getAllBlogs = () => request.get(`/blog/getAllBlogs`)
export const getBlogInfro = (id) => request.get(`/blog/getBlogInfro?id=${id}`)
export const addBlogs = (data) => request.post('/blog/addBlog', data)
export const searchBlogsByKey = (data) => request.post(`/blog/searchBlogs`,data)

export const deleteBlogById = (id) => request.post(`/blog/deleteBlog?id=${id}`)

export const getRandomBlogs = () => request.get(`/blog/getRandomBlogs`)
export const getSummary = (content) => request.post(`/summary/chat`,{content})

export const getUserInfroByBlogId = (id) => request.get(`/blog/getUserInfroByBlogId?id=${id}`)