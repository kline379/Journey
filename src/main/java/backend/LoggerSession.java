package backend;

import java.util.List;
import java.util.ArrayList;

public class LoggerSession 
{
	private List<String> _Logs;

	public LoggerSession() 
	{
		_Logs = new ArrayList<String>();
	}

	public void AddLog(String log)
	{
		_Logs.add(log);
	}

	public int size() 
	{
		return _Logs.size();
	}

	public String getLog(int index)
	{
		return _Logs.get(index);
	}
}