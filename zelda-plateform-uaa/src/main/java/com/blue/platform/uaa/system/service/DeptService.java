package com.blue.platform.uaa.system.service;

import com.blue.platform.uaa.system.entity.Dept;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author gongrui
 */
public interface DeptService extends IService<Dept> {

    /**
     * 根据应用 ID 查询部门列表
     *
     * @param appId 应用 ID
     * @return 部门列表
     */
    List<Dept> listByAppId(String appId);

    /**
     * 根据父部门 ID 查询子部门
     *
     * @param parentId 父部门 ID
     * @return 子部门列表
     */
    List<Dept> listByParentId(String parentId);

    /**
     * 构建树形部门结构
     *
     * @param appId 应用 ID
     * @return 树形部门列表
     */
    List<Dept> buildTree(String appId);
}
