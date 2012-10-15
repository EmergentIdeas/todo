package com.emergentideas.todo.handles;

import javax.annotation.security.RolesAllowed;

import com.emergentideas.todo.data.Todo;
import com.emergentideas.todo.data.TodoList;
import com.emergentideas.todo.services.TodoService;
import com.emergentideas.webhandle.Inject;
import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.RequestMessages;
import com.emergentideas.webhandle.assumptions.oak.dob.tables.TableDataModel;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;
import com.emergentideas.webhandle.composites.db.Db;
import com.emergentideas.webhandle.handlers.Handle;
import com.emergentideas.webhandle.handlers.HttpMethod;
import com.emergentideas.webhandle.output.Show;
import com.emergentideas.webhandle.output.Template;
import com.emergentideas.webhandle.output.Wrap;

@RolesAllowed("users")
public class TodoListHandle {
	
	protected TodoService todoService;
	
	@Handle({"/lists", "/menu"})
	@Template
	@Wrap("app_page")
	public Object lists(User user, Location location) {
		TableDataModel table = new TableDataModel()
			.setHeaders("List Name")
			.setProperties("listName")
			.setDeleteURLPattern("/list/", "id", "/delete")
			.setEditURLPattern(0, "/list/", "id", "/edit")
			.setCreateNewURL("/lists/create")
			.setItems(todoService.getLists(user.getProfileName()).toArray());
		
		location.put("lists", table);
		
		return "todo/lists";
	}
	
	@Handle(value = "/lists/create", method = HttpMethod.POST)
	public Object createPost(User user, String listName, Location location) {
		TodoList tdl = todoService.createList(user.getProfileName(), listName);
		location.add(tdl);
		return new Show("/list/" + tdl.getId() + "/edit");
	}
	
	@Handle(value = "/lists/create", method = HttpMethod.GET)
	@Template
	@Wrap("app_page")
	public Object createGet(User user, Location location) {
		return "todo/todoListCreate";
	}
	
	@Handle("/list/{id:\\d+}/edit")
	@Template
	@Wrap("app_page")
	public Object editListGet(User user, Integer id, Location location) {
		TodoList tdl = todoService.getList(id);
		location.add(tdl);
		return "todo/todoListEdit";
	}
	
	@Handle(value = "/list/{id:\\d+}/item/create", method = HttpMethod.GET)
	@Template
	public String todoCreateDialogGet(User user, Location location, Integer id) {
		location.put("listId", id);
		return "todo/todoCreate";
	}
	
	@Handle(value = "/list/{id:\\d+}/item/create", method = HttpMethod.POST)
	@Template
	public String todoCreateDialogPost(User user, Location location, @Db("id") TodoList tdl, String todoText) {
		Todo todo = todoService.createTodo(tdl, todoText);
		location.add(todo);
		return "todo/todoEditDisplay";
	}
	
	@Handle(value = "/list-item/{tdid:\\d+}/delete", method = HttpMethod.POST)
	public String todoDeleteTodoDialogPost(User user, @Db("tdid") Todo td) {
		todoService.deleteTodo(td);
		return "";
	}

	@Handle(value = "/list-item/{tdid:\\d+}/edit", method = HttpMethod.GET)
	@Template
	public String todoEditGet(User user, Location location, @Db("tdid") Todo td) {
		location.add(td);
		return "todo/todoEdit";
	}

	@Handle(value = "/list-item/{tdid:\\d+}/edit", method = HttpMethod.POST)
	@Template
	public String todoEditPost(User user, Location location, @Db("tdid") @Inject Todo td, RequestMessages messages) {
		location.add(td);
		if(td.getTodoText().contains("meat")) {
			messages.getErrorMessages().add("I don't think you need any meat");
			todoService.dontSave(td);
			return "todo/todoEdit";
		}
		return "todo/todoEditDisplay";
	}

	public TodoService getTodoService() {
		return todoService;
	}

	@Wire
	public void setTodoService(TodoService todoService) {
		this.todoService = todoService;
	}
	
	
	

}
