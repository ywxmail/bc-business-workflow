/**
 * 
 */
package cn.bc.business.workflow.delegate;

import cn.bc.workflow.activiti.delegate.AbstractAssign2GroupOrUserListener;

/**
 * 将任务分配到"分公司经理"的监听器
 * <p>
 * 调用此监听器前需已设置分公司的id信息，此监听器自动获取此单位下名称为"分公司经理"岗位内的用户信息，如果只有一个用户，直接将用户分配给此用户，
 * 否则就直接将岗位作为任务的候选岗位。
 * </p>
 * 
 * @author dragon
 * @deprecated 请使用<code>Assign2GroupUserListener</code>代替此类
 * 
 */
public class Assign2UnitManagerListener extends
		AbstractAssign2GroupOrUserListener {
	private final static String GROUP_NAME = "分公司经理";

	@Override
	protected String getGroupName() {
		return GROUP_NAME;
	}

	@Override
	protected String getVariableName() {
		return "verifyUnitId";
	}
}
