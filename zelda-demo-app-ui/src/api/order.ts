import { http } from './request'

export interface Order {
  id: number
  orderNo: string
  customerName: string
  amount: number
  status: string
  createTime: string
  updateTime: string
}

export interface OrderQuery {
  page?: number
  size?: number
  orderNo?: string
  customerName?: string
  status?: string
}

// 获取订单列表
export const getOrderList = (params: OrderQuery) => {
  return http.get<Order[]>('/orders', { params })
}

// 获取订单详情
export const getOrderDetail = (id: number) => {
  return http.get<Order>(`/orders/${id}`)
}

// 创建订单
export const createOrder = (data: Partial<Order>) => {
  return http.post<Order>('/orders', data)
}

// 更新订单
export const updateOrder = (id: number, data: Partial<Order>) => {
  return http.put<Order>(`/orders/${id}`, data)
}

// 删除订单
export const deleteOrder = (id: number) => {
  return http.delete(`/orders/${id}`)
}
