package MultiThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FutureTest {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter base directory : ");
		String directory = in.nextLine();
		System.out.print("Enter keyword : ");
		String keyword = in.nextLine();
		in.close();
		
		MatchCounter counter = new MatchCounter(new File(directory), keyword);
		FutureTask<Integer> task = new FutureTask<Integer>(counter);
		Thread t = new Thread(task);
		t.start();
		try
		{
			System.out.println(task.get() + " matching files. ");
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
		}
	}
}

/**
 * This task counts the files in a directory and its subdirectories that contain a given keyword.
 */
class MatchCounter implements Callable<Integer>
{

	public MatchCounter(File directory, String keyword)
	{
		this.directory = directory;
		this.keyword = keyword;
	}
	
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		count = 0;
		try
		{
			File[] files = directory.listFiles();
			ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
	
			for (File file : files)
			{
				if (file.isDirectory())
				{
					MatchCounter counter = new MatchCounter(file, keyword);
					FutureTask<Integer> task = new FutureTask<Integer>(counter);
					results.add(task);
					Thread t = new Thread(task);
					t.start();
				}
				else
				{
					if(search(file)) count++;
				}
			}
			for(Future<Integer> result : results)
			{
				try
				{
					count += result.get();
				}
				catch(ExecutionException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(InterruptedException e)
		{
		}
		return count;
	}
	
	public boolean search(File file)
	{
		try
		{
			Scanner in = new Scanner(new FileInputStream(file));
			boolean found = false;
			while(!found && in.hasNextLine())
			{
				String line = in.nextLine();
				if(line.contains(keyword)) found = true;
			}
			in.close();
			return found;
		}
		catch(IOException e)
		{
			return false;
		}
	}
	
	private File directory;
	private String keyword;
	private int count;
}