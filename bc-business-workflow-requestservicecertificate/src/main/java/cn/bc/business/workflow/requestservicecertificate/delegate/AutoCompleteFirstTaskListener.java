/**
 * 
 */
package cn.bc.business.workflow.requestservicecertificate.delegate;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.util.SpringUtils;
import cn.bc.workflow.service.WorkflowService;

/**
 *  根据条件自动完成第一个任务监听器
 * @author lbj
 * 
 */
public class AutoCompleteFirstTaskListener implements TaskListener {
	private static final Log logger = LogFactory
			.getLog(AutoCompleteFirstTaskListener.class);

	private WorkflowService workflowService;
	private TaskService taskService;
	
	public AutoCompleteFirstTaskListener() {
		workflowService = SpringUtils.getBean(WorkflowService.class);
		taskService = SpringUtils.getBean(TaskService.class);
	}
	


	public void notify(DelegateTask delegateTask) {
		if (logger.isDebugEnabled()) {
			logger.debug("taskDefinitionKey="
					+ delegateTask.getTaskDefinitionKey());
			logger.debug("taskId=" + delegateTask.getId());
			logger.debug("eventName=" + delegateTask.getEventName());
		}
		
		//有司机入职审批的流程实例ID 代表此流程为自动动发起
		if(delegateTask.hasVariable("CarManEntry_ProcessInstanceId")){

			Task task = this.taskService.createTaskQuery()
					.processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
			// 完成第一步办理
			workflowService.claimTask(task.getId());
			
		}

	}
}
