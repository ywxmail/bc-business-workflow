package cn.bc.business.workflow.jiaoche;

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
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.workflow.activiti.ActivitiUtils;

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

	/**
	 * 交车+续保 流程处理
	 * 
	 * @throws Exception
	 */
	@Test
	@Deployment(resources = {
			"cn/bc/business/workflow/jiaoche/CheckExitOperationCarsProcess.bpmn20.xml",
			"cn/bc/business/workflow/jiaoche/ExitOperationCars.form" })
	public void testAgreeRequest() throws Exception {
		String formResourceName = "cn/bc/business/workflow/jiaoche/ExitOperationCars.form";
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

		// 验证任务：实际环境中向用户展示表单
		Task task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("gatherCars", task.getTaskDefinitionKey());
		String taskId = task.getId();

		// 表单验证
		TaskFormData d = formService.getTaskFormData(task.getId());
		Assert.assertEquals(formResourceName, d.getFormKey());
		Object from = formService.getRenderedTaskForm(task.getId());
		Assert.assertNotNull(from);
		Assert.assertEquals(String.class, from.getClass());

		// 提交表单数据（会自动完成当前任务）
		Map<String, String> properties = new LinkedHashMap<String, String>();
		JSONArray cars = new JSONArray();// 车辆列表
		JSONObject car = new JSONObject();// 车辆
		car.put("plate", "c1");
		cars.put(car);
		car = new JSONObject();
		car.put("plate", "c2");
		cars.put(car);
		car = new JSONObject();
		car.put("plate", "c3");
		cars.put(car);
		properties.put("cars", cars.toString());// 指定交车列表(使用json格式)
		properties.put("fenGongSi", "admin");// 指定下一任务的分公司办理人
		formService.submitTaskFormData(task.getId(), properties);
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNull(task);

		// 验证提交的数据
		List<HistoricDetail> hfps = historyService.createHistoricDetailQuery()
				.formProperties().taskId(taskId).orderByFormPropertyId().asc()
				.list();
		Assert.assertNotNull(hfps);
		Assert.assertEquals(2, hfps.size());
		HistoricFormProperty hfp = (HistoricFormProperty) hfps.get(0);
		Assert.assertEquals(taskId, hfp.getTaskId());// 这里证明你懂的
		Assert.assertEquals("cars", hfp.getPropertyId());
		Assert.assertNotNull(hfp.getPropertyValue());
		cars = new JSONArray(hfp.getPropertyValue());
		Assert.assertEquals("c1", cars.getJSONObject(0).get("plate"));
		Assert.assertEquals("c2", cars.getJSONObject(1).get("plate"));
		Assert.assertEquals("c3", cars.getJSONObject(2).get("plate"));
		hfp = (HistoricFormProperty) hfps.get(1);
		Assert.assertEquals("fenGongSi", hfp.getPropertyId());
		Assert.assertEquals("admin", hfp.getPropertyValue());

		// 验证下一任务
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("admin").singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("verifyExitOperationDate",
				task.getTaskDefinitionKey());

		// 完成当前任务:
		// -- 确定交车车辆 TODO 使用恰当的数据类型
		taskService.setVariable(task.getId(), "exitOperationCars",
				new Object[] { "c1,c2" });
		// -- 确定续保车辆 TODO 使用恰当的数据类型
		taskService.setVariable(task.getId(), "renewCars",
				new Object[] { "c3" });

		taskService.complete(task.getId());
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee("admin").singleResult();
		Assert.assertNull(task);

		// 验证下一任务
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskCandidateGroup("zongHeYeWuZu").singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("lastVerify", task.getTaskDefinitionKey());

		// 领取任务:
		taskService.claim(task.getId(), initiator);
		task = taskService.createTaskQuery()
				.processInstanceId(pi.getProcessInstanceId())
				.taskAssignee(initiator).singleResult();
		Assert.assertNotNull(task);
		Assert.assertEquals("lastVerify", task.getTaskDefinitionKey());

		// 完成当前任务:
		if (taskService.getVariable(task.getId(), "exitOperationCars") != null) {// 自动交车
			taskService.setVariable(task.getId(),
					"autoStartCarExitOperationProcess", true);
		}
		if (taskService.getVariable(task.getId(), "renewCars") != null) {// 自动续保
			taskService.setVariable(task.getId(), "autoStartCarRenewProcess",
					true);
		}
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