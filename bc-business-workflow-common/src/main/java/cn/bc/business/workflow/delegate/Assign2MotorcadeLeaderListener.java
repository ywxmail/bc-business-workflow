/**
 * 
 */
package cn.bc.business.workflow.delegate;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;

/**
 * 将任务分配到"车队长"的监听器
 * <p>
 * 调用此监听器前需已设置车队的id信息，此监听器自动获取车队长的用户信息，直接将车队长分派到任务，
 * </p>
 * 
 * @author lbj
 * 
 */
public class Assign2MotorcadeLeaderListener implements TaskListener {
	private static final Log logger = LogFactory
			.getLog(Assign2MotorcadeLeaderListener.class);
	protected MotorcadeService motorcadeService;

	@Autowired
	public void setMotorcadeService(
			@Qualifier(value = "motorcadeService") MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}
	
	protected ActorService actorService;

	@Autowired
	public void setActorService(
			@Qualifier(value = "actorService") ActorService actorService) {
		this.actorService = actorService;
	}
	
	public void notify(DelegateTask delegateTask) {
		Long orgId = (Long) delegateTask.getVariable("motorcadeId");
		if (logger.isDebugEnabled()) {
			logger.debug("variableName=motorcadeId");
			logger.debug("taskId=" + delegateTask.getId());
			logger.debug("orgId=" + orgId);
			logger.debug("eventName=" + delegateTask.getEventName());
			logger.debug("processInstanceId"
					+ delegateTask.getProcessInstanceId());
			logger.debug("executionId=" + delegateTask.getExecutionId());
			logger.debug("taskDefinitionKey="
					+ delegateTask.getTaskDefinitionKey());
		}
		
		//获取车队长信息
		Motorcade motorcade=motorcadeService.load(orgId);
		if(motorcade==null){
			throw new CoreException("没找到id=" + orgId + "的车队");
		}
		Actor user=actorService.load(motorcade.getPrincipalId());
		if(user!=null){
			//分派到车队负责人
			delegateTask.setAssignee(user.getCode());
			if (logger.isDebugEnabled()) {
				logger.debug("user=" + user.getCode() + ","
						+ user.getName());
			}
		}else{
			throw new CoreException("在用户表中找不到" +motorcade.getName()
					+"，车队负责人名称为"+ motorcade.getPrincipalName()+"的用户");
		}
	}
}
