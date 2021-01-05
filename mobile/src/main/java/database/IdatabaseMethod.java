package database;

import java.util.List;

public interface IdatabaseMethod {
	public List<?> getAllBy(String selection, String[] selectionArgs, String orderBy);

	public void DeleteAllRecords();

}
