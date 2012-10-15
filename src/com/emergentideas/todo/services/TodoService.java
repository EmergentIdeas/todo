package com.emergentideas.todo.services;

import java.util.List;

import javax.persistence.EntityManager;

import com.emergentideas.todo.data.Todo;
import com.emergentideas.todo.data.TodoList;
import com.emergentideas.webhandle.Type;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;

@Type("com.emergentideas.todo.services.TodoService")
public class TodoService {

	protected EntityManager entityManager;
	protected AuthenticationService authenticationService;
	
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
	
	public User createNewUser(String email, String fullName, String password, String authSystem) {
		User user = authenticationService.createUser(email, email, password);
		authenticationService.setFullName(email, fullName);
		authenticationService.setAuthenticationSystem(email, authSystem);
		authenticationService.addMember("users", email);
		return user;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Wire
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	@Wire
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	
	
}
