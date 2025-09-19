<template>
  <a-table :columns="columns" :data-source="orders" row-key="id" :loading="loading" bordered>
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'serviceTime'">
        {{ record.serviceTime ? record.serviceTime : '-' }}
      </template>
      <template v-else-if="column.key === 'status'">
        <a-tag :color="statusColor(record.status)">{{ statusText(record.status) }}</a-tag>
      </template>
      <template v-else-if="column.key === 'action'">
        <a-popconfirm
          title="确定要取消该预约吗？"
          ok-text="确定"
          cancel-text="取消"
          @confirm="cancelOrder(record.id)"
          v-if="record.status === '待处理'"
        >
          <a-button type="link" danger size="small">取消</a-button>
        </a-popconfirm>
      </template>
      <template v-else>
        {{ record[column.dataIndex] }}
      </template>
    </template>
  </a-table>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import axios from '../utils/axios';

const orders = ref([]);
const loading = ref(false);

const columns = [
  { title: '预约时间', dataIndex: 'serviceTime', key: 'serviceTime' },
  { title: '上门地址', dataIndex: 'address', key: 'address' },
  { title: '联系电话', dataIndex: 'phone', key: 'phone' },
  { title: '宠物名称', dataIndex: 'petName', key: 'petName' },
  { title: '宠物类型', dataIndex: 'petType', key: 'petType' },
  { title: '备注', dataIndex: 'remark', key: 'remark' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' },
];

const statusText = (status) => {
  switch (status) {
    case '待处理': return '待处理';
    case '已完成': return '已完成';
    case '已取消': return '已取消';
    default: return status;
  }
};
const statusColor = (status) => {
  switch (status) {
    case '待处理': return 'blue';
    case '已完成': return 'green';
    case '已取消': return 'red';
    default: return 'default';
  }
};

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await axios.get('/api/service-orders');
    orders.value = res
  } catch (e) {
    message.error('获取预约失败');
  } finally {
    loading.value = false;
  }
};

const cancelOrder = async (id) => {
  try {
    const res = await axios.put(`/api/service-orders/${id}/cancel`);
    message.success('取消成功');
    fetchOrders();
  } catch (e) {
    message.error('取消失败');
  }
};

onMounted(fetchOrders);
</script>

<style scoped>
</style>
