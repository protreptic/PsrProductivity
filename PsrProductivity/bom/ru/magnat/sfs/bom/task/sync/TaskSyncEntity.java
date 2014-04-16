package ru.magnat.sfs.bom.task.sync;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;

@OrmEntityOwner(owner = TaskSyncJournal.class)
public final class TaskSyncEntity extends TaskGenericEntity<TaskSyncJournal, TaskWorkdayEntity> {
}
