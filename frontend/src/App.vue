<template>
  <div class="app-container">
    <div class="header">
      <h2>📦 简易仓库管理系统</h2>
      <el-button type="primary" @click="openDialog()">+ 新增物资</el-button>
    </div>

    <el-table :data="items" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="物资名称" />
      <el-table-column prop="quantity" label="数量" width="120" />
      <el-table-column prop="remark" label="备注" />

      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button
            size="small"
            type="success"
            plain
            @click="handleStockOp(scope.row, 'in')"
            >入库</el-button
          >

          <el-button
            size="small"
            type="warning"
            plain
            @click="handleStockOp(scope.row, 'out')"
            >出库</el-button
          >

          <el-button size="small" circle @click="editItem(scope.row)"
            >✏️</el-button
          >
          <el-button
            size="small"
            type="danger"
            circle
            @click="deleteItem(scope.row.id)"
            >🗑️</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑物资' : '新增物资'"
      width="30%"
    >
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入物资名称" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input
            v-model="form.quantity"
            type="number"
            placeholder="请输入数量"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveItem">确定保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus"; // 引入消息提示组件

const API_URL = "http://localhost:8080/api/items";

// --- 数据定义 ---
const items = ref([]);
const dialogVisible = ref(false); // 控制弹窗显示的开关
const form = ref({
  id: null,
  name: "",
  quantity: "",
  remark: "",
});

// --- 方法定义 ---

// 1. 查询
const fetchItems = async () => {
  const res = await axios.get(API_URL);
  items.value = res.data;
};

// 2. 打开弹窗 (可能是新增，也可能是编辑)
const openDialog = () => {
  // 每次打开如果是新增，先清空表单
  form.value = { id: null, name: "", quantity: "", remark: "" };
  dialogVisible.value = true;
};

// 3. 点击编辑时
const editItem = (row) => {
  // 把当前行的数据复制给表单
  form.value = { ...row };
  dialogVisible.value = true; // 打开弹窗
};

// 4. 保存
const saveItem = async () => {
  if (!form.value.name) {
    ElMessage.warning("请填写物资名称"); // 漂亮的警告提示
    return;
  }

  try {
    if (form.value.id) {
      await axios.put(API_URL, form.value);
      ElMessage.success("修改成功");
    } else {
      await axios.post(API_URL, form.value);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false; // 关闭弹窗
    fetchItems(); // 刷新列表
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 5. 删除
const deleteItem = (id) => {
  // ElMessageBox 是一个确认框组件
  ElMessageBox.confirm("确定要删除该物资吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      // 用户点了确定
      await axios.delete(`${API_URL}/${id}`);
      ElMessage.success("删除成功");
      fetchItems();
    })
    .catch(() => {
      // 用户点了取消，什么都不做
    });
};

// 处理出入库操作 (type 为 'in' 或 'out')
const handleStockOp = (row, type) => {
  const actionText = type === "in" ? "入库" : "出库";

  // 1. 弹出输入框
  ElMessageBox.prompt(`请输入${actionText}数量`, actionText, {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputType: "number", // 限制只能输数字
    inputPattern: /^[1-9]\d*$/, // 正则校验：必须是正整数
    inputErrorMessage: "请输入有效的正整数",
  })
    .then(async ({ value }) => {
      // 2. 用户点了确定，value 就是输入的数字
      const count = parseInt(value);

      // 3. 根据类型调用不同的后端接口
      const url =
        type === "in"
          ? `${API_URL}/${row.id}/inbound?count=${count}`
          : `${API_URL}/${row.id}/outbound?count=${count}`;

      try {
        await axios.post(url);
        ElMessage.success(`${actionText}成功`);
        fetchItems(); // 刷新列表看最新库存
      } catch (error) {
        // 如果后端报错（比如库存不足），在这里捕获
        // axios 的错误信息通常在 error.response.data 里面，
        // 但因为我们后端写的比较简单直接抛异常，Spring Boot 默认返回结构里 message 包含了错误信息
        console.error(error);
        ElMessage.error("操作失败或库存不足");
      }
    })
    .catch(() => {
      // 用户点了取消，不做任何事
    });
};

onMounted(() => {
  fetchItems();
});
</script>

<style scoped>
.app-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 给整个卡片加个阴影，显高级 */
  border-radius: 8px;
}
.header {
  display: flex;
  justify-content: space-between; /* 标题在左，按钮在右 */
  align-items: center;
  margin-bottom: 20px;
}
</style>
