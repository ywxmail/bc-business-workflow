package cn.bc.business.workflow.companycomplainhandle;

import org.activiti.engine.delegate.DelegateExecution;

import cn.bc.workflow.activiti.delegate.Attach4ProcessListener;

/**
 * 驾驶员客管投诉处理流程附件监听器
 * 
 * @author zxr
 * 
 */
public class ComplainHandleAttach4ProcessListener extends
		Attach4ProcessListener {
	// private static final Log logger = LogFactory
	// .getLog(ComplainHandleAttach4ProcessListener.class);

	// 获取分公司名称filiale
	public void notify(DelegateExecution execution) {
		// 分公司名称
		String branchName = null;
		if (execution.getVariable("filiale") != null) {
			branchName = execution.getVariable("filiale").toString();
		}
		// 通用附件
		addAttach4Resource("complainHandle_commonTemplate", execution);
		// 根据不同的分公司插入不同的投诉回复附件
		if (branchName.equals("四分公司")) {

			addAttach4Resource("complaintHandlingReply4guangfa_commonTemplate",
					execution);// 广发

		} else {
			addAttach4Resource(
					"complaintHandlingReply4baocheng_commonTemplate", execution);// 宝城

		}
	}

}
