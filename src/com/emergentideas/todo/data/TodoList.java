package com.emergentideas.todo.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TodoList {

	@Id
	@GeneratedValue
	protected int id;
	
	protected String listName;
	protected String owningProfile;
	
	@OneToMany
	protected List<Todo> todos = new ArrayList<Todo>();

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public List<Todo> getTodos() {
		return todos;
	}

	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}

	public String getOwningProfile() {
		return owningProfile;
	}

	public void setOwningProfile(String owningProfile) {
		this.owningProfile = owningProfile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
