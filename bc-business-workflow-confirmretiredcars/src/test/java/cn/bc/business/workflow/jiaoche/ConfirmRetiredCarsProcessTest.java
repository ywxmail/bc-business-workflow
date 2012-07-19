package cn.bc.business.workflow.jiaoche;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.util.JsonUtils;
import cn.bc.workflow.activiti.ActivitiUtils;
import cn.bc.workflow.activiti.form.BcFormEngine;
import cn.bc.workflow.activiti.test.AbstractActivitiTest;

/**
 * 月即将退出营运车辆确认流程
 * 
 * @author dragon
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ConfirmRetiredCarsProcessTest extends AbstractActivitiTest {
	private static final Log logger = LogFactory
			.getLog(ConfirmRetiredCarsProcessTest.class);

	/**
	 * 常规流程处理
	 * 
	 * @throws Exception
	 */
	@Rollback(true)
	@Test
	@Deployment(resources = { "cn/bc/business/workflow/confirmretiredcars/ConfirmRetiredCars.bpmn20.xml" })
	public void testAgreeRequest() throws Exception {
		// 测试固定信息：一分公司的车：分公司合同管理员-曾祥汉zeng、分公司经理-胡志勇hzy
		String initiator = "may";
		String processKey = "ConfirmRetiredCars";

		// 设置认证用户
		setAuthenticatedUser(initiator);

		// 启动流程（指定编码流程的最新版本，编码对应xml文件中process节点的id值）
		logger.debug("--------start process--------");
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey(processKey);
		logger.debug("pi=" + ActivitiUtils.toString(pi));
		logger.debug("--------process getVariable--------");
		Assert.assertEquals(initiator, runtimeService.getVariable(
				pi.getProcessInstanceId(), "initiator"));

		// 任务1：验证
		logger.debug("--------task1 query--------");
		Task task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("gatherCars", task.getTaskDefinitionKey());

		// 任务1：表单验证
		logger.debug("--------task1 getTaskFormData--------");
		TaskFormData d = formService.getTaskFormData(task.getId());
		Assert.assertTrue(d.getFormKey().endsWith("GatherCars.jsp"));
		logger.debug("--------task1 getRenderedTaskForm--------");
		Object from = formService.getRenderedTaskForm(task.getId(),
				BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务1：完成办理
		List<Map<String, Object>> cars = new ArrayList<Map<String, Object>>();
		Map<String, Object> car = new LinkedHashMap<String, Object>();
		car.put("plateNo", "XXXX1");
		cars.add(car);
		car = new LinkedHashMap<String, Object>();
		car.put("plateNo", "XXXX2");
		cars.add(car);
		logger.debug("--------task1 setVariable--------");
		taskService.setVariable(task.getId(), "list_cars",
				JsonUtils.toJson(cars));// 指定交车列表
		taskService.setVariable(task.getId(), "verifyUnitId", new Long(3));// 指定车辆所属分公司的ID
		logger.debug("--------task1 complete--------");
		taskService.complete(task.getId());
		logger.debug("--------task1 complete verify--------");
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNull(task);

		// 任务2：验证
		logger.debug("--------task2 query--------");
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("zeng").singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("verifyDate", task.getTaskDefinitionKey());

		// 任务2：表单验证
		logger.debug("--------task2 form--------");
		d = formService.getTaskFormData(task.getId());
		Assert.assertTrue(d.getFormKey().endsWith("VerifyDate.jsp"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务2：完成办理
		cars = new ArrayList<Map<String, Object>>();
		car = new LinkedHashMap<String, Object>();
		car.put("plateNo", "XXXX1");
		car.put("returnDate", "2012-01-01");
		car.put("xubao", false);
		cars.add(car);
		car = new LinkedHashMap<String, Object>();
		car.put("plateNo", "XXXX2");
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
		Assert.assertTrue(d.getFormKey().endsWith("Fallback.htm"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务3：完成办理
		taskService.setVariable(task.getId(), "fallback", false);// 同意
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
		Assert.assertTrue(d.getFormKey().endsWith("LastVerify.jsp"));
		from = formService.getRenderedTaskForm(task.getId(), BcFormEngine.NAME);
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 任务4：完成办理
		taskService.setVariable(task.getId(), "fireCarRetiredProcess", false);// 自动发起交车流程
		taskService.setVariable(task.getId(), "fireCarRenewProcess", false);// 自动发起续保流程
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