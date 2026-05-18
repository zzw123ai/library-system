# 图书馆管理系统 - Bug 记录

## 1. 文档信息

| 项目 | 内容 |
|------|------|
| 文档名称 | Bug 记录 |
| 文档版本 | v1.0 |
| 创建日期 | 2026-05-18 |
| 维护人 | 项目组 |
| 关联项目 | 校园图书借阅管理系统 |
| 关联文档 | `测试计划.md`、`测试用例.md`、`修复验证.md` |

## 2. 记录说明

### 2.1 严重程度定义

| 级别 | 说明 |
|------|------|
| P0 / 严重 | 核心功能不可用、数据损坏、无法登录等 |
| P1 / 中等 | 功能异常但可绕行，或影响管理/测试流程 |
| P2 / 低 | 边界场景、并发、体验类问题 |

### 2.2 状态标识

| 状态 | 含义 |
|------|------|
| ✅ 已修复 | 已改代码并经验证关闭 |
| ⏳ 观察中 | 已记录，待修复或待复测 |
| ❌ 未修复 | 确认存在且尚未处理 |

### 2.3 Bug 编号规则

格式：`BUG-XXX`（三位序号，按发现时间递增）。

---

## 3. Bug 总览

| Bug ID | 发现日期 | 发现人 | 模块 | 严重程度 | 优先级 | 状态 | 关闭日期 |
|--------|----------|--------|------|----------|--------|------|----------|
| BUG-001 | 2026-05-16 | 张栋 | 后端-UserControllerTest | 中等(P1) | P1 | ✅ 已修复 | 2026-05-16 |
| BUG-002 | 2026-05-16 | 张栋 | 前端-UserManage.vue | 严重(P0) | P0 | ✅ 已修复 | 2026-05-16 |
| BUG-003 | 2026-05-16 | 张栋 | 后端-BorrowService | 低(P2) | P2 | ✅ 已修复 | 2026-05-18 |
| BUG-004 | 2026-05-17 | 张栋 | 后端-BookService | 中等(P1) | P1 | ✅ 已修复 | 2026-05-17 |
| BUG-005 | 2026-05-17 | 张栋 | 后端-BorrowService | 中等(P1) | P1 | ✅ 已修复 | 2026-05-17 |
| BUG-006 | 2026-05-18 | 张栋 | 测试配置 | 中等(P1) | P1 | ✅ 已修复 | 2026-05-18 |
| BUG-007 | 2026-05-18 | 赵云扬 | 后端-UserController | 严重(P0) | P0 | ✅ 已修复 | 2026-05-18 |
| BUG-008 | 2026-05-18 | 赵云扬 | 前端-BookManage.vue | 中等(P1) | P1 | ✅ 已修复 | 2026-05-18 |
| BUG-009 | 2026-05-18 | 赵云扬 | 前端-UserManage.vue | 中等(P1) | P1 | ✅ 已修复 | 2026-05-18 |

**统计**：共 9 条；已关闭 9 条；未关闭 0 条。

---

## 4. Bug 详细记录

### BUG-001：用户登录测试密码不匹配

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-001 |
| 发现日期 | 2026-05-16 |
| 发现人 | 张栋 |
| 指派给 | 张栋 |
| 模块 | UserController / UserControllerTest |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-16 |

**问题描述**  
单元测试/集成测试登录接口时使用密码 `admin123`，而 `data.sql` 中管理员初始密码为 `password`，导致登录相关用例失败或结果不可信。

**复现步骤**  
1. 执行 `mvn test`，运行 `UserControllerTest` 中依赖登录的用例。  
2. 使用 `admin` / `admin123` 调用 `POST /api/user/login`。  
3. 返回业务失败或断言不通过。

**期望结果**  
测试密码与初始化数据一致，登录用例稳定通过。

**实际结果**  
密码不一致导致测试失败。

**根因分析**  
测试数据与 `data.sql` 初始账号未对齐。

**修复方案**  
将测试中登录密码统一改为 `password`（与 `data.sql` 中 admin 账号一致）。

**涉及文件**  
`Backend/src/test/java/com/library/system/controller/UserControllerTest.java`

---

### BUG-002：UserManage.vue CSS 语法错误

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-002 |
| 发现日期 | 2026-05-16 |
| 发现人 | 张栋 |
| 指派给 | 张栋 |
| 模块 | 前端-UserManage.vue |
| 严重程度 | 严重(P0) |
| 优先级 | P0 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-16 |

**问题描述**  
用户管理页样式存在多余 `}` 或重复样式块，导致 `npm run build` 报错或页面样式异常。

**复现步骤**  
1. 进入 `frontend` 目录执行 `npm run build`。  
2. 构建失败或控制台报 CSS 解析错误。

**期望结果**  
前端可正常构建，用户管理页样式正常。

**实际结果**  
构建失败或样式错乱。

**根因分析**  
手工合并样式时引入语法错误。

**修复方案**  
删除多余结束括号与重复样式定义。

**涉及文件**  
`frontend/src/views/UserManage.vue`

---

### BUG-003：借阅库存并发扣减不安全

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-003 |
| 发现日期 | 2026-05-16 |
| 发现人 | 张栋 |
| 指派给 | 赵云扬 |
| 模块 | 后端-BorrowService |
| 严重程度 | 低(P2) |
| 优先级 | P2 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-18 |

**问题描述**  
`borrowBook` 采用「先查询 `available` 再 `update`」的 read-modify-write 方式；多用户同时借阅同一图书最后一册时，可能出现库存被扣成负数或重复借出。

**复现步骤**  
1. 某书 `available = 1`。  
2. 两个请求几乎同时调用 `POST /api/borrow/borrow` 借同一 `bookId`。  
3. 观察 `book.available` 可能为 0 以下或产生两条有效借阅。

**期望结果**  
仅一个借阅成功，库存最小为 0。

**实际结果**  
存在并发竞态风险。

**根因分析**  
未使用数据库原子扣减；缺少事务边界。

**修复方案**  
1. 使用 `BookMapper.decreaseAvailable`（`UPDATE ... WHERE available > 0`）。  
2. `borrowBook`、`returnBook` 增加 `@Transactional`。

**涉及文件**  
`Backend/src/main/java/com/library/system/service/BorrowService.java`  
`Backend/src/main/java/com/library/system/mapper/BookMapper.java`

---

### BUG-004：书籍删除后 ID 不连续

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-004 |
| 发现日期 | 2026-05-17 |
| 发现人 | 张栋 |
| 指派给 | 张栋 |
| 模块 | 后端-BookService |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-17 |

**问题描述**  
删除图书记录后，表中剩余记录 ID 出现空洞，不符合项目组数据维护约定（演示环境要求 ID 连续）。

**复现步骤**  
1. 管理员删除中间一条图书记录。  
2. 查询列表，可见 ID 不连续（如 1,3,4）。

**期望结果**  
删除后 ID 重新编号为 1..n（或项目约定策略）。

**实际结果**  
ID 空洞保留。

**修复方案**  
在 `BookService.delete()` 中增加 ID 重排与自增重置逻辑。

**涉及文件**  
`Backend/src/main/java/com/library/system/service/BookService.java`  
`Backend/src/main/java/com/library/system/mapper/BookMapper.java`

---

### BUG-005：借阅记录删除后 ID 不连续

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-005 |
| 发现日期 | 2026-05-17 |
| 发现人 | 张栋 |
| 指派给 | 张栋 |
| 模块 | 后端-BorrowService |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-17 |

**问题描述**  
与 BUG-004 类似，删除借阅记录后 ID 未重排。

**修复方案**  
在 `BorrowService.delete()` 中从大到小更新 ID，避免主键冲突，并重置自增计数器。

**涉及文件**  
`Backend/src/main/java/com/library/system/service/BorrowService.java`  
`Backend/src/main/java/com/library/system/mapper/BorrowMapper.java`

---

### BUG-006：测试环境 Spring Security 拦截

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-006 |
| 发现日期 | 2026-05-18 |
| 发现人 | 张栋 |
| 指派给 | 张栋 |
| 模块 | 测试配置 |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-18 |

**问题描述**  
`@ActiveProfiles("test")` 下执行 Controller 测试时，Spring Security 仍拦截请求，导致 MockMvc 用例返回 401/403，批量测试失败。

**复现步骤**  
1. 在测试 profile 下运行 `mvn test`。  
2. `UserControllerTest` 等接口测试失败。

**期望结果**  
测试环境可稳定调用 API 并完成断言。

**修复方案**  
1. 新增 `TestSecurityConfig`（测试 profile 放行）。  
2. 新增 `TestAuthFilter` 自动注入测试管理员身份。  
3. 测试类使用 `@AutoConfigureMockMvc` 保持过滤器链一致。

**涉及文件**  
`Backend/src/test/java/com/library/system/config/TestSecurityConfig.java`  
`Backend/src/test/java/com/library/system/config/TestAuthFilter.java`  
`Backend/src/test/resources/application-test.properties`

---

### BUG-007：编辑用户时空密码覆盖原密码

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-007 |
| 发现日期 | 2026-05-18 |
| 发现人 | 赵云扬 |
| 指派给 | 赵云扬 |
| 模块 | 后端-UserController |
| 严重程度 | 严重(P0) |
| 优先级 | P0 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-18 |

**问题描述**  
管理员在用户管理中「编辑用户」时，密码框留空表示不修改；但前端仍提交 `password: ""`，后端 `UPDATE` 将密码置空（或明文空串），导致该用户无法再登录。

**复现步骤**  
1. 管理员登录 → 用户管理 → 编辑 `user1`。  
2. 不修改密码，仅改用户名或角色，保存。  
3. 使用 `user1` 原密码 `user123` 登录。

**期望结果**  
未填密码时保持原密码不变，可正常登录。

**实际结果**  
登录失败（密码被清空或破坏）。

**根因分析**  
`UserController.update` 未区分「空密码=不修改」与「新密码」。

**修复方案**  
`password` 为空或空白时保留 `existingUser.getPassword()`；非空则 `PasswordUtil.encode` 后更新。

**涉及文件**  
`Backend/src/main/java/com/library/system/controller/UserController.java`

---

### BUG-008：添加图书未重置表单

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-008 |
| 发现日期 | 2026-05-18 |
| 发现人 | 赵云扬 |
| 指派给 | 赵云扬 |
| 模块 | 前端-BookManage.vue |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-18 |

**问题描述**  
「添加图书」按钮直接设置 `showAddModal = true`，未调用 `addBookHandler()`。若用户先「编辑」某书再点「添加」，弹窗中仍显示上一本书的数据，可能误提交覆盖。

**复现步骤**  
1. 图书管理 → 编辑任意图书 → 关闭。  
2. 点击「添加图书」。  
3. 弹窗中仍为刚编辑的图书信息。

**期望结果**  
添加弹窗应为空白表单。

**修复方案**  
添加按钮事件改为 `@click="addBookHandler"`。

**涉及文件**  
`frontend/src/views/BookManage.vue`

---

### BUG-009：添加用户未重置表单

| 属性 | 内容 |
|------|------|
| Bug ID | BUG-009 |
| 发现日期 | 2026-05-18 |
| 发现人 | 赵云扬 |
| 指派给 | 赵云扬 |
| 模块 | 前端-UserManage.vue |
| 严重程度 | 中等(P1) |
| 优先级 | P1 |
| 状态 | ✅ 已修复 |
| 关闭日期 | 2026-05-18 |

**问题描述**  
与 BUG-008 相同逻辑：「添加用户」未调用 `addUserHandler()`，编辑后再添加会残留上一用户数据。

**修复方案**  
添加按钮事件改为 `@click="addUserHandler"`。

**涉及文件**  
`frontend/src/views/UserManage.vue`

---

## 5. 附录

### 5.1 与测试用例对应关系（节选）

| Bug ID | 相关用例/测试 |
|--------|----------------|
| BUG-001 | UC-LOGIN 系列 / UserControllerTest |
| BUG-003 | BRC-BORROW 系列 / BorrowServiceTest |
| BUG-007 | UC-UPDATE / UserControllerTest.testUpdateUserPreservesPasswordWhenEmpty |

### 5.2 文档版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2026-05-18 | 初始版本，汇总 BUG-001～BUG-009 | 赵云扬 |
