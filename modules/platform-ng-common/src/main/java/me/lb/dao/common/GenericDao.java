package me.lb.dao.common;

import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;

public interface GenericDao<T> {

	public T findById(int id);

	public List<T> findAll();

	public List<T> findAll(Map<String, Object> params);

	public int save(T obj);

	public void update(int id, T obj);

	public void delete(int id);

	public void deleteAll();

	public void deleteAll(List<Integer> ids);

	public Pagination<T> pagingQuery();

	public Pagination<T> pagingQuery(Map<String, Object> params);

}