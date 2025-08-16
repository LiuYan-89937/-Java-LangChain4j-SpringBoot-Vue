import request from '@/utils/request'

export const followUserById = (data) => request.post(`/follow/followUserById`,data)
export const unfollowUserById = (data) => request.post(`/follow/unfollowUserById`,data)
export const getFollowing = (userId) => request.get(`/follow/getFollowingList?userId=${userId}`)