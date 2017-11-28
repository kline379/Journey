package backend;

import java.util.List;

import com.sun.tools.javah.resources.l10n;

import java.io.*;
import java.io.Writer;

public class Logger
{
	private File _File;
	private PrintWriter _Out;	
	private Object _Lock = new Object();

	public Logger(String path) throws IOException
	{ 
		_File = new File(path);
		_File.createNewFile();
		FileWriter fw = new FileWriter(_File, true);
		BufferedWriter bw = new BufferedWriter(fw);
		_Out = new PrintWriter(bw);
	}

	protected void finalize()
	{
		_Out.close();
	}

	public LoggerSession getSession()
	{
		return new LoggerSession();
	}

	private void _CheckSize() throws IOException
	{
		if(_File.length() > 1e6)
		{
			_File.delete();
			_File.createNewFile();
			FileWriter fw = new FileWriter(_File, true);
			BufferedWriter bw = new BufferedWriter(fw);
			_Out = new PrintWriter(bw);
		}
	}

	public void Write(LoggerSession session) throws IOException
	{
		synchronized(_Lock)
		{
			_CheckSize();
			for(int i = 0; i < session.size(); i++)
			{
				_Out.println(session.getLog(i));
			}
			_Out.flush();
		}
	}
}
