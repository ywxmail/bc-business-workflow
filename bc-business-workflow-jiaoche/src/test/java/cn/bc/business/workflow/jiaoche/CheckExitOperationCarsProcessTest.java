package cn.bc.business.workflow.jiaoche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.util.JsonUtils;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.identity.web.SystemContextImpl;
import cn.bc.workflow.activiti.ActivitiUtils;
import cn.bc.workflow.activiti.form.BcFormEngine;
import cn.bc.workflow.activiti.test.ActivitiRule;

/**
 * 月即将退出营运车辆确认流程
 * 
 * @author dragon
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class CheckExitOperationCarsProcessTest {
	private static final Log logger = LogFactory
			.getLog(CheckExitOperationCarsProcessTest.class);
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	@Rule
	public ActivitiRule activitiSpringRule;

	@Before
	public void setUp() throws Exception {
		SystemContext context = new SystemContextImpl();
		SystemContextHolder.set(context);
		ActorHistory h = new ActorHistory();
		h.setId(new Long(1146));
		h.setActorId(new Long(9));
		h.setActorType(4);
		h.setCode("admin");
		h.setName("系统管理员");
		h.setPname("宝城总部/信息技术部");
		context.setAttr(SystemContext.KEY_USER_HISTORY, h);
	}

	/**
	 * 交车+续保 流程处理
	 * 
	 * @throws Exception
	 */
	@Rollback(true)
	@Test
	@Deployment(resources = {
			"cn/bc/business/workflow/jiaoche/CheckExitOperationCarsProcess.bpmn20.xml",
			"cn/bc/business/workflow/jiaoche/GatherCars.form",
			"cn/bc/business/workflow/jiaoche/VerifyDate.form",
			"cn/bc/business/workflow/jiaoche/ManagerVerify.form",
			"cn/bc/business/workflow/jiaoche/LastVerify.form" })
	public void testAgreeRequest() throws Exception {
		// 测试固定信息：一分公司的车：分公司合同管理员-曾祥汉zeng、分公司经理-胡志勇hzy
		String initiator = "may";
		String processKey = "CheckExitOperationCarsProcess";

		// 设置认证用户
		identityService.setAuthenticatedUserId(initiator);

		// 启动流程（指定编码流程的最新版本，编码对应xml文件中process节点的id值）
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey(processKey);
		logger.debug("pi=" + ActivitiUtils.toString(pi));
		Assert.assertEquals(initiator, runtimeService.getVariable(
				pi.getProcessInstanceId(), "initiator"));

		// 任务1：验证
		Task task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("gatherCars", task.getTaskDefinitionKey());

		// 任务1：表单验证
		TaskFormData d = formService.getTaskFormData(task.getId());
		System.out.println(d.getFormKey());
		Assert.assertTrue(d.getFormKey().endsWith("GatherCars.form"));
		Object from = formService.getRenderedTaskForm(task.getId(),
				BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务1：完成办理
		List<Map<String, Object>> cars = new ArrayList<Map<String, Object>>();
		Map<String, Object> car = new LinkedHashMap<String, Object>();
		car.put("plate", "XXXX1");
		car.put("unit", "一分公司");
		cars.add(car);
		car = new LinkedHashMap<String, Object>();
		car.put("plate", "XXXX2");
		car.put("unit", "一分公司");
		cars.add(car);
		// Map<String, Object> args = new HashMap<String, Object>();
		// args.put("list_gatherCars", JsonUtils.toJson(cars));// 指定交车列表
		// args.put("orgId", new Long(3));// 指定车辆所属分公司的ID
		// taskService.complete(task.getId(), args);
		taskService.setVariable(task.getId(), "list_gatherCars",
				JsonUtils.toJson(cars));// 指定交车列表
		taskService.setVariable(task.getId(), "orgId", new Long(3));// 指定车辆所属分公司的ID
		taskService.complete(task.getId());
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNull(task);

		// 验证提交的数据
		// List<HistoricDetail> hfps =
		// historyService.createHistoricDetailQuery()
		// .formProperties().taskId(taskId).orderByFormPropertyId().asc()
		// .list();
		// Assert.assertNotNull(hfps);
		// Assert.assertEquals(2, hfps.size());
		// HistoricFormProperty hfp = (HistoricFormProperty) hfps.get(0);
		// Assert.assertEquals(taskId, hfp.getTaskId());// 这里证明你懂的
		// Assert.assertEquals("cars", hfp.getPropertyId());
		// Assert.assertNotNull(hfp.getPropertyValue());
		// cars = new JSONArray(hfp.getPropertyValue());
		// Assert.assertEquals("c1", cars.getJSONObject(0).get("plate"));
		// Assert.assertEquals("c2", cars.getJSONObject(1).get("plate"));
		// Assert.assertEquals("c3", cars.getJSONObject(2).get("plate"));
		// hfp = (HistoricFormProperty) hfps.get(1);
		// Assert.assertEquals("fenGongSi", hfp.getPropertyId());
		// Assert.assertEquals("admin", hfp.getPropertyValue());

		// 任务2：验证
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("zeng").singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("verifyDate", task.getTaskDefinitionKey());

		// 任务2：表单验证
		d = formService.getTaskFormData(task.getId());
		Assert.assertTrue(d.getFormKey().endsWith("VerifyDate.form"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务2：完成办理
		cars = new ArrayList<Map<String, Object>>();
		car = new LinkedHashMap<String, Object>();
		car.put("plate", "XXXX1");
		car.put("returnDate", "2012-01-01");
		car.put("xubao", false);
		cars.add(car);
		car = new LinkedHashMap<String, Object>();
		car.put("plate", "XXXX2");
		car.put("xubao", true);
		car.put("returnDate", "2012-01-02");
		cars.add(car);
		taskService.setVariable(task.getId(), "list_verifyCars",
				JsonUtils.toJson(cars));// 交车确认信息
		taskService.complete(task.getId());
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("zeng").singleResult();
		Assert.assertNull(task);

		// 任务3：验证
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("hzy").singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("managerVerify", task.getTaskDefinitionKey());

		// 任务3：表单验证
		d = formService.getTaskFormData(task.getId());
		Assert.assertTrue(d.getFormKey().endsWith("ManagerVerify.form"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务3：完成办理
		taskService.setVariable(task.getId(), "back", false);// 同意
		taskService.complete(task.getId());
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("hzy").singleResult();
		Assert.assertNull(task);

		// 任务4：验证
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("lastVerify", task.getTaskDefinitionKey());

		// 任务4：表单验证
		d = formService.getTaskFormData(task.getId());
		Assert.assertTrue(d.getFormKey().endsWith("LastVerify.form"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务4：完成办理
		taskService.setVariable(task.getId(),
				"autoStartCarExitOperationProcess", false);// 自动发起交车流程
		taskService.setVariable(task.getId(), "autoStartCarRenewProcess",
				false);// 自动发起续保流程
		taskService.setVariable(task.getId(), "manual", true);// 手工处理
		taskService.complete(task.getId());
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNull(task);

		// 流程结束
		Assert.assertEquals(0, runtimeService.createProcessInstanceQuery()
				.processInstanceId(pi.getProcessInstanceId()).count());
	}
}