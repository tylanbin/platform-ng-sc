package me.lb.service.system.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.lb.model.system.Emp;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.EmpService;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmpServiceImpl extends GenericServiceImpl<Emp> implements EmpService {

}