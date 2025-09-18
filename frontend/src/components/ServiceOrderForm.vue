<template>
  <a-form
    :model="formState"
    layout="vertical"
    @finish="onFinish"
    style="max-width: 500px; margin: 0 auto;"
  >
    <a-form-item label="联系电话" name="phone" :rules="[{ required: true, message: '请输入联系电话' }]">
      <a-input v-model:value="formState.phone" placeholder="请输入联系电话" />
    </a-form-item>
    <a-form-item label="上门地址" name="address" :rules="[{ required: true, message: '请输入上门地址' }]">
      <a-input v-model:value="formState.address" placeholder="请输入上门地址" />
    </a-form-item>
    <a-form-item label="预约时间" name="serviceTime" :rules="[{ required: true, message: '请选择预约时间' }]">
      <a-date-picker
        v-model:value="formState.serviceTime"
        show-time
        style="width: 100%"
        :disabled-date="disabledDate"
        placeholder="请选择预约时间"
      />
    </a-form-item>
    <a-form-item label="宠物名称" name="petName" :rules="[{ required: true, message: '请输入宠物名称' }]">
      <a-input v-model:value="formState.petName" placeholder="如：小黑" />
    </a-form-item>
    <a-form-item label="宠物类型" name="petType" :rules="[{ required: true, message: '请输入宠物类型' }]">
      <a-input v-model:value="formState.petType" placeholder="如：猫/狗" />
    </a-form-item>
    <a-form-item label="备注" name="remark">
      <a-textarea v-model:value="formState.remark" placeholder="可填写特殊需求" />
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit" :loading="loading">提交预约</a-button>
    </a-form-item>
  </a-form>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import axios from '../utils/axios';

const loading = ref(false);
const formState = reactive({
  phone: '',
  address: '',
  serviceTime: null,
  petName: '',
  petType: '',
  remark: '',
});

const disabledDate = (current) => {
  // 禁止选择今天之前的日期
  return current && current < dayjs().startOf('day');
};

const onFinish = async () => {
  loading.value = true;
  try {
    const payload = {
      ...formState,
      serviceTime: formState.serviceTime ? formState.serviceTime.format('YYYY-MM-DD HH:mm:ss') : '',
    };
    const res = await axios.post('/api/service-orders', payload);
    message.success('预约成功！');
    Object.keys(formState).forEach(k => formState[k] = '');
    formState.serviceTime = null;
  } catch (e) {
    message.error('预约失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
</style>
