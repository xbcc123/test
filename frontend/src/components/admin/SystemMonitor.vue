<template>
  <section class="system-monitor-section">
    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="service" tab="服务监控">
        <a-card title="服务健康" :loading="healthLoading">
          <div v-if="healthError" class="error-tip">{{ healthError }}</div>
          <div v-else-if="health">
            <div>状态：<span :style="{color: health.status === 'UP' ? 'green' : 'red'}">{{ health.status }}</span></div>
            <div v-for="(v, k) in health.components" :key="k">{{ k }}: {{ v.status }}</div>
          </div>
          <div v-else class="empty-tip">暂无数据</div>
        </a-card>
        <a-card title="系统资源" style="margin-top:16px;" :loading="metricsLoading">
          <div style="margin-bottom:8px;">
            <a-button size="small" @click="fetchMetrics">手动刷新</a-button>
            <a-switch v-model:checked="autoRefresh" style="margin-left:16px;" checked-children="自动刷新" un-checked-children="手动" />
          </div>
          <div v-if="metricsError" class="error-tip">{{ metricsError }}</div>
          <div v-else-if="metrics">
            <div>CPU 使用率：{{ (metrics['system.cpu.usage'] * 100).toFixed(2) }}%（核心数：{{ metrics['system.cpu.count'] }}）</div>
            <div>JVM 内存使用：{{ (metrics['jvm.memory.used'] / 1024 / 1024).toFixed(2) }} MB / {{ (metrics['jvm.memory.max'] / 1024 / 1024).toFixed(2) }} MB</div>
            <div>JVM 已提交内存：{{ (metrics['jvm.memory.committed'] / 1024 / 1024).toFixed(2) }} MB</div>
            <div>系统物理内存：{{ (metrics['system.memory.total'] / 1024 / 1024 / 1024).toFixed(2) }} GB，总可用：{{ (metrics['system.memory.free'] / 1024 / 1024 / 1024).toFixed(2) }} GB</div>
            <div>系统负载：{{ metrics['system.load.average.1m'] }}</div>
            <div>JVM 线程数：{{ metrics['jvm.threads.live'] }}，峰值：{{ metrics['jvm.threads.peak'] }}</div>
            <div>系统运行时长：{{ (metrics['process.uptime'] / 60).toFixed(2) }} 分钟</div>
            <div v-if="metrics['disk.total']">磁盘总量：{{ (metrics['disk.total'] / 1024 / 1024 / 1024).toFixed(2) }} GB，剩余：{{ (metrics['disk.free'] / 1024 / 1024 / 1024).toFixed(2) }} GB</div>
          </div>
          <div v-else class="empty-tip">暂无数据</div>
        </a-card>
      </a-tab-pane>
      <a-tab-pane key="online" tab="在线用户">
        <a-table :columns="onlineUserColumns" :data-source="onlineUsers" :loading="onlineLoading" rowKey="userId" bordered :locale="{emptyText: onlineError || '暂无在线用户'}" />
        <a-button size="small" @click="fetchOnlineUsers" style="margin-top:8px;">刷新</a-button>
      </a-tab-pane>
      <a-tab-pane key="operation" tab="操作日志">
        <div style="margin-bottom: 12px;">
          <a-form layout="inline" :model="operationLogFilter">
            <a-form-item label="用户名">
              <a-input v-model:value="operationLogFilter.username" placeholder="用户名" allow-clear style="width:120px" />
            </a-form-item>
            <a-form-item label="方法">
              <a-select v-model:value="operationLogFilter.method" allow-clear style="width:100px">
                <a-select-option value="POST">POST</a-select-option>
                <a-select-option value="PUT">PUT</a-select-option>
                <a-select-option value="DELETE">DELETE</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="URI">
              <a-input v-model:value="operationLogFilter.uri" placeholder="URI" allow-clear style="width:140px" />
            </a-form-item>
            <a-form-item label="IP">
              <a-input v-model:value="operationLogFilter.ip" placeholder="IP" allow-clear style="width:120px" />
            </a-form-item>
            <a-form-item label="结果">
              <a-input v-model:value="operationLogFilter.result" placeholder="结果" allow-clear style="width:120px" />
            </a-form-item>
            <a-form-item label="时间">
              <a-range-picker v-model:value="operationLogFilter.timeRange" show-time format="YYYY-MM-DD HH:mm:ss" style="width:260px" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="onOperationLogFilter">筛选</a-button>
              <a-button style="margin-left:8px;" @click="onOperationLogReset">重置</a-button>
            </a-form-item>
          </a-form>
        </div>
        <a-table :columns="operationLogColumns" :data-source="operationLogs" :pagination="operationLogPagination" @change="onOperationLogPageChange" rowKey="id" bordered :loading="operationLogLoading" :locale="{emptyText: operationLogError || '暂无操作日志'}" />
        <a-modal v-model:visible="operationLogDetailVisible" title="操作日志详情" footer="null">
          <div v-if="operationLogDetail">
            <div v-for="(v, k) in operationLogDetail" :key="k"><b>{{ k }}：</b>{{ v }}</div>
          </div>
        </a-modal>
      </a-tab-pane>
      <a-tab-pane key="login" tab="登录日志">
        <a-table :columns="loginLogColumns" :data-source="loginLogs" :pagination="loginLogPagination" @change="onLoginLogPageChange" rowKey="id" bordered :loading="loginLogLoading" :locale="{emptyText: loginLogError || '暂无登录日志'}" />
      </a-tab-pane>
      <a-tab-pane key="redis" tab="缓存监控">
        <a-card title="Redis 信息" :loading="redisLoading">
          <div v-if="redisError" class="error-tip">{{ redisError }}</div>
          <div v-else-if="redisInfo">
            <div>Key 数量：{{ redisInfo.dbSize }}</div>
            <div>内存使用：{{ redisInfo.usedMemory }}</div>
            <div>连接数：{{ redisInfo.connectedClients }}</div>
            <div>运行时长：{{ redisInfo.uptime }} 秒</div>
            <div>版本：{{ redisInfo.version }}</div>
          </div>
          <div v-else class="empty-tip">暂无数据</div>
        </a-card>
        <a-button size="small" @click="fetchRedisInfo" style="margin-top:8px;">刷新</a-button>
      </a-tab-pane>
    </a-tabs>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, h } from 'vue'
import axios from '@/utils/axios'

const activeTab = ref('service')

// 服务监控
const health = ref(null)
const healthLoading = ref(false)
const healthError = ref('')
const metrics = ref(null)
const metricsLoading = ref(false)
const metricsError = ref('')
const autoRefresh = ref(true)
let metricsTimer = null
const fetchHealth = async () => {
  healthLoading.value = true
  healthError.value = ''
  try {
    health.value = await axios.get('/actuator/health')
  } catch (e) {
    healthError.value = '获取服务健康信息失败'
  } finally {
    healthLoading.value = false
  }
}
const fetchMetrics = async () => {
  metricsLoading.value = true
  metricsError.value = ''
  try {
    // 先获取可用指标名
    const metricsListRes = await axios.get('/actuator/metrics')
    const availableMetrics = metricsListRes.names || []
    // 定义所有需要的指标
    const metricKeys = [
      'system.cpu.usage', 'system.cpu.count',
      'jvm.memory.used', 'jvm.memory.max', 'jvm.memory.committed',
      'system.memory.total', 'system.memory.free',
      'system.load.average.1m',
      'jvm.threads.live', 'jvm.threads.peak',
      'process.uptime',
      'disk.total', 'disk.free'
    ]
    const metricFetches = {}
    for (const key of metricKeys) {
      if (availableMetrics.includes(key)) {
        try {
          const res = await axios.get(`/actuator/metrics/${key}`)
          metricFetches[key] = res.measurements?.[0]?.value || 0
        } catch {
          metricFetches[key] = null
        }
      } else {
        metricFetches[key] = null
      }
    }
    metrics.value = metricFetches
  } catch (e) {
    metricsError.value = '获取系统资源信息失败'
  } finally {
    metricsLoading.value = false
  }
}
watch(autoRefresh, (val) => {
  if (val) {
    fetchMetrics()
    metricsTimer = setInterval(fetchMetrics, 30000)
  } else if (metricsTimer) {
    clearInterval(metricsTimer)
    metricsTimer = null
  }
})

// 在线用户
const onlineUsers = ref([])
const onlineUserColumns = [
  { title: '用户ID', dataIndex: 'userId' },
  { title: '用户名', dataIndex: 'username' },
  { title: '活跃时间', dataIndex: 'lastActive', customRender: ({text}) => text ? new Date(Number(text)).toLocaleString() : '' }
]
const onlineLoading = ref(false)
const onlineError = ref('')
const fetchOnlineUsers = async () => {
  onlineLoading.value = true
  onlineError.value = ''
  try {
    onlineUsers.value = await axios.get('/api/monitor/online-users')
  } catch (e) {
    onlineError.value = '获取在线用户失败'
  } finally {
    onlineLoading.value = false
  }
}

// 操作日志
const operationLogs = ref([])
const operationLogColumns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '用户', dataIndex: 'username' },
  { title: '方法', dataIndex: 'method' },
  { title: 'URI', dataIndex: 'uri' },
  { title: '参数', dataIndex: 'params' },
  { title: 'IP', dataIndex: 'ip' },
  { title: '时间', dataIndex: 'time', customRender: ({text}) => text ? new Date(text).toLocaleString() : '' },
  { title: '结果', dataIndex: 'result' },
  { title: '操作', key: 'action', customRender: ({record}) =>
    h('a', { style: 'color:#1890ff;cursor:pointer;', onClick: () => viewOperationLogDetail(record) }, '详情')
  }
]
const operationLogPagination = ref({ current: 1, pageSize: 10, total: 0 })
const operationLogLoading = ref(false)
const operationLogError = ref('')
const operationLogFilter = ref({
  username: '',
  method: '',
  uri: '',
  ip: '',
  result: '',
  timeRange: null
})
const operationLogDetailVisible = ref(false)
const operationLogDetail = ref(null)
const fetchOperationLogs = async (page = 1, size = 10) => {
  operationLogLoading.value = true
  operationLogError.value = ''
  try {
    const params = {
      page: page - 1,
      size,
      username: operationLogFilter.value.username || undefined,
      method: operationLogFilter.value.method || undefined,
      uri: operationLogFilter.value.uri || undefined,
      ip: operationLogFilter.value.ip || undefined,
      result: operationLogFilter.value.result || undefined
    }
    if (operationLogFilter.value.timeRange && operationLogFilter.value.timeRange.length === 2) {
      params.startTime = operationLogFilter.value.timeRange[0].format('YYYY-MM-DDTHH:mm:ss')
      params.endTime = operationLogFilter.value.timeRange[1].format('YYYY-MM-DDTHH:mm:ss')
    }
    const res = await axios.get('/api/monitor/operation-logs', { params })
    operationLogs.value = res.content
    operationLogPagination.value.total = res.totalElements
    operationLogPagination.value.current = page
  } catch (e) {
    operationLogError.value = '获取操作日志失败'
  } finally {
    operationLogLoading.value = false
  }
}
const onOperationLogPageChange = (pagination) => {
  fetchOperationLogs(pagination.current, pagination.pageSize)
}
const onOperationLogFilter = () => {
  fetchOperationLogs(1, operationLogPagination.value.pageSize)
}
const onOperationLogReset = () => {
  operationLogFilter.value = {
    username: '',
    method: '',
    uri: '',
    ip: '',
    result: '',
    timeRange: null
  }
  fetchOperationLogs(1, operationLogPagination.value.pageSize)
}
const viewOperationLogDetail = (record) => {
  operationLogDetail.value = record
  operationLogDetailVisible.value = true
}

// 登录日志
const loginLogs = ref([])
const loginLogColumns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '用户', dataIndex: 'username' },
  { title: 'IP', dataIndex: 'ip' },
  { title: '时间', dataIndex: 'time', customRender: ({text}) => text ? new Date(text).toLocaleString() : '' },
  { title: '成功', dataIndex: 'success', customRender: ({text}) => text ? '是' : '否' },
  { title: '消息', dataIndex: 'message' }
]
const loginLogPagination = ref({ current: 1, pageSize: 10, total: 0 })
const loginLogLoading = ref(false)
const loginLogError = ref('')
const fetchLoginLogs = async (page = 1, size = 10) => {
  loginLogLoading.value = true
  loginLogError.value = ''
  try {
    const res = await axios.get(`/api/monitor/login-logs?page=${page-1}&size=${size}`)
    loginLogs.value = res.content
    loginLogPagination.value.total = res.totalElements
    loginLogPagination.value.current = page
  } catch (e) {
    loginLogError.value = '获取登录日志失败'
  } finally {
    loginLogLoading.value = false
  }
}
const onLoginLogPageChange = (pagination) => {
  fetchLoginLogs(pagination.current, pagination.pageSize)
}

// Redis 监控
const redisInfo = ref(null)
const redisLoading = ref(false)
const redisError = ref('')
const fetchRedisInfo = async () => {
  redisLoading.value = true
  redisError.value = ''
  try {
    redisInfo.value = await axios.get('/api/monitor/redis-info')
  } catch (e) {
    redisError.value = '获取Redis信息失败'
  } finally {
    redisLoading.value = false
  }
}

onMounted(() => {
  fetchHealth()
  fetchMetrics()
  fetchOnlineUsers()
  fetchOperationLogs()
  fetchLoginLogs()
  fetchRedisInfo()
  if (autoRefresh.value) {
    metricsTimer = setInterval(fetchMetrics, 30000)
  }
})
</script>

<style scoped>
.system-monitor-section { padding: 20px; }
.error-tip { color: #d00; margin-bottom: 8px; }
.empty-tip { color: #888; margin: 12px 0; }
</style>
