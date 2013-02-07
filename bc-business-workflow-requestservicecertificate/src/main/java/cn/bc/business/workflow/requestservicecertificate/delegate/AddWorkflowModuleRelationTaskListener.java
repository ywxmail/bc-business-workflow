/**
 * 
 */
package cn.bc.business.workflow.requestservicecertificate.delegate;

import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.SpringUtils;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;

/**
 * 增加流程与司机招聘关系的任务监听器
 * 
 * @author lbj
 * 
 */
public class AddWorkflowModuleRelationTaskListener implements TaskListener {
	private static final Log logger = LogFactory
			.getLog(AddWorkflowModuleRelationTaskListener.class);

	private WorkflowModuleRelationService workflowModuleRelationService;

	public AddWorkflowModuleRelationTaskListener() {
		workflowModuleRelationService = SpringUtils
				.getBean(WorkflowModuleRelationService.class);
	}

	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		if (logger.isDebugEnabled()) {
			logger.debug("taskDefinitionKey="
					+ delegateTask.getTaskDefinitionKey());
			logger.debug("taskId=" + delegateTask.getId());
			logger.debug("eventName=" + delegateTask.getEventName());
		}

		//读取司机集合
		List<Object> drivers = (List<Object>) JsonUtils
				.toCollection(delegateTask.getVariable("list_driver")
						.toString());
		//声明流程模块关系
		WorkflowModuleRelation wmr;
		Long driverId;
		for (Object obj_driver : drivers) {
			driverId = Long.valueOf(((Map<String, Object>) obj_driver)
					.get("id").toString());
			wmr = new WorkflowModuleRelation();
			wmr.setMid(driverId);
			wmr.setMtype(TempDriver.WORKFLOW_MTYPE);
			wmr.setPid(delegateTask.getProcessInstanceId());
			//保存流程模块关系
			workflowModuleRelationService.save(wmr);
		}
	}
}
