package backend;

import java.util.List;

import com.sun.tools.javah.resources.l10n;

import java.io.*;
import java.io.Writer;

public class Logger
{
	private PrintWriter _Out;
	private Object _Lock = new Object();

	public Logger(String path) throws IOException
	{ 
		FileWriter fw = new FileWriter(path, true);
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

	public void Write(LoggerSession session)
	{
		synchronized(_Lock)
		{
			for(int i = 0; i < session.size(); i++)
			{
				_Out.println(session.getLog(i));
			}
			_Out.flush();
		}
	}
}
