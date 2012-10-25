/**
 * 
 */
package cn.bc.business.workflow.generalorder.delegate;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 判断岗位是否为配置中其中的一个岗位
 * 
 * @author lbj
 * 
 */
public class IsGroupInConfigGroupsListener implements TaskListener {
	private static final Log logger = LogFactory
			.getLog(IsGroupInConfigGroupsListener.class);

	/**
	 * 保存的岗位名称
	 * 
	 */
	private Expression verifyGroupName;

	/**
	 * 匹配的岗位名称（多个）
	 */
	private Expression anyGroupNames;

	/**
	 * 自定义key
	 * 
	 */
	private Expression customKey;

	/**
	 * 全部变量中已存在此自定key的变量，此变量判断是否需要更新自定义的key值，默认不更新；
	 * 
	 */
	private Expression exist;

	public void notify(DelegateTask delegateTask) {
		if (logger.isDebugEnabled()) {
			logger.debug("anyGroupNames="
					+ (anyGroupNames != null ? anyGroupNames
							.getExpressionText() : null));
			logger.debug("taskDefinitionKey="
					+ delegateTask.getTaskDefinitionKey());
			logger.debug("taskId=" + delegateTask.getId());
			logger.debug("eventName=" + delegateTask.getEventName());
		}

		Object verifyGroupObj = delegateTask.getVariable(this.verifyGroupName
				.getExpressionText());

		if (verifyGroupObj == null)
			return;

		Object key_bf = delegateTask.getVariable(this.customKey.getExpressionText());

		//默认配置或判断的customKey已为true时不再进行判断
		if (key_bf != null&& (exist == null || exist.getExpressionText().equals("false") || (Boolean)key_bf)) {
			return;
		}

		String[] groupNamesArr = anyGroupNames.getExpressionText().split(",");
		for (String gn : groupNamesArr) {
			if (gn.equals(verifyGroupObj.toString())) {
				delegateTask.setVariable(customKey.getExpressionText(), true);
				return;
			}
		}

		delegateTask.setVariable(customKey.getExpressionText(), false);

	}
}
