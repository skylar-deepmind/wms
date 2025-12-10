<template>
  <div class="container">
    <h1>📦 简易仓库管理系统</h1>

    <div class="input-group">
      <input v-model="form.name" placeholder="货物名称" />
      <input v-model="form.quantity" type="number" placeholder="数量" />
      <input v-model="form.remark" placeholder="备注" />
      <button @click="saveItem">保存入库</button>
    </div>

    <table border="1" cellspacing="0" cellpadding="10">
      <thead>
      <tr>
        <th>ID</th>
        <th>名称</th>
        <th>数量</th>
        <th>备注</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in items" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.name }}</td>
        <td>{{ item.quantity }}</td>
        <td>{{ item.remark }}</td>
        <td>
          <button @click="editItem(item)">编辑</button>
          <button @click="deleteItem(item.id)" style="color:red">删除</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// 定义后端接口地址
const API_URL = 'http://localhost:8080/api/items';

// 响应式数据
const items = ref([]); // 列表数据
const form = ref({     // 表单数据
  id: null,
  name: '',
  quantity: '',
  remark: ''
});

// 1. 查询列表
const fetchItems = async () => {
  try {
    const res = await axios.get(API_URL);
    items.value = res.data;
  } catch (error) {
    console.error("查询失败", error);
  }
};

// 2. 保存（新增或修改）
const saveItem = async () => {
  if (!form.value.name) return alert("请输入名称");

  try {
    if (form.value.id) {
      // 有ID就是修改
      await axios.put(API_URL, form.value);
    } else {
      // 没ID就是新增
      await axios.post(API_URL, form.value);
    }
    // 重置表单并刷新列表
    resetForm();
    fetchItems();
  } catch (error) {
    console.error("保存失败", error);
  }
};

// 3. 删除
const deleteItem = async (id) => {
  if(!confirm("确定删除吗？")) return;
  try {
    await axios.delete(`${API_URL}/${id}`);
    fetchItems();
  } catch (error) {
    console.error("删除失败", error);
  }
};

// 4. 点击编辑回显数据
const editItem = (item) => {
  // 深拷贝，防止修改表单时直接影响列表显示
  form.value = { ...item };
};

// 重置表单
const resetForm = () => {
  form.value = { id: null, name: '', quantity: '', remark: '' };
};

// 页面加载时自动查询
onMounted(() => {
  fetchItems();
});
</script>

<style>
.container { width: 600px; margin: 50px auto; text-align: center; }
.input-group { margin-bottom: 20px; }
input { padding: 8px; margin-right: 5px; }
button { padding: 8px 15px; cursor: pointer; }
table { width: 100%; margin-top: 20px; }
</style>