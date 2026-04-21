<template>
  <n-card title="订单列表">
    <div class="toolbar">
      <n-space>
        <n-input v-model:value="searchForm.orderNo" placeholder="订单号" style="width: 200px" clearable />
        <n-input v-model:value="searchForm.customerName" placeholder="客户名称" style="width: 200px" clearable />
        <n-select
          v-model:value="searchForm.status"
          :options="statusOptions"
          placeholder="订单状态"
          style="width: 150px"
          clearable
        />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
        <n-button type="success" @click="handleCreate" :disabled="!hasPermission('order:create')">
          <template #icon><n-icon :component="PlusOutline" /></template>
          新建订单
        </n-button>
      </n-space>
    </div>

    <n-data-table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      @update:checked-row-keys="onChecked"
    />
  </n-card>

  <!-- 新建/编辑订单弹窗 -->
  <n-modal v-model:show="showModal" preset="dialog" :title="modalTitle">
    <n-form :model="formData" :rules="formRules" ref="formRef">
      <n-form-item label="订单号" path="orderNo">
        <n-input v-model:value="formData.orderNo" :disabled="isEdit" />
      </n-form-item>
      <n-form-item label="客户名称" path="customerName">
        <n-input v-model:value="formData.customerName" />
      </n-form-item>
      <n-form-item label="金额" path="amount">
        <n-input-number v-model:value="formData.amount" :min="0" />
      </n-form-item>
      <n-form-item label="状态" path="status">
        <n-select v-model:value="formData.status" :options="statusOptions" />
      </n-form-item>
    </n-form>
    <template #action>
      <n-space>
        <n-button @click="showModal = false">取消</n-button>
        <n-button type="primary" @click="handleSubmit" :loading="submitting">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import { NButton, NIcon, useMessage, useDialog } from 'naive-ui'
import { PlusOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5'
import { getOrderList, createOrder, updateOrder, deleteOrder, type Order, type OrderQuery } from '@/api/order'
import { useAuthStore } from '@/stores/auth'

const message = useMessage()
const dialog = useDialog()
const authStore = useAuthStore()

// 权限判断
const hasPermission = (permission: string) => {
  const permissions = authStore.permissions || []
  return permissions.includes('*') || permissions.includes(permission) || permissions.some(p => p.endsWith(':*'))
}

// 搜索表单
const searchForm = reactive<OrderQuery>({
  page: 1,
  size: 10,
})

// 状态选项
const statusOptions = [
  { label: '待支付', value: 'PENDING' },
  { label: '已支付', value: 'PAID' },
  { label: '已发货', value: 'SHIPPED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' },
]

// 表格数据
const tableData = ref<Order[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  onChange: (page: number) => {
    pagination.page = page
    loadData()
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize
    pagination.page = 1
    loadData()
  },
})

// 表格列定义
const columns = [
  { type: 'selection' },
  { title: '订单号', key: 'orderNo', width: 150 },
  { title: '客户名称', key: 'customerName', width: 150 },
  { title: '金额', key: 'amount', width: 100, render: (row: Order) => `¥${row.amount.toFixed(2)}` },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row: Order) => {
      const statusMap: Record<string, string> = {
        PENDING: '待支付',
        PAID: '已支付',
        SHIPPED: '已发货',
        COMPLETED: '已完成',
        CANCELLED: '已取消',
      }
      return statusMap[row.status] || row.status
    },
  },
  { title: '创建时间', key: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render: (row: Order) => {
      return h('div', { style: 'display: flex; gap: 8px;' }, [
        h(
          NButton,
          {
            size: 'small',
            type: 'primary',
            onClick: () => handleEdit(row),
            disabled: !hasPermission('order:update'),
          },
          { default: () => '编辑', icon: () => h(NIcon, { component: CreateOutline }) }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            onClick: () => handleDelete(row.id),
            disabled: !hasPermission('order:delete'),
          },
          { default: () => '删除', icon: () => h(NIcon, { component: TrashOutline }) }
        ),
      ])
    },
  },
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      size: pagination.pageSize,
    }
    const res = await getOrderList(params)
    tableData.value = res.data || []
  } catch (error) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.orderNo = undefined
  searchForm.customerName = undefined
  searchForm.status = undefined
  handleSearch()
}

// 弹窗相关
const showModal = ref(false)
const isEdit = ref(false)
const modalTitle = computed(() => (isEdit.value ? '编辑订单' : '新建订单'))
const formRef = ref(null)
const formData = reactive<Partial<Order>>({
  orderNo: '',
  customerName: '',
  amount: 0,
  status: 'PENDING',
})

const formRules = {
  orderNo: { required: true, message: '请输入订单号', trigger: 'blur' },
  customerName: { required: true, message: '请输入客户名称', trigger: 'blur' },
  amount: { required: true, message: '请输入金额', trigger: 'blur' },
}

// 新建订单
const handleCreate = () => {
  isEdit.value = false
  Object.assign(formData, {
    orderNo: '',
    customerName: '',
    amount: 0,
    status: 'PENDING',
  })
  showModal.value = true
}

// 编辑订单
const handleEdit = (row: Order) => {
  isEdit.value = true
  Object.assign(formData, row)
  showModal.value = true
}

// 提交表单
const submitting = ref(false)
const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value && formData.id) {
      await updateOrder(formData.id, formData)
      message.success('更新成功')
    } else {
      await createOrder(formData)
      message.success('创建成功')
    }
    showModal.value = false
    loadData()
  } catch (error) {
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除订单
const handleDelete = (id: number) => {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除该订单吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteOrder(id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        message.error('删除失败')
      }
    },
  })
}

// 选中行
const onChecked = (rowKeys: any[]) => {
  console.log('选中的行:', rowKeys)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
</style>
