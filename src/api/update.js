import request from '@/utils/request'

export const InsertUpdate = (data) => request.post('/update/InsertUpdate', {data})