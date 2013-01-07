/**
 * 
 */
package cn.bc.business.workflow.carmanentry.delegate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.tempdriver.service.TempDriverService;
import cn.bc.core.util.SpringUtils;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.workflow.domain.ExcutionLog;
import cn.bc.workflow.service.ExcutionLogService;

/**
 * 更新根据流程信息更新司机招聘状态的监听器
 * 
 * @author lbj
 * 
 */
public class UpdateTempDriverStatusListener implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(UpdateTempDriverStatusListener.class);

	private TempDriverService tempDriverService;
	private ExcutionLogService excutionLogService;
	
	public UpdateTempDriverStatusListener() {
		this.tempDriverService = SpringUtils.getBean(TempDriverService.class);
		excutionLogService = SpringUtils.getBean(ExcutionLogService.class);
	}
	
	public void notify(DelegateExecution execution) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("execution=" + execution.getClass());
			logger.debug("this=" + this.getClass());
			logger.debug("id=" + execution.getId());
			logger.debug("eventName=" + execution.getEventName());
			logger.debug("processInstanceId" + execution.getProcessInstanceId());
			logger.debug("processBusinessKey="
					+ execution.getProcessBusinessKey());
			logger.debug("listener="+this.getClass().getName());
		}
		
		//没此公用变量 不自动发起流程；
		if(!execution.hasVariable("isPass"))
			return;
		
		//当前用户
		ActorHistory h = SystemContextHolder.get().getUserHistory();
		
		//创建日志
		ExcutionLog log=new ExcutionLog();
		log.setFileDate(Calendar.getInstance());
		log.setAuthorId(h.getId());
		log.setAuthorCode(h.getCode());
		log.setAuthorName(h.getName());
		log.setListener(execution.getClass().getName());
		log.setExcutionId(execution.getId());
		log.setType(ExcutionLog.TYPE_PROCESS_SYNC_INFO);
		log.setProcessInstanceId(execution.getProcessInstanceId());
		
		// 记录流程的编码
		if (execution instanceof ExecutionEntity) {
			ExecutionEntity e = (ExecutionEntity) execution;
			log.setExcutionCode(e.getProcessDefinitionId());
			log.setExcutionName(e.getProcessDefinition().getName());
		}

		//获取司机入职是否通过的控制值。
		boolean isPass=(Boolean) execution.getVariable("isPass");
		long id=(Long) execution.getVariable("tempDriver_id");
		Map<String, Object> args=new HashMap<String, Object>();
		if(isPass&&execution.hasVariable("isGiveUp")&&!(Boolean) execution.getVariable("isGiveUp")){
			//聘用
			args.put("status", 2);
			log.setDescription("由于"+execution.getVariable("name").toString()+"通过入职审批，所以其招聘信息的状态修改为聘用。");
		}else{
			//弃用
			args.put("status", 3);
			if(isPass){
				log.setDescription("由于"+execution.getVariable("name").toString()+"放弃入职申请，所以其招聘信息的状态修改为未聘用。");
			}else{
				log.setDescription("由于"+execution.getVariable("name").toString()+"未通过入职审批，所以其招聘信息的状态修改为未聘用。");
			}
		}
		
		tempDriverService.update(id, args);
		//保存日志
		excutionLogService.save(log);
	}

}
