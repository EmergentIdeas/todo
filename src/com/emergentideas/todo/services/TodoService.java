package com.emergentideas.todo.services;

import java.util.List;

import javax.persistence.EntityManager;

import com.emergentideas.todo.data.Todo;
import com.emergentideas.todo.data.TodoList;
import com.emergentideas.webhandle.Type;
import com.emergentideas.webhandle.Wire;

@Type("com.emergentideas.todo.services.TodoService")
public class TodoService {

	protected EntityManager entityManager;
	
	public List<TodoList> getLists(String owningProfile) {
		return entityManager.createQuery("select tdl from TodoList tdl where owningProfile = ?1").setParameter(1, owningProfile).getResultList();
	}
	
	public TodoList createList(String owningProfile, String listName) {
		TodoList tdl = new TodoList();
		tdl.setOwningProfile(owningProfile);
		tdl.setListName(listName);
		entityManager.persist(tdl);
		return tdl;
	}
	
	public Todo createTodo(TodoList todoList, String todoText) {
		Todo todo = new Todo();
		todo.setTodoText(todoText);
		todo.setTodoList(todoList);
		todoList.getTodos().add(todo);
		entityManager.persist(todo);
		entityManager.persist(todoList);
		return todo;
	}
	
	public void deleteTodo(Todo todo) {
		TodoList tdl = todo.getTodoList();
		tdl.getTodos().remove(todo);
		entityManager.remove(todo);
	}
	public TodoList getList(Integer id) {
		return entityManager.find(TodoList.class, id);
	}
	
	public void dontSave(Object o) {
		entityManager.detach(o);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Wire
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	
}
