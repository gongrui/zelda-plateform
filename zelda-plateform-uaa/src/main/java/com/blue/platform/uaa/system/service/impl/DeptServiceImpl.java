package com.blue.platform.uaa.system.service.impl;

import com.blue.platform.uaa.system.entity.Dept;
import com.blue.platform.uaa.system.mapper.DeptMapper;
import com.blue.platform.uaa.system.service.DeptService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 *
 * @author gongrui
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> listByAppId(String appId) {
        // TODO: 后续可通过 AppDept 关联表实现应用与部门的关联
        return deptMapper.selectListByQuery(
            QueryWrapper.create().eq(Dept::getStatus, true)
        );
    }

    @Override
    public List<Dept> listByParentId(String parentId) {
        return deptMapper.selectListByQuery(
            QueryWrapper.create()
                .eq(Dept::getParentId, parentId)
                .eq(Dept::getStatus, true)
                .orderByAsc(Dept::getSequence)
        );
    }

    @Override
    public List<Dept> buildTree(String appId) {
        List<Dept> allDepts = listByAppId(appId);
        return buildTreeRecursive(allDepts, "0");
    }

    /**
     * 递归构建树形结构
     */
    private List<Dept> buildTreeRecursive(List<Dept> allDepts, String parentId) {
        return allDepts.stream()
            .filter(dept -> parentId.equals(dept.getParentId()))
            .peek(dept -> dept.setChildren(buildTreeRecursive(allDepts, dept.getId())))
            .collect(Collectors.toList());
    }
}
