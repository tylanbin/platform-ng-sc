package me.lb.service.system.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.lb.dao.system.OrgDao;
import me.lb.model.system.Org;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.OrgService;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrgServiceImpl extends GenericServiceImpl<Org> implements OrgService {

	@Override
	public List<Org> findTopOrgs() {
		return ((OrgDao) dao).findTopOrgs();
	}

}