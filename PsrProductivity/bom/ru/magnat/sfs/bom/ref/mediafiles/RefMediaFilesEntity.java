package ru.magnat.sfs.bom.ref.mediafiles;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.gcasstate.RefGcasStateEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefMediaFiles.class)
public final class RefMediaFilesEntity extends
		RefGenericEntity<RefMediaFiles, RefMediaFilesEntity> {
	@OrmEntityField(DisplayName = "Имя файла", isPrimary = 0, fields = "FIleName")
	@EntityCardField(DisplayName = "ИмяФайла", Sortkey = 1, SelectMethod = "showFileName")
	public String FileName;
	@EntityCardField(DisplayName = "Начало действия", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "Начало действия", isPrimary = 0, fields = "BeginOfActivity")
	public Date BeginOfActivity;
	@EntityCardField(DisplayName = "Окончание действия", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "Окончание действия", isPrimary = 0, fields = "EndOfActivity")
	public Date EndOfActivity;
	@OrmEntityField(DisplayName = "Порядок", isPrimary = 0, fields = "Seq")
	public long Seq;
	@EntityCardField(DisplayName = "Размер (байт)", Sortkey = 4, SelectMethod = "")
	@OrmEntityField(DisplayName = "Размер", isPrimary = 0, fields = "FileSize")
	public long FileSize;
	
	public boolean isExists() {
		if (FileName==null)
			return false;
		return Globals.findDownloadedFile(FileName);
	}
}
