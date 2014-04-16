package ru.magnat.sfs.bom.task.generic;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;

public abstract class TaskGenericEntity<C extends TaskGeneric<? extends TaskGenericEntity<C, M>>, M extends TaskGenericEntity<?, ?>>
		extends GenericEntity<C> {
	@EntityCardField(DisplayName = "Пользователь", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "Автор", isPrimary = 1, fields = "Author")
	public RefUserEntity Author;
	@OrmEntityField(DisplayName = "Основание", isPrimary = 0, fields = "MasterTask")
	public M MasterTask;
	@OrmEntityField(DisplayName = "Удален", isPrimary = 0, fields = "IsMark")
	public Boolean IsMark;
	@OrmEntityField(DisplayName = "Дата создания", isPrimary = 0, fields = "CreateDate")
	public Date TaskDate;
	@EntityCardField(DisplayName = "Начало", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "Начало", isPrimary = 0, fields = "StartDate")
	public Date TaskBegin;
	@EntityCardField(DisplayName = "Конец", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "Конец", isPrimary = 0, fields = "EndDate")
	public Date TaskEnd;
	@OrmEntityField(DisplayName = "Завершена", isPrimary = 0, fields = "IsCompleted")
	public Boolean IsCompleted;
	@EntityCardField(DisplayName = "Результат", Sortkey = 4, SelectMethod = "")
	@OrmEntityField(DisplayName = "Результат", isPrimary = 0, fields = "Result")
	public String Result;

	public void setDefaults(M owner) {
		this.Author = Globals.getUser();
		this.IsMark = false;
		this.TaskDate = new Date();
		this.IsCompleted = false;
		this.TaskBegin = new Date();
		this.MasterTask = owner;
	}

	public boolean getReadOnly() {
		
		return IsCompleted;
	}

}
