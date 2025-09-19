<template>
  <section class="petsale-publish-section">
    <h2 class="petsale-title">发布宠物出售信息</h2>
    <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" @finish="submit">
      <a-form-item label="名称" name="name" :rules="[{ required: true, message: '请输���名称' }]">
        <a-input v-model:value="form.name" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="类型" name="type" :rules="[{ required: true, message: '请输入类型' }]">
        <a-input v-model:value="form.type" placeholder="如猫、狗等" />
      </a-form-item>
      <a-form-item label="年龄" name="age" :rules="[{ required: true, message: '请输入年龄' }]">
        <a-input-number v-model:value="form.age" :min="0" style="width:100%" />
      </a-form-item>
      <a-form-item label="价格" name="price" :rules="[{ required: true, message: '请输入价格' }]">
        <a-input-number v-model:value="form.price" :min="0" :step="0.01" style="width:100%" />
      </a-form-item>
      <a-form-item label="描述" name="description">
        <a-input v-model:value="form.description" placeholder="可选" />
      </a-form-item>
      <a-form-item label="图片URL" name="imgUrl">
        <a-input v-model:value="form.imgUrl" placeholder="可选，图片链接" />
      </a-form-item>
      <a-form-item label="状态" name="status" :rules="[{ required: true, message: '新增宠物出售\n' }]">
        <a-select v-model:value="form.status" placeholder="请选择状态">
          <a-select-option value="在售">在售</a-select-option>
          <a-select-option value="下架">下架</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">发布</a-button>
        <a-button style="margin-left: 12px;" @click="goBack">返回</a-button>
      </a-form-item>
    </a-form>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import { message } from 'ant-design-vue'

const router = useRouter()
const formRef = ref()
const form = ref({
  name: '',
  type: '',
  age: 0,
  price: 0,
  description: '',
  imgUrl: '',
  status: '在售'
})
const rules = {
  name: [{ required: true, message: '请输入名称' }],
  type: [{ required: true, message: '请输入类型' }],
  age: [{ required: true, message: '请输入年龄' }],
  price: [{ required: true, message: '请输入价格' }],
}

const submit = async () => {
  try {
    await axios.post('/api/pet-sale', form.value)
    message.success('发布成功！')
    router.push('/pet-sale')
  } catch (e) {
    message.error('发布失败')
  }
}

const goBack = () => {
  router.push('/pet-sale')
}
</script>

<style scoped>
.petsale-publish-section { padding: 20px; max-width: 400px; margin: 0 auto; }
.petsale-title { font-size: 1.5em; margin-bottom: 10px; }
</style>
