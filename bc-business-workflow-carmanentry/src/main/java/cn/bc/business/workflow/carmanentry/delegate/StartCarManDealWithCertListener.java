/**
 * 
 */
package cn.bc.business.workflow.carmanentry.delegate;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.SpringUtils;

/**
 * 自动发起司机办证流程监听器
 * 
 * @author lbj
 * 
 */
public class StartCarManDealWithCertListener implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(StartCarManDealWithCertListener.class);

	private RuntimeService runtimeService;

	public StartCarManDealWithCertListener(){
		runtimeService=SpringUtils.getBean(RuntimeService.class);
	}
	
	/**
	 * 司机办证流程key
	 */
	private Expression key;
	
	/**
	 * 司机办证流程需要的变量 配置信息为“{"自定义key1":"全局变量名1","自定义key2":"全局变量名2","自定义key3":"全局变量名3"...}" 
	 */
	private Expression vKeys;

	public void notify(DelegateExecution execution) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("from=" + execution.getProcessInstanceId());
			logger.debug("key="
					+ key.getExpressionText());
			logger.debug("vKeys="
					+ vKeys.getExpressionText());
		}
		
		//没此公用变量 不自动发起流程；
		if(!execution.hasVariable("executionType"))
			return;
		
		String executionType=(String) execution.getVariable("executionType");
		
		//手动处理和不处理 也不发起流程
		if(executionType.equals("manual")||executionType.equals("noProcess"))
			return;
		
		Map<String, Object> _vKeys=JsonUtils.toMap(vKeys.getExpressionText());
		
		Map<String, Object> variables=new HashMap<String, Object>();
		
		for(String vKey: _vKeys.keySet()){
			if(execution.hasVariable(_vKeys.get(vKey).toString())){
				variables.put(vKey,execution.getVariable(_vKeys.get(vKey).toString()));
			}
		}
		System.out.println("123");
		runtimeService.startProcessInstanceByKey(key.getExpressionText(), variables);
		
	}

}
