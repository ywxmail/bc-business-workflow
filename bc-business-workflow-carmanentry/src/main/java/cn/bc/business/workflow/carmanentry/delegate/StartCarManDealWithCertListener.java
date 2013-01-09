/**
 * 
 */
package cn.bc.business.workflow.carmanentry.delegate;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.util.SpringUtils;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 自动发起司机办证流程监听器
 * 
 * @author lbj
 * 
 */
public class StartCarManDealWithCertListener implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(StartCarManDealWithCertListener.class);

	private WorkflowModuleRelationService  workflowModuleRelationService;
	private WorkflowService workflowService;

	public StartCarManDealWithCertListener(){
		workflowService=SpringUtils.getBean(WorkflowService.class);
		workflowModuleRelationService=SpringUtils.getBean(WorkflowModuleRelationService.class);
	}
	
	/**
	 * 司机办证流程key
	 */
	private Expression key;
	
	@SuppressWarnings("unused")
	private Expression vKeys;

	public void notify(DelegateExecution execution) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("processInstanceId=" + execution.getProcessInstanceId());
			logger.debug("key="
					+ key.getExpressionText());
		}
		
		//没此公用变量 不自动发起流程；
		if(!execution.hasVariable("executionType"))
			return;
		
		String executionType=(String) execution.getVariable("executionType");
		
		//手动处理和不处理 也不发起流程
		if(executionType.equals("manual")||executionType.equals("noProcess"))
			return;
		
		Map<String, Object> variables=new HashMap<String, Object>();
		
		//设置名称
		variables.put("name", execution.getVariable("name").toString());
		//设置身份证号
		variables.put("certIdentity",execution.getVariable("certIdentity").toString());
		//设置司机招聘ID
		variables.put("tempDriver_id",execution.getVariable("tempDriver_id").toString());
		//设置司机入职流程的Id
		variables.put("CarManEntry_ProcessInstanceId", execution.getProcessInstanceId());

		//发起办证流程
		String procInstId=workflowService.startFlowByKey(key.getExpressionText(), variables);
		
		//审批流程加入办证流程的实例ID
		execution.setVariable("RequestDerviceCertficate_ProcessInstanceId", procInstId);
		
		//创建办证流程与司机招聘的流程关系
		WorkflowModuleRelation workflowModuleRelation=new WorkflowModuleRelation();
		workflowModuleRelation.setMid(Long.valueOf(execution.getVariable("tempDriver_id").toString()));
		workflowModuleRelation.setPid(procInstId);
		workflowModuleRelation.setMtype(TempDriver.WORKFLOW_MTYPE);
		this.workflowModuleRelationService.save(workflowModuleRelation);
		
	}

}
