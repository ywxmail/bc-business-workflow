/**
 * 
 */
package cn.bc.business.workflow.delegate;

import cn.bc.workflow.activiti.delegate.AbstractAssign2GroupOrUserListener;

/**
 * 将任务分配到"分公司合同管理员"的监听器
 * <p>
 * 调用此监听器前需已设置分公司的id信息，此监听器自动获取此单位下名称为"分公司合同管理员"岗位内的用户信息，如果只有一个用户，直接将用户分配给此用户，
 * 否则就直接将岗位作为任务的候选岗位。
 * </p>
 * 
 * @author dragon
 * 
 */
public class Assign2UnitContractHandlerListener extends
		AbstractAssign2GroupOrUserListener {
	private final static String GROUP_NAME = "分公司合同管理员";

	@Override
	protected String getGroupName() {
		return GROUP_NAME;
	}
}
