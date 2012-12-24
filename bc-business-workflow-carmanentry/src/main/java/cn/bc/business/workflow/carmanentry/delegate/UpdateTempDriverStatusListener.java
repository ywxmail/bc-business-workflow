/**
 * 
 */
package cn.bc.business.workflow.carmanentry.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.tempdriver.service.TempDriverWorkFlowService;
import cn.bc.core.util.SpringUtils;

/**
 * 更新根据流程信息更新司机招聘状态的监听器
 * 
 * @author lbj
 * 
 */
public class UpdateTempDriverStatusListener implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(UpdateTempDriverStatusListener.class);

	private TempDriverWorkFlowService tempDriverWorkFlowService;
	
	public UpdateTempDriverStatusListener() {
		this.tempDriverWorkFlowService = SpringUtils.getBean(TempDriverWorkFlowService.class);
	}

	public void notify(DelegateExecution execution) throws Exception {
		if(logger.isDebugEnabled()){
			logger.debug("proc_inst_id_="+execution.getProcessInstanceId());
		}
		
		//没此公用变量 不自动发起流程；
		if(!execution.hasVariable("isPass"))
			return;

		//获取司机入职是否通过的控制值。
		boolean isPass=(Boolean) execution.getVariable("isPass");
		if(isPass){
			//获取司机入职是否放弃控制值	
			if(execution.hasVariable("isGiveUp")&&(Boolean) execution.getVariable("isGiveUp")){
				tempDriverWorkFlowService.doUpdateGiveUp(execution.getProcessInstanceId());
			}else{
				tempDriverWorkFlowService.doUpdatePass(execution.getProcessInstanceId());
			}		
		}else{
			tempDriverWorkFlowService.doUpdateGiveUp(execution.getProcessInstanceId());
		}
		
		
	}

}
